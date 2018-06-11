/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.pool;

import com.cbmwebdevelopment.connections.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author cmeehan
 */
public class PoolData {

    public HashMap<String, Double> getOccupancy() {
        HashMap<String, Double> results = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(new Date());
        String sql = "SELECT COUNT(ID) AS 'TOTAL', DATE_FORMAT(TIME_ON, '%l %p') AS 'TIME' FROM POOL_OCCUPANCY WHERE TIME_ON = ? GROUP BY TIME_ON ORDER BY TIME_ON ASC;";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    String time = rs.getString("TIME");
                    Double count = rs.getDouble("TOTAL");
                    results.put(time, count);

                } while (rs.next());
            }
        } catch (SQLException ex) {
            System.out.println("Error getting occupancy: " + ex.getMessage());
        }
        return results;
    }

    public HashMap<String, Double> getChlorine(Date date) {
        HashMap<String, Double> results = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(date);
        String sql = "SELECT PH, DATE_FORMAT(TIME_ON, '%l:%i %p') AS 'TIME' FROM POOL_OCCUPANCY WHERE TIME_ON = ? GROUP BY TIME_ON ORDER BY TIME_ON ASC;";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    String time = rs.getString("TIME");
                    Double count = rs.getDouble("PH");
                    results.put(time, count);

                } while (rs.next());
            }
        } catch (SQLException ex) {
            System.out.println("Error getting occupancy: " + ex.getMessage());
        }
        return results;
    }

    public HashMap<String, Double> getPH(Date date) {
        HashMap<String, Double> results = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(date);
        String sql = "SELECT CHLORINE, DATE_FORMAT(TIME_ON, '%l:%i %p') AS 'TIME' FROM POOL_OCCUPANCY WHERE TIME_ON = ? GROUP BY TIME_ON ORDER BY TIME_ON ASC;";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    String time = rs.getString("TIME");
                    Double count = rs.getDouble("PH");
                    results.put(time, count);

                } while (rs.next());
            }
        } catch (SQLException ex) {
            System.out.println("Error getting occupancy: " + ex.getMessage());
        }
        return results;
    }
}
