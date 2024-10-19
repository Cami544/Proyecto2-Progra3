package pos.data;

import pos.logic.Categoria;
import pos.logic.Linea;
import pos.logic.Producto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LineaDao {
    Database db;

    public LineaDao() {db = Database.instance();}

    // Método para crear una nueva línea de factura
    public Linea create(Linea e) throws Exception {
        String sql = "INSERT INTO Linea (producto, factura, cantidad, descuento) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stm = db.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Asignar valores a los parámetros del PreparedStatement
            stm.setString(1, e.getProducto().getId());  // Llave foránea a Producto
            stm.setInt(2, e.getFactura().getNumero());  // Llave foránea a Factura
            stm.setInt(3, e.getCantidad());
            stm.setFloat(4, e.getDescuento());

            // Ejecutar la inserción y verificar si fue exitosa
            int count = stm.executeUpdate();
            if (count == 0) {
                throw new Exception("No se insertó ninguna línea en la base de datos.");
            }

            // Obtener el número de línea auto-generado por la base de datos
            try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    e.setNumero(generatedKeys.getInt(1));  // Asignar el número de línea generado
                    System.out.println("Línea insertada correctamente con número: " + e.getNumero());  // Registro para depuración
                } else {
                    throw new Exception("No se pudo obtener el número de la línea.");
                }
            }

            return e;  // Retornar la línea con el número generado
        } catch (SQLException ex) {
            // Manejo del error específico de integridad referencial
            if (ex.getSQLState().equals("23000")) {  // Código de error de integridad referencial
                throw new Exception("Error: Las llaves foráneas no coinciden con registros existentes.", ex);
            } else {
                throw new Exception("Error al insertar la línea", ex);
            }
        }
    }



    // Leer todas las líneas de una factura específica
    public List<Linea> readByFactura(int numeroFactura) throws Exception {
        List<Linea> lineas = new ArrayList<>();
        String sql = "SELECT l.*, p.*, c.* " +
                "FROM Linea l " +
                "INNER JOIN Producto p ON l.producto = p.codigo " +
                "INNER JOIN Categoria c ON p.categoria = c.id " +
                "WHERE l.factura = ?";

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setInt(1, numeroFactura);
            ResultSet rs = db.executeQuery(stm);

            while (rs.next()) {
                Linea linea = from(rs, "l");  // Reutilizar el método `from`
                lineas.add(linea);
            }
        }
        return lineas;
    }

    // Leer una línea específica por número
    public Linea read(int numeroLinea) throws Exception {
        String sql = "SELECT l.*, p.*, c.* " +
                "FROM Linea l " +
                "INNER JOIN Producto p ON l.producto = p.codigo " +
                "INNER JOIN Categoria c ON p.categoria = c.id " +
                "WHERE l.numero = ?";

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setInt(1, numeroLinea);
            ResultSet rs = db.executeQuery(stm);

            if (rs.next()) {
                return from(rs, "l");  // Reutilizar el método `from`
            } else {
                throw new Exception("Línea NO EXISTE");
            }
        }
    }

    // Actualizar una línea
    public void update(Linea e) throws Exception {
        String sql = "UPDATE Linea SET cantidad=?, descuento=?, producto=? WHERE numero=?";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setInt(1, e.getCantidad());
            stm.setFloat(2, e.getDescuento());
            stm.setString(3, e.getProducto().getId());
            stm.setInt(4, e.getNumero());

            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Línea NO EXISTE");
            }
        }
    }

    // Eliminar una línea
    public void delete(int facturaNumero) throws Exception {
        String sql = "DELETE FROM Linea WHERE factura_numero=?";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setInt(1, facturaNumero); // Asignar el número de factura
            stm.executeUpdate(); // Ejecutar la eliminación
        } catch (SQLException ex) {
            throw new Exception("Error al eliminar las líneas de la factura", ex);
        }
    }


    // Buscar líneas por descripción del producto
    public List<Linea> search(Linea e) throws Exception {
        List<Linea> resultado = new ArrayList<>();
        String sql = "SELECT l.*, p.*, c.* FROM Linea l " +
                "INNER JOIN Producto p ON l.producto = p.codigo " +
                "INNER JOIN Categoria c ON p.categoria = c.id " +
                "WHERE p.descripcion LIKE ? ORDER BY p.descripcion";

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, "%" + e.getProducto().getDescripcion() + "%");
            ResultSet rs = db.executeQuery(stm);

            while (rs.next()) {
                Linea linea = from(rs, "l");  // Reutilizar el método `from`
                resultado.add(linea);
            }
        }
        return resultado;
    }

    // Obtener el siguiente número de línea disponible
    public int obtenerSiguienteNumeroLinea() throws Exception {
        String sql = "SELECT COALESCE(MAX(numero), 0) + 1 AS siguiente_numero FROM Linea";
        try (PreparedStatement stm = db.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("siguiente_numero");
            } else {
                throw new Exception("No se pudo obtener el siguiente número de línea");
            }
        }
    }

    // Método reutilizable para construir una línea desde un ResultSet
    public Linea from(ResultSet rs, String alias) throws Exception {
        Linea e = new Linea();
        e.setNumero(rs.getInt(alias + ".numero"));
        e.setCantidad(rs.getInt(alias + ".cantidad"));
        e.setDescuento(rs.getFloat(alias + ".descuento"));

        Producto producto = new Producto();
        producto.setId(rs.getString("p.codigo"));
        producto.setDescripcion(rs.getString("p.descripcion"));
        producto.setUnidadMedida(rs.getString("p.unidadMedida"));
        producto.setPrecio(rs.getFloat("p.precioUnitario"));

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getString("c.id"));
        categoria.setNombreCategoria(rs.getString("c.nombre"));

        producto.setCategoria(categoria);
        e.setProducto(producto);

        return e;
    }
}
