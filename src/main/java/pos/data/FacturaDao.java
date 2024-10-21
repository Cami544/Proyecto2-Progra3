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
            db.commit();  // Confirmar la transacción si todo salio bien
        } catch (SQLException ex) {
            db.rollback();  // Revertir la transaccion en caso de error
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

    // Método para obtener todas las facturas
    public List<Factura> obtenerTodasFacturas() throws Exception {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT f.numero, f.fecha, c.id AS clienteId, c.nombre AS clienteNombre, " +
                "ca.id AS cajeroId, ca.nombre AS cajeroNombre, " +
                "l.numero AS lineaNumero, l.cantidad, l.descuento, " +
                "p.codigo AS productoCodigo, p.nombre AS productoNombre, p.descripcion AS productoDescripcion, p.precioUnitario, " +
                "cat.id AS categoriaId, cat.nombre AS categoriaNombre " +
                "FROM factura f " +
                "JOIN cliente c ON f.cliente = c.id " +
                "JOIN cajero ca ON f.cajero = ca.id " +
                "LEFT JOIN linea l ON f.numero = l.factura " +
                "LEFT JOIN producto p ON l.producto = p.codigo " +
                "LEFT JOIN categoria cat ON p.categoria = cat.id " +
                "ORDER BY f.numero ASC";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();

            Factura factura = null;
            int facturaNumeroAnterior = -1;

            while (rs.next()) {
                int numeroFactura = rs.getInt("numero");

                // Si cambia el número de factura, crea una nueva factura
                if (factura == null || numeroFactura != facturaNumeroAnterior) {
                    factura = new Factura();
                    factura.setNumero(numeroFactura);
                    factura.setFecha(rs.getDate("fecha").toLocalDate()); // Asignando la fecha de la factura

                    // Configuración del cliente
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getString("clienteId")); // Usando alias clienteId
                    cliente.setNombre(rs.getString("clienteNombre")); // Usando alias clienteNombre
                    factura.setCliente(cliente);

                    // Configuración del cajero
                    Cajero cajero = new Cajero();
                    cajero.setId(rs.getString("cajeroId")); // Usando alias cajeroId
                    cajero.setNombre(rs.getString("cajeroNombre")); // Usando alias cajeroNombre
                    factura.setCajero(cajero);

                    // Añadiendo la factura a la lista
                    facturas.add(factura);
                    facturaNumeroAnterior = numeroFactura;
                }

                // Si hay líneas asociadas a la factura, agrégalas
                if (rs.getInt("lineaNumero") != 0) {
                    Linea linea = new Linea();
                    linea.setNumero(rs.getInt("lineaNumero")); // Asignando número de línea
                    linea.setCantidad(rs.getInt("cantidad"));  // Asignando cantidad
                    linea.setDescuento(rs.getFloat("descuento")); // Asignando descuento

                    // Configuración del producto
                    Producto producto = new Producto();
                    producto.setId(rs.getString("productoCodigo")); // Usando alias productoCodigo
                    producto.setNombre(rs.getString("productoNombre")); // Usando alias productoNombre
                    producto.setDescripcion(rs.getString("productoDescripcion")); // Usando alias productoDescripcion
                    producto.setPrecio(rs.getFloat("precioUnitario")); // Usando alias precioUnitario

                    // Configuración de la categoría del producto
                    Categoria categoria = new Categoria();
                    categoria.setIdCategoria(rs.getString("categoriaId")); // Usando alias categoriaId
                    categoria.setNombreCategoria(rs.getString("categoriaNombre")); // Usando alias categoriaNombre
                    producto.setCategoria(categoria);

                    // Añadiendo el producto a la línea
                    linea.setProducto(producto);

                    // Añadiendo la línea a la factura
                    factura.getLineas().add(linea);
                }
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
                Factura fac = from(rs); // Método para construir la factura desde el ResultSet

                // Comprobar si el cliente de la factura obtenida coincide con el nombre del filtro
                if (fac.getCliente() != null &&
                        fac.getCliente().getNombre().toLowerCase().contains(nombreCliente)) {

                    List<Linea> lineas = lineaDao.readByFactura(fac.getNumero()); // Obtener líneas por factura
                    fac.setLineas(lineas); // Asignar las líneas a la factura obtenida
                    filteredFacturas.add(fac); // Agregar la factura filtrada a la lista
                }
            }
        } catch (SQLException ex) {
            throw new Exception("Error al buscar facturas por cliente", ex);
        }

        return filteredFacturas; // Devolver la lista de facturas filtradas
    }

    public float getVentas(Categoria c, int anio, int mes) throws Exception {
        float total = 0;

        // Consulta SQL corregida para recuperar los IDs de las facturas filtradas
        String sql = "SELECT DISTINCT f.numero " +
                "FROM Factura f " +
                "JOIN Linea l ON f.numero = l.factura " +  // Cambio de f.id a f.numero
                "JOIN Producto p ON l.producto = p.codigo " +
                "JOIN Categoria ca ON p.categoria = ca.id " +
                "WHERE ca.id = ? AND " +
                "YEAR(f.fecha) = ? AND MONTH(f.fecha) = ?";

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, c.getIdCategoria());  // ID de la categoría
            stm.setInt(2, anio);                   // Año
            stm.setInt(3, mes);                    // Mes

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int facturaId = rs.getInt("numero");  // ID de la factura
                    Factura factura = this.read(facturaId);  // Recuperar la factura completa

                    // Asegúrate de que la factura y sus líneas estén correctamente cargadas
                    if (factura != null) {
                        total += Service.instance().precioTotalPagar(factura);  // Sumar total con descuento
                    }
                }
            }
        } catch (SQLException ex) {
            throw new Exception("Error al obtener las ventas para la categoría: " + c.getIdCategoria() + " en " + anio + "/" + mes, ex);
        }
        return total;
    }



    /* public Factura from(ResultSet rs) throws Exception {
        Factura factura = new Factura();
        factura.setNumero(rs.getInt("f.numero")); // Número de la factura
        factura.setCliente(new Cliente());
        factura.getCliente().setId(rs.getString("c.id")); // ID del cliente
        factura.getCliente().setNombre(rs.getString("c.nombre")); // Nombre del cliente
        factura.setCajero(new Cajero());
        factura.getCajero().setId(rs.getString(".categoriaId")); // ID del cajero
        factura.getCajero().setNombre(rs.getString(".categoriaNombre")); // ID del cajero
        factura.setFecha(rs.getDate("f.fecha").toLocalDate()); // Fecha de la factura
        return factura;
    }*/
// Método para mapear una factura desde el ResultSet
   public Factura from(ResultSet rs) throws Exception {
       Factura factura = new Factura();
       factura.setNumero(rs.getInt("numero"));
       Cliente cliente = new Cliente();
       cliente.setId(rs.getString("clienteId"));  // Usando alias clienteId de la consulta SQL
       cliente.setNombre(rs.getString("clienteNombre")); // Usando alias clienteNombre de la consulta SQL
       factura.setCliente(cliente);
       Cajero cajero = new Cajero();
       cajero.setId(rs.getString("cajeroId"));  // Usando alias cajeroId de la consulta SQL
       cajero.setNombre(rs.getString("cajeroNombre")); // Usando alias cajeroNombre de la consulta SQL
       factura.setCajero(cajero);
       factura.setFecha(rs.getDate("fecha").toLocalDate()); // Usando alias fecha de la consulta SQL
       return factura;
   }
}
