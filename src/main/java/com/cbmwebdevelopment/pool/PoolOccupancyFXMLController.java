/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.pool;

import com.cbmwebdevelopment.tablecontrollers.PoolOccupancyTableController;
import com.cbmwebdevelopment.tablecontrollers.PoolOccupancyTableController.PoolOccupancy;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class PoolOccupancyFXMLController implements Initializable {

    @FXML
    TextField searchTermsTextField;

    @FXML
    DatePicker datePicker;

    @FXML
    TableView<PoolOccupancy> poolOccupancyTableView;
    
    private PoolOccupancyTableController poolOccupancyTableController;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dfTime = new SimpleDateFormat("h:m a");
    private SimpleDateFormat timeOutFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @FXML
    protected void checkInAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PoolCheckInFXML.fxml"));
            Parent root = (Parent) loader.load();
            PoolCheckInController controller = (PoolCheckInController) loader.getController();

            controller.poolOccupantTableView = poolOccupancyTableView;

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Member/Guest Check In");
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    protected void checkOutAction(ActionEvent event) {
        System.out.println("Check Out Action Clicked");
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(()->{
            System.out.println("executor submitted");
            ObservableList<PoolOccupancy> items = poolOccupancyTableView.getSelectionModel().getSelectedItems(); 
            System.out.println("items: " + items);           
            PoolData poolData = new PoolData();   
            PoolOccupancy data = poolOccupancyTableView.getSelectionModel().getSelectedItem();
            
            String id = String.valueOf(data.getId());     
            Date date = new Date();
            String dateOut = timeOutFormat.format(date);
            
            boolean success = poolData.checkMemberOut(id, dateOut);
            System.out.println("Success: " + success);
            if(success){
                Platform.runLater(()->{
                   data.setTimeOut(dfTime.format(date));
                   poolOccupancyTableView.refresh();
                });
            }else{
                System.out.println("Failed");
            }
            executor.shutdown();
        });
    }

    @FXML
    protected void searchOccupancyAction(ActionEvent event) {
        String terms = searchTermsTextField.getText();
        String date = df.format(datePicker.getValue());
        getOccupants(terms, date);
    }
    
    @FXML
    protected void getTodayAction(ActionEvent event){
        datePicker.setValue(LocalDate.now());
    }
    
    @FXML
    protected void resetAction(ActionEvent event){
        searchTermsTextField.setText(null);
        datePicker.setValue(LocalDate.now());
        String now = df.format(new Date());
        getOccupants(null, now);
    }

    public void getOccupants(String terms, String date){
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(()->{
            PoolData poolData = new PoolData();
            ObservableList<PoolOccupancy> data = poolData.getOccupants(terms, date);
            Platform.runLater(()->{
                poolOccupancyTableView.getItems().setAll(data);
            });
            executor.shutdown();
        });
    }
    private void initTableView(){
        poolOccupancyTableController = new PoolOccupancyTableController();
        poolOccupancyTableController.tableController(poolOccupancyTableView);
    }
    
    private void initInputs(){        
        datePicker.setValue(LocalDate.now());
    }
    
    private void setListeners(){
        searchTermsTextField.setOnKeyReleased(event -> {
                String date = datePicker.getValue() == null ? df.format(LocalDate.now()) : datePicker.getValue().toString();
                String terms = searchTermsTextField.getText();
            if(event.getCode() == KeyCode.ENTER){
                getOccupants(terms, date);
            }else if( searchTermsTextField == null || searchTermsTextField.getText().trim().isEmpty()){
                getOccupants(terms, date);
            }
        });
        
        datePicker.valueProperty().addListener((obs, ov, nv) -> {
            String date = datePicker.getValue() == null ? df.format(LocalDate.now()) : datePicker.getValue().toString();
            String terms = searchTermsTextField.getText();
            getOccupants(terms, date);
        });
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initInputs();
        initTableView();
        setListeners();
        
        LocalDate date = datePicker.getValue();
        getOccupants(null, date.toString());
    }

}
