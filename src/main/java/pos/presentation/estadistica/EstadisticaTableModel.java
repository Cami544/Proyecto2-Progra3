package pos.presentation.estadistica;

import javax.swing.table.AbstractTableModel;

public class EstadisticaTableModel extends AbstractTableModel {
    private String[] rows;
    private String[] cols;
    private Float[][] data;

    public EstadisticaTableModel(String[] rows, String[] cols, Float[][] data) {
        this.rows = rows;
        this.cols = cols;
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return rows.length;
    }

    @Override
    public int getColumnCount() {
        return cols.length + 1;
    }


    @Override
    public Object getValueAt(int row, int col) {
        if (col == 0) {
            return rows[row];
        } else {
            return data[row][col - 1];
        }
    }

    @Override
    public String getColumnName(int col) {
        if (col == 0) {
            return "Categoria";
        } else {
            return String.valueOf(cols[col - 1]);
        }
    }
}
