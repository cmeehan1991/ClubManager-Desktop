/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.user;

import com.cbmwebdevelopment.connections.Links;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.JSONObject;

/**
 *
 * @author cmeehan
 */
public class User implements Links{
    
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
        try{
            URL url = new URL(USERS_LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("sign_in", "UTF-8");
            data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            JSONObject jsonObj = new JSONObject(reader.readLine());
            
            if(jsonObj.getBoolean("is_valid")){
                result = true;
                System.getProperties().put("USER_ID", jsonObj.getString("ID"));
            }
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        
        return result;
    }
    
    public String getUsername(String id){
       String username = null;
       try{
           URL url = new URL(USERS_LINK);
           URLConnection conn = url.openConnection();
           conn.setDoOutput(true);
           
           String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_username", "UTF-8");
           data += "&" + URLEncoder.encode("id", ENCODING) + "=" + URLEncoder.encode(id, ENCODING);
           System.out.println(data);
           
           OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
           writer.write(data);
           writer.flush();
           System.out.println(writer);
           BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));         
           System.out.println(reader);
           JSONObject jsonObj = new JSONObject(reader.readLine());
           System.out.println(jsonObj);
           if(jsonObj.getBoolean("has_result")){
               username = jsonObj.getString("USERNAME");
           }
           
       }catch(IOException ex){
           System.out.println(ex.getMessage());
       }
        return username;
    }
}
