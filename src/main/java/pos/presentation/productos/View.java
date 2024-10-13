package pos.presentation.productos;
import pos.logic.Categoria;
import pos.Application;
import pos.logic.Producto;
import pos.logic.Service;

import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View implements PropertyChangeListener {


    private JPanel panel1;

    public JTable getList() {
        return list;
    }

    private JLabel idLbl;
    private JTextField idTxtField;
    private JLabel nombreLbl;
    private JTextField nombreTxtField;
    private JLabel DescripcionLbl;
    private JTextField descripcionTxtField;
    private JLabel UnidadMedidaLbl;
    private JTextField UnidadMedidaTxtField;
    private JLabel existenciaLbl;
    private JTextField existenciaTxtField;
    private JButton save;
    private JButton delete;
    private JButton clear;
    private JTextField BuscarNombreTxtField;
    private JButton report;
    private JButton BuscarButton;
    private JTable list;
    private JTextField precioTxtField;
    private JComboBox categoriaComboBox;
    private JLabel precioLbl;
    private JLabel categoriaLbl;
    private JLabel BuscarNombreLbl;

    public JPanel getPanel1() {
        return panel1;
    }


    public View() {

        BuscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Producto filter = new Producto();
                    filter.setNombre(BuscarNombreTxtField.getText());
                    System.out.println("Buscando producto con nombre: " + filter.getNombre());
                    controller.search(filter);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel1, ex.getMessage(), "Información", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validate()) {
                    Producto n = take();
                    try {
                        controller.save(n);
                        JOptionPane.showMessageDialog(panel1, "REGISTRO APLICADO", "", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel1, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = list.getSelectedRow();
                controller.edit(row);
            }
        });

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.delete();
                    JOptionPane.showMessageDialog(panel1, "REGISTRO BORRADO", "", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel1, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.clear();
            }
        });

        categoriaComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Categoria selectedCategoria = (Categoria) categoriaComboBox.getSelectedItem();
            }
        });


        report.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.print();
                    if(Desktop.isDesktopSupported()) {
                        File myFile = new File("productos.pdf");
                        Desktop.getDesktop().open(myFile);
                    }
                }catch (Exception ex) {}
            }
        });
    }

    private boolean validate() {
        boolean valid = true;
        if (idTxtField.getText().isEmpty()) {
            valid = false;
            idLbl.setBorder(Application.BORDER_ERROR);
            idLbl.setToolTipText("Codigo requerido");
        } else {
            idLbl.setBorder(null);
            idLbl.setToolTipText(null);
        }

        if (nombreTxtField.getText().isEmpty()) {
            valid = false;
            nombreLbl.setBorder(Application.BORDER_ERROR);
            nombreLbl.setToolTipText("Nombre requerido");
        } else {
            nombreLbl.setBorder(null);
            nombreLbl.setToolTipText(null);
        }

        if (descripcionTxtField.getText().isEmpty()) {
            valid = false;
            DescripcionLbl.setBorder(Application.BORDER_ERROR);
            DescripcionLbl.setToolTipText("Telefono requerido");
        } else {
            DescripcionLbl.setBorder(null);
            DescripcionLbl.setToolTipText(null);
        }

        if (UnidadMedidaTxtField.getText().isEmpty()) {
            valid = false;
            UnidadMedidaLbl.setBorder(Application.BORDER_ERROR);
            UnidadMedidaLbl.setToolTipText("Unidad requerida");
        } else {
            UnidadMedidaLbl.setBorder(null);
            UnidadMedidaLbl.setToolTipText(null);
        }
        if (existenciaTxtField.getText().isEmpty()) {
            valid = false;
            existenciaLbl.setBorder(Application.BORDER_ERROR);
            existenciaLbl.setToolTipText("Unidad requerida");
        } else {
            existenciaLbl.setBorder(null);
            existenciaLbl.setToolTipText(null);
        }
        if (precioTxtField.getText().isEmpty()) {
            valid = false;
            precioLbl.setBorder(Application.BORDER_ERROR);
            precioLbl.setToolTipText("Unidad requerida");
        } else {
            precioLbl.setBorder(null);
            precioLbl.setToolTipText(null);
        }

        return valid;
    }

    public Producto take() {
        Producto e = new Producto();
        e.setId(idTxtField.getText());
        e.setNombre(nombreTxtField.getText());
        e.setDescripcion(descripcionTxtField.getText());
        e.setUnidadMedida(UnidadMedidaTxtField.getText());
        e.setExistencias(Integer.parseInt(existenciaTxtField.getText()));
        e.setPrecio(Double.parseDouble(precioTxtField.getText()));
        Categoria selectedCategoria = (Categoria) categoriaComboBox.getSelectedItem();
        e.setCategoria(selectedCategoria);
        return e;
    }

    Model model;
    Controller controller;

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }
    public void setController(Controller controller) {
        this.controller = controller;
        fillCategoriaComboBox();
    }

    private void fillCategoriaComboBox() {
        categoriaComboBox.removeAllItems();

        List<Categoria> categorias = null;
        try {
            categorias = Service.instance().obtenerTodasCategorias();
        } catch (Exception e) {
            System.out.println("Error al cargar categorías: " + e.getMessage());
            return;
        }

        if (categorias == null || categorias.isEmpty()) {
            System.out.println("No hay categorías disponibles");
            return;
        }

        for (Categoria cat : categorias) {
            categoriaComboBox.addItem(cat);
        }


        this.panel1.revalidate();
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Model.LIST:
                int[] cols = {TableModel.ID, TableModel.NOMBRE, TableModel.DESCRIPCION, TableModel.UNIDADMEDIDA, TableModel.PRECIO, TableModel.EXISTENCIA, TableModel.CATEGORIA};
                list.setModel(new TableModel(cols, model.getList()));
                list.setRowHeight(30);
                TableColumnModel columnModel = list.getColumnModel();
                columnModel.getColumn(1).setPreferredWidth(100);
                columnModel.getColumn(2).setPreferredWidth(130);
                columnModel.getColumn(3).setPreferredWidth(110);
                columnModel.getColumn(6).setPreferredWidth(110);
                break;
            case Model.CURRENT:
                idTxtField.setText(model.getCurrent().getId());
                nombreTxtField.setText(model.getCurrent().getNombre());
                descripcionTxtField.setText(model.getCurrent().getDescripcion());
                UnidadMedidaTxtField.setText(model.getCurrent().getUnidadMedida());
                existenciaTxtField.setText("" + model.getCurrent().getExistencias());
                precioTxtField.setText("" + model.getCurrent().getPrecio());
                categoriaComboBox.setSelectedItem(model.getCurrent().getCategoria());

                if (model.getMode() == Application.MODE_EDIT) {
                    idTxtField.setEnabled(false);
                    delete.setEnabled(true);
                } else {
                    idTxtField.setEnabled(true);
                    delete.setEnabled(false);
                }

                idLbl.setBorder(null);
                idLbl.setToolTipText(null);
                nombreLbl.setBorder(null);
                nombreLbl.setToolTipText(null);
                DescripcionLbl.setBorder(null);
                DescripcionLbl.setToolTipText(null);
                UnidadMedidaLbl.setBorder(null);
                UnidadMedidaLbl.setToolTipText(null);
                existenciaLbl.setBorder(null);
                existenciaLbl.setToolTipText(null);
                precioLbl.setBorder(null);
                precioLbl.setToolTipText(null);
                categoriaLbl.setBorder(null);
                categoriaLbl.setToolTipText(null);
                break;
            case Model.FILTER:
                BuscarNombreTxtField.setText(model.getFilter().getNombre());
                break;
        }

        this.panel1.revalidate();
    }

}

