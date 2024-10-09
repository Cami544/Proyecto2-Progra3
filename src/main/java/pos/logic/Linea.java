package pos.logic;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;

import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Linea {
    @XmlID
    String numero;
    @XmlIDREF
    Producto producto;
    @XmlIDREF
    Factura factura;
    int cantidad;
    float descuento;

    public Linea() {
        this("",null,null,1,0);
    }

    public Linea(String numero, Producto producto, Factura factura, int cantidad, float descuento) {
        this.numero = numero;
        this.producto = producto;
        this.factura = factura;
        this.cantidad = cantidad;
        this.descuento = descuento;
    }

    public double getNeto() {
        if (producto == null) {
            return 0;
        }
        return producto.getPrecio() * cantidad;
    }

    public float getTotal() {
        double neto = getNeto();
        double total= neto - (neto * (descuento/100));
        return Math.round(total);
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getDescuento() {
        return descuento;
    }

    public void setDescuento(float descuento) {
        this.descuento = descuento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Linea linea = (Linea) o;
        return Objects.equals(numero, linea.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return numero;
    }
}
