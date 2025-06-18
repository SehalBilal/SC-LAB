package model;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Properties properties = new Properties();
    
    static {
        try {
            // Load database properties
            FileInputStream input = new FileInputStream("database.properties");
            properties.load(input);
            input.close();
            
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        
        return DriverManager.getConnection(url, username, password);
    }
} 