package pos.data;

import pos.logic.Categoria;
import pos.logic.Linea;
import pos.logic.Producto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LineaDao {
    Database db;

    public LineaDao() {
        db = Database.instance();
    }

    public void create(Linea e) throws Exception {
        String sql = "INSERT INTO Linea (producto, factura, cantidad, descuento) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            // Asignacion de valores para las llaves foraneas
            stm.setString(1, e.getProducto().getId());  // Llave foranea a Producto (debe existir en la tabla Producto)
            stm.setInt(2, e.getFactura().getNumero());  // Llave foranea a Factura (debe existir en la tabla Factura)
            stm.setInt(3, e.getCantidad());
            stm.setFloat(4, e.getDescuento());

            int count = stm.executeUpdate();
            if (count == 0) {
                throw new Exception("No se insertó ninguna línea en la base de datos.");
            }
        } catch (SQLException ex) {
            // revisar el error SQL específico para saber si fue por llaves foráneas
            if (ex.getSQLState().equals("23000")) {  // 23000 es el codigo para errores de integridad referencial
                throw new Exception("Error: Las llaves foraneas no coinciden con registros existentes.", ex);
            } else {
                throw new Exception("Error al insertar la linea", ex);
            }
        }
    }


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
                Linea linea = new Linea();
                linea.setNumero(rs.getInt("numero"));
                linea.setCantidad(rs.getInt("cantidad"));
                linea.setDescuento(rs.getFloat("descuento"));

                Producto producto = new Producto();
                producto.setId(rs.getString("codigo"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setUnidadMedida(rs.getString("unidadMedida"));
                producto.setPrecio(rs.getFloat("precioUnitario"));

                Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getString("c.id"));
                categoria.setNombreCategoria(rs.getString("c.nombre"));
                producto.setCategoria(categoria);

                linea.setProducto(producto);
                lineas.add(linea);
            }
        }
        return lineas;
    }

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
                Linea linea = new Linea();
                linea.setNumero(rs.getInt("numero"));
                linea.setCantidad(rs.getInt("cantidad"));
                linea.setDescuento(rs.getFloat("descuento"));

                Producto producto = new Producto();
                producto.setId(rs.getString("p.codigo"));
                producto.setDescripcion(rs.getString("p.descripcion"));
                producto.setUnidadMedida(rs.getString("p.unidadMedida"));
                producto.setPrecio(rs.getFloat("p.precioUnitario"));

                Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getString("c.id"));
                categoria.setNombreCategoria(rs.getString("c.nombre"));
                producto.setCategoria(categoria);

                linea.setProducto(producto);
                return linea;
            } else {
                throw new Exception("Línea NO EXISTE");
            }
        }
    }

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

    public void delete(Linea e) throws Exception {
        String sql = "DELETE FROM Linea WHERE numero=?";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setInt(1, e.getNumero());

            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Línea NO EXISTE");
            }
        }
    }

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
                Linea linea = new Linea();
                linea.setNumero(rs.getInt("numero"));
                linea.setCantidad(rs.getInt("cantidad"));
                linea.setDescuento(rs.getFloat("descuento"));

                Producto producto = new Producto();
                producto.setId(rs.getString("p.codigo"));
                producto.setDescripcion(rs.getString("p.descripcion"));
                producto.setUnidadMedida(rs.getString("p.unidadMedida"));
                producto.setPrecio(rs.getFloat("p.precioUnitario"));

                Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getString("c.id"));
                categoria.setNombreCategoria(rs.getString("c.nombre"));
                producto.setCategoria(categoria);

                linea.setProducto(producto);
                resultado.add(linea);
            }
        }
        return resultado;
    }

    public int obtenerSiguienteNumeroLinea() throws Exception {
        String sql = "SELECT COALESCE(MAX(numero), 0) + 1 AS siguiente_numero FROM Linea"; // para tener el numero siguiente de linea
        try (PreparedStatement stm = db.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("siguiente_numero");
            } else {
                throw new Exception("No se pudo obtener el siguiente número de línea");
            }
        }
    }

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

        e.setProducto(producto);
        return e;
    }
}
