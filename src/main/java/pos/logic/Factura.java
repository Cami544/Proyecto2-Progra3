package pos.logic;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import pos.data.LocalDateAdapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public  class Factura   {
    @XmlID
    String numero;
    @XmlIDREF
            Cliente cliente;
    @XmlIDREF
            Cajero cajero;
    @XmlJavaTypeAdapter(value= LocalDateAdapter.class)
    LocalDate fecha;
    @XmlIDREF
    @XmlElementWrapper(name="Lineas")
    @XmlElement(name="Linea")
    List<Linea> lineas; //productos


    public Factura() {
        this. numero=generateFacturaNumber();
        this.cliente=null;
        this.cajero=null;
        this.fecha=null;
        this.lineas = new ArrayList<>();
    }


    public Factura(Cliente cliente, Cajero cajero, LocalDate fecha, List<Linea> lineas) {
        this.numero = generateFacturaNumber() ;
        this.cliente = cliente;
        this.cajero = cajero;
        this.fecha = fecha;
        this.lineas = lineas;
    }


    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
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

    public double precioTotalPagar() {
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

    public double precioNetoPagarT() {
        double subtotal = 0.0;
        for (Linea linea : getLineas()) {
            subtotal += linea.getNeto();
        }
        return subtotal;
    }

    public double ahorroXDescuentoT() {
        double descuentos = 0.0;
        for (Linea linea : getLineas()) {
            descuentos += (linea.getNeto() - linea.getTotal());
        }
        return descuentos;
    }

    public String generateFacturaNumber() {
        String facturaNumber = "FAC-";
        return (facturaNumber+((int) (Math.random() * 100000)));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Factura factura = (Factura) o;
        return Objects.equals(numero, factura.numero);
    }



    @Override
    public String toString() {
        return numero;
    }
}
