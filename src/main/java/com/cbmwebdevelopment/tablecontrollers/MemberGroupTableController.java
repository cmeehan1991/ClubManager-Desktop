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
public class MemberGroupTableController {
    
    
    public void tableController(TableView tableView){
        TableColumn<MemberGroup, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<MemberGroup, String> nameColumn = new TableColumn<>("Name");
        TableColumn<MemberGroup, Integer> ageColumn = new TableColumn<>("Age");
        
        idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        ageColumn.setCellValueFactory(new PropertyValueFactory("age"));
        
        idColumn.setPrefWidth(100);
        nameColumn.setPrefWidth(300);
        ageColumn.setPrefWidth(70);
        
        tableView.getColumns().setAll(idColumn, nameColumn, ageColumn);
    }
    
    public static class MemberGroup{
        public final SimpleIntegerProperty id, age;
        public final SimpleStringProperty name;
        
        public MemberGroup(Integer id, String name, Integer age){
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.age = new SimpleIntegerProperty(age);
        }
        
        public Integer getId(){
            return id.get();
        }
        
        public void setId(Integer val){
            id.set(val);
        }
        
        public String getName(){
            return name.get();
        }      
        
        public void setName(String val){
            name.set(val);
        }
        
        public Integer getAge(){
            return age.get();
        }
        
        public void setAge(Integer val){
            age.set(val);
        }
    }
}
