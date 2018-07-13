/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.user;

import com.cbmwebdevelopment.connections.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author cmeehan
 */
public class User {
    
    /**
     * Sign the user in. 
     * If the user exists then we are going to add the user id to the system 
     * properties for internal usage then we are going to return the boolean 
     * value true. If the user does not exist we are returning false.     * 
     * 
     * @param username
     * @param password
     * @return 
     */
    public boolean signIn(String username, String password){
        boolean result = false;
        String sql = "SELECT ID FROM EMPLOYEES WHERE USERNAME = ? AND PASSWORD = MD5(?)";
        try{
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                result = true;
                System.getProperties().put("USER_ID", rs.getString("ID"));
            }
            conn.close();
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return result;
    }
    
    public String getUsername(String id){
        String username = null;
        String sql = "SELECT USERNAME FROM EMPLOYEES WHERE ID = ?";
        try{
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                username = rs.getString("USERNAME");
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return username;
    }
}
