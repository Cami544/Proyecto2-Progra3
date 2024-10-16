package pos.logic;

import pos.data.*;

import java.util.List;

public class Service {
    private static Service theInstance;

    public static Service instance(){
        if (theInstance == null) theInstance = new Service();
        return theInstance;
    }

    private CategoriaDao categoriaDao;
    private ProductoDao productoDao;
    private ClienteDao clientesDao;
    private CajeroDao cajerosDao;
    private LineaDao lineasDao;
    private FacturaDao facturasDao;



    public Service() {
        try{
            categoriaDao = new CategoriaDao();
            productoDao = new ProductoDao();
            clientesDao = new ClienteDao();
            cajerosDao = new CajeroDao();
            lineasDao = new LineaDao();
            facturasDao = new FacturaDao();
        }
        catch(Exception e){
        }
    }

    public void stop(){
    }


    //================= PRODUCTOS ============
    public void create(Producto e) throws Exception {
        productoDao.create(e);
    }

    public Producto read(Producto e) throws Exception {
        return productoDao.read(e.getId());
    }

    public void update(Producto e) throws Exception {
        productoDao.update(e);
    }

    public void delete(Producto e) throws Exception {
        productoDao.delete(e);
    }

    public List<Producto> search(Producto e) throws RuntimeException {
        try {
            return productoDao.search(e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Producto> obtenerTodosProductos() throws Exception{
        try{
            return productoDao.ObtenerTodosProductos();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Producto buscarProductoPorId(String id) throws Exception{
        try{
            System.out.println("Buscando  producto, service" );
            return productoDao.buscarProductoPorId(id);
        }catch(Exception ex){
            System.out.println("no encontrado producto, service" );
            throw new RuntimeException(ex);
        }
    }


    //================= CATEGORIAS ============
    public List<Categoria> search(Categoria e) {
        try {
            return categoriaDao.search(e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    public List<Categoria> obtenerTodasCategorias() throws Exception {
        return categoriaDao.obtenerTodasCategorias();
    }

    //================ CLIENTES ================

    public void create(Cliente e) throws Exception {
        clientesDao.create(e);
    }

    public Cliente read(Cliente e) throws Exception {
        return clientesDao.read(e.getId());
    }

    public void update(Cliente e) throws Exception {
        clientesDao.update(e);
    }

    public void delete(Cliente e) throws Exception {
        clientesDao.delete(e);
    }

    public List<Cliente> search(Cliente e) {
        try {
            return clientesDao.search(e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Cliente> obtenerTodosClientes() throws Exception {
        return clientesDao.obtenerTodosClientes();
    }


    //================ CAJEROS ================
    public void create(Cajero e) throws Exception {
        cajerosDao.create(e);
    }

    public Cajero read(Cajero e) throws Exception {
        return cajerosDao.read(e.getId());
    }

    public void update(Cajero e) throws Exception {
        cajerosDao.update(e);
    }

    public void delete(Cajero e) throws Exception {
        cajerosDao.delete(e);
    }

    public List<Cajero> search(Cajero e) {
        try {
            System.out.println("buscando cajero" );
            return cajerosDao.search(e);

        } catch (Exception ex) {
            System.out.println("no encontrado en daoCajero" );
            throw new RuntimeException(ex);
        }
    }

    public List<Cajero> obtenerTodosCajeros() throws Exception {
        return cajerosDao.obtenerTodosCajeros();
    }


    //================ FACTURAS ================
    public void create(Factura e) throws Exception {
        facturasDao.create(e);
    }

    public Factura read(Factura e) throws Exception {
        return facturasDao.read(e.getNumero());
    }

    public void update(Factura e) throws Exception {
        facturasDao.update(e);
    }

    public void delete(Factura e) throws Exception {
        facturasDao.delete(e);
    }

    public List<Factura> search(Factura e) throws Exception {
        return facturasDao.search(e);
    }

    public List<Factura> obtenerTodasFacturas() throws Exception {
        return facturasDao.obtenerTodasFacturas();
    }

    public int obtenerSiguienteNumeroFactura() throws Exception {
        return facturasDao.obtenerSiguienteNumeroFactura();
    }

    public List<Factura> obtenerFacturasDeCliente(Factura factura) throws Exception {
        return facturasDao.obtenerFacturasDeCliente(factura);
    }


    //================ LINEAS ================
    public void create(Linea e) throws Exception {
        lineasDao.create(e);
    }

    public Linea read(int id) throws Exception {
        return lineasDao.read(id);
    }

    public void update(Linea e) throws Exception {
        lineasDao.update(e);
    }

    public void delete(Linea e) throws Exception {
        lineasDao.delete(e);
    }

    public List<Linea> search(Linea e) {
        try {
            return lineasDao.search(e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Linea> readByFactura(int facturaNumero) {
        try {
            return lineasDao.readByFactura(facturaNumero);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public int obtenerSiguienteNumeroLinea() throws Exception{
        return lineasDao.obtenerSiguienteNumeroLinea();
    }


    //================ MÉTODOS DE CÁLCULO ================
    public double precioTotalPagar(Factura factura) throws Exception {
        return precioNetoPagarT(factura) - ahorroXDescuentoT(factura);
    }

    public double precioNetoPagarT(Factura factura) throws Exception {
        double neto = 0.0;
        List<Linea> lineas = readByFactura(factura.getNumero());
        for (Linea linea : lineas) {
            neto += linea.getProducto().getPrecio() * linea.getCantidad();
        }
        return neto;
    }

    public double ahorroXDescuentoT(Factura factura) throws Exception {
        double ahorro = 0.0;
        List<Linea> lineas = readByFactura(factura.getNumero());
        for (Linea linea : lineas) {
            ahorro += linea.getProducto().getPrecio() * linea.getCantidad() * linea.getDescuento();
        }
        return ahorro;
    }

    public int cantProductosT(Factura factura) throws Exception {
        int cantidad = 0;
        List<Linea> lineas = readByFactura(factura.getNumero());
        for (Linea linea : lineas) {
            cantidad += linea.getCantidad();
        }
        return cantidad;
    }
    public float getVentas(Categoria c, int anio, int mes) throws Exception {
        return facturasDao.getVentas(c, anio, mes);
    }


}
