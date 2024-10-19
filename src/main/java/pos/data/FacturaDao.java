package pos.data;

import pos.logic.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FacturaDao {
    Database db;
    LineaDao lineaDao; // Instancia de LineaDao para manejar las líneas

    public FacturaDao() {
        db = Database.instance();
        lineaDao = new LineaDao(); // Inicializa la instancia de LineaDao
    }
    // Método para crear una nueva factura

    public void create(Factura e) throws Exception {
        String insertFacturaSQL = "INSERT INTO factura (cliente, cajero, fecha) VALUES (?, ?, ?)";
        String insertLineaSQL = "INSERT INTO linea (producto, factura, cantidad, descuento) VALUES (?, ?, ?, ?)";

        try {
            db.setAutoCommit(false);  // Iniciar la transacción

            // 1. Insertar la factura
            try (PreparedStatement stmFactura = db.prepareStatement(insertFacturaSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmFactura.setString(1, e.getCliente().getId());
                stmFactura.setString(2, e.getCajero().getId());
                stmFactura.setDate(3, Date.valueOf(e.getFecha()));
                int affectedRows = stmFactura.executeUpdate();
                if (affectedRows == 0) {
                    throw new Exception("No se pudo insertar la factura.");
                }
                // 2. Obtener el número de la factura generada
                try (ResultSet generatedKeys = stmFactura.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        e.setNumero(generatedKeys.getInt(1));  // Asignar número de la factura
                    } else {
                        throw new Exception("No se pudo obtener el número de la factura.");
                    }
                }
            }
            // 3. Insertar las líneas asociadas a la factura
            try (PreparedStatement stmLinea = db.prepareStatement(insertLineaSQL)) {
                for (Linea linea : e.getLineas()) {
                    stmLinea.setString(1, linea.getProducto().getId());
                    stmLinea.setInt(2, e.getNumero());  // Asociar número de factura
                    stmLinea.setInt(3, linea.getCantidad());
                    stmLinea.setFloat(4, linea.getDescuento());

                    stmLinea.addBatch();  // Agregar la línea a un lote para ejecución
                }
                stmLinea.executeBatch();  // Ejecutar todas las inserciones en lote
            }
            db.commit();  // Confirmar la transacción si todo salió bien
        } catch (SQLException ex) {
            db.rollback();  // Revertir la transacción en caso de error
            throw new Exception("Error al insertar la factura y sus líneas", ex);
        } finally {
            db.setAutoCommit(true);  // Restaurar auto-commit
        }
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
            Factura factura = from(rs);

            List<Linea> lineas = lineaDao.readByFactura(numeroFactura); // Obtener líneas por factura
            factura.setLineas(lineas);
            return factura;
        } else {
            throw new Exception("Factura NO EXISTE");
        }
    }

    public void update(Factura e) throws Exception {
        String sql = "UPDATE Factura SET cliente=?, cajero=?, fecha=? WHERE numero=?";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, e.getCliente().getId()); // ID del cliente
            stm.setString(2, e.getCajero().getId()); // ID del cajero
            stm.setDate(3, java.sql.Date.valueOf(e.getFecha())); // Convertir LocalDate a java.sql.Date
            stm.setInt(4, e.getNumero()); // Número de la factura

            int count = stm.executeUpdate();
            if (count == 0) {
                throw new Exception("Factura NO EXISTE");
            }
            // Actualizar las líneas de la factura
            for (Linea linea : e.getLineas()) {
                lineaDao.update(linea); // Actualizar las líneas usando LineaDao
            }
        } catch (SQLException ex) {
            throw new Exception("Error al actualizar la factura", ex);
        }
    }

    public void delete(Factura e) throws Exception {
        // Verificar si la factura tiene líneas asociadas y eliminarlas
        if (!e.getLineas().isEmpty()) {
            lineaDao.delete(e.getNumero()); // Eliminar todas las líneas de la factura
        }
        // Eliminar la factura después de eliminar sus líneas
        String sql = "DELETE FROM Factura WHERE numero=?";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setInt(1, e.getNumero()); // Asignar el número de factura
            int count = stm.executeUpdate();
            if (count == 0) {
                throw new Exception("Factura NO EXISTE");
            }
        } catch (SQLException ex) {
            throw new Exception("Error al eliminar la factura", ex);
        }
    }

    public List<Factura> search(Factura e) throws Exception {
        List<Factura> resultado = new ArrayList<>();
        String sql = "SELECT f.*, c.*, ca.* " +
                "FROM Factura f " +
                "INNER JOIN Cliente c ON f.cliente = c.id " +
                "INNER JOIN Cajero ca ON f.cajero = ca.id " +
                "WHERE c.nombre LIKE ? OR f.fecha = ?";

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            if (e.getCliente() != null && e.getCliente().getNombre() != null) {
                stm.setString(1, "%" + e.getCliente().getNombre() + "%");
            } else {
                stm.setString(1, "%");
            }
            if (e.getFecha() != null) {
                stm.setDate(2, Date.valueOf(e.getFecha()));
            } else {
                stm.setDate(2, null);
            }
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Factura factura = from(rs); // Construir la factura
                List<Linea> lineas = lineaDao.readByFactura(factura.getNumero()); // Cargar líneas
                factura.setLineas(lineas);
                resultado.add(factura); // Agregar la factura a la lista de resultados
            }
        } catch (SQLException ex) {
            throw new Exception("Error al buscar facturas", ex);
        }
        return resultado;
    }

    public List<Factura> obtenerTodasFacturas() throws Exception {
        List<Factura> facturas = new ArrayList<>();

        String sql = "SELECT f.numero, f.fecha, c.id AS id, c.nombre AS nombre, " +
                "ca.id AS id, ca.nombre AS nombre, " +
                "l.numero AS numero, l.cantidad, l.descuento, " +
                "p.codigo AS codigo, p.nombre AS nombre, p.descripcion AS descripcion, p.unidadMedida, " +
                "p.precioUnitario, p.existencias, " +
                "cat.id AS id, cat.nombre AS nombre " +
                "FROM factura f " +
                "JOIN cliente c ON f.cliente = c.id " +
                "JOIN cajero ca ON f.cajero = ca.id " +
                "JOIN linea l ON f.numero = l.factura " +
                "JOIN producto p ON l.producto = p.codigo " +
                "JOIN categoria cat ON p.categoria = cat.id";

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();

            Factura factura = null;
            int facturaNumeroAnterior = -1;

            while (rs.next()) {
                int numeroFactura = rs.getInt("numero");

                // Crear una nueva factura solo si el número ha cambiado
                if (factura == null || numeroFactura != facturaNumeroAnterior) {
                    factura = from(rs);  // Construir la factura desde el ResultSet
                    facturas.add(factura);
                    facturaNumeroAnterior = numeroFactura;
                }

                // Crear la línea y asociar el producto y la categoría
                Linea linea = new Linea();
                linea.setNumero(rs.getInt("numero"));
                linea.setCantidad(rs.getInt("cantidad"));
                linea.setDescuento(rs.getFloat("descuento"));

                Producto producto = new Producto();
                producto.setId(rs.getString("codigo"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setPrecio(rs.getFloat("precioUnitario"));

                Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getString("id"));
                categoria.setNombreCategoria(rs.getString("nombre"));

                producto.setCategoria(categoria);
                linea.setProducto(producto);
                factura.getLineas().add(linea); // Añadir línea a la factura
            }
        }
        return facturas;
    }

    public int obtenerSiguienteNumeroFactura() throws Exception {
        String sql = "SELECT COALESCE(MAX(numero), 0) + 1 AS siguiente_numero FROM factura";
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
                "JOIN Linea l ON f.id = l.factura " +
                "JOIN Producto p ON l.producto = p.id " +  // Relacionar Linea con Producto
                "JOIN Categoria ca ON p.categoria = ca.id " + // Relacionar Producto con Categoria
                "WHERE ca.id = ? AND " +  // Filtrar por la categoría
                "YEAR(f.fecha) = ? AND MONTH(f.fecha) = ?";

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, c.getIdCategoria()); // Establecer el ID de la categoría
            stm.setInt(2, anio);                  // Establecer el año
            stm.setInt(3, mes);                   // Establecer el mes

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    total = rs.getFloat("total_ventas"); // Obtener el total de ventas
                }
            }
        } catch (SQLException ex) {
            throw new Exception("Error al obtener las ventas", ex);
        }

        return total;
    }

    public Factura from(ResultSet rs) throws Exception {
        Factura factura = new Factura();
        factura.setNumero(rs.getInt("f.numero")); // Número de la factura
        factura.setCliente(new Cliente());
        factura.getCliente().setId(rs.getString("c.id")); // ID del cliente
        factura.getCliente().setNombre(rs.getString("c.nombre")); // Nombre del cliente
        factura.setCajero(new Cajero());
        factura.getCajero().setId(rs.getString("ca.id")); // ID del cajero
        factura.setFecha(rs.getDate("f.fecha").toLocalDate()); // Fecha de la factura
        return factura;
    }
}
