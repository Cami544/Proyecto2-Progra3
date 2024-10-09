package pos.presentation.facturas;
import pos.logic.Factura;
import pos.logic.Linea;
import pos.logic.Producto;
import pos.presentation.AbstractTableModel;

import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel<Factura> implements javax.swing.table.TableModel {
    private List<Linea> lineas = new ArrayList<>();

    public TableModel(int[] cols, List<Factura> rows) {
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
    protected Object getPropetyAt(Factura e, int col) {
        switch (cols[col]) {
            case NUMERO:
                return e.getNumero();
            case ARTICULO:
                return e.getLineas().getFirst().getProducto().getNombre();
            case CATEGORIA:
                return e.getLineas().getFirst().getProducto().getCategoria();
            case CANTIDAD:
                return e.getLineas().getFirst().getCantidad();
            case PRECIO:
                return e.getLineas().getFirst().getProducto().getPrecio();
            case DESCUENTO:
                return e.getLineas().getFirst().getDescuento();
            case NETO:
                return e.getLineas().getFirst().getNeto();
            case IMPORTE:
                return e.getLineas().getFirst().getTotal();

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
        colNames[NUMERO] = "Código";
        colNames[ARTICULO] = "Articulo";
        colNames[CATEGORIA] = "Categoria";
        colNames[CANTIDAD] = "Cantidad";
        colNames[PRECIO] = "Precio";
        colNames[DESCUENTO] = "Descuento";
        colNames[NETO] = "Neto";
        colNames[IMPORTE] = "Total";

    }

    public void removeLinea(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < lineas.size()) {
            lineas.remove(rowIndex);
        }

    }

    public void addLinea(Linea linea) {
        lineas.add(linea);
        fireTableRowsInserted(lineas.size() - 1, lineas.size() - 1);
    }

    public void updateLinea(int rowIndex) {
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    @Override
    public int getRowCount() {
        return lineas.size();
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    public Linea getLineaAt(int rowIndex) {
        return lineas.get(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Linea linea = lineas.get(rowIndex);
        switch (columnIndex) {
            case 0: return linea.getProducto().getId();
            case 1: return linea.getProducto().getNombre();
            case 2: return linea.getProducto().getCategoria();
            case 3: return linea.getCantidad();
            case 4: return linea.getProducto().getPrecio();
            case 5: return linea.getDescuento();
            case 6: return linea.getNeto();
            case 7: return linea.getTotal();

            default: return "";
        }
    }



}

