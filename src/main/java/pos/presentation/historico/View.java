package pos.presentation.historico;
import pos.logic.Cliente;
import pos.logic.Factura;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class View implements PropertyChangeListener {

        private JLabel BuscarNombreLbl;
        private JTextField BuscarNombreTxtField;
        private JButton report;
        private JButton search;
        JTable listFacturas;
        private JPanel panel;
        JTable listLineas;

    Model model;
    Controller controller;

    public JPanel getPanel() {

            return panel;
        }

        public View() {
            addTableSelectionListeners();
            addSearchButtonListener();

            search.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Cliente filter = new Cliente();
                        filter.setNombre(BuscarNombreTxtField.getText());
                        controller.searchByClienteNombre(filter.getNombre());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, ex.getMessage(), "Información", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });

            report.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        controller.print();
                        if(Desktop.isDesktopSupported()) {
                            File myFile = new File("historico.pdf");
                            Desktop.getDesktop().open(myFile);
                        }
                    }catch (Exception ex) {}
                }
            });
        }
    private void addSearchButtonListener() {
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String clienteNombre = BuscarNombreTxtField.getText().trim();
                try {
                    controller.searchByClienteNombre(clienteNombre);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    private void addTableSelectionListeners() {
        listFacturas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = listFacturas.getSelectedRow();
                    if (selectedRow >= 0) {
                        Factura selectedFactura = model.getList().get(selectedRow);
                       controller.updateLineasTable(selectedFactura);
                    }
                }
            }
        });
    }


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
                controller.updateFacturasTable();
                break;
            case Model.FILTER:
                if(model.getFilter().getCliente() != null) {
                    BuscarNombreTxtField.setText(model.getFilter().getCliente().getNombre());
                }
                break;
        }
    }

}
