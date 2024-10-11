package pos.presentation.facturas;

import pos.Application;
import pos.logic.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    View view;
    Model model;

    public Controller(View view, Model model) throws Exception {
        this.view = view;
        this.model = model;

        view.setController(this);
        view.setModel(model);

        List<Factura> facturas = Service.instance().obtenerTodasFacturas();
        List<Cliente> clientes = Service.instance().obtenerTodosClientes();
        List<Producto> productos = Service.instance().obtenerTodosProductos();
        List<Cajero> cajeros = Service.instance().obtenerTodosCajeros();

        model.init(facturas, clientes, productos, cajeros);

        view.updateClientesComboBox(clientes);
        view.updateCajerosComboBox(cajeros);
    }

    public void search(Factura filter) throws Exception {
        model.setFilter(filter);
        model.setMode(Application.MODE_CREATE);
        model.setCurrent(new Factura());
        model.setList(Service.instance().search(model.getFilter()));
    }

    public void saveFactura(Factura e) throws Exception {
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

    public void saveLinea(Linea e) throws Exception {
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

    public void delete() throws Exception {
        Service.instance().delete(model.getCurrent());
        search(model.getFilter());
    }

    public void clear() {
        model.getCurrent().getLineas().clear();
        model.updateModel();
    }

    public void buscarProductoPorCodigo(String id) throws Exception {
        if (id != null && !id.trim().isEmpty()) {
            try {
                Producto producto = Service.instance().buscarProductoPorId(id);
                if (producto != null) {
                    Factura facturaActual = model.getCurrent();
                    boolean existeProd = false;


                    for (int i = 0; i < facturaActual.getLineas().size(); i++) {
                        Linea linea = facturaActual.getLineas().get(i);
                        if (linea.getProducto().getId().equals(id)) {
                            int cantnueva = linea.getCantidad() + 1;

                            if (producto.getExistencias() >= cantnueva) {
                                linea.setCantidad(cantnueva);
                                ((TableModel) view.getListLineas().getModel()).updateLinea(i);
                                existeProd = true;
                                break;
                            } else {
                                JOptionPane.showMessageDialog(view.getPanel(), "No contamos con suficientes unidades de este producto: " + producto.getNombre(), "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }

                    if (!existeProd) {
                        int numeroLinea = facturaActual.getLineas().size() + 1;
                        String numeroLineaa= facturaActual.getNumero()+"-"+numeroLinea;
                        if (producto.getExistencias() >= 1) {
                            Linea nuevaLinea = new Linea(Integer.parseInt(numeroLineaa), producto, facturaActual, 1, 0);
                            facturaActual.getLineas().add(nuevaLinea);
                            ((TableModel) view.getListLineas().getModel()).addLinea(nuevaLinea);
                        } else {
                            JOptionPane.showMessageDialog(view.getPanel(), "No contamos con suficientes unidades de este producto: " + producto.getNombre(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(view.getPanel(), "Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view.getPanel(), "Error al buscar el producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(view.getPanel(), "El campo de ID del producto es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Producto> buscaProductoConNombre(String nombre) throws Exception {
        List<Producto> productos = Service.instance().obtenerTodosProductos();
        List<Producto> resultado = new ArrayList<>();
        for (Producto producto : productos) {
            if (producto.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                resultado.add(producto);
            }
        }
        return resultado;
    }

    public void addLineaToTable(Linea linea) {
        TableModel model = (TableModel) view.getListLineas().getModel();
        model.addLinea(linea);
    }

    public void searchProducto() {
        String productId = view.getSearchProductoText();
        if (productId != null && !productId.trim().isEmpty()) {
            try {
                buscarProductoPorCodigo(productId);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view.getPanel(), "Error al buscar el producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(view.getPanel(), "Por favor, ingrese un ID de producto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarExistenciasFactura(Factura factura) throws Exception {
        for (Linea linea : factura.getLineas()) {
            Producto producto = linea.getProducto();
            int cantidadActual = producto.getExistencias();
            int cantidadVendida = linea.getCantidad();

            if (cantidadActual >= cantidadVendida) {
                producto.setExistencias(cantidadActual - cantidadVendida);

                Service.instance().update(producto);
            } else {
                throw new Exception("No contamos con suficientes unidades de este producto: " + producto.getNombre());
            }
        }
    }

    public void updateTotales() {
        view.getArticulos().setText(String.valueOf(model.getCurrent().getCantidadTotal()));
        view.getSubTotal().setText(String.valueOf(model.getCurrent().precioTotalNetoAPagar()));
        view.getDescuentos().setText(String.valueOf(model.getCurrent().totalAhorradoPorDescuento()));
        view.getTotal().setText(String.valueOf(model.getCurrent().precioTotalAPagar()));
    }

}
