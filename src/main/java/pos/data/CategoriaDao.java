package pos.data;

import pos.logic.Cajero;
import pos.logic.Categoria;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {
    Database db;

    public CategoriaDao() {
        db = Database.instance();
    }

    public List<Categoria> search(Categoria e) throws Exception {
        List<Categoria> resultado = new ArrayList<Categoria>();
        String sql = "select * " +
                "from " +
                "Categoria t " +
                "where t.nombre like ?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, "%" + e.getNombreCategoria() + "%");
        ResultSet rs = db.executeQuery(stm);
        while (rs.next()) {
            Categoria r= from(rs, "t");
            resultado.add(r);
        }
        return resultado;
    }

    public Categoria from(ResultSet rs, String alias) throws Exception {
        Categoria e = new Categoria();
        e.setIdCategoria(rs.getString(alias + ".id"));
        e.setNombreCategoria(rs.getString(alias + ".nombre"));
        return e;
    }
    public List<Categoria> obtenerTodasCategorias() throws Exception {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM Categoria";

        try (PreparedStatement stm = db.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(String.valueOf(rs.getInt("id"))); // Ajusta el nombre de la columna
                categoria.setNombreCategoria(rs.getString("nombre"));

                categorias.add(categoria); // Agregar la categoría a la lista
            }
        } catch (SQLException ex) {
            throw new Exception("Error al obtener todas las categorías", ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return categorias; // Devolver la lista de categorías
    }
}
