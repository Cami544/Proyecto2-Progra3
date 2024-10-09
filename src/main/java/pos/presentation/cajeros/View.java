package pos.presentation.cajeros;

import pos.Application;
import pos.logic.Cajero;

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
    private JLabel idLbl;
    private JLabel nombreLbl;
    private JButton clear;
    private JLabel BuscarNombreLbl;

    public JPanel getPanel() {
        return panel;
    }

    public View() {

        BuscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Cajero filter = new Cajero();
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
                    Cajero n = take();
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
                        File myFile = new File("cajeros.pdf");
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


        return valid;
    }

    public Cajero take() {
        Cajero e = new Cajero();
        e.setId(idTxtField.getText());
        e.setNombre(nombreTxtField.getText());
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
                int[] cols = {TableModel.ID, TableModel.NOMBRE };
                list.setModel(new TableModel(cols, model.getList()));
                list.setRowHeight(30);
                TableColumnModel columnModel = list.getColumnModel();
                columnModel.getColumn(1).setPreferredWidth(150);
                columnModel.getColumn(1).setPreferredWidth(150);
                break;
            case Model.CURRENT:
                idTxtField.setText(model.getCurrent().getId());
                nombreTxtField.setText(model.getCurrent().getNombre());
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

                break;
            case Model.FILTER:
                BuscarNombreTxtField.setText(model.getFilter().getNombre());
                break;
        }

        this.panel.revalidate();
    }

}
