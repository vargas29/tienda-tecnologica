package dominio.unitaria;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dominio.Producto;
import dominio.GarantiaExtendida;
import testdatabuilder.ProductoTestDataBuilder;

public class ProductoTest {

	private static final String CODIGO = "S01H1AT51";
	private static final String NOMBRE_PRODUCTO = "Impresora";
	private static final int PRECIO = 550000;

	@Test
	public void crearProductoTest() {
		
		// arrange
		ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder().
				conNombre(NOMBRE_PRODUCTO).
				conCodigo(CODIGO).
				conPrecio(PRECIO);

		// act
		Producto producto = productoTestDataBuilder.build();

		// assert
		assertEquals(NOMBRE_PRODUCTO, producto.getNombre());
		assertEquals(CODIGO, producto.getCodigo());
		assertEquals(PRECIO, producto.getPrecio(),0);
}
	
	@Test
	public void crearGarantiaProductoTest() {
		
		// arrange
		ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder().
				conNombre(NOMBRE_PRODUCTO).
				conCodigo(CODIGO).
				conPrecio(PRECIO);

		// act
		Producto producto = productoTestDataBuilder.build();
		GarantiaExtendida garantiaExtendida = new GarantiaExtendida(producto);
		// assert
		assertEquals(NOMBRE_PRODUCTO, garantiaExtendida.getProducto().getNombre());
		assertEquals(CODIGO, garantiaExtendida.getProducto().getCodigo());
		assertEquals(PRECIO, garantiaExtendida.getProducto().getPrecio(),0);
	}

}
