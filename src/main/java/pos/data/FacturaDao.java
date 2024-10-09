package pos.data;

import pos.logic.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FacturaDao {
    Database db;
    LineaDao lineaDao; // Agregar una instancia de LineaDao

    public FacturaDao() {
        db = Database.instance();
        lineaDao = new LineaDao(); // Inicializa la instancia de LineaDao
    }

    public void create(Factura e) throws Exception {
        // Primero, se inserta la factura en la base de datos
        String sql = "INSERT INTO Factura (cliente, cajero, fecha) VALUES (?, ?, ?)";
        PreparedStatement stm = db.prepareStatement(sql);

        // Asignar los valores a los parámetros del PreparedStatement
        stm.setString(1, e.getCliente().getId()); // ID del cliente
        stm.setString(2, e.getCajero().getId()); // ID del cajero
        stm.setDate(3, Date.valueOf(e.getFecha().format(DateTimeFormatter.ISO_DATE))); // Convertir LocalDate a Date

        // Ejecutar la actualización para insertar la factura
        int numero = db.executeUpdate(stm);
        e.setNumero(String.valueOf(numero));
    }
    public Factura read(int numeroFactura) throws Exception {
        String sql = "SELECT * " +
                "FROM Factura f " +
                "INNER JOIN Cliente c ON f.cliente = c.id " +
                "INNER JOIN Cajero ca ON f.cajero = ca.id " +
                "WHERE f.numero = ?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setInt(1, numeroFactura);
        ResultSet rs = db.executeQuery(stm);

        if (rs.next()) {
            Factura factura = from(rs); // Método para construir la factura desde el ResultSet

            // Aquí, puedes cargar las líneas asociadas a la factura
            LineaDao lineaDao = new LineaDao();
            List<Linea> lineas = lineaDao.readByFactura(numeroFactura); // Método para obtener líneas por factura
            factura.setLineas(lineas); // Asumiendo que hay un método setLineas en la clase Factura

            return factura;
        } else {
            throw new Exception("Factura NO EXISTE");
        }
    }

    public void update(Factura e) throws Exception {
        String sql = "UPDATE Factura SET cliente=?, cajero=?, fecha=? WHERE numero=?";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            // Asegúrate de que el cliente y el cajero tengan un método getId() para obtener sus IDs
            stm.setString(1, e.getCliente().getId());
            stm.setString(2, e.getCajero().getId());
            stm.setDate(3, java.sql.Date.valueOf(e.getFecha())); // Convierte LocalDate a java.sql.Date
            stm.setInt(4, e.getNumero()); // Asumiendo que el número de factura es de tipo int

            int count = stm.executeUpdate();
            if (count == 0) {
                throw new Exception("Factura NO EXISTE");
            }

            // Actualizar las líneas de la factura
            for (Linea linea : e.getLineas()) {
                lineaDao.update(linea); // metodo update de LineaDao
            }
        } catch (SQLException ex) {
            // Manejo de la excepción
            throw new Exception("Error al actualizar la factura", ex);
        }
    }



    public void delete(Factura e) throws Exception {
        String sql = "DELETE FROM Factura WHERE numero=?"; // Consulta SQL para eliminar la factura
        PreparedStatement stm = db.prepareStatement(sql); // Prepara la declaración
        stm.setInt(1, e.getNumero()); // Establece el número de la factura en el parámetro
        int count = db.executeUpdate(stm); // Ejecuta la actualización y obtiene el número de filas afectadas

        // Si count es 0, significa que no se encontró ninguna factura con el número proporcionado
        if (count == 0) {
            throw new Exception("Factura NO EXISTE"); // Lanza una excepción si no se encuentra la factura
        }
    }

    public List<Factura> search(Factura e) throws Exception {
        List<Factura> resultado = new ArrayList<>();
        String sql = "SELECT f.*, c.*, ca.* " +
                "FROM Factura f " +
                "INNER JOIN Cliente c ON f.cliente = c.id " +
                "INNER JOIN Cajero ca ON f.cajero = ca.id " +
                "WHERE c.nombre LIKE ? OR f.fecha = ?"; // Puedes ajustar las condiciones según sea necesario

        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, "%" + e.getCliente().getNombre() + "%"); // Suponiendo que tienes un método getNombre en Cliente
        stm.setDate(2, Date.valueOf(e.getFecha())); // Convertir LocalDate a java.sql.Date para la comparación

        ResultSet rs = db.executeQuery(stm);

        while (rs.next()) {
            Factura factura = from(rs); // Método from para construir la factura desde el ResultSet
            // Cargar las líneas asociadas a la factura
            List<Linea> lineas = lineaDao.readByFactura(factura.getNumero()); // Método para obtener líneas por factura
            factura.setLineas(lineas); // Asumiendo que hay un método setLineas en la clase Factura
            resultado.add(factura); // Agregar la factura encontrada a la lista
        }

        return resultado; // Retornar la lista de facturas encontradas
    }


    public Factura from(ResultSet rs) throws Exception {
        Factura factura = new Factura();
        factura.setNumero(rs.getInt("f.numero")); //  'numero' de la tabla Factura
        factura.setCliente(new Cliente()); // Crear un nuevo cliente
        factura.getCliente().setId(rs.getString("c.id")); // Asume que la columna 'id' está en la tabla Cliente
        factura.getCliente().setNombre(rs.getString("c.nombre")); // esto seun la estructura de la tabla Cliente
        factura.setCajero(new Cajero()); // Crear un nuevo cajero
        factura.getCajero().setId(rs.getString("ca.id")); // 'id' de la tabla Cajero
        factura.setFecha(rs.getDate("f.fecha").toLocalDate()); // Convertir de java.sql.Date a LocalDate
        return factura;
    }

}
