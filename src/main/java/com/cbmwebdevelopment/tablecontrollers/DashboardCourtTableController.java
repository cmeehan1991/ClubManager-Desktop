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
public class DashboardCourtTableController {
    public void tableView(TableView tableView){
        TableColumn<CourtOccupancy, Integer> courtColumn = new TableColumn<>("Court");
        TableColumn<CourtOccupancy, String> playersColumn = new TableColumn<>("Players");
        TableColumn<CourtOccupancy, String> timeOnColumn = new TableColumn<>("Time On");
        
        courtColumn.setCellValueFactory(new PropertyValueFactory<>("court"));
        courtColumn.setCellValueFactory(new PropertyValueFactory<>("players"));
        courtColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        
        courtColumn.setPrefWidth(75);
        playersColumn.setPrefWidth(190);
        timeOnColumn.setPrefWidth(93);
        
        tableView.getColumns().setAll(courtColumn, playersColumn, timeOnColumn);
        
    }
    
    public static class CourtOccupancy{
        public final SimpleIntegerProperty court;
        public final SimpleStringProperty players, time;
        
        public CourtOccupancy(Integer court, String players, String time){
            this.court = new SimpleIntegerProperty(court);
            this.players = new SimpleStringProperty(players);
            this.time = new SimpleStringProperty(time);
        }
        
        public Integer getCourt(){
            return court.get();
        }
        
        public void setCourt(Integer val){
            court.set(val);
        }
        
        public String getPlayers(){
            return players.get();
        }
        
        public void setPlayers(String val){
            players.set(val);
        }
        
        public String getTime(){
            return time.get();
        }
        
        public void setTime(String val){
            time.set(val);
        }
    }
}
