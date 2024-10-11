package pos.logic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
//sin xml

public class Factura {
    int numero;  // Asignado por la base de datos
    Cliente cliente;
    Cajero cajero;
    LocalDate fecha;
    List<Linea> lineas; // productos

    public Factura() {
        this.numero = 0;  // el número será asignado automáticamente por la base de datos
        this.cliente = null;
        this.cajero = null;
        this.fecha = null;
        this.lineas = new ArrayList<>();
    }

    public Factura(Cliente cliente, Cajero cajero, LocalDate fecha, List<Linea> lineas) {
        this.numero = 0;  // el número será asignado automáticamente por la base de datos
        this.cliente = cliente;
        this.cajero = cajero;
        this.fecha = fecha;
        this.lineas = lineas;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Cajero getCajero() {
        return cajero;
    }

    public void setCajero(Cajero cajero) {
        this.cajero = cajero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public List<Linea> getLineas() {
        return lineas;
    }

    public void setLineas(List<Linea> lineas) {
        this.lineas = lineas;
    }

    public double precioTotalAPagar() {
        double total = 0.0;
        for (Linea linea : getLineas()) {
            total += linea.getTotal();
        }
        return total;
    }

    public int getCantidadTotal() {
        int cantidad = 0;
        for (Linea linea : getLineas()) {
            cantidad += linea.getCantidad();
        }
        return cantidad;
    }

    public double precioTotalNetoAPagar() {
        double subtotal = 0.0;
        for (Linea linea : getLineas()) {
            subtotal += linea.getNeto();
        }
        return subtotal;
    }

    public double totalAhorradoPorDescuento() {
        double descuentos = 0.0;
        for (Linea linea : getLineas()) {
            descuentos += (linea.getNeto() - linea.getTotal());
        }
        return descuentos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Factura factura = (Factura) o;
        return numero == factura.numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return String.valueOf(numero);
    }
}
