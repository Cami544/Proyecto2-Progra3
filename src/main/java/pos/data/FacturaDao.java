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
        e.setNumero(numero);
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
            Factura factura = from(rs); // Metodo para construir la factura desde el ResultSet

            // Aquí, puedes cargar las líneas asociadas a la factura
            LineaDao lineaDao = new LineaDao();
            List<Linea> lineas = lineaDao.readByFactura(numeroFactura); // Metodo para obtener líneas por factura
            factura.setLineas(lineas); // Asumiendo que hay un metodo setLineas en la clase Factura

            return factura;
        } else {
            throw new Exception("Factura NO EXISTE");
        }
    }

    public void update(Factura e) throws Exception {
        String sql = "UPDATE Factura SET cliente=?, cajero=?, fecha=? WHERE numero=?";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            // Asegúrate de que el cliente y el cajero tengan un metodo getId() para obtener sus IDs
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
        stm.setString(1, "%" + e.getCliente().getNombre() + "%"); // Suponiendo que tienes un metodo getNombre en Cliente
        stm.setDate(2, Date.valueOf(e.getFecha())); // Convertir LocalDate a java.sql.Date para la comparación

        ResultSet rs = db.executeQuery(stm);

        while (rs.next()) {
            Factura factura = from(rs); // Metodo from para construir la factura desde el ResultSet
            // Cargar las líneas asociadas a la factura
            List<Linea> lineas = lineaDao.readByFactura(factura.getNumero()); // Metodo para obtener líneas por factura
            factura.setLineas(lineas); // Asumiendo que hay un metodo setLineas en la clase Factura
            resultado.add(factura); // Agregar la factura encontrada a la lista
        }

        return resultado; // Retornar la lista de facturas encontradas
    }

    public List<Factura> obtenerTodasFacturas() throws Exception {
        List<Factura> facturas = new ArrayList<>();

        String sql = "SELECT f.numero, f.fecha, c.id AS cliente_id, c.nombre AS cliente_nombre, " +
                "ca.id AS cajero_id, ca.nombre AS cajero_nombre, " +
                "l.numero AS linea_id, l.cantidad, l.descuento, " +
                "p.codigo AS producto_codigo, p.descripcion AS producto_descripcion, p.precioUnitario, " +
                "cat.id AS categoria_id, cat.nombre AS categoria_nombre " +
                "FROM factura f " +
                "JOIN cliente c ON f.cliente = c.id " +
                "JOIN cajero ca ON f.cajero = ca.id " +
                "JOIN linea l ON f.numero = l.factura " +
                "JOIN producto p ON l.producto = p.codigo " +
                "JOIN categoria cat ON p.categoria = cat.id";

        try (PreparedStatement stm = db.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {

            Factura factura = null;
            int facturaNumeroAnterior = -1;

            while (rs.next()) {
                int numeroFactura = rs.getInt("f.numero");

                // Solo crear una nueva factura si el número de factura ha cambiado
                if (factura == null || numeroFactura != facturaNumeroAnterior) {
                    factura = from(rs);  // Crear nueva factura
                    facturas.add(factura);
                    facturaNumeroAnterior = numeroFactura;
                }

                // Crear y agregar la línea de productos a la factura actual
                Linea linea = new Linea();
                linea.setNumero(rs.getInt("linea_id"));
                linea.setCantidad(rs.getInt("cantidad"));
                linea.setDescuento(rs.getFloat("descuento"));

                // Crear el producto y su categoría
                Producto producto = new Producto();
                producto.setId(rs.getString("producto_codigo"));
                producto.setDescripcion(rs.getString("producto_descripcion"));
                producto.setPrecio(rs.getFloat("precioUnitario"));

                Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getString("categoria_id"));
                categoria.setNombreCategoria(rs.getString("categoria_nombre"));

                producto.setCategoria(categoria);
                linea.setProducto(producto);

                // Añadir la línea a la factura
                factura.getLineas().add(linea);
            }
        }
        return facturas;
    }
    public int obtenerSiguienteNumeroFactura() throws Exception {
        String sql = "SELECT COALESCE(MAX(numero), 0) + 1 AS siguiente_numero FROM factura"; // Asegúrate de que el nombre de la tabla sea correcto

        try (PreparedStatement stm = db.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("siguiente_numero");
            } else {
                throw new SQLException("No se pudo obtener el siguiente número de factura.");
            }
        }
    }
    public List<Factura> obtenerFacturasDeCliente(Factura factura) throws Exception {
        List<Factura> filteredFacturas = new ArrayList<>();

        // Verificar que el cliente no es nulo y tiene un nombre
        if (factura.getCliente() == null || factura.getCliente().getNombre() == null) {
            throw new Exception("El cliente no puede ser nulo o no tener nombre.");
        }

        String nombreCliente = factura.getCliente().getNombre().toLowerCase(); // Convertir a minúsculas para búsqueda insensible

        // Consulta SQL para obtener todas las facturas
        String sql = "SELECT f.*, ca.*, c.* FROM Factura f " +
                "INNER JOIN Cajero ca ON f.cajero = ca.id " +
                "INNER JOIN Cliente c ON f.cliente = c.id";

        try (PreparedStatement stm = db.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {

            while (rs.next()) {
                Factura fac = from(rs); // Metodo para construir la factura desde el ResultSet

                // Comprobar si el cliente de la factura coincide con el nombre del filtro
                if (factura.getCliente() != null &&
                        factura.getCliente().getNombre().toLowerCase().contains(nombreCliente)) {
                    List<Linea> lineas = lineaDao.readByFactura(factura.getNumero()); // Obtener líneas por factura
                    factura.setLineas(lineas); // Asumiendo que hay un metodo setLineas en la clase Factura
                    filteredFacturas.add(factura); // Agregar factura filtrada a la lista
                }
            }
        } catch (SQLException ex) {
            throw new Exception("Error al buscar facturas por cliente", ex);
        }
        return filteredFacturas; // Devolver la lista de facturas filtradas
    }

    public float getVentas(Categoria c, int anio, int mes) throws Exception {
        float total = 0;
        String sql = "SELECT SUM(l.total) AS total_ventas " +
                "FROM Factura f " +
                "JOIN Linea l ON f.id = l.factura_id " + // Ajusta el nombre de la columna de relación
                "WHERE l.producto_categoria_id = ? AND " + // Asumiendo que tienes un campo para la relación de categorías
                "YEAR(f.fecha) = ? AND MONTH(f.fecha) = ?";

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, c.getIdCategoria());
            stm.setInt(2, anio);
            stm.setInt(3, mes);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    total = rs.getFloat("total_ventas"); // Obtiene el total de ventas
                }
            }
        } catch (SQLException ex) {
            throw new Exception("Error al obtener las ventas", ex);
        }

        return total;
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
