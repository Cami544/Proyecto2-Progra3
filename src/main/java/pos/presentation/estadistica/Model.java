package pos.presentation.estadistica;

import pos.logic.Categoria;
import pos.logic.Service;
import pos.presentation.AbstractModel;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Model extends AbstractModel {
    private List<Categoria> categorias;
    private List<Categoria> categoriasAll;
    private Rango rango;
    private List<String> rows = new ArrayList<>();
    private String[] cols;
    private Float[][] data;

    public static final String DATA = "data";
    public static final String RANGE = "range";
    public static final String CATEGORIES_ALL = "CATEGORIES";

    public Model() throws Exception {
        categoriasAll = Service.instance().obtenerTodasCategorias();
        categorias = new ArrayList<>(categoriasAll);
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
        firePropertyChange(DATA);
    }

    public void setCategoriasAll(List<Categoria> categoriasAll) {
        this.categoriasAll = categoriasAll;
        firePropertyChange(DATA);
    }

    public Float[][] getData() {
        return data;
    }

    public String[] getCols() {
        return cols;
    }

    public List<String> getRows() {
        return rows;
    }

    public void setRows(List<String> rows) {
        this.rows = rows;
        firePropertyChange(DATA);
    }

    public void agregarCategoria(Categoria nuevaCategoria) {
        if (!categorias.contains(nuevaCategoria)) {
            categorias.add(nuevaCategoria);
            agregarLinea(nuevaCategoria.getNombreCategoria(), new Float[cols.length]);
            firePropertyChange(DATA);
        }
    }

    public void agregarLinea(String categoria, Float[] datos) {
        if (categoria == null || datos == null || datos.length != cols.length) {
            System.err.println("Error: Datos inválidos para agregar una línea.");
            return;
        }

        int index = rows.indexOf(categoria);
        if (index == -1) {
            rows.add(categoria);
            index = rows.size() - 1;
        }

        if (data == null || data.length < rows.size()) {
            Float[][] newData = new Float[rows.size()][cols.length];
            if (data != null) {
                System.arraycopy(data, 0, newData, 0, data.length);
            }
            data = newData;
        }

        data[index] = datos;
        firePropertyChange(DATA);
    }

    public void agregarTotalesPorMes(Float[] totales) {
        String totalCategoria = "Total";
        Float[] datosTotales = new Float[cols.length];

        if (totales.length != cols.length) {
            System.err.println("Error: Longitud de totales no coincide con columnas.");
            return;
        }

        agregarLinea(totalCategoria, totales);
    }

    public void init(List<Categoria> allCategorias) {
        this.categoriasAll = allCategorias;
        this.categorias = new ArrayList<>();
        this.rango = new Rango(0, 0, 0, 0);
        this.rows = new ArrayList<>();
        this.cols = new String[0];
        this.data = new Float[0][0];
    }


    public EstadisticaTableModel getTableModel() {
        if (cols == null || data == null || rows == null) {
            return new EstadisticaTableModel(new String[0], new String[0], new Float[0][0]);
        }
        return new EstadisticaTableModel(rows.toArray(new String[0]), cols, data);
    }

    public void setData(Float[][] data) {
        this.data = data;
        firePropertyChange(DATA);
    }

    public void setCols(String[] cols) {
        this.cols = cols;
        firePropertyChange(DATA);
    }


    public Rango getRango() {
        return rango;
    }

    public void setRango(Rango rango) {
        this.rango = rango;
        firePropertyChange(RANGE);
    }

    public void eliminarCategoriaPorNombre(String nombreCategoria) {
        int index = -1;
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getNombreCategoria().equals(nombreCategoria)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            categorias.remove(index);
            eliminarLinea(index);
            firePropertyChange(DATA);
        }
    }

    public void eliminarLinea(int index) {
        if (index >= 0 && index < rows.size()) {
            rows.remove(index);

            Float[][] newData = new Float[rows.size()][cols.length];
            int newIndex = 0;

            for (int i = 0; i < data.length; i++) {
                if (i != index) {
                    newData[newIndex++] = data[i];
                }
            }

            data = newData;
            firePropertyChange(DATA);
        }
    }
    public List<Categoria> getCategorias() {
        return categorias;
    }

    public List<Categoria> getCategoriasAll() {
        return categoriasAll;
    }

    public Float[] obtenerDatosCategoria(String nombre) {
        int index = rows.indexOf(nombre);
        if (index == -1) {
            return data[index];
        }
        return new Float[0];
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        firePropertyChange(DATA);
        firePropertyChange(CATEGORIES_ALL);

    }

}
