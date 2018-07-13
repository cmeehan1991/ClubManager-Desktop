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
public class GuestSearchTableController {
    public void tableController(TableView tableView){
        TableColumn<GuestSearch, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<GuestSearch, String> nameColumn = new TableColumn<>("First & Last Name");
        TableColumn<GuestSearch, Integer> membershipIdColumn = new TableColumn<>("Membership ID");
        
        idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        membershipIdColumn.setCellValueFactory(new PropertyValueFactory("membershipId"));
        
        idColumn.setPrefWidth(130);
        nameColumn.setPrefWidth(310);
        membershipIdColumn.setPrefWidth(130);
        
        tableView.getColumns().setAll(idColumn, nameColumn, membershipIdColumn);
    }
    
    public static class GuestSearch{
        public final SimpleIntegerProperty id, membershipId;
        public final SimpleStringProperty name;
        
        public GuestSearch(Integer id, String name, Integer membershipId){
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.membershipId = new SimpleIntegerProperty(membershipId);
        }
        
        public void setId(Integer newVal){
            id.set(newVal);
        }
        
        public Integer getId(){
            return id.get();
        }
        
        public void setName(String newVal){
            name.set(newVal);
        }
        
        public String getName(){
            return name.get();
        }
        
        public void setMembershipId(Integer newVal){
            membershipId.set(newVal);
        }
        
        public Integer getMembershipId(){
            return membershipId.get();
        }
    }
}
