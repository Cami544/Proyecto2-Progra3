package pos.data;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class Database {
    private static Database theInstance;

    public static Database instance() {
        if (theInstance == null) {
            theInstance = new Database();
        }
        return theInstance;
    }

    private static final String PROPERTIES_FILE_NAME = "/database.properties";

    Connection cnx;

    public Database() {
        getConnection();
    }

    public void getConnection() {
        try {
            String driver = "com.mysql.cj.jdbc.Driver"; // Asegúrate de que este es el driver correcto
            String server = "localhost"; // Cambia esto si tu servidor no está en localhost
            String port = "3306"; // Cambia esto si tu puerto no es el 3306
            String user = "root";
            String password = "root";
            String database = "Pos"; // Cambia esto al nombre de tu base de datos

            String URL_conexion = "jdbc:mysql://" + server + ":" + port + "/" +
                    database + "?user=" + user + "&password=" + password + "&serverTimezone=UTC";
            Class.forName(driver).newInstance();
            cnx = DriverManager.getConnection(URL_conexion);
        } catch (Exception e) {
            System.err.println("FALLÓ CONEXION A BASE DE DATOS");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }





//    public void getConnection() {
//        try {
//            Properties prop = new Properties();
//            prop.load(getClass().getResourceAsStream(PROPERTIES_FILE_NAME));
//            String driver = prop.getProperty("database_driver");
//            String server = prop.getProperty("database_server");
//            String port = prop.getProperty("database_port");
//            String user = prop.getProperty("database_user");
//            String password = prop.getProperty("database_password");
//            String database = prop.getProperty("database_name");
//
//            String URL_conexion = "jdbc:mysql://" + server + ":" + port + "/" +
//                    database + "?user=" + user + "&password=" + password + "&serverTimezone=UTC";
//            Class.forName(driver).newInstance();
//            cnx = DriverManager.getConnection(URL_conexion);
//        } catch (Exception e) {
//            System.err.println("FALLÓ CONEXION A BASE DE DATOS");
//            System.err.println(e.getMessage());
//            e.printStackTrace();
//            System.exit(-1);
//        }
//    }

    public PreparedStatement prepareStatement(String statement) throws Exception {
        try {
            return cnx.prepareStatement(statement,Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            throw new Exception("ERROR DE BASE DE DATOS Prepared Statement");
        }
    }

    public int executeUpdate(PreparedStatement statement) throws Exception {
        try {
            statement.executeUpdate();
            return statement.getUpdateCount();
        } catch (SQLIntegrityConstraintViolationException ex) {
            throw new Exception("REGISTRO DUPLICADO o REFERENCIA NO EXISTE");
        } catch (Exception ex) {
            throw new Exception("ERROR DE BASE DE DATOS Update");
        }
    }

    public int executeUpdateWithKeys(PreparedStatement statement) throws Exception {
        try {
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        } catch (SQLIntegrityConstraintViolationException ex) {
            throw new Exception("REGISTRO DUPLICADO o REFERENCIA NO EXISTE");
        } catch (Exception ex) {
            throw new Exception("ERROR DE BASE DE DATOS Update with Keys");
        }
    }

    public ResultSet executeQuery(PreparedStatement statement) throws Exception {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new Exception("ERROR DE BASE DE DATOS Query");
        }
    }
}
