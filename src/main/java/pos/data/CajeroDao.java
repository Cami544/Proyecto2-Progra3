package pos.data;

import pos.logic.Cajero;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CajeroDao {
    Database db;

    public CajeroDao() {
        db = Database.instance();
        if (db == null) {
            System.err.println("No se pudo establecer la conexión con la base de datos.");
        } else {
            System.out.println("Conexión con la base de datos establecida.");
        }
    }

    public void create(Cajero cajero) throws Exception {
        String sql = "INSERT INTO cajero (id, nombre) VALUES (?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            System.out.println("Creando cajero: " + stmt);
            // Validación de campos antes de insertar
            if (cajero.getId() == null || cajero.getNombre() == null){
                throw new Exception("Datos del cajero incompletos.");
            }

            stmt.setString(1, cajero.getId());
            stmt.setString(2, cajero.getNombre());
            db.executeUpdate(stmt);
        }
        catch (SQLException ex) {
            throw new Exception("Error al crear cajero: " + ex.getMessage(), ex);
        }

    }

    public Cajero read(String id) throws Exception {
        String sql = "SELECT * FROM cajero WHERE id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setString(1, id);
            System.out.println("Ejecutando SQL: " + stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Cajero encontrado: " + rs.getString("nombre"));
                    return from(rs);
                } else {
                    System.out.println("No se encontró el cajero con ID: " + id);
                    throw new Exception("Cajero NO EXISTE");
                }
            }
        }
    }

    public void update(Cajero e) throws Exception {
        String sql = "UPDATE cajero SET nombre=? WHERE id=?";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, e.getNombre());
            stm.setString(2, e.getId());

            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Cajero NO EXISTE");
            }
        }
    }

    public void delete(Cajero e) throws Exception {
        String sql = "DELETE FROM cajero WHERE id=?";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, e.getId());
            int count = db.executeUpdate(stm);
            if (count == 0) {
                throw new Exception("Cajero NO EXISTE");
            }
        }
    }

    public List<Cajero> search(Cajero e) throws Exception {
        List<Cajero> resultado = new ArrayList<>();
        String sql = "SELECT * FROM cajero WHERE nombre LIKE ? ORDER BY nombre"; // Cambiado a nombre
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, "%" + e.getNombre() + "%");
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Cajero c = from(rs);
                    resultado.add(c);
                }
            }
        }
        return resultado;
    }


    public List<Cajero> obtenerTodosCajeros() throws Exception {
        List<Cajero> cajeros = new ArrayList<>();
        String sql = "SELECT * FROM cajero";
        try (PreparedStatement stm = db.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                cajeros.add(from(rs));
            }
            if (cajeros.isEmpty()) {
                System.out.println("No se encontraron cajeros.");
            }
        } catch (SQLException ex) {
            System.err.println("Error al obtener cajeros: " + ex.getMessage());
            throw new Exception("Error al obtener cajeros", ex);
        }
        return cajeros;
    }


    public Cajero from(ResultSet rs) throws Exception {
        Cajero c = new Cajero();
        c.setId(rs.getString("id"));
        c.setNombre(rs.getString("nombre"));
        return c;
    }
}
