/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tennis;

import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.tablecontrollers.DashboardCourtTableController.CourtOccupancy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author cmeehan
 */
public class TennisData {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    public ObservableList<CourtOccupancy> getCourtOccupancy(Date date, String facility){
        ObservableList<CourtOccupancy> data = FXCollections.observableArrayList();
        String sql = "SELECT COURT_NUMBER, IF(TENNIS_OCCUPANCY = NULL, CONCAT(MEMBER_INFORMATION.SURNAME, ', ', MEMBER_INFORMATION.NAME),  CONCAT(GUEST_INFORMATION.SURNAME, ', ', GUEST_INFORMATION.NAME))AS 'NAME', DATE_FORMAT(TIME_ON, '%l:%i %p') AS 'TIME' FROM TENNIS_OCCUPANCY INNER JOIN MEMBER_INFORMATION ON TENNIS_OCCUPANCY.MEMBERSHIP_ID = MEMBER_INFORMATION.MEMBERSHIP_ID AND TENNIS_OCCUPANCY.MEMBER_ID = MEMBER_INFORMATION.ID JOIN GUEST_INFORMATION ON TENNIS_OCCUPANCY.GUEST_ID = GUEST_INFORMATION.ID WHERE TIME_ON = ? AND FACILITY = ? GROUP BY COURT_NUMBER ORDER BY COURT_NUMBER ASC";
        try{
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, format.format(date));
            ps.setString(2, facility);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                do{
                    Integer courtNumber = rs.getInt("COURT_NUMBER");
                    String name = rs.getString("NAME");
                    String time = rs.getString("TIME");
                    data.add(new CourtOccupancy(courtNumber, name, time));
                }while(rs.next());
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return data;
    }
    
    public HashMap<String, Double> getOccupancy(String facility){
        HashMap<String, Double> results = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(new Date());
        String sql = "SELECT COUNT(ID) AS 'TOTAL', DATE_FORMAT(TIME_ON, '%l %p') AS 'TIME' FROM TENNIS_OCCUPANCY WHERE LOCATION = ? AND TIME_ON = ? GROUP BY TIME_ON ORDER BY TIME_ON ASC;";
        try{
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, facility);
            ps.setString(2, today);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                do{
                    String time = rs.getString("TIME");
                    Double count = rs.getDouble("TOTAL");
                    results.put(time, count);
                    
                }while(rs.next());
            }
            ps.close();
            conn.close();
        }catch(SQLException ex){
            System.out.println("Error getting occupancy: " + ex.getMessage());
        }
        return results;
    }
}
