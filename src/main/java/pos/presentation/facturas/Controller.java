package pos.presentation.facturas;

import pos.Application;
import pos.logic.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    View view;
    Model model;
    //1
    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;

        view.setController(this);
        view.setModel(model);

        List<Factura> facturas = Service.instance().getAllFacturas();
        List<Cliente> clientes = Service.instance().getAllClientes();
        List<Producto> productos = Service.instance().getAllProductos();
        List<Cajero> cajeros = Service.instance().getAllCajeros();

        model.init(facturas, clientes, productos, cajeros);

        view.updateClientesComboBox(clientes);
        view.updateCajerosComboBox(cajeros);
    }
    //2
    public void search(Factura filter) throws Exception {
        model.setFilter(filter);
        model.setMode(Application.MODE_CREATE);
        model.setCurrent(new Factura());
        model.setList(Service.instance().search(model.getFilter()));
    }
    //3
    public void save(Factura e) throws Exception {
        switch (model.getMode()) {
            case Application.MODE_CREATE:
                Service.instance().create(e);
                break;
            case Application.MODE_EDIT:
                Service.instance().update(e);
                break;
        }
        model.setFilter(new Factura());
        search(model.getFilter());
    }
    //4
    public void save(Linea e) throws Exception {
        switch (model.getMode()) {
            case Application.MODE_CREATE:
                Service.instance().create(e);
                break;
            case Application.MODE_EDIT:
                Service.instance().update(e);
                break;
        }
        model.setFilter(new Factura());
        search(model.getFilter());
    }
    //5
    public void edit(int row) {
        if (row >= 0 && row < model.getList().size()) {
            Factura e = model.getList().get(row);
            try {
                model.setMode(Application.MODE_EDIT);
                model.setCurrent(Service.instance().read(e));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(view.getPanel(), "Invalid row selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //6
    public void delete() throws Exception {
        Service.instance().delete(model.getCurrent());
        search(model.getFilter());
    }
    //7
    public void clear() {
        model.getCurrent().getLineas().clear();
        model.updateModel();
    }
    //8
    public void buscarProductoPorId(String productId) throws Exception {
        if (productId != null && !productId.trim().isEmpty()) {
            try {
                Producto producto = Service.instance().getProductoById(productId);
                if (producto != null) {
                    Factura currentFactura = model.getCurrent();
                    boolean productoExistente = false;


                    for (int i = 0; i < currentFactura.getLineas().size(); i++) {
                        Linea linea = currentFactura.getLineas().get(i);
                        if (linea.getProducto().getId().equals(productId)) {
                            int nuevaCantidad = linea.getCantidad() + 1;


                            if (producto.getExistencias() >= nuevaCantidad) {
                                linea.setCantidad(nuevaCantidad);
                                ((TableModel) view.getListLineas().getModel()).updateLinea(i);
                                productoExistente = true;
                                break;
                            } else {
                                JOptionPane.showMessageDialog(view.getPanel(), "No hay suficientes existencias para el producto " + producto.getNombre(), "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }


                    if (!productoExistente) {
                        int numeroLinea = currentFactura.getLineas().size() + 1;
                        String numeroLineaa= currentFactura.getNumero()+"-"+numeroLinea;
                        if (producto.getExistencias() >= 1) {
                            Linea nuevaLinea = new Linea(numeroLineaa, producto, currentFactura, 1, 0);
                            currentFactura.getLineas().add(nuevaLinea);
                            ((TableModel) view.getListLineas().getModel()).addLinea(nuevaLinea);
                        } else {
                            JOptionPane.showMessageDialog(view.getPanel(), "No hay suficientes existencias para el producto " + producto.getNombre(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(view.getPanel(), "Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view.getPanel(), "Error al buscar el producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(view.getPanel(), "El ID del producto no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //9
    public List<Producto> buscarProductos(String buscar) {
        List<Producto> productos = Service.instance().getAllProductos();
        List<Producto> resultado = new ArrayList<>();
        for (Producto producto : productos) {
            if (producto.getNombre().toLowerCase().contains(buscar.toLowerCase())) {
                resultado.add(producto);
            }
        }
        return resultado;
    }
    //10
    public void addLineaToTable(Linea linea) {
        TableModel model = (TableModel) view.getListLineas().getModel();
        model.addLinea(linea);
    }
    //11
    public void searchProducto() {
        String productId = view.getSearchProductoText();
        if (productId != null && !productId.trim().isEmpty()) {
            try {
                buscarProductoPorId(productId);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view.getPanel(), "Error al buscar el producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(view.getPanel(), "Por favor, ingrese un ID de producto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //12
    public void actualizarExistenciasFactura(Factura factura) throws Exception {
        for (Linea linea : factura.getLineas()) {
            Producto producto = linea.getProducto();
            int cantidadActual = producto.getExistencias();
            int cantidadVendida = linea.getCantidad();

            if (cantidadActual >= cantidadVendida) {
                producto.setExistencias(cantidadActual - cantidadVendida);

                Service.instance().updateProducto(producto);
            } else {
                throw new Exception("No hay suficientes existencias para el producto " + producto.getNombre());
            }
        }
    }
    //13
    public void actualizarTotales() {

        view.getArticulos().setText(String.valueOf(model.getCurrent().getCantidadTotal()));
        view.getSubTotal().setText(String.valueOf(model.getCurrent().precioNetoPagarT()));
        view.getDescuentos().setText(String.valueOf(model.getCurrent().ahorroXDescuentoT()));
        view.getTotal().setText(String.valueOf(model.getCurrent().precioTotalPagar()));

    }

}
