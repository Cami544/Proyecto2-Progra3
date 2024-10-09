package pos.data;

import pos.logic.Cajero;
import pos.logic.Categoria;
import pos.logic.Producto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao {
    Database db;

    public ProductoDao() {
        db = Database.instance();
    }

    public void create(Producto e) throws Exception {
        String sql = "insert into " +
                "Producto " +
                "(codigo ,descripcion, unidadMedida,precioUnitario,categoria) " +
                "values(?,?,?,?,?)";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, e.getId());
        stm.setString(2, e.getDescripcion());
        stm.setString(3, e.getUnidadMedida());
        stm.setDouble(4, e.getPrecio());
        stm.setString(5, e.getCategoria().getIdCategoria());
        db.executeUpdate(stm);
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
                "set descripcion=?, unidadMedida=?, precioUnitario=?, categoria=? " +
                "where codigo=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, e.getDescripcion());
        stm.setString(2, e.getUnidadMedida());
        stm.setDouble(3, e.getPrecio());
        stm.setString(4, e.getCategoria().getIdCategoria());
        stm.setString(5, e.getId());
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
    public List<Producto> getAllProductos() throws Exception {
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


    public Producto from(ResultSet rs, String alias) throws Exception {
        Producto e = new Producto();
        e.setId(rs.getString(alias + ".codigo"));
        e.setDescripcion(rs.getString(alias + ".descripcion"));
        e.setUnidadMedida(rs.getString(alias + ".unidadMedida"));
        e.setPrecio(rs.getFloat(alias + ".precioUnitario"));
        // Crear un objeto Categoria y establecer su id
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getString(alias + ".categoria")); // Ajusta según el nombre de la columna en la tabla

        e.setCategoria(categoria);

        return e;
    }

}
