package pos.logic;

import pos.data.Data;
import pos.data.XmlPersister;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.*;

public class Service {
    private static Service theInstance;

    public static Service instance(){
        if (theInstance == null) theInstance = new Service();
        return theInstance;
    }
    private Data data;

    private Service(){
        try{
            data = XmlPersister.instance().load();
        }
        catch(Exception e){
            out.println("Error al cargar el XML: " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace para mayor detalle
            data = new Data();
        }
    }

    public void stop(){
        try {
            XmlPersister.instance().store(data);
        } catch (Exception e) {
            out.println(e);
        }
    }

//================= CLIENTES ============

    public void create(Cliente e) throws Exception{
        Cliente result = data.getClientes().stream().filter(i->i.getId().equals(e.getId())).findFirst().orElse(null);
        if (result==null) data.getClientes().add(e);
        else throw new Exception("Cliente ya existe");
    }

    public Cliente read(Cliente e) throws Exception{
        Cliente result = data.getClientes().stream().filter(i->i.getId().equals(e.getId())).findFirst().orElse(null);
        if (result!=null) return result;
        else throw new Exception("Cliente no existe");
    }

    public void update(Cliente e) throws Exception{
        Cliente result;
        try{
            result = this.read(e);
            data.getClientes().remove(result);
            data.getClientes().add(e);
        }catch (Exception ex) {
            throw new Exception("Cliente no existe");
        }
    }

    public void delete(Cliente e) throws Exception{
        data.getClientes().remove(e);
    }

    public List<Cliente> search(Cliente e){
        return data.getClientes().stream()
                .filter(i->i.getNombre().contains(e.getNombre()))
                .sorted(Comparator.comparing(Cliente::getNombre))
                .collect(Collectors.toList());
    }

    public List<Cliente> getAllClientes() {
        return data.getClientes().stream()
                .sorted(Comparator.comparing(Cliente::getNombre))
                .collect(Collectors.toList());
    }

    //=============Cajeros=============

    public void create(Cajero c) throws Exception{
        Cajero result = data.getCajeros().stream().filter(i->i.getId().equals(c.getId())).findFirst().orElse(null);
        if (result==null) data.getCajeros().add(c);
        else throw new Exception("Cajero ya existente");
    }


    public Cajero read(Cajero c) throws Exception{
        Cajero result = data.getCajeros().stream().filter(i->i.getId().equals(c.getId())).findFirst().orElse(null);
        if (result!=null) return result;
        else throw new Exception("Cajero no existe");
    }

    public void update(Cajero c) throws Exception{
        Cajero result;
        try{
            result = this.read(c);
            data.getCajeros().remove(result);
            data.getCajeros().add(c);
        }catch (Exception ex) {
            throw new Exception("Cajero no existe");
        }
    }


    public void delete(Cajero c) throws Exception{
        data.getCajeros().remove(c);
    }

    public List<Cajero> search(Cajero c){
        return data.getCajeros().stream()
                .filter(i->i.getNombre().contains(c.getNombre()))
                .sorted(Comparator.comparing(Cajero::getNombre))
                .collect(Collectors.toList());
    }

    public List<Cajero> getAllCajeros() {
        return data.getCajeros().stream()
                .sorted(Comparator.comparing(Cajero::getNombre))
                .collect(Collectors.toList());
    }
    //============Productos================

    public void create(Producto e) throws Exception{
        Producto result = data.getProductos().stream().filter(i->i.getId().equals(e.getId())).findFirst().orElse(null);
        if (result==null) data.getProductos().add(e);
        else throw new Exception("Producto ya existe");
    }

    public Producto read(Producto e) throws Exception{
        Producto result = data.getProductos().stream().filter(i->i.getId().equals(e.getId())).findFirst().orElse(null);
        if (result!=null) return result;
        else throw new Exception("Producto no existe");
    }

    public void update(Producto e) throws Exception{
        Producto result;
        try{
            result = this.read(e);
            data.getProductos().remove(result);
            data.getProductos().add(e);
        }catch (Exception ex) {
            throw new Exception("Producto no existe");
        }
    }

    public void delete(Producto e) throws Exception{
        data.getProductos().remove(e);
    }

    public List<Producto> search(Producto e){
        return data.getProductos().stream()
                .filter(i->i.getNombre().contains(e.getNombre()))
                .sorted(Comparator.comparing(Producto::getNombre))
                .collect(Collectors.toList());
    }
    public Producto getProductoById(String id) throws Exception {

        return data.getProductos().stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new Exception("Producto no existe"));

    }
    public List<Producto> getAllProductos() {
        return data.getProductos().stream()
                .sorted(Comparator.comparing(Producto::getNombre))
                .collect(Collectors.toList());
    }
    public void updateProducto(Producto producto) throws Exception {
        Producto existingProducto = data.getProductos().stream()
                .filter(p -> p.getId().equals(producto.getId()))
                .findFirst()
                .orElse(null);
        if (existingProducto != null) {
            data.getProductos().remove(existingProducto);
            data.getProductos().add(producto);
        } else {

            throw new Exception("Producto no encontrado para actualizar.");
        }
    }
//================Facturas===============


