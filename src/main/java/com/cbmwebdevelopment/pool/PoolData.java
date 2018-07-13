/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.pool;

import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.tablecontrollers.ChemicalDataTableController.Chemicals;
import com.cbmwebdevelopment.tablecontrollers.PoolOccupancyTableController.PoolOccupancy;
import com.mysql.cj.api.jdbc.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author cmeehan
 */
public class PoolData {

    /**
     * Checks a member or guest into the pool. Returns a boolean based on
     * whether the check in was successful or not.
     *
     * @param membershipId
     * @param memberId
     * @param guestId
     * @param date
     * @return
     */
    public boolean checkMemberIn(String membershipId, String memberId, String guestId, String date) {
        boolean success = false;
        String sql = "INSERT INTO POOL_OCCUPANCY(MEMBERSHIP_ID, MEMBER_ID, GUEST_ID, TIME_IN) VALUES(?,?,?,?)";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, membershipId);
            ps.setString(2, memberId);
            ps.setString(3, guestId);
            ps.setString(4, date);
            int rs = ps.executeUpdate();
            if (rs > 0) {
                success = true;
            }
            System.out.println("Success: " + success);
        } catch (SQLException ex) {
            System.out.println("Error checking member in: " + ex.getMessage());
        }
        return success;
    }

    /**
     * Check the member or guest out. returns a boolean value based on the
     * success of the update.
     *
     * @param membershipId
     * @param memberId
     * @param guestId
     * @param dateIn
     * @param dateOut
     * @return
     */
    public boolean checkMemberOut(String id, String dateOut) {
        System.out.println("checkMemberOut");
        boolean success = false;
        String sql = "UPDATE POOL_OCCUPANCY SET TIME_OUT = ? WHERE ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, dateOut);
            ps.setString(2, id);
            int rs = ps.executeUpdate();
            System.out.println(rs);
            if (rs > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.out.println("Error checking member out: " + ex.getMessage());
        }
        return success;
    }

    /**
     * Get an observable list of the pool occupants based on the date and any
     * search terms.
     *
     * @param terms
     * @param date
     * @return
     */
    public ObservableList<PoolOccupancy> getOccupants(String terms, String date) {
        ObservableList<PoolOccupancy> data = FXCollections.observableArrayList();

        String sql = "SELECT POOL_OCCUPANCY.ID, POOL_OCCUPANCY.MEMBERSHIP_ID, POOL_OCCUPANCY.MEMBER_ID AS 'MEMBER_ID', POOL_OCCUPANCY.GUEST_ID, DATE_FORMAT(POOL_OCCUPANCY.TIME_IN, '%l:%i %p') AS 'TIME_IN' , DATE_FORMAT(POOL_OCCUPANCY.TIME_OUT, '%l:%i %p') AS 'TIME_OUT', MEMBER_INFORMATION.SURNAME, MEMBER_INFORMATION.NAME, GUEST_INFORMATION.SURNAME, GUEST_INFORMATION.NAME, IF(POOL_OCCUPANCY.GUEST_ID != NULL, true, false) AS 'IS_GUEST' "
                + "FROM POOL_OCCUPANCY "
                + "INNER JOIN MEMBER_INFORMATION ON POOL_OCCUPANCY.MEMBER_ID = MEMBER_INFORMATION.ID "
                + "LEFT JOIN GUEST_INFORMATION ON POOL_OCCUPANCY.GUEST_ID = GUEST_INFORMATION.ID "
                + "WHERE DATE_FORMAT(TIME_IN, '%Y-%m-%d') = DATE_FORMAT(?, '%Y-%m-%d')";

        if (terms != null) {
            sql += " " + "AND (MEMBER_INFORMATION.NAME = ? OR MEMBER_INFORMATION.SURNAME = ? OR MEMBER_INFORMATION.ID = ? OR MEMBER_INFORMATION.MEMBERSHIP_ID = ? OR MEMBER_INFORMATION.NAME LIKE ? OR MEMBER_INFORMATION.SURNAME LIKE ? OR MEMBER_INFORMATION.ID LIKE ? OR MEMBER_INFORMATION.MEMBERSHIP_ID LIKE ? OR GUEST_INFORMATION.NAME = ? OR GUEST_INFORMATION.SURNAME = ? OR GUEST_INFORMATION.ID = ? OR GUEST_INFORMATION.NAME LIKE ? OR GUEST_INFORMATION.SURNAME LIKE ? OR GUEST_INFORMATION.ID LIKE ?)";
        }

        sql += " " + "ORDER BY DATE_FORMAT(POOL_OCCUPANCY.TIME_IN, '%r') DESC";

        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, date);
            if (terms != null) {
                ps.setString(2, terms);
                ps.setString(3, terms);
                ps.setString(4, terms);
                ps.setString(5, terms);
                ps.setString(6, "%" + terms + "%");
                ps.setString(7, "%" + terms + "%");
                ps.setString(8, "%" + terms + "%");
                ps.setString(9, "%" + terms + "%");
                ps.setString(10, terms);
                ps.setString(11, terms);
                ps.setString(12, terms);
                ps.setString(13, "%" + terms + "%");
                ps.setString(14, "%" + terms + "%");
                ps.setString(15, "%" + terms + "%");
            }
            System.out.println(ps);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    Integer id = rs.getInt("ID");
                    Integer memberId = rs.getInt("MEMBER_ID");
                    String surname = rs.getString("SURNAME");
                    String name = rs.getString("NAME");
                    Integer membershipId = rs.getInt("MEMBERSHIP_ID");
                    String timeIn = rs.getString("TIME_IN");
                    String timeOut = rs.getString("TIME_OUT");
                    Boolean isGuest = rs.getBoolean("IS_GUEST");
                    data.add(new PoolOccupancy(id, memberId, surname, name, membershipId, timeIn, timeOut, isGuest));
                } while (rs.next());
            }
        } catch (SQLException ex) {
            System.out.println("Error getting data: " + ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
        return data;
    }

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
        /* String sql = "SELECT PH, DATE_FORMAT(TIME_ON, '%l:%i %p') AS 'TIME' FROM POOL_OCCUPANCY WHERE TIME_ON = ? GROUP BY TIME_ON ORDER BY TIME_ON ASC;";
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
            System.out.println("Error getting Chlroine: " + ex.getMessage());
        }*/
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

    public ObservableList<Chemicals> getPoolChemicalData(String date, String facility) {
        ObservableList<Chemicals> data = FXCollections.observableArrayList();
        String sql = "SELECT ID, CHLORINE, PH, ALKALINITY, CALCIUM, WATER_TEMP, AIR_TEMP, BATHER_LOAD, WEATHER, DATE_FORMAT(DATE_CHECKED, '%h:%i') as 'DATE_CHECKED' FROM POOL_CHEMICALS WHERE DATE_FORMAT(DATE_CHECKED, '%Y-%m-%d') = ? AND FACILITY = ?";

        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, date);
            ps.setString(2, facility);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    int id = rs.getInt("ID");
                    String time = rs.getString("DATE_CHECKED");
                    double chlorine = rs.getDouble("CHLORINE");
                    double ph = rs.getDouble("PH");
                    double calcium = rs.getDouble("CALCIUM");
                    double airTemp = rs.getDouble("AIR_TEMP");
                    double waterTemp = rs.getDouble("WATER_TEMP");
                    String weather = rs.getString("WEATHER");
                    double batherLoad = rs.getDouble("BATHER_LOAD");
                    data.add(new Chemicals(id, time, chlorine, ph, calcium, airTemp, waterTemp, weather, batherLoad));
                } while (rs.next());
            }
        } catch (SQLException ex) {

        }
        return data;
    }

    /**
     * Get the individual entry
     *
     * @param id
     * @return
     */
    public HashMap<String, String> getEntry(String id) {
        HashMap<String, String> data = new HashMap<>();
        String sql = "SELECT CHLORINE, PH, ALKALINITY, CALCIUM, WATER_TEMP, AIR_TEMP, BATHER_LOAD, WEATHER, DATE_FORMAT(DATE_CHECKED, '%h:%i') as 'TIME_CHECKED', NOTES FROM POOL_CHEMICALS WHERE ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                data.put("CHLORINE", rs.getString("CHLORINE"));
                data.put("PH", rs.getString("PH"));
                data.put("ALKALINITY", rs.getString("ALKALINITY"));
                data.put("CALCIUM", rs.getString("CALCIUM"));
                data.put("WATER_TEMP", rs.getString("WATER_TEMP"));
                data.put("AIR_TEMP", rs.getString("AIR_TEMP"));
                data.put("BATHER_LOAD", rs.getString("BATHER_LOAD"));
                data.put("WEATHER", rs.getString("WEATHER"));
                data.put("TIME_CHECKED", rs.getString("TIME_CHECKED"));
                data.put("NOTES", rs.getString("NOTES"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return data;
    }

    /**
     * Add a new entry and return the generated key as the unique ID
     * @param dateTimeChecked
     * @param time
     * @param chlorine
     * @param ph
     * @param calcium
     * @param alkalinity
     * @param airTemp
     * @param waterTemp
     * @param weather
     * @param batherLoad
     * @param notes
     * @param facility
     * @return 
     */
    public String addNewEntry(String dateTimeChecked, String chlorine, String ph, String calcium, String alkalinity, String airTemp, String waterTemp, String weather, String batherLoad, String notes, String facility) {
        String id = null;
        String sql = "INSERT INTO POOL_CHEMICALS(CHLORINE, PH, ALKALINITY, CALCIUM, WATER_TEMP, AIR_TEMP, BATHER_LOAD, WEATHER, DATE_CHECKED, NOTES, FACILITY) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, chlorine);
            ps.setString(2, ph);
            ps.setString(3, alkalinity);
            ps.setString(4, calcium);
            ps.setString(5, waterTemp);
            ps.setString(6, airTemp);
            ps.setString(7, batherLoad);
            ps.setString(8, weather);
            ps.setString(9, dateTimeChecked);
            ps.setString(10, notes);
            ps.setString(11, facility);
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            id = String.valueOf(rs.getInt(1));
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return id;
    }

    /**
     * Update an existing entry
     *
     * @param id
     * @param time
     * @param chlorine
     * @param ph
     * @param calcium
     * @param alkalinity
     * @param airTemp
     * @param waterTemp
     * @param weather
     * @param batherLoad
     * @param notes
     * @param facility
     * @return
     */
    public boolean updateEntry(String id, String time, String chlorine, String ph, String calcium, String alkalinity, String airTemp, String waterTemp, String weather, String batherLoad, String notes, String facility) {
        boolean updated = false;
        String sql = "UPDATE POOL_CHEMICALS SET CHLORINE = ?, PH = ?, ALKALINITY = ?, CALCIUM = ?, WATER_TEMP = ?, AIR_TEMP = ?, BATHER_LOAD = ?, WEATHER = ?, DATE_CHECKED = ?, NOTES = ?, FACILITY = ? WHERE ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, time);
            ps.setString(2, chlorine);
            ps.setString(3, ph);
            ps.setString(4, calcium);
            ps.setString(5, alkalinity);
            ps.setString(6, airTemp);
            ps.setString(7, waterTemp);
            ps.setString(8, weather);
            ps.setString(9, batherLoad);
            ps.setString(10, notes);
            ps.setString(11, facility);
            ps.setString(12, id);
            int rs = ps.executeUpdate();
            updated = rs >= 1;
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return updated;
    }
}
