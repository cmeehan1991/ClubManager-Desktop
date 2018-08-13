/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.pool;

import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.connections.Links;
import com.cbmwebdevelopment.tablecontrollers.ChemicalDataTableController.Chemicals;
import com.cbmwebdevelopment.tablecontrollers.PoolOccupancyTableController.PoolOccupancy;
import com.mysql.cj.api.jdbc.Statement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cmeehan
 */
public class PoolData implements Links {

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
        try {
            URL url = new URL(POOL_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("checkMemberIn", "UTF-8");
            data += "&" + URLEncoder.encode("membership_id", "UTF-8") + "=" + URLEncoder.encode(membershipId, "UTF-8");
            data += "&" + URLEncoder.encode("member_id", "UTF-8") + "=" + URLEncoder.encode(memberId, "UTF-8");
            data += "&" + URLEncoder.encode("guest_id", "UTF-8") + "=" + URLEncoder.encode(guestId, "UTF-8");
            data += "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            reader.close();

            JSONObject jsonObj = new JSONObject(sb.toString());
            success = jsonObj.getBoolean("SUCCESS");

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return success;
    }

    /**
     * Check the member or guest out. returns a boolean value based on the
     * success of the update.
     *
     * @param id
     * @param dateOut
     * @return
     */
    public boolean checkMemberOut(String id, String dateOut) {
        boolean success = false;
        String sql = "UPDATE POOL_OCCUPANCY SET TIME_OUT = ? WHERE ID = ?";
        try {
            URL url = new URL(POOL_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", ENCODING) + "=" + URLEncoder.encode("check_member_out", ENCODING);
            data += "&" + URLEncoder.encode("id", ENCODING) + "=" + URLEncoder.encode(id, ENCODING);
            data += "&" + URLEncoder.encode("out", ENCODING) + "=" + URLEncoder.encode(dateOut, ENCODING);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();

            JSONObject jsonObj = new JSONObject(line);
            success = jsonObj.getBoolean("success");

            writer.close();
            reader.close();

        } catch (IOException ex) {
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

        try {
            URL url = new URL(POOL_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String urlData = URLEncoder.encode("action", ENCODING) + "=" + URLEncoder.encode("get_occupants", ENCODING);
            urlData += "&" + URLEncoder.encode("terms", ENCODING) + "=" + URLEncoder.encode(terms, ENCODING);
            urlData += "&" + URLEncoder.encode("date", ENCODING) + "=" + URLEncoder.encode(date, ENCODING);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(urlData);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONArray jsonArr = new JSONArray(reader.readLine());

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);

                Integer id = Integer.parseInt((String) jsonObj.get("ID"));
                Integer memberId = Integer.parseInt((String) jsonObj.get("MEMBER_ID"));
                String surname = String.valueOf(jsonObj.get("SURNAME"));

                String name = String.valueOf(jsonObj.get("NAME"));
                Integer membershipId = Integer.parseInt(jsonObj.getString("MEMBERSHIP_ID"));
                String timeIn = String.valueOf(jsonObj.get("TIME_IN"));
                String timeOut = String.valueOf(jsonObj.get("TIME_OUT"));
                Boolean isGuest = Boolean.parseBoolean((String) jsonObj.get("IS_GUEST"));

                data.add(new PoolOccupancy(id, memberId, surname, name, membershipId, timeIn, timeOut, isGuest));
            }
        } catch (IOException ex) {
            System.out.println("Error getting data: " + ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }

        return data;
    }

    public HashMap<String, Double> getOccupancy() {
        HashMap<String, Double> results = new HashMap<>();
        try {
            URL url = new URL(POOL_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String urlData = URLEncoder.encode("action", ENCODING) + "=" + URLEncoder.encode("get_occupancy");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(urlData);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONArray jsonArr = new JSONArray(reader.readLine());
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                results.put(jsonObj.getString("TIME"), jsonObj.getDouble("TOTAL"));
            }
        } catch (IOException ex) {
            System.out.println("Error getting occupancy: " + ex.getMessage());
        }
        return results;
    }

    public HashMap<String, Double> getChlorine(Date date) {
        HashMap<String, Double> results = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(date);

        try {
            URL url = new URL(POOL_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", ENCODING) + "=" + URLEncoder.encode("get_chlorine", ENCODING);
            data += "&" + URLEncoder.encode("date", ENCODING) + "=" + URLEncoder.encode(today, ENCODING);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            JSONArray jsonArr = new JSONArray(reader.readLine());
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                results.put(jsonObject.getString("TIME"), jsonObject.getDouble("CHLORINE"));
            }

            writer.close();
            reader.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return results;
    }

    public HashMap<String, Double> getPH(Date date) {
        HashMap<String, Double> results = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(date);

        try {
            URL url = new URL(POOL_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", ENCODING) + "=" + URLEncoder.encode("get_ph", ENCODING);
            data += "&" + URLEncoder.encode("date", ENCODING) + "=" + URLEncoder.encode(today, ENCODING);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            JSONArray jsonArr = new JSONArray(reader.readLine());
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                results.put(jsonObject.getString("TIME"), jsonObject.getDouble("PH"));
            }

            writer.close();
            reader.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return results;
    }

    /**
     * Get the pool chemicals to populate the pool chemical data table Based on
     * date and facility selected
     *
     * @param date
     * @param facility
     * @return
     */
    public ObservableList<Chemicals> getPoolChemicalData(String date, String facility) {
        ObservableList<Chemicals> results = FXCollections.observableArrayList();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(date);

        try {
            URL url = new URL(POOL_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", ENCODING) + "=" + URLEncoder.encode("get_ph", ENCODING);
            data += "&" + URLEncoder.encode("date", ENCODING) + "=" + URLEncoder.encode(today, ENCODING);
            data += "&" + URLEncoder.encode("facility", ENCODING) + "=" + URLEncoder.encode(facility, ENCODING);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            JSONArray jsonArr = new JSONArray(reader.readLine());
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObject = jsonArr.getJSONObject(i);

                //int id, String time, double chlorine, double ph, double calcium, double airTemp, double waterTemp, String weather, double batherLoad
                results.add(new Chemicals(jsonObject.getInt("ID"), jsonObject.getString("DATE_CHECKED"), jsonObject.getDouble("CHLORINE"), jsonObject.getDouble("PH"), jsonObject.getDouble("CALCIUM"), jsonObject.getDouble("ALKALINITY"), jsonObject.getDouble("WATER_TEMP"), jsonObject.getDouble("AIR_TEMP"), jsonObject.getString("WEATHER"), jsonObject.getDouble("BATHER_LOAD")));

            }
            writer.close();
            reader.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return results;
    }

    /**
     * Delete the entry from the pool chemicals table
     *
     * @param id
     * @return
     */
    public boolean removeEntry(String id) {
        boolean removed = false;
        try {
            URL url = new URL(POOL_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", ENCODING) + "=" + URLEncoder.encode("remove_entry", ENCODING);
            data += "&" + URLEncoder.encode("id", ENCODING) + "=" + URLEncoder.encode(id, ENCODING);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            JSONObject jsonObj = new JSONObject(reader.readLine());

            removed = jsonObj.getBoolean("SUCCESS");
            writer.close();
            reader.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return removed;
    }

    /**
     * Get the individual entry
     *
     * @param id
     * @return
     */
    public HashMap<String, String> getEntry(String id) {
        HashMap<String, String> results = new HashMap<>();

        try {
            URL url = new URL(POOL_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", ENCODING) + "=" + URLEncoder.encode("get_entry", ENCODING);
            data += "&" + URLEncoder.encode("id", ENCODING) + "=" + URLEncoder.encode(id, ENCODING);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject jsonObj = new JSONObject(reader.readLine());

            results.put("CHLORINE", jsonObj.getString("CHLORINE"));
            results.put("PH", jsonObj.getString("PH"));
            results.put("ALKALINITY", jsonObj.getString("ALKALINITY"));
            results.put("CALCIUM", jsonObj.getString("CALCIUM"));
            results.put("WATER_TEMP", jsonObj.getString("WATER_TEMP"));
            results.put("AIR_TEMP", jsonObj.getString("AIR_TEMP"));
            results.put("BATHER_LOAD", jsonObj.getString("BATHER_LOAD"));
            results.put("WEATHER", jsonObj.getString("WEATHER"));
            results.put("TIME_CHECKED", jsonObj.getString("TIME_CHECKED"));
            results.put("FACILITY", jsonObj.getString("FACILITY"));
            results.put("NOTES", jsonObj.getString("NOTES"));

            writer.close();
            reader.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return results;
    }

    /**
     * Add a new entry and return the generated key as the unique ID
     *
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
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = String.valueOf(rs.getInt(1));
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.println("Error adding new entry: " + ex.getMessage());
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
