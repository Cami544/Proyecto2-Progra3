package pos.presentation.estadistica;

import org.jfree.chart.plot.PlotOrientation;
        import pos.logic.Categoria;
import javax.swing.*;
import javax.swing.border.Border;
        import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
        import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;


public class View implements PropertyChangeListener {

    private JComboBox<Integer> AnioHasta;
    private JComboBox<Integer> AnioDesde;
    private JComboBox<String> mesDesde;
    private JComboBox<String> mesHasta;
    JComboBox<Categoria> categoriaComboBox;
    private JTable list;
    private JButton Check1;
    private JButton CheckTodo;
    private JButton borrar1;
    private JButton borrarTodo;
    private JPanel panel;
    private JPanel graficaPanel;
    private JPanel panelData;


    private Model model;
    private Controller controller;

    public JPanel getPanel() {
        return panel;
    }

    public View() {

        CheckTodo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.agregarTodasLasCategorias();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

    });

        borrar1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                {
                    if (list.getSelectedRow() != -1) {
                        try {
                            controller.quitarCategoria(list.getSelectedRow());
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    } else {
                        JOptionPane.showMessageDialog(panel, "Seleccione una categoria", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        borrarTodo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.clear();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        ActionListener comboBoxActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validar()) {
                    try {
                        controller.actualizarRangos(
                                Integer.parseInt((String) AnioDesde.getSelectedItem()),
                                mesDesde.getSelectedIndex() + 1,
                                Integer.parseInt((String) AnioHasta.getSelectedItem()),
                                mesHasta.getSelectedIndex() + 1

                        );
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                }
            }
        };

        mesDesde.addActionListener(comboBoxActionListener);
        mesHasta.addActionListener(comboBoxActionListener);
        AnioDesde.addActionListener(comboBoxActionListener);
        AnioHasta.addActionListener(comboBoxActionListener);

        Check1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    Categoria c = (Categoria) categoriaComboBox.getSelectedItem();
                try {
                    controller.agregarCategoria(c);
                } catch (Exception ex) {

                }

            }
        });
    }

    private boolean validar() {
        int anioDesde = Integer.parseInt((String) AnioDesde.getSelectedItem());
        int anioHasta = Integer.parseInt((String) AnioHasta.getSelectedItem());
        int mesInicio = mesDesde.getSelectedIndex() + 1;
        int mesFin = mesHasta.getSelectedIndex() + 1;

        resetComboBoxBorders();

        if (anioDesde > anioHasta) {
            setErrorBorder(AnioDesde, AnioHasta);
            return false;
        } else if (anioDesde == anioHasta && mesInicio > mesFin) {
            setErrorBorder(mesDesde, mesHasta);
            return false;
        }

        return true;
    }

    private void setErrorBorder(JComboBox<?>... comboBoxes) {
        Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);
        for (JComboBox<?> comboBox : comboBoxes) {
            comboBox.setBorder(redBorder);
        }
    }

    private void resetComboBoxBorders() {
        Border defaultBorder = BorderFactory.createEmptyBorder();
        AnioDesde.setBorder(defaultBorder);
        AnioHasta.setBorder(defaultBorder);
        mesDesde.setBorder(defaultBorder);
        mesHasta.setBorder(defaultBorder);
    }

    private Categoria obtenerCategoriaSeleccionada() {
        return (Categoria) categoriaComboBox.getSelectedItem();
    }

    public void setController(Controller controller) {
        this.controller = controller;

    }

    public JTable getTable() {
        return list;

    }

    public JPanel getGraficoPanel(){
        return graficaPanel;
    }

    public void setModel(Model model) {
        this.model = model;
        this.model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Model.CATEGORIES_ALL:

                for(Categoria categoria : model.getCategoriasAll()) {
                    categoriaComboBox.addItem(categoria);
                }
                actualizarVista();
                break;
            case Model.DATA:
                List<String> listRows = model.getRows();
                String[] rows = listRows.toArray(new String[0]);
                list.setModel(new EstadisticaTableModel(rows, model.getCols(),model.getData()));
                list.setRowHeight(30);
                TableColumnModel columnModel = list.getColumnModel();

                if (model.getCols().length > 2) {
                    list.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                    for (int i = 0; i < columnModel.getColumnCount(); i++) {
                        if (i == 0) {
                            columnModel.getColumn(i).setPreferredWidth(150);
                        } else {
                            columnModel.getColumn(i).setPreferredWidth(100);
                        }
                    }
                }
                else {

                    list.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                }

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                Float[][] data = model.getData();
                if (model.getData().length == 0) {
                    list.setModel(new EstadisticaTableModel(new String[0], new String[0], new Float[0][0]));
                } else {
                    if (data != null && data.length > 0) {
                        for (int i = 0; i < model.getRows().size(); i++) {
                            for (int j = 0; j < model.getCols().length; j++) {
                                if (i < data.length && j < data[i].length) {
                                    dataset.addValue(data[i][j], model.getRows().get(i), model.getCols()[j]);
                                }
                            }
                        }
                    }
                }

                JFreeChart lineChart = ChartFactory.createLineChart(
                        "Ventas por mes",
                        "Categorías",
                        "Valores",
                        dataset,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false
                );


                ChartPanel chartPanel = new ChartPanel(lineChart);


                graficaPanel.removeAll();
                graficaPanel.add(chartPanel);
                break;
            case Model.RANGE:
                try {
                    controller.actualizarDatos();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                actualizarVista();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + evt.getPropertyName());
        }
        this.panel.revalidate();
    }

    private DefaultCategoryDataset getDefaultCategoryDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Float[][] data = model.getData();
        String[] rows = model.getRows().toArray(new String[0]);
        String[] cols = model.getCols();
        if (rows.length > 0 && cols.length > 0 && data.length > 0) {
            for (int i = 0; i < rows.length; i++) {
                for (int j = 0; j < cols.length; j++) {
                    dataset.addValue(data[i][j].intValue(), rows[i], cols[j]);
                }
            }
        }
        return dataset;
    }

    public void fillCategoriaComboBox() {
        List<Categoria> categorias = model.getCategoriasAll();  // Obtener las categorías del modelo
        categoriaComboBox.removeAllItems();  // Limpia las categorías previas
        for (Categoria categoria : categorias) {
            categoriaComboBox.addItem(categoria);  // Agrega las categorías al combo box
        }
    }


    public void actualizarVista() {
        if (model != null) {
            list.setModel(model.getTableModel());
            list.revalidate();
            list.repaint();
        }
    }

}