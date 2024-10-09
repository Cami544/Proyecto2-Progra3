package pos.logic;

import java.util.Objects;
//sin xml
public class Categoria {
    String id;
    String nombre;

    public Categoria() {
        this("", "");
    }

    public Categoria(String id, String nombreCat) {
        this.id = id;
        this.nombre = nombreCat;
    }

    public String getIdCategoria() {
        return id;
    }

    public String getNombreCategoria() {
        return nombre;
    }

    public void setIdCategoria(String id) {
        this.id = id;
    }

    public void setNombreCategoria(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(nombre, categoria.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}
