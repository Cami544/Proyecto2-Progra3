package pos.data;

import pos.logic.Cliente;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClienteDao {
    Database db = new Database();

    public ClienteDao() {
        db = new Database();
    }

    public void create(Cliente cliente) throws Exception {
        String sql = "insert into " +
                "Cliente " +
                "(id, nombre, telefono, email, descuento) " +
                "values (?,?,?,?,?) ";
        PreparedStatement stmt = db.prepareStatement(sql);
        stmt.setString(1, cliente.getId());
        stmt.setString(2, cliente.getNombre());
        stmt.setString(3, cliente.getTelefono());
        stmt.setString(4, cliente.getEmail());
        stmt.setDouble(5, cliente.getDescuento());
        db.executeUpdate(stmt);
    }

    public Cliente read(String id) throws Exception {
        String sql = "select * from Cliente where id = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = db.prepareStatement(sql);
            stmt.setString(1, id); // Aquí se pasa el parámetro 'id' como String
            rs = stmt.executeQuery(); // Ejecutar la consulta

            if (rs.next()) {
                // Llamar al metodo 'from' para construir el cliente desde el ResultSet
                return from(rs);
            } else {
                throw new Exception("Cliente NO EXISTE");
            }
        } finally {
            // Asegurarse de cerrar ResultSet y PreparedStatement
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void update(Cliente e) throws Exception {
        String sql = "update " +
                "Cliente " +
                "set id=?, nombre=?, telefono=?, email=? , descuento =?" +
                "where id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, e.getId());
        stm.setString(2, e.getNombre());
        stm.setString(3, e.getTelefono());
        stm.setString(4, e.getEmail());
        stm.setFloat(5, e.getDescuento());
        stm.setString(6, e.getId());
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Cliente NO EXISTE");
        }

    }

    public void delete(Cliente e) throws Exception {
        String sql = "delete " +
                "from Cliente " +
                "where id=?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, e.getId());
        int count = db.executeUpdate(stm);
        if (count == 0) {
            throw new Exception("Cliente NO EXISTE");
        }
    }

    public List<Cliente> search(Cliente e) throws Exception {
        List<Cliente> resultado = new ArrayList<Cliente>();
        String sql = "select * from Cliente where id = ? order by id";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, e.getId()); // Busqueda exacta por el ID del cliente (que es un String)
        ResultSet rs = stm.executeQuery(); // Ejecutar la consulta

        while (rs.next()) {
            Cliente c = from(rs); // Construye el objeto Cliente desde el ResultSet
            resultado.add(c); // Añade el cliente a la lista de resultados
        }

        return resultado;
    }

    public List<Cliente> obtenerTodosClientes() throws Exception {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente";

        // Usar try-with-resources para asegurar el cierre de los recursos
        try (PreparedStatement stm = db.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) { // Ejecutar la consulta
            while (rs.next()) {
                Cliente c = from(rs); // Convertir el ResultSet en un objeto Cliente
                clientes.add(c); // Agregar el Cliente a la lista
            }
        } // El PreparedStatement y el ResultSet se cierran automáticamente aquí

        return clientes; // Retornar la lista de Clientes
    }

    public Cliente from(ResultSet rs) throws Exception {
        Cliente c = new Cliente();
        c.setId(rs.getString("id")); // Asigna el id del cliente
        c.setNombre(rs.getString("nombre")); // Asigna el nombre del cliente
        c.setTelefono(rs.getString("telefono")); // Asigna el teléfono del cliente
        c.setEmail(rs.getString("email")); // Asigna el email del cliente
        c.setDescuento(rs.getFloat("descuento")); // Asigna el descuento del cliente
        return c;
    }
}
