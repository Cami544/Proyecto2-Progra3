package pos.logic;

import java.util.Objects;
//sin xml
public class Producto {
    String codigo;
    String nombre;
    String descripcion;
    String unidadMedida;
    double precioUnitario;
    int existencias;
    Categoria categoria;

    public Producto() {
        this("", "", "", 0, 0, "", null);
    }

    public Producto(String codigo, String nombre, String descripcion, int existencias, double precioUnitario, String unidadMedida, Categoria categoria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.existencias = existencias;
        this.precioUnitario = precioUnitario;
        this.unidadMedida = unidadMedida;
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getId() {
        return codigo;
    }

    public void setId(String codigo) {this.codigo = codigo;}

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public double getPrecio() {
        return precioUnitario;
    }

    public void setPrecio(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getExistencias() {
        return existencias;
    }

    public void setExistencias(int existencias) {
        this.existencias = existencias;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return Objects.equals(codigo, producto.codigo);
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
