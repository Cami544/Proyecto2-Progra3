package pos.presentation.facturas;

import pos.logic.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;

public class View implements PropertyChangeListener {
    private JLabel BuscarNombreLbl;
    private JTextField buscarProductoTxtField;
    private JButton search;
    private JTable listLineas;
    private JComboBox<Cliente> clientes;
    private JComboBox<Cajero> cajeros;
    private JButton cobrarButton;
    private JButton cancelarButton;
    private JButton buscarButton;
    private JButton cantidadButton;
    private JButton quitarButton;
    private JButton descuentoButton;
    private JLabel articulosLbl ;
    private JLabel subtotalLbl;
    private JLabel descuentoaLbl ;
    private JLabel totalLbl ;
    private JPanel panel;
    private JPanel totalesPanel;
    private JLabel articulos;
    private JLabel SubTotal;
    private JLabel Descuentos;
    private JLabel total;

    private JTable table;



    Model model;
    Controller controller;

    public JPanel getPanel() {
        controller.actualizarTotales();
        return panel;
    }

    public JTable getListLineas() {
        return listLineas;
    }

    public String getSearchProductoText() {
        return buscarProductoTxtField.getText();
    }

    public JComboBox<Cliente> getClientes() {
        return clientes;
    }

    public View() {

        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String productId = buscarProductoTxtField.getText();
                if (productId != null && !productId.trim().isEmpty()) {
                    try {
                        controller.buscarProductoPorId(productId);
                        controller.actualizarTotales();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "Por favor, ingrese un ID de producto.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        cantidadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = listLineas.getSelectedRow();

                if (selectedRow != -1) {
                    String input = JOptionPane.showInputDialog(panel, "CANTIDAD: ");
                    if (input != null && !input.trim().isEmpty()) {
                        try {
                            int nuevaCantidad = Integer.parseInt(input);

                            if (nuevaCantidad > 0) {
                                TableModel model = (TableModel) listLineas.getModel();
                                Linea lineaSeleccionada = model.getLineaAt(selectedRow);
                                Producto producto = lineaSeleccionada.getProducto();

                                if (producto.getExistencias() >= nuevaCantidad) {
                                    lineaSeleccionada.setCantidad(nuevaCantidad);
                                    model.fireTableRowsUpdated(selectedRow, selectedRow);
                                } else {
                                    JOptionPane.showMessageDialog(panel, "No hay suficientes existencias para el producto " + producto.getNombre(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(panel, "La cantidad debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(panel, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "Seleccione una línea en la lista para modificar la cantidad.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                controller.actualizarTotales();
            }
        });

        cobrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CobroDialog dialog = new CobroDialog((Frame) SwingUtilities.getWindowAncestor(panel));
                dialog.setVisible(true);
            }
        });

        descuentoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = listLineas.getSelectedRow();

                if (selectedRow != -1) {
                    String input = JOptionPane.showInputDialog(panel, "Descuento: ");
                    if (input != null && !input.trim().isEmpty()) {
                        try {
                            float nuevaDescuento = Integer.parseInt(input);

                            if (nuevaDescuento > 0) {

                                TableModel model = (TableModel) listLineas.getModel();
                                Linea lineaSeleccionada = model.getLineaAt(selectedRow);
                                lineaSeleccionada.setDescuento(nuevaDescuento);

                                model.fireTableRowsUpdated(selectedRow, selectedRow);
                            } else {
                                JOptionPane.showMessageDialog(panel, "La cantidad debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(panel, "Por favor, ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "Seleccione una línea en la lista para modificar la cantidad.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                controller.actualizarTotales();
            }
        });

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Producto> productos = controller.buscarProductos("");
                SearchDialog dialog = new SearchDialog((Frame) SwingUtilities.getWindowAncestor(panel), productos);
                dialog.setVisible(true);

            }
        });

        clientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Cliente selectedCliente = (Cliente) clientes.getSelectedItem();
            }
        });

        cajeros.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Cajero selectedCajero = (Cajero) cajeros.getSelectedItem();
            }
        });

        int[] cols = {TableModel.NUMERO, TableModel.ARTICULO,TableModel.CATEGORIA,TableModel.CANTIDAD, TableModel.PRECIO,TableModel.DESCUENTO,TableModel.NETO,TableModel.IMPORTE};

        listLineas.setModel(new pos.presentation.facturas.TableModel(cols, new ArrayList<>()));

        quitarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = listLineas.getSelectedRow();
                if (selectedRow != -1) {
                    TableModel tableModel = (TableModel) listLineas.getModel();
                    Linea lineaSeleccionada = tableModel.getLineaAt(selectedRow);
                    model.getCurrent().getLineas().remove(lineaSeleccionada);
                    tableModel.removeLinea(selectedRow);
                    tableModel.fireTableRowsDeleted(selectedRow, selectedRow);
                    controller.actualizarTotales();
                } else {
                    JOptionPane.showMessageDialog(panel, "Seleccione una línea para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelar();
            }
        });


    }

    public void setController(Controller controller)
    {
        this.controller = controller;
    }

    private void cobrar() {
        Factura factura = take();
        try {

            controller.save(factura);

            controller.actualizarExistenciasFactura(factura);
            cancelar();
            JOptionPane.showMessageDialog(panel, "Factura guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "Error al guardar la factura: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelar() {
        try {
            controller.clear();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Ocurrió un error al cancelar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateClientesComboBox(List<Cliente> clientesList) {
        clientes.removeAllItems();
        if (clientesList == null || clientesList.isEmpty()) {
            return;
        }
        for (Cliente cliente : clientesList) {
            clientes.addItem(cliente);
        }
        this.panel.revalidate();
        this.panel.repaint();
    }

    public void updateCajerosComboBox(List<Cajero> cajerosList) {
        cajeros.removeAllItems();
        if (cajerosList == null || cajerosList.isEmpty()) {
            return;
        }
        for (Cajero cajero : cajerosList) {
            cajeros.addItem(cajero);
        }
        this.panel.revalidate();
        this.panel.repaint();
    }


    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        int[] cols = {TableModel.NUMERO, TableModel.ARTICULO, TableModel.CATEGORIA, TableModel.CANTIDAD, TableModel.PRECIO, TableModel.DESCUENTO, TableModel.NETO, TableModel.IMPORTE};
        switch (evt.getPropertyName()) {
            case pos.presentation.clientes.Model.LIST:
                listLineas.setModel(new pos.presentation.facturas.TableModel(cols, new ArrayList<>()));
                listLineas.setRowHeight(30);
                TableColumnModel columnModel = listLineas.getColumnModel();
                columnModel.getColumn(1).setPreferredWidth(150);
                columnModel.getColumn(5).setPreferredWidth(150);
                controller.actualizarTotales();
                break;
            case Model.CURRENT:
                controller.actualizarTotales();
                break;
            case Model.FILTER:
                List<Linea> lineas = model.getFilter().getLineas();
                if (!lineas.isEmpty()) {
                    buscarProductoTxtField.setText(lineas.getFirst().getProducto().getNombre());
                } else {
                    buscarProductoTxtField.setText("");
                }
                break;
        }
    }

    public JLabel getArticulos() {
        return articulos;
    }

    public void setArticulos(JLabel articulos) {
        this.articulos = articulos;
    }

    public JLabel getSubTotal() {
        return SubTotal;
    }

    public void setSubTotal(JLabel subTotal) {
        SubTotal = subTotal;
    }

    public JLabel getDescuentos() {
        return Descuentos;
    }

    public void setDescuentos(JLabel descuentos) {
        Descuentos = descuentos;
    }

    public JLabel getTotal() {
        return total;
    }

    public void setTotal(JLabel total) {
        this.total = total;
    }

    public Factura take() {
        Factura factura = new Factura();

        factura.setNumero(generateFacturaNumber());

        Cliente clienteSeleccionado = (Cliente) clientes.getSelectedItem();

        Cajero cajeroSeleccionado = (Cajero) cajeros.getSelectedItem();

        factura.setCajero(cajeroSeleccionado);

        factura.setCliente(clienteSeleccionado);

        factura.setFecha(LocalDate.now());

        TableModel tableModel = (TableModel) listLineas.getModel();
        List<Linea> lineas = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Linea linea = tableModel.getLineaAt(i);
            lineas.add(linea);
            try {
                controller.save(linea);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        factura.setLineas(lineas);
        return factura;
    }

    private String generateFacturaNumber() {
        String facturaNumber = "FAC-";
        return (facturaNumber+((int) (Math.random() * 100000)));
    }




    public class SearchDialog extends JDialog {
        private JLabel nombre;
        private JTextField searchField;
        private JTable resultTable;
        private DefaultTableModel tableModel;
        private List<Producto> productos;
        private List<Producto> productosFiltrados;
        private Producto selectedProducto;
        private JButton okButton;
        private JButton cancelButton;

        public SearchDialog(Frame parent, List<Producto> productos) {
            super(parent, "Buscar Producto", true);
            this.productos = productos;

            setLayout(new BorderLayout());
            controller.actualizarTotales();
            JPanel searchPanel = new JPanel();
            searchPanel.setLayout(new FlowLayout());
            nombre = new JLabel("Nombre: ");
            searchField = new JTextField(20);
            searchPanel.add(nombre);
            searchPanel.add(searchField);

            tableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Categoría", "Precio"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            resultTable = new JTable(tableModel);
            JScrollPane tableScrollPane = new JScrollPane(resultTable);

            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            tablePanel.add(tableScrollPane, BorderLayout.CENTER);

            add(searchPanel, BorderLayout.NORTH);
            add(tablePanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            okButton = new JButton("OK");
            cancelButton = new JButton("Cancelar");
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            add(buttonPanel, BorderLayout.SOUTH);

            setResizable(false);
            loadAllProductos();

            searchField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    search();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    search();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    search();
                }
            });

            resultTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int selectedRow = resultTable.getSelectedRow();
                        if (selectedRow != -1) {
                            selectedProducto = productosFiltrados.get(selectedRow);
                            try {
                                controller.buscarProductoPorId(selectedProducto.getId());
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                            controller.actualizarTotales();
                            dispose();
                        }
                    }
                }
            });

            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = resultTable.getSelectedRow();
                    if (selectedRow != -1) {
                        selectedProducto = productosFiltrados.get(selectedRow);
                        try {
                            controller.buscarProductoPorId(selectedProducto.getId());
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        controller.actualizarTotales();
                        dispose();
                    }
                }
            });

            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedProducto = null;
                    dispose();
                }
            });

            setSize(500, 300);
            setLocationRelativeTo(parent);
        }

        private void loadAllProductos() {
            tableModel.setRowCount(0);
            productosFiltrados = new ArrayList<>(productos);

            for (Producto producto : productos) {
                tableModel.addRow(new Object[]{
                        producto.getId(),
                        producto.getNombre(),
                        producto.getCategoria(),
                        producto.getPrecio()
                });
            }
        }

        private void search() {
            String query = searchField.getText().toLowerCase();
            tableModel.setRowCount(0);
            productosFiltrados = new ArrayList<>();

            for (Producto producto : productos) {
                if (producto.getNombre().toLowerCase().contains(query)) {
                    productosFiltrados.add(producto);
                    tableModel.addRow(new Object[]{
                            producto.getId(),
                            producto.getNombre(),
                            producto.getCategoria(),
                            producto.getPrecio()
                    });
                }
            }
        }

        public Producto getSelectedProducto() {
            return selectedProducto;
        }
    }



    public class CobroDialog extends JDialog {

        private JLabel efectivo;
        private JTextField efectivoField;
        private JLabel tarjeta;
        private JTextField tarjetaField;
        private JLabel cheque;
        private JTextField chequeField;
        private JLabel sinpe;
        private JTextField sinpeField;
        private JButton okButton;
        private JButton cancelButton;
        private JLabel totalLabel;

        public CobroDialog(Frame parent) {
            super(parent, "Pago", true);
            setLayout(new BorderLayout());
            controller.actualizarTotales();
            JPanel pagosRecibidos = new JPanel();
            pagosRecibidos.setLayout(new GridLayout(4, 2, 2, 2));
            pagosRecibidos.setBorder(BorderFactory.createTitledBorder("Pagos recibidos"));

            efectivo = new JLabel("Efectivo:");
            efectivoField = new JTextField(20);
            tarjeta = new JLabel("Tarjeta:");
            tarjetaField = new JTextField(20);
            cheque = new JLabel("Cheque:");
            chequeField = new JTextField(20);
            sinpe = new JLabel("Sinpe:");
            sinpeField = new JTextField(20);

            pagosRecibidos.add(efectivo);
            pagosRecibidos.add(efectivoField);
            pagosRecibidos.add(tarjeta);
            pagosRecibidos.add(tarjetaField);
            pagosRecibidos.add(cheque);
            pagosRecibidos.add(chequeField);
            pagosRecibidos.add(sinpe);
            pagosRecibidos.add(sinpeField);
            JPanel importePanel = new JPanel();
            importePanel.setBorder(BorderFactory.createTitledBorder("Importe a pagar"));
            totalLabel = new JLabel(String.valueOf(model.current.precioTotalPagar()));
            totalLabel.setFont(new Font("Serif", Font.BOLD, 20));
            totalLabel.setForeground(Color.BLUE);
            importePanel.add(totalLabel);
            importePanel.setLayout(new GridLayout(1, 2, 1, 1));
            JPanel buttonPanel = new JPanel();
            okButton = new JButton("OK");
            cancelButton = new JButton("Cancelar");
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            JPanel mainPanel = new JPanel(new BorderLayout(1, 1));
            mainPanel.add(pagosRecibidos, BorderLayout.CENTER);
            mainPanel.add(importePanel, BorderLayout.   CENTER);
            mainPanel.add(pagosRecibidos, BorderLayout.NORTH);
            add(mainPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);



            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        double efectivo = efectivoField.getText().isEmpty() ? 0 : Double.parseDouble(efectivoField.getText());
                        double tarjeta = tarjetaField.getText().isEmpty() ? 0 : Double.parseDouble(tarjetaField.getText());
                        double cheque = chequeField.getText().isEmpty() ? 0 : Double.parseDouble(chequeField.getText());
                        double sinpe = sinpeField.getText().isEmpty() ? 0 : Double.parseDouble(sinpeField.getText());

                        double totalPago = efectivo + tarjeta + cheque + sinpe;
                        double totalFactura = model.current.precioTotalPagar();

                        if (totalPago >= totalFactura) {

                            cobrar();
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(CobroDialog.this,
                                    "El monto total ingresado debe ser igual o mayor al total a pagar.",
                                    "Pago insuficiente",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(CobroDialog.this,
                                "Ingrese un valor numerico válido.",
                                "Error de entrada",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            setSize(500, 300);
            setLocationRelativeTo(parent);
            setResizable(false);
        }


    }

}
