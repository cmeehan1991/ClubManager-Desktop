/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecontrollers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author cmeehan
 */
public class GuestAttendanceTableController {
    
    
    public void tableController(TableView tableView){
        TableColumn<GuestAttendance, Integer> idColumn = new TableColumn<>();
        TableColumn<GuestAttendance, Integer> guestIdColumn = new TableColumn<>();
        TableColumn<GuestAttendance, String> facilityColumn = new TableColumn<>("Facility");
        TableColumn<GuestAttendance, String> memberColumn = new TableColumn<>("Member");
        TableColumn<GuestAttendance, Integer> dateColumn = new TableColumn<>("Date");
        
        idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        guestIdColumn.setCellValueFactory(new PropertyValueFactory("guestId"));
        facilityColumn.setCellValueFactory(new PropertyValueFactory("facility"));
        memberColumn.setCellValueFactory(new PropertyValueFactory("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory("date"));
        
        idColumn.setMaxWidth(0);
        idColumn.setVisible(false);
        idColumn.setResizable(false);
        guestIdColumn.setMaxWidth(0);
        guestIdColumn.setVisible(false);
        guestIdColumn.setResizable(false);
        facilityColumn.setPrefWidth(150);
        memberColumn.setPrefWidth(350);
        dateColumn.setPrefWidth(200);
        
        tableView.getColumns().setAll(idColumn, guestIdColumn, facilityColumn, memberColumn, dateColumn);
    }
    
    public static class GuestAttendance{
        public final SimpleIntegerProperty id, guestId;
        public final SimpleStringProperty facility, name, date;
        
        public GuestAttendance(Integer id, Integer guestId, String facility, String name, String date){
            this.id = new SimpleIntegerProperty(id);
            this.guestId = new SimpleIntegerProperty(guestId);
            this.facility = new SimpleStringProperty(facility);
            this.name = new SimpleStringProperty(name);
            this.date = new SimpleStringProperty(date);
        }
        
        public Integer getId(){
            return id.get();
        }
        
        public void setId(Integer val){
            id.set(val);
        }
        
        public Integer getGuestId(){
            return id.get();
        }
        
        public void setGuestId(Integer val){
            id.set(val);
        }
        
        public String getFacility(){
            return facility.get();
        }      
        
        public void setFacility(String val){
            facility.set(val);
        }
        
        public String getName(){
            return name.get();
        }      
        
        public void setName(String val){
            name.set(val);
        }
        
        public String getDate(){
            return date.get();
        }      
        
        public void setDate(String val){
            date.set(val);
        }
    }
}
