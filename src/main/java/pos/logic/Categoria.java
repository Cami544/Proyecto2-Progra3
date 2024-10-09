package pos.logic;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Categoria {

    @XmlID
    String codigo;
    @XmlElement
    String nombre;

    public Categoria() {
        this("", "");
    }
    public Categoria(String codigo, String nombreCat) {
        this.codigo = codigo;
        this.nombre = nombreCat;
    }

    public String getIdCategoria() {
        return codigo;
    }

    public String getNombreCategoria() {
        return nombre;
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
