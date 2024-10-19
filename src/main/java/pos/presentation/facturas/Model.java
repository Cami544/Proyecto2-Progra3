package pos.presentation.facturas;
import pos.logic.Linea;
import pos.Application;
import pos.logic.Factura;
import pos.logic.Cliente;
import pos.logic.Producto;
import pos.logic.Cajero;
import pos.presentation.AbstractModel;

import java.beans.PropertyChangeListener;
import java.util.List;

public class Model extends AbstractModel {
    Factura current;
    List<Factura> list;
    Factura filter;
    int mode;
    List<Cliente> listClientes;
    List<Producto> listProductos;
    List<Cajero> listCajeros;

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
    }

    public Model() {
        this.current = new Factura();
        this.filter = new Factura();
        this.mode = Application.MODE_CREATE;
    }

    public void init(List<Factura> list, List<Cliente> listClientes, List<Producto> listProductos, List<Cajero> listCajeros) {
        this.list = list;
        this.listClientes = listClientes;
        this.listProductos = listProductos;
        this.listCajeros = listCajeros;
        this.current = new Factura();
        this.filter = new Factura();
        this.mode = Application.MODE_CREATE;
    }

    public List<Factura> getList() {
        return list;
    }

    public void setList(List<Factura> list) {
        this.list = list;
        firePropertyChange(LIST);
    }

    public Factura getCurrent() {
        return current;
    }

    public void setCurrent(Factura current) {
        this.current = current;
    }

    public Factura getFilter() {
        return filter;
    }

    public void setFilter(Factura filter) {
        this.filter = filter;
        firePropertyChange(FILTER);
    }

    public void addLinea(Linea linea) {
        current.getLineas().add(linea);

    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }


    public List<Cliente> getListClientes() {
        return listClientes;
    }
    void setListClientes(List<Cliente> listClientes) { this.listClientes = listClientes; }


    public List<Cajero> getListCajeros() {
        return listCajeros;
    }
    void setListCajeros(List<Cajero> listCajeros) { this.listCajeros = listCajeros; }


    // Constantes para las propiedades
    public static final String LIST = "list";
    public static final String CURRENT = "current";
    public static final String FILTER = "filter";
    public static final String LIST_CLIENTES = "listClientes";
    public static final String LIST_PRODUCTOS = "listProductos";
    public static final String LIST_CAJEROS = "listCajeros";

    public void updateModel() {
        firePropertyChange(LIST);
        firePropertyChange(CURRENT);
        firePropertyChange(FILTER);
        firePropertyChange(LIST_CLIENTES);
        firePropertyChange(LIST_PRODUCTOS);
        firePropertyChange(LIST_CAJEROS);
    }

}
