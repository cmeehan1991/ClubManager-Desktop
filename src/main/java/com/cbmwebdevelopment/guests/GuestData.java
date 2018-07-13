/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.guests;

import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.tablecontrollers.GuestSearchTableController.GuestSearch;
import com.cbmwebdevelopment.tablecontrollers.MemberSearchTableController;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author cmeehan
 */
public class GuestData {
    
/**
     * Get the basic data for the guest.
     *
     * @param guestId
     * @return
     */
    public HashMap<String, String> guestData(String guestId) {
        HashMap<String, String> data = new HashMap<>();
        String sql = "SELECT MEMBERSHIP_ID, SURNAME, NAME, DATE_FORMAT(DATE_OF_BIRTH, '%m/%e/%Y') AS 'DATE_OF_BIRTH', GENDER, STREET, APT, CITY, STATE, POSTAL_CODE, EMAIL_ADDRESS, HOME_PHONE, MOBILE_PHONE, NOTES, PROFILE_PICTURE FROM MEMBER_INFORMATION WHERE MEMBER_INFORMATION.ID = ?;";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, guestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    data.put("MEMBERSHIP_ID", rs.getString("MEMBERSHIP_ID"));
                    data.put("SURNAME", rs.getString("SURNAME"));
                    data.put("NAME", rs.getString("NAME"));
                    data.put("DATE_OF_BIRTH", rs.getString("DATE_OF_BIRTH"));
                    data.put("GENDER", rs.getString("GENDER"));
                    data.put("STREET", rs.getString("STREET"));
                    data.put("APT", rs.getString("APT"));
                    data.put("CITY", rs.getString("CITY"));
                    data.put("STATE", rs.getString("STATE"));
                    data.put("POSTAL_CODE", rs.getString("POSTAL_CODE"));
                    data.put("EMAIL_ADDRESS", rs.getString("EMAIL_ADDRESS"));
                    data.put("HOME_PHONE", rs.getString("HOME_PHONE"));
                    data.put("MOBILE_PHONE", rs.getString("MOBILE_PHONE"));
                    data.put("PROFILE_PICTURE", rs.getString("PROFILE_PICTURE"));
                } while (rs.next());
            }
        } catch (SQLException ex) {
            System.out.println("Error retrieving guest data: " + ex.getMessage());
        }
        return data;
    }

    /**
     * Save the guest data
     *
     * @param isNew
     * @param id
     * @param surname
     * @param name
     * @param dateOfBirth
     * @param gender
     * @param street
     * @param apt
     * @param city
     * @param state
     * @param postalCode
     * @param emailAddress
     * @param homePhone
     * @param mobilePhone
     * @param notes
     * @param profilePicture
     * @param groupId
     * @return
     */
    public String saveGuestData(boolean isNew, String id, String surname, String name, String dateOfBirth, String gender, String street, String apt, String city, String state, String postalCode, String emailAddress, String homePhone, String mobilePhone, String notes, File profilePicture) {
        String guestId = id;
        String sql = null;
        if (isNew) {
            sql = "INSERT INTO MEMBER_INFORMATION(SURNAME, NAME, DATE_OF_BIRTH, GENDER, STREET, APT, CITY, STATE, POSTAL_CODE, EMAIL_ADDRESS, HOME_PHONE, MOBILE_PHONE, NOTES) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        } else {
            sql = "UPDATE MEMBER_INFORMATION SET SURNAME = ?, NAME = ?, DATE_OF_BIRTH = ?, GENDER = ?, STREET = ?, APT = ?, CITY = ?, STATE = ?, POSTAL_CODE = ?, EMAIL_ADDRESS = ?, HOME_PHONE = ?, MOBILE_PHONE = ?, NOTES = ?, PROFILE_PICTURE = ? WHERE ID = ?";
        }

        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, surname);
            ps.setString(2, name);
            ps.setString(3, dateOfBirth);
            ps.setString(4, gender);
            ps.setString(5, street);
            ps.setString(6, apt);
            ps.setString(7, city);
            ps.setString(8, state);
            ps.setString(9, postalCode);
            ps.setString(10, emailAddress);
            ps.setString(11, homePhone);
            ps.setString(12, mobilePhone);
            ps.setString(13, notes);
            if (!isNew) {
                if (profilePicture != null) {
                    ps.setString(14, "http://www.cbmwebdevelopment.com/sample-sites/clubmanager/images/guests/" + saveProfilePicture(id, profilePicture));
                } else {
                    ps.setString(14, null);
                }
                ps.setString(15, id);
            }
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next() && isNew) {
                guestId = String.valueOf(rs.getInt(1));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return guestId;
    }

    /**
     * Encode the file name to be MD5 for security
     *
     * @param id
     * @return
     */
    private String encodeName(String id) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(id.getBytes());

            byte byteData[] = md.digest();

            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
        return sb.toString();
    }

    /**
     * Get the group id for the guest
     *
     * @param id
     * @return
     */
    public String getGuestshipGroup(String id) {
        String sql = "SELECT MEMBERSHIP_ID FROM MEMBER_INFORMATION WHERE ID = ?";
        String groupId = null;
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                groupId = rs.getString("MEMBERSHIP_ID");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return groupId;
    }

    /**
     * Change the primary guest of the guestship group
     *
     * @param userId
     * @param groupId
     * @return
     */
    public boolean changePrimaryGuest(String userId, String groupId) {
        boolean success = false;
        String sql = "UPDATE TABLE MEMBERSHIP_INFORMATION SET MEMBER_ID = ? WHERE ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            ps.setString(2, groupId);
            success = ps.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return success;
    }

    /**
     * Add a new or existing user to a guestship group.
     *
     * @param userId
     * @param groupId
     * @return
     */
    public boolean addUserToGroup(String userId, String groupId) {
        String sql = "UPDATE MEMBER_INFORMATION SET MEMBERSHIP_ID = ? WHERE ID = ?";
        boolean updated = false;
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            ps.setString(2, groupId);
            updated = ps.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return updated;
    }

    /**
     * Creates a new guest group
     *
     * @param id
     * @return
     */
    public String createNewGuestGroup(String id) {
        String sql = "INSERT INTO MEMBERSHIP_INFORMATION(MEMBERSHIP_INFORMATION.MEMBER_ID, MEMBERSHIP_INFORMATION.STATUS) VALUES(?, ?);";
        String groupId = null;
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, id);
            ps.setBoolean(2, true);
            ps.setString(3, "Active");
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                groupId = String.valueOf(rs.getInt(1));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return groupId;
    }

    /**
     * Save the profile picture to a directory
     *
     * @param id
     * @param picture
     * @return
     */
    public String saveProfilePicture(String id, File picture) {
        String server = "ftp.utterfare.com";
        int port = 21;
        String user = "clubmanager@cbmwebdevelopment.com";
        String pass = "Mia2016!";

        FTPClient ftpClient = new FTPClient();
        String fileName = null;
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            InputStream inputStream = new FileInputStream(picture);

            fileName = encodeName(id) + ".png";
            boolean done = ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            inputStream.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                ftpClient.logout();
                ftpClient.disconnect();;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return fileName;
    }

   public ObservableList<String> guestsList() {
        ObservableList<String> list = FXCollections.observableArrayList();

        String sql = "SELECT CONCAT(SURNAME, ', ', NAME) AS 'NAME' FROM MEMBER_INFORMATION ORDER BY SURNAME ASC";

        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    list.add(rs.getString("NAME"));
                } while (rs.next());
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.println("Error retrieving guest list: " + ex.getMessage());
        }

        return list;
    }
   
   /**
     * Returns an ObservableList of the member id, name based
     * on the search parameters. If the parameters are null it will return all
     * guests.
     *
     * @param terms
     * @return
     */
    public ObservableList<GuestSearch> getGuests(String terms) {
        ObservableList<GuestSearch> data = FXCollections.observableArrayList();
        String sql = "SELECT ID, CONCAT(NAME, ' ', SURNAME) AS 'NAME', MEMBERSHIP_ID FROM MEMBER_INFORMATION";
        if (terms != null) {
            sql += " ";
            sql += "WHERE ID = ? OR NAME = ? OR SURNAME = ? OR MEMBERSHIP_ID = ? OR ID LIKE ? OR NAME LIKE ? OR SURNAME LIKE ? OR MEMBERSHIP_ID LIKE ?";
        }
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            if (terms != null) {
                ps.setString(1, terms);
                ps.setString(2, terms);
                ps.setString(3, terms);
                ps.setString(4, terms);
                ps.setString(5, "%" + terms + "%");
                ps.setString(6, "%" + terms + "%");
                ps.setString(7, "%" + terms + "%");
                ps.setString(8, "%" + terms + "%");
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    Integer id = rs.getInt("ID");
                    String name = rs.getString("NAME");
                    Integer guestshipId = rs.getInt("MEMBERSHIP_ID");
                    data.add(new GuestSearch(id, name, guestshipId));
                } while (rs.next());
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return data;
    }

}
