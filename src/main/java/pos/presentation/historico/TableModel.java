package pos.presentation.historico;

import pos.logic.Factura;
import pos.presentation.AbstractTableModel;

import java.util.List;

public class TableModel extends AbstractTableModel<Factura> implements javax.swing.table.TableModel {

    public TableModel(int[] cols, List<Factura> rows) {
        super(cols, rows);
    }

    public static final int NUMERO = 0;
    public static final int NOMBRECliente = 1;
    public static final int NOMBRECAJERO = 2;
    public static final int FECHA = 3;
    public static final int IMPORTE = 4;


    @Override
    protected Object getPropetyAt(Factura e, int col) {
        switch (cols[col]) {
            case NUMERO: return e.getNumero();
            case NOMBRECliente: return e.getCliente().getNombre();
            case NOMBRECAJERO: return e.getCajero().getNombre();
            case FECHA: return e.getFecha();
            case IMPORTE: return e.precioTotalAPagar();
            default: return "";
        }
    }

    @Override
    protected Object getPropertyAt(Object o, int col) {
        return null;
    }

    @Override
    protected void initColNames() {
        colNames = new String[5];
        colNames[NUMERO] = "Numero";
        colNames[NOMBRECliente] = "Cliente";
        colNames[NOMBRECAJERO] = "Cajero";
        colNames[FECHA] = "Fecha";
        colNames[IMPORTE] = "Total";
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Factura factura = rows.get(rowIndex);
        return getPropetyAt(factura, columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return colNames[column];
    }
}
