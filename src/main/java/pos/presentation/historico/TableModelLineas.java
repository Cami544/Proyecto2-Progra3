package pos.presentation.historico;

import pos.logic.Linea;
import pos.presentation.AbstractTableModel;

import java.util.List;

public class TableModelLineas extends AbstractTableModel<Linea> implements javax.swing.table.TableModel {

    public TableModelLineas(int[] cols, List<Linea> rows) {
        super(cols, rows);
    }

    public static final int NUMERO = 0;
    public static final int ARTICULO = 1;
    public static final int CATEGORIA = 2;
    public static final int CANTIDAD = 3;
    public static final int PRECIO = 4;
    public static final int DESCUENTO = 5;
    public static final int NETO = 6;
    public static final int IMPORTE = 7;


    @Override
    protected Object getPropetyAt(Linea e, int col) {
        switch (cols[col]) {
            case NUMERO:
                return e.getNumero();
            case ARTICULO:
                return e.getProducto().getNombre();
            case CATEGORIA:
                return e.getProducto().getCategoria().getNombreCategoria();
            case CANTIDAD:
                return e.getCantidad();
            case PRECIO:
                return e.getProducto().getPrecio();
            case DESCUENTO:
                return e.getDescuento();
            case NETO:
                return e.getNeto();
            case IMPORTE:
                return e.getTotal();

            default:
                return "";
        }
    }

    @Override
    protected Object getPropertyAt(Object o, int col) {
        return null;
    }


    @Override
    protected void initColNames() {
        colNames = new String[8];
        colNames[NUMERO] = "Codigo";
        colNames[ARTICULO] = "Articulo";
        colNames[CATEGORIA] = "Categoria";
        colNames[CANTIDAD] = "Cantidad";
        colNames[PRECIO] = "Precio";
        colNames[DESCUENTO] = "Descuento";
        colNames[NETO] = "Neto";
        colNames[IMPORTE] = "Total";

    }
}
