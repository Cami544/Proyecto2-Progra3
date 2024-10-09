package pos.presentation.cajeros;

import pos.logic.Cajero;
import pos.presentation.AbstractTableModel;

import java.util.List;

public class TableModel extends AbstractTableModel<Cajero> implements javax.swing.table.TableModel {

    public TableModel(int[] cols, List<Cajero> rows) {
        super(cols, rows);
    }

    public static final int ID=0;
    public static final int NOMBRE=1;
    @Override
    protected Object getPropetyAt(Cajero c, int col) {
        switch (cols[col]){
            case ID: return c.getId();
            case NOMBRE: return c.getNombre();
            default: return "";
        }
    }

    @Override
    protected Object getPropertyAt(Object o, int col) {
        return null;
    }

    @Override
    protected void initColNames(){
        colNames = new String[5];
        colNames[ID]= "Id";
        colNames[NOMBRE]= "Nombre";
    }

}
