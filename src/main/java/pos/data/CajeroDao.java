package pos.data;

import pos.logic.Cajero;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CajeroDao {
    Database db = new Database();

    public CajeroDao() {
        db = new Database();
    }

    public void create(Cajero cajero) throws Exception {
        String sql = "INSERT INTO Cajero (id, nombre) VALUES (?, ?)";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setString(1, cajero.getId());
            stmt.setString(2, cajero.getNombre());
            db.executeUpdate(stmt);
        }
    }

    public Cajero read(String id) throws Exception {
        String sql = "SELECT * FROM Cajero WHERE id = ?";
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return from(rs);
                } else {
                    throw new Exception("Cajero NO EXISTE");
                }
            }
        }
    }

    public void update(Cajero e) throws Exception {
        String sql = "UPDATE Cajero SET nombre=? WHERE id=?";
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
        String sql = "DELETE FROM Cajero WHERE id=?";
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
        String sql = "SELECT * FROM Cajero WHERE id = ? ORDER BY id";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, e.getId());
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Cajero c = from(rs);
                    resultado.add(c);
                }
            }
        }
        return resultado;
    }

    //hay que hacer esto para cada clase DAO MENOS categorias
    public List<Cajero> getAllCajeros() throws Exception {
        List<Cajero> cajeros = new ArrayList<>();
        String sql = "SELECT * FROM Cajero"; // Consulta SQL para seleccionar todos los cajeros
        try (PreparedStatement stm = db.prepareStatement(sql)) { // Prepara la declaración SQL
            try (ResultSet rs = stm.executeQuery()) { // Ejecuta la consulta y obtiene el resultado
                while (rs.next()) { // Itera a través de los resultados
                    cajeros.add(from(rs)); // Convierte el resultado en un objeto Cajero y lo añade a la lista
                }
            }
        }
        return cajeros; // Devuelve la lista de cajeros
    }

    public Cajero from(ResultSet rs) throws Exception {
        Cajero c = new Cajero();
        c.setId(rs.getString("id"));
        c.setNombre(rs.getString("nombre"));
        return c;
    }
}
