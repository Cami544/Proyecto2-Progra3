package pos.presentation.productos;

import pos.logic.Producto;
import pos.presentation.AbstractTableModel;

import java.util.List;

public class TableModel extends AbstractTableModel<Producto> implements javax.swing.table.TableModel {

    public TableModel(int[] cols, List<Producto> rows) {
        super(cols, rows);
    }

    public static final int ID=0;
    public static final int NOMBRE=1;
    public static final int DESCRIPCION=2;
    public static final int UNIDADMEDIDA=3;
    public static final int PRECIO=4;
    public static final int EXISTENCIA=5;
    public static final int CATEGORIA=6;

    @Override
    protected Object getPropetyAt(Producto e, int col) {
        switch (cols[col]){
            case ID: return e.getId();
            case NOMBRE: return  e.getNombre();
            case DESCRIPCION: return e.getDescripcion();
            case UNIDADMEDIDA: return e.getUnidadMedida();
            case PRECIO: return e.getPrecio();
            case EXISTENCIA: return e.getExistencias();
            case CATEGORIA:return e.getCategoria() != null ? e.getCategoria().getNombreCategoria() : "";
            default: return "";
        }
    }

    @Override
    protected Object getPropertyAt(Object o, int col) {
        return null;
    }

    @Override
    protected void initColNames(){
        colNames = new String[7];
        colNames[ID]= "Código";
        colNames[NOMBRE]= "Nombre";
        colNames[DESCRIPCION]= "Descripcion";
        colNames[UNIDADMEDIDA]= "Unidad Medida";
        colNames[PRECIO]= "Precio";
        colNames[EXISTENCIA]= "Existencia";
        colNames[CATEGORIA]= "Categoria";
    }

}
