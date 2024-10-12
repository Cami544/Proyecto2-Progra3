package pos.data;

import pos.logic.Categoria;
import pos.logic.Producto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao {
    Database db;

    public ProductoDao() {
        db = Database.instance();
    }


    public void create(Producto e) throws Exception {
        String sql = "INSERT INTO Producto " +
                "(codigo, nombre, descripcion, unidadMedida, precioUnitario, existencias, categoria) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            // Validación de campos antes de insertar
            if (e.getId() == null || e.getNombre() == null || e.getCategoria() == null) {
                throw new Exception("Datos del producto incompletos.");
            }

            // Configuración de valores en el PreparedStatement
            stm.setString(1, e.getId());
            stm.setString(2, e.getNombre());
            stm.setString(3, e.getDescripcion());
            stm.setString(4, e.getUnidadMedida());
            stm.setDouble(5, e.getPrecio());
            stm.setInt(6, e.getExistencias());
            stm.setString(7, e.getCategoria().getIdCategoria());

            // Ejecutar la inserción
            stm.executeUpdate();
        } catch (SQLException ex) {
            throw new Exception("Error al crear el producto: " + ex.getMessage(), ex);
        }
    }


    public Producto read(String codigo) throws Exception {
        String sql = "select " +
                "* " +
                "from  Producto t " +
                "inner join Categoria c on t.categoria=c.id " +
                "where t.codigo=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, codigo);
        ResultSet rs = db.executeQuery(stm);
        CategoriaDao categoriaDao=new CategoriaDao();
        if (rs.next()) {
            Producto r = from(rs, "t");
            r.setCategoria(categoriaDao.from(rs, "c"));
           return r;
        } else {
            throw new Exception("Producto NO EXISTE");
        }
    }

    public void update(Producto e) throws Exception {
        String sql = "update " +
                "Producto " +
                "set nombre=?, descripcion=?, unidadMedida=?, precioUnitario=?, existencias=?, categoria=? " +
                "where codigo=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, e.getId());
        stm.setString(2, e.getNombre());
        stm.setString(3, e.getDescripcion());
        stm.setString(4, e.getUnidadMedida());
        stm.setDouble(5, e.getPrecio());
        stm.setInt(6, e.getExistencias());
        stm.setString(7, e.getCategoria().getIdCategoria()); //Revisar esta
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Producto NO EXISTE");
        }
    }

    public void delete(Producto e) throws Exception {
        String sql = "delete " +
                "from Producto " +
                "where codigo=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, e.getId());
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Producto NO EXISTE");
        }
    }

    public List<Producto> search(Producto e) throws Exception {
        List<Producto> resultado = new ArrayList<Producto>();
        String sql = "select * " +
                "from " +
                "Producto t " +
                "inner join Categoria c on t.categoria=c.id " +
                "where t.descripcion like ? order by t.descripcion";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, "%" + e.getDescripcion() + "%");
        ResultSet rs = db.executeQuery(stm);
        CategoriaDao categoriaDao=new CategoriaDao();
        while (rs.next()) {
            Producto r = from(rs, "t");
            r.setCategoria(categoriaDao.from(rs, "c"));
            resultado.add(r);
        }
        return resultado;
    }
    public List<Producto> ObtenerTodosProductos() throws Exception {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.id AS categoria FROM Producto p " +
                "JOIN Categoria c ON p.categoria = c.id"; // Asegúrate de que el nombre de la columna sea correcto

        try (PreparedStatement stm = db.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                Producto producto = from(rs, "p");
                productos.add(producto);
            }
        }
        return productos;
    }
    public Producto buscarProductoPorId(String codigo) throws Exception {
        String sql = "SELECT * FROM Producto t " +
                "INNER JOIN Categoria c ON t.categoria = c.id " +
                "WHERE t.codigo = ?";

        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, codigo);
        ResultSet rs = db.executeQuery(stm);

        CategoriaDao categoriaDao = new CategoriaDao();
        if (rs.next()) {
            Producto r = from(rs, "t");
            r.setCategoria(categoriaDao.from(rs, "c"));
            return r;
        } else {
            // Aqui se lanza una excepción si no se encuentra el producto
            throw new Exception("Producto NO EXISTE");
        }
    }

    public Producto from(ResultSet rs, String alias) throws Exception {
        Producto e = new Producto();
        e.setId(rs.getString(alias + ".codigo"));
        e.setNombre(rs.getString(alias + ".nombre"));
        e.setDescripcion(rs.getString(alias + ".descripcion"));
        e.setUnidadMedida(rs.getString(alias + ".unidadMedida"));
        e.setPrecio(rs.getFloat(alias + ".precioUnitario"));
        e.setExistencias(rs.getInt(alias + ".existencias"));

        // Crear un objeto Categoria y establecer su id
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getString(alias + ".categoria")); // Ajusta según el nombre de la columna en la tabla

        e.setCategoria(categoria);

        return e;
    }

}