    public void create(Factura e) throws Exception{
        Factura result = data.getFacturas().stream().filter(i->i.getNumero().equals(e.getNumero())).findFirst().orElse(null);
        if (result==null) data.getFacturas().add(e);
        else throw new Exception("Factura ya existe");
    }

    public Factura read(Factura e) throws Exception{
        Factura result = data.getFacturas().stream().filter(i->i.getNumero().equals(e.getNumero())).findFirst().orElse(null);
        if (result!=null) return result;
        else throw new Exception("Factura no existe");
    }

    public void update(Factura e) throws Exception{
        Factura result;
        try{
            result = this.read(e);
            data.getFacturas().remove(result);
            data.getFacturas().add(e);
        }catch (Exception ex) {
            throw new Exception("Factura no existe");
        }
    }

    public void delete(Factura e) throws Exception{
        data.getFacturas().remove(e);
    }

    public List<Factura> search(Factura e){
        return data.getFacturas().stream()
                .filter(i->i.getNumero().contains(e.getNumero()))
                .sorted(Comparator.comparing(Factura::getNumero))
                .collect(Collectors.toList());
    }
    public List<Factura> searchCliente(Factura e){
        return data.getFacturas().stream()
                .filter(i->i.getCliente().getNombre().contains(e.getCliente().getNombre()))
                .sorted(Comparator.comparing(Factura::getNumero))
                .collect(Collectors.toList());
    }

    public List<Factura> getAllFacturas() {
        return data.getFacturas().stream()
                .sorted(Comparator.comparing(Factura::getNumero))
                .collect(Collectors.toList());
    }

    public List<Factura> searchFacturasByCliente(Factura filter) {
        return data.getFacturas().stream()
                .filter(factura -> factura.getCliente().getNombre().contains(filter.getCliente().getNombre()))
                .sorted(Comparator.comparing(Factura::getNumero))
                .collect(Collectors.toList());
    }
//categorias


    public List<Categoria> getAllCategorias() {
        try {
            Data data = XmlPersister.instance().load();
            return data.getCategorias();
        } catch (Exception e) {
            System.out.println("Error al cargar las categorías: " + e.getMessage());
            return new ArrayList<>();
        }

    }


//lineas


    public void create(Linea e) throws Exception{
        Linea result = data.getLineas().stream().filter(i->i.getNumero().equals(e.getNumero())).findFirst().orElse(null);
        if (result==null) data.getLineas().add(e);
        else throw new Exception("Linea ya existe");
    }

    public Linea read(Linea e) throws Exception{
        Linea result = data.getLineas().stream().filter(i->i.getNumero().equals(e.getNumero())).findFirst().orElse(null);
        if (result!=null) return result;
        else throw new Exception("Linea no existe");
    }

    public void update(Linea e) throws Exception{
        Linea result;
        try{
            result = this.read(e);
            data.getLineas().remove(result);
            data.getLineas().add(e);
        }catch (Exception ex) {
            throw new Exception("Producto no existe");
        }
    }

    public List<Linea> getAllLineas() {
        return data.getLineas().stream()
                .sorted(Comparator.comparing(Linea::getNumero))
                .collect(Collectors.toList());
    }

    public float getVentas(Categoria c, int year, int month) {
        float total = 0;

        for (Factura f : data.getFacturas()) {
            if (f.getFecha().getYear() == year && f.getFecha().getMonthValue() == month) {
                for (Linea l : f.getLineas()) {
                    if (l.getProducto().getCategoria().equals(c)) {
                        total += l.getTotal();
                    }
                }
            }
        }
        return total;
    }


}



