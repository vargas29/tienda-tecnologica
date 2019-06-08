package dominio;

import dominio.repositorio.RepositorioProducto;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.NoResultException;

import dominio.excepcion.GarantiaExtendidaException;

import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
    public static final String EL_PRODUCTO_NO_TIENE_GARANTIA  = "Este producto no tiene garantía extendida";
    public static final String EL_PRODUCTO_NO_REGISTRA = "El producto no se encuentra registrado";

    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;

    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;

    }

    public void generarGarantia(String codigo,String nombreCliente,Date fechaSolicitudGarantia) {
    	Date fechaFinalGarantia;
    	Producto producto;
    	double precioGarantia;
    	
    	try {
			producto = repositorioProducto.obtenerPorCodigo(codigo);
		} catch (NoResultException e) {
			throw new GarantiaExtendidaException(EL_PRODUCTO_NO_REGISTRA);
		}   	
    	

    	if(tieneGarantia(codigo)) {
    		throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
    	}
    	
     // se valida la cantidad de vocales para el codigo 
    	if (tieneTresVocales(codigo)) {
			throw new GarantiaExtendidaException(EL_PRODUCTO_NO_TIENE_GARANTIA );
		}
    	
    	
    	// Se calcula el valor de la garantia y la fecha final de ella dependiendo del valor del producto.
    	if (producto.getPrecio() > 500000) {
    		
    		// Valor de la garantia es igual al 20%
    		precioGarantia = producto.getPrecio() * 0.20;
    		
    		// Calcular fecha final de la solicitud 
    		fechaFinalGarantia = fechaGarantia(fechaSolicitudGarantia, 200);
    		
		} else {
			
			// Valor de la garantia es igual al 10%
			precioGarantia = producto.getPrecio() * 0.10;
			
			// Calcular fecha final de la solicitud
			fechaFinalGarantia = fechaGarantia(fechaSolicitudGarantia, 100);
			
		}    	
    	    	
    	// Se crea garantia del producto
    	GarantiaExtendida garantia = new GarantiaExtendida(producto, fechaSolicitudGarantia, fechaFinalGarantia,
                precioGarantia, nombreCliente);
    			
    	repositorioGarantia.agregar(garantia);

    }

   //Metodo para verificar si el producto tiene garantia
    public boolean tieneGarantia(String codigo) {
		if (repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo) != null) {
			return true;
		}
		return false;
	}

    //metodo general de la garantia con las 3 vocales
    public boolean tieneTresVocales(String codigoProd){
    	int num = 0;
    	String codigo = codigoProd.toLowerCase();
    	for(int i=0;i<codigo.length();i++) {
    		  if ((codigo.charAt(i)=='a') || (codigo.charAt(i)=='e') || (codigo.charAt(i)=='i') || 
    				  (codigo.charAt(i)=='o') || (codigo.charAt(i)=='u')){
    			  num++;
    		  }
    		}
    	if(num == 3){
    		return true;
    	}
    	return false;
    }
    public Date fechaGarantia(Date fechaSolicitud, int diasSuma) {

 		Calendar fechaFinalGarantia = Calendar.getInstance();
 		fechaFinalGarantia.setTime(fechaSolicitud);

 		// Se válida los dias de la garantia sin contar los lunes,
 		// 	si la fecha final cae domingo se suman dos dias
 		
 		for (int i = 0; i < diasSuma; i++) {
 		    fechaFinalGarantia.add(Calendar.DAY_OF_YEAR, 1);
 			if(fechaFinalGarantia.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
 			    fechaFinalGarantia.add(Calendar.DAY_OF_YEAR, 1);
 			}
 		}
 		if(fechaFinalGarantia.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
 			fechaFinalGarantia.add(Calendar.DAY_OF_YEAR, 2);
 		}
 		
 		return fechaFinalGarantia.getTime();

 	}
}
