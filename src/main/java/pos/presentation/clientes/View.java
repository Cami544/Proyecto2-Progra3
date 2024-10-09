package pos.presentation.clientes;

import pos.Application;
import pos.logic.Cliente;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class View implements PropertyChangeListener {
    private JPanel panel;
    private JTextField BuscarNombreTxtField;
    private JButton BuscarButton;
    private JButton save;
    private JTable list;
    private JButton delete;
    private JButton report;
    private JTextField idTxtField;
    private JTextField nombreTxtField;
    private JTextField emailTxtField;
    private JLabel idLbl;
    private JLabel nombreLbl;
    private JLabel emailLbl;
    private JButton clear;
    private JLabel telefonoLbl;
    private JTextField telefonoTxtField;
    private JLabel descuentoLbl;
    private JTextField descuentoTxtField;
    private JLabel buscarNombreLbl;


    public JPanel getPanel() {
        return panel;
    }

    public View() {
        BuscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Cliente filter = new Cliente();
                    filter.setNombre(BuscarNombreTxtField.getText());
                    controller.search(filter);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, ex.getMessage(), "Información", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validate()) {
                    Cliente n = take();
                    try {
                        controller.save(n);
                        JOptionPane.showMessageDialog(panel, "REGISTRO APLICADO", "", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(panel, "REGISTRO BORRADO", "", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.clear();
            }
        });
        report.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.print();
                    if(Desktop.isDesktopSupported()) {
                        File myyFile = new File("clientes.pdf");
                        Desktop.getDesktop().open(myyFile);
                    }
                } catch (Exception ex) {
                }
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

        if (telefonoTxtField.getText().isEmpty()) {
            valid = false;
            telefonoLbl.setBorder(Application.BORDER_ERROR);
            telefonoLbl.setToolTipText("Telefono requerido");
        } else {
            telefonoLbl.setBorder(null);
            telefonoLbl.setToolTipText(null);
        }

        if (emailTxtField.getText().isEmpty()) {
            valid = false;
            emailLbl.setBorder(Application.BORDER_ERROR);
            emailLbl.setToolTipText("Unidad requerida");
        } else {
            emailLbl.setBorder(null);
            emailLbl.setToolTipText(null);
        }

        try {
            Float.parseFloat(descuentoTxtField.getText());
            descuentoLbl.setBorder(null);
            descuentoLbl.setToolTipText(null);
        } catch (Exception e) {
            valid = false;
            descuentoLbl.setBorder(Application.BORDER_ERROR);
            descuentoLbl.setToolTipText("Descuento invalido");
        }

        return valid;
    }

    public Cliente take() {
        Cliente e = new Cliente();
        e.setId(idTxtField.getText());
        e.setNombre(nombreTxtField.getText());
        e.setTelefono(telefonoTxtField.getText());
        e.setEmail(emailTxtField.getText());
        e.setDescuento(Float.parseFloat(descuentoTxtField.getText()));
        return e;
    }

// MVC
    Model model;
    Controller controller;

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Model.LIST:
                int[] cols = {TableModel.ID, TableModel.NOMBRE, TableModel.TELEFONO, TableModel.EMAIL, TableModel.DESCUENTO};
                list.setModel(new TableModel(cols, model.getList()));
                list.setRowHeight(30);
                TableColumnModel columnModel = list.getColumnModel();
                columnModel.getColumn(1).setPreferredWidth(150);
                columnModel.getColumn(3).setPreferredWidth(150);
                break;
            case Model.CURRENT:
                idTxtField.setText(model.getCurrent().getId());
                nombreTxtField.setText(model.getCurrent().getNombre());
                telefonoTxtField.setText(model.getCurrent().getTelefono());
                emailTxtField.setText(model.getCurrent().getEmail());
                descuentoTxtField.setText("" + model.getCurrent().getDescuento());

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
                emailLbl.setBorder(null);
                emailLbl.setToolTipText(null);
                telefonoLbl.setBorder(null);
                telefonoLbl.setToolTipText(null);
                descuentoLbl.setBorder(null);
                descuentoLbl.setToolTipText(null);
                break;
            case Model.FILTER:
                BuscarNombreTxtField.setText(model.getFilter().getNombre());
                break;
        }

        this.panel.revalidate();
    }

}
