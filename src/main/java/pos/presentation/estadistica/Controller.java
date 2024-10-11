package pos.presentation.estadistica;

import pos.logic.Categoria;
import pos.logic.Factura;
import pos.logic.Service;

import java.util.ArrayList;
import java.util.List;

import static pos.presentation.estadistica.Model.CATEGORIES_ALL;

public class Controller {

    private View view;
    private Model model;


    public Controller(View view, Model model) throws Exception {
        this.view = view;
        this.model = model;
        model.init(new ArrayList<Categoria>(Service.instance().obtenerTodasCategorias()));
        view.setController(this);
        view.setModel(model);
    }

    public void agregarCategoria(Categoria nuevaCategoria) throws Exception {

        if (model.getCategorias().contains(nuevaCategoria)) {throw new Exception();}
        try {
            model.getCategorias().add(nuevaCategoria);
            actualizarDatos();
        }catch(Exception e) {
            System.err.println("Categoria ya en la lista: " + e.getMessage());
        }
    }

    public void agregarTodasLasCategorias() {
        try {
            List<Categoria> categorias = Service.instance().obtenerTodasCategorias();
            for (Categoria categoria : categorias) {
                if (!model.getCategorias().contains(categoria)) {
                    model.getCategorias().add(categoria);
                }
            }
            actualizarDatos();
        } catch (Exception e) {
            System.err.println("Error al agregar todas las categorías: " + e.getMessage());
        }
    }

    public void botonAgregarCategoriaActionPerformed(Categoria nuevaCategoria) {

    }

    public void clear() throws Exception {
        model.getCategorias().clear();
        model.init(model.getCategoriasAll());
        model.firePropertyChange(CATEGORIES_ALL);
        actualizarDatos();
        view.actualizarVista();
    }

    public List<Factura> getFacturas() {
        try {
            return Service.instance().obtenerTodasFacturas();
        } catch (Exception e) {
            System.out.println("Error al obtener facturas: " + e.getMessage());
            return null;
        }
    }

    public void actualizarRangos(int aniodesde, int mesdesde, int anioHasta, int mesHasta) throws Exception {
        if (aniodesde <= anioHasta && (aniodesde != anioHasta || mesdesde <= mesHasta)) {
            model.setRango(new Rango(aniodesde, mesdesde, anioHasta, mesHasta));
            actualizarDatos();
        } else {
            System.err.println("Error: Rango de fechas inválido.");
        }
    }

    public void fillCategoriaComboBox() throws Exception {
        List<Categoria> categorias = Service.instance().obtenerTodasCategorias();
        if (categorias != null && !categorias.isEmpty()) {
            for (Categoria cat : categorias) {
                view.categoriaComboBox.addItem(cat);
            }
            view.getPanel().revalidate();
        } else {
            System.out.println("No hay categorías disponibles");
        }
    }

    public void agregarLineaACategoria(String categoria, Float[] datos) throws Exception {
        if (categoria != null && datos != null) {
            int index = model.getRows().indexOf(categoria);
            if (index != -1) {
                for (int i = 0; i < datos.length; i++) {
                    model.getData()[index][i] = datos[i];
                }
            } else {
                model.agregarLinea(categoria, datos);
            }
          actualizarDatos();
        } else {
            System.err.println("Error: Categoría o datos inválidos.");
        }
    }

    public void quitarCategoria(int selectedRow) throws Exception {
        model.getCategorias().remove(selectedRow);
        if (model.getCategorias().isEmpty()) { model.init(Service.instance().obtenerTodasCategorias());}
        model.firePropertyChange(CATEGORIES_ALL);
        actualizarDatos();
    }


    public void actualizarDatos() throws Exception {

        Rango r = model.getRango();
        int colCount = (r.getAnnosHasta() - r.getAnnosDesde()) * 12 + r.getMesHasta() - r.getMesDesde() + 1;
        int rowCount = model.getCategorias().size();

        String[] cols = new String[colCount];
        String[] rows = new String[rowCount];

        int year = r.getAnnosDesde();
        int month = r.getMesDesde();

        for (int i = 0; i < colCount; i++) {
            cols[i] = year + "-" + (month < 10 ? "0" + month : month);
            month++;
            if (month > 12) {
                month = 1;
                year++;
            }
        }

        Float[][] data = new Float[rowCount][colCount];

        if(!model.getCategorias().isEmpty()) {

            for (int i = 0; i < rowCount; i++) {
                Categoria categoria = model.getCategorias().get(i);
                year = r.getAnnosDesde();
                month = r.getMesDesde();

                for (int j = 0; j < colCount; j++) {
                    Float ventas = Service.instance().getVentas(categoria, year, month);
                    data[i][j] = ventas;

                    month++;
                    if (month > 12) {
                        month = 1;
                        year++;
                    }
                }
            }

            int i = 0;

            for (Categoria c : model.getCategorias()) {

                rows[i] = c.getNombreCategoria();
                i++;
            }

            model.setCols(cols);
            model.setRows(List.of(rows));
            model.setData(data);

            return;
        }
    }

}
