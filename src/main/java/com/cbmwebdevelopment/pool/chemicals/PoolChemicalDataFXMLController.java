/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.pool.chemicals;

import com.cbmwebdevelopment.pool.PoolData;
import com.cbmwebdevelopment.tablecontrollers.ChemicalDataTableController;
import com.cbmwebdevelopment.tablecontrollers.ChemicalDataTableController.Chemicals;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class PoolChemicalDataFXMLController implements Initializable {

    @FXML
    AnchorPane anchorPane;

    @FXML
    TableView<Chemicals> chemicalDataTableView;

    @FXML
    DatePicker datePicker;

    @FXML
    Button viewEntryButton, addEntryButton;

    @FXML
    ComboBox facilityComboBox;

    @FXML
    ProgressIndicator progressIndicator;

    private ChemicalDataTableController tableController;

    private void initFields() {
        tableController = new ChemicalDataTableController();
        facilityComboBox.getItems().setAll("Main Pool", "Wading Pool");
        facilityComboBox.getSelectionModel().select(0);
        datePicker.setValue(LocalDate.now());
    }

    protected void setTableView() {
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressIndicator.setVisible(true);
        progressIndicator.toFront();
        tableController.tableController(chemicalDataTableView);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            PoolData poolData = new PoolData();
            String date = datePicker.getValue().format(DateTimeFormatter.ISO_DATE);
            String facility = facilityComboBox.getSelectionModel().getSelectedItem().toString();
            ObservableList<Chemicals> data = poolData.getPoolChemicalData(date, facility);
            Platform.runLater(() -> {
                chemicalDataTableView.getItems().setAll(data);
                progressIndicator.setProgress(0);
                progressIndicator.setVisible(false);
                progressIndicator.toBack();
            });
            executor.shutdown();
        });
    }

    private void notifyUser(){
       Alert alert = new Alert(AlertType.WARNING);
       alert.setTitle("Invalid Selection");
       alert.setContentText("Please select a valid entry and try again.");
       alert.showAndWait();
    }
    
    private void setActionListeners() {
        viewEntryButton.setOnAction(evt -> {
            Chemicals data = chemicalDataTableView.getSelectionModel().getSelectedItem();
            if(data != null && data.getId() != 0){
                openEntryController(String.valueOf(data.getId()));
            }else{
               notifyUser(); 
            }
        });

        addEntryButton.setOnAction(evt -> {
            openEntryController(null);
        });
        
        facilityComboBox.valueProperty().addListener((obs, ov, nv)->{
            setTableView();
        });
        
        datePicker.valueProperty().addListener((obs, ov, nv)->{
            setTableView();
        });
    }

    private void openEntryController(String id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChemicalsEntryFXML.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            ChemicalsEntryController controller = (ChemicalsEntryController) loader.getController();
            controller.poolDataController = this;
            if (id != null) {
                controller.getEntry(id);
            }else{
                controller.facilityComboBox.getSelectionModel().select(facilityComboBox.getSelectionModel().getSelectedItem().toString());
            }
            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            System.out.println("Error opening entry controller: " + ex.getMessage());
            System.out.println(ex.getCause());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initFields();
        setTableView();
        setActionListeners();
    }

}
