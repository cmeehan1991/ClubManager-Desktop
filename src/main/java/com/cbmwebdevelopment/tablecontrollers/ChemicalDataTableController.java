/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecontrollers;

import com.cbmwebdevelopment.tablecells.TableCellTimePicker;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 *
 * @author cmeehan
 */
public class ChemicalDataTableController {
    
    public void tableController(TableView tableView){
        Callback<TableColumn<Chemicals, String>, TableCell<Chemicals, String>> timeCellFactory = (TableColumn<Chemicals, String> param) -> new TableCellTimePicker();
        
        TableColumn<Chemicals, Integer> idColumn = new TableColumn<>();
        TableColumn<Chemicals, String> timeColumn = new TableColumn<>("Time");
        TableColumn<Chemicals, Double> chlorineColumn = new TableColumn<>("Chlorine");
        TableColumn<Chemicals, Double> phColumn = new TableColumn<>("pH");
        TableColumn<Chemicals, Double> calciumColumn = new TableColumn<>("Calcium");
        TableColumn<Chemicals, Double> alkalinityColumn = new TableColumn<>("Alkalinity");
        TableColumn<Chemicals, String> environmentColumn = new TableColumn<>("Environment");
        TableColumn<Chemicals, Double> airTempColumn = new TableColumn<>("Air Temp.");
        TableColumn<Chemicals, Double> waterTempColumn = new TableColumn<>("Water Temp.");
        TableColumn<Chemicals, String> weatherColumn = new TableColumn<>("Weather");
        TableColumn<Chemicals, Double> batherLoadColumn = new TableColumn<>("Bather Load");
        
        // Hide the ID column
        idColumn.setMaxWidth(0.0);
        idColumn.setVisible(false);
        
        // Set the rest of the column sizes. 
        double columnWidth = tableView.getPrefWidth()/9;
        
        System.out.println(tableView.getPrefWidth());
        
        timeColumn.setPrefWidth(columnWidth);
        chlorineColumn.setPrefWidth(columnWidth);
        phColumn.setPrefWidth(columnWidth);
        calciumColumn.setPrefWidth(columnWidth);
        alkalinityColumn.setPrefWidth(columnWidth);
        airTempColumn.setPrefWidth(columnWidth);
        waterTempColumn.setPrefWidth(columnWidth);
        weatherColumn.setPrefWidth(columnWidth);
        batherLoadColumn.setPrefWidth(columnWidth);
        
        // Set the cell value factory for each column
        idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        timeColumn.setCellValueFactory(new PropertyValueFactory("time"));
        chlorineColumn.setCellValueFactory(new PropertyValueFactory("chlorine"));
        phColumn.setCellValueFactory(new PropertyValueFactory("ph"));
        calciumColumn.setCellValueFactory(new PropertyValueFactory("calcium"));
        airTempColumn.setCellValueFactory(new PropertyValueFactory("airTemp"));
        waterTempColumn.setCellValueFactory(new PropertyValueFactory("waterTemp"));
        weatherColumn.setCellValueFactory(new PropertyValueFactory("weather"));
        batherLoadColumn.setCellValueFactory(new PropertyValueFactory("batherLoad"));
        
        // Add the temp and weather columns to the environment column
        environmentColumn.getColumns().setAll(airTempColumn, waterTempColumn, weatherColumn);
        
        tableView.getColumns().setAll(idColumn, timeColumn, chlorineColumn, calciumColumn, alkalinityColumn, environmentColumn, batherLoadColumn);  
    }
    
    public static class Chemicals{
        final SimpleIntegerProperty id;
        final SimpleDoubleProperty chlorine, ph, calcium, airTemp, waterTemp, batherLoad, alkalinity;
        final SimpleStringProperty time, weather;
        
        public Chemicals(int id, String time, double chlorine, double ph, double calcium, double alkalinity, double airTemp, double waterTemp, String weather, double batherLoad){
            this.id = new SimpleIntegerProperty(id);
            this.time = new SimpleStringProperty(time);
            this.chlorine = new SimpleDoubleProperty(chlorine);
            this.ph = new SimpleDoubleProperty(ph);
            this.calcium = new SimpleDoubleProperty(calcium);
            this.airTemp = new SimpleDoubleProperty(airTemp);
            this.waterTemp = new SimpleDoubleProperty(waterTemp);
            this.weather = new SimpleStringProperty(weather);
            this.batherLoad = new SimpleDoubleProperty(batherLoad);
            this.alkalinity = new SimpleDoubleProperty(alkalinity);
        }
        
        public void setId(int id){
            this.id.set(id);
        }
        
        public int getId(){
            return this.id.get();
        }
        
        public String getTime(){
            return time.get();
        }
        
        public void setTime(String val){
            this.time.set(val);
        }
        
        public Double getChlorine(){
            return this.chlorine.get();
        }
        
        public void setChlorine(Double val){
            this.chlorine.set(val);
        }
        
        public Double getPh(){
            return this.ph.get();
        }
        
        public void setPh(Double val){
            this.ph.set(val);
        }
        
        public Double getAlkalinity(){
            return this.alkalinity.get();
        }
        
        public void setAlkalinity(Double val){
            this.alkalinity.set(val);
        }
        
        public Double getCalcium(){
            return this.calcium.get();
        }
        
        public void setCalcium(Double val){
            this.calcium.set(val);
        }
        
        public Double getAirTemp(){
            return this.airTemp.get();
        }
        
        public void setAirTemp(Double val){
            this.airTemp.set(val);
        }
        
        public Double getWaterTemp(){
            return this.waterTemp.get();
        }
        
        public void setWaterTemp(Double val){
            this.waterTemp.set(val);
        }
        
        public String getWeather(){
            return this.weather.get();
        }
        
        public void setWeather(String val){
            this.weather.set(val);
        }
        
        public Double getBatherLoad(){
            return this.batherLoad.get();
        }
        
        public void setBatherLoad(Double val){
            this.batherLoad.set(val);
        }       
    }
}
