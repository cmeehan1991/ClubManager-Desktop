/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecontrollers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author cmeehan
 */
public class MemberCheckInTableController {
    public void tableController(TableView tableView){
        
        // Initialize the columns
        TableColumn<MemberCheckInInfo, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<MemberCheckInInfo, String> surnameColumn = new TableColumn<>("Surname");
        TableColumn<MemberCheckInInfo, String> nameColumn = new TableColumn<>("Name");
        TableColumn<MemberCheckInInfo, Integer> membershipIdColumn = new TableColumn<>("Membership ID");
        
        // Set the column factory
        idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory("surname"));
        nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        membershipIdColumn.setCellValueFactory(new PropertyValueFactory("membershipId"));
        
        // Set preferred column widths
        idColumn.setPrefWidth(75);
        surnameColumn.setPrefWidth(190);
        nameColumn.setPrefWidth(190);
        membershipIdColumn.setPrefWidth(134);
        
        // Add the columns to the tableview
        tableView.getColumns().setAll(idColumn, surnameColumn, nameColumn, membershipIdColumn);
        
        // Allow the user to select multiple entries
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    public static class MemberCheckInInfo{
        private final SimpleIntegerProperty id, membershipId;
        private final SimpleStringProperty surname, name;
        
        public MemberCheckInInfo(Integer id, String surname, String name, Integer membershipId){
            this.id = new SimpleIntegerProperty(id);
            this.surname = new SimpleStringProperty(surname);
            this.name = new SimpleStringProperty(name);
            this.membershipId = new SimpleIntegerProperty(membershipId);
        }
        
        public Integer getId(){
            return id.get();
        }
        
        public void setId(Integer newVal){
            id.set(newVal);
        }
        
        public String getSurname(){
            return surname.get();
        }
        
        public void setSurname(String newVal){
            surname.set(newVal);
        }
        
        public String getName(){
            return name.get();
        }
        
        public void setName(String newVal){
            name.set(newVal);
        }
        
        public Integer getMembershipId(){
            return membershipId.get();
        }
        
        public void setMembershipId(Integer newVal){
            membershipId.set(newVal);
        }
    }
}
