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
        Linea lineaGuardada;  // Variable para guardar la línea creada o actualizada
        switch (model.getMode()) {
            case Application.MODE_CREATE:
                // Capturar la línea devuelta por el servicio con el número asignado
                lineaGuardada = Service.instance().create(e);
                break;
            case Application.MODE_EDIT:
                Service.instance().update(e);
                lineaGuardada = e;  // En el modo de edición, no se requiere recapturar la línea
                break;
            default:
                throw new IllegalStateException("Modo no reconocido: " + model.getMode());
        }
        List<Linea> lineas = model.getCurrent().getLineas();
        if (model.getMode() == Application.MODE_CREATE) {
            lineas.add(lineaGuardada);  // Agregar la línea recién creada a la lista
            ((TableModel) view.getListLineas().getModel()).addLinea(lineaGuardada);
        } else {
            // Si es una actualización, deberías asegurarte de reflejar los cambios en la vista
            int index = lineas.indexOf(e);
            lineas.set(index, lineaGuardada);  // Actualizar la línea existente
            ((TableModel) view.getListLineas().getModel()).updateLinea(index);
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
        // Validación de que el ID no sea nulo o vacío
        if (id == null || id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view.getPanel(),
                    "El campo de ID del producto es obligatorio.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Producto producto = Service.instance().buscarProductoPorId(id);
            if (producto == null) {
                JOptionPane.showMessageDialog(view.getPanel(), "Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Factura facturaActual = model.getCurrent();
            List<Linea> lineas = facturaActual.getLineas();  // Mejor rendimiento
            boolean existeProd = false;

            for (int i = 0; i < lineas.size(); i++) {
                Linea linea = lineas.get(i);
                if (linea.getProducto().getId().equals(id)) {
                    int cantNueva = linea.getCantidad() + 1;

                    if (producto.getExistencias() >= cantNueva) {
                        linea.setCantidad(cantNueva);
                        ((TableModel) view.getListLineas().getModel()).updateLinea(i);
                        // Disminuir existencias en la base de datos
                        actualizarExistenciasProducto(producto, 1);  // Disminuir en 1
                        existeProd = true;
                    } else {
                        JOptionPane.showMessageDialog(view.getPanel(),
                                "No contamos con suficientes unidades de este producto: " + producto.getNombre(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                }
            }

            if (!existeProd) {
                if (producto.getExistencias() >= 1) {
                    int numeroLinea = lineas.size() + 1;  // Solo el número de línea
                    Linea nuevaLinea = new Linea(numeroLinea, producto, facturaActual, 1, 0);  // El número de línea es un entero
                    lineas.add(nuevaLinea);
                    ((TableModel) view.getListLineas().getModel()).addLinea(nuevaLinea);
                    // Disminuir existencias en la base de datos
                    actualizarExistenciasProducto(producto, 1);
                } else {
                    JOptionPane.showMessageDialog(view.getPanel(),
                            "No contamos con suficientes unidades de este producto: " + producto.getNombre(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(view.getPanel(), "Error al buscar el producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void actualizarExistenciasProducto(Producto producto, int cantidadVendida) throws Exception {
        int existenciasActuales = producto.getExistencias();
        if (existenciasActuales >= cantidadVendida) {
            producto.setExistencias(existenciasActuales - cantidadVendida);

            // Actualizar el producto en la base de datos
            Service.instance().update(producto);
        } else {
            throw new Exception("No hay suficientes unidades disponibles para este producto.");
        }
    }


    public void actualizarComboBox() {
        try {
            // Obtener las listas desde la base de datos mediante el servicio


            List<Cajero> cajeros = Service.instance().obtenerTodosCajeros();
            List<Cliente> clientes = Service.instance().obtenerTodosClientes();

            if (cajeros != null && clientes != null) {
                // Actualizar el modelo con los datos obtenidos
                model.setListCajeros(cajeros);
                model.setListClientes(clientes);
                // Actualizar los ComboBox en la vista
                view.updateClientesComboBox(clientes);
                view.updateCajerosComboBox(cajeros);

                System.out.println("ComboBox actualizados correctamente.");
            } else {
                System.out.println("Error: Los cajeros o clientes son nulos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al actualizar los datos de los ComboBox: " + e.getMessage());
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
