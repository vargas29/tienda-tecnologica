package dominio.integracion;

import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Vendedor;
import dominio.Producto;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioProducto;
import dominio.repositorio.RepositorioGarantiaExtendida;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.ProductoTestDataBuilder;

public class VendedorTest {

	private static final String COMPUTADOR_LENOVO = "Computador Lenovo";
	private static final String NOMBRE_CLIENTE = "MARIA MENDOZA";
	private static final SimpleDateFormat FORMATOFECHA = new SimpleDateFormat("yyyy-MM-dd");
	private static final String CODIGO_GARANTIA = "F01TSA0150";
	private static final String CODIGO_TRES_VOCALES_TIENE_GARANTIA = "67atyuojj";
	
	private static final double PRECIO_PRODUCTO_MAYOR = 830000;
	private static final double PRECIO_GARANTIA_MAYOR = PRECIO_PRODUCTO_MAYOR * 0.20;
	private static final double PRECIO_PRODUCTO_MENOR = 200000;
	private static final double PRECIO_GARANTIA_MENOR = PRECIO_PRODUCTO_MENOR * 0.10;
	
	private static final String FECHA_SOLICITUD = "2019-07-25";
	private static final String FECHA_FINAL_BAJO = "2019-11-19";
	private static final String FECHA_SOLICITUD_DOMINGO = "2019-07-31";
	private static final String FECHA_SOLICITUD_DOMINGO_FINAL = "2019-11-26";

 private SistemaDePersistencia sistemaPersistencia;
	
	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	@Before
	public void setUp() {
		
		sistemaPersistencia = new SistemaDePersistencia();
		
		repositorioProducto = sistemaPersistencia.obtenerRepositorioProductos();
		repositorioGarantia = sistemaPersistencia.obtenerRepositorioGarantia();
		
		sistemaPersistencia.iniciar();
	}

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void generarGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, new Date());

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));
	}

	@Test
	public void productoYaTieneGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, new Date());
		try {

			vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, new Date());
			fail();
			
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_TIENE_GARANTIA, e.getMessage());
		}
	}

	@Test
	public void productoTieneTresVocalesTest() throws ParseException{
		// arrange
		Producto producto = new ProductoTestDataBuilder().conCodigo(CODIGO_TRES_VOCALES_TIENE_GARANTIA).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		try {
			vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, new Date());
			fail();
	
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_NO_TIENE_GARANTIA, e.getMessage());
		}
	}
	
	
	@Test
	public void productoNoRegistradoTest() throws ParseException{
		// arrange
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		try {
			vendedor.generarGarantia(CODIGO_GARANTIA, NOMBRE_CLIENTE, new Date());
			fail();
					
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_NO_REGISTRA, e.getMessage());
		}
	}
	
	@Test
	public void precioProductoMayorTest() throws ParseException{
		// arrange
		Producto producto = new ProductoTestDataBuilder().conPrecio(PRECIO_PRODUCTO_MAYOR).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, FORMATOFECHA.parse(FECHA_SOLICITUD));

		// assert
		Assert.assertEquals(repositorioGarantia.obtener(producto.getCodigo()).getPrecioGarantia(), PRECIO_GARANTIA_MAYOR, 0);
		
	}
	
	@Test
	public void precioProductoMenorTest() throws ParseException{
		// arrange
		Producto producto = new ProductoTestDataBuilder().conPrecio(PRECIO_PRODUCTO_MENOR).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, FORMATOFECHA.parse(FECHA_SOLICITUD));
		
		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertEquals(repositorioGarantia.obtener(producto.getCodigo()).getFechaFinGarantia(), 
				FORMATOFECHA.parse(FECHA_FINAL_BAJO));
	}
	
	@Test
	public void precioProductoMenorFechaFinalGarantiaTest() throws ParseException{
		// arrange
		Producto producto = new ProductoTestDataBuilder().conPrecio(PRECIO_PRODUCTO_MENOR).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, FORMATOFECHA.parse(FECHA_SOLICITUD));
		
		// assert
		Assert.assertEquals(repositorioGarantia.obtener(producto.getCodigo()).getPrecioGarantia(), PRECIO_GARANTIA_MENOR, 0);
	}
	
	@Test
	public void fechaGarantiaDomingoTest() throws ParseException{
		// arrange
		Producto producto = new ProductoTestDataBuilder().conPrecio(PRECIO_PRODUCTO_MENOR).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, FORMATOFECHA.parse(FECHA_SOLICITUD_DOMINGO));
		
		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertEquals(repositorioGarantia.obtener(producto.getCodigo()).getFechaFinGarantia(), 
				FORMATOFECHA.parse(FECHA_SOLICITUD_DOMINGO_FINAL));
	}
	

	
	
}
