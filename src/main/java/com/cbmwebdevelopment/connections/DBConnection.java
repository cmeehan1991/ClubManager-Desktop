/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author cmeehan
 */
public class DBConnection {
    private Connection conn;
    private final String DRIVER = "mysql";
    private final String SERVER_NAME = "127.0.0.1";
    private final String USERNAME = "root";
    private final String PASSWORD = "root";
    private final String PORT_NUMBER = "3306";
    private final String DB_NAME = "CLUB_MANAGER";
    
    public Connection connect() throws SQLException{
        
        // Assign the connection properties
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", USERNAME);
        connectionProperties.put("password", PASSWORD);
        
        // Connect using the driver manager
        conn = DriverManager.getConnection("jdbc:" + DRIVER + "://" + SERVER_NAME + ":" + PORT_NUMBER + "/" + DB_NAME + "?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", connectionProperties);
        
        return conn;
    }
}
