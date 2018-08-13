/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.pool.chemicals;

import com.calendarfx.view.TimeField;
import com.cbmwebdevelopment.pool.PoolData;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.PrefixSelectionComboBox;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class ChemicalsEntryController implements Initializable {

    @FXML
    ProgressIndicator progressIndicator;

    @FXML
    TimeField timeField;

    @FXML
    Label idLabel;

    @FXML
    TextField chlorineTextField, phTextField, calciumTextField, alkalinityTextField, airTempTextField, waterTempTextField, batherLoadTextField;

    @FXML
    PrefixSelectionComboBox facilityComboBox, weatherComboBox;

    @FXML
    Button saveEntryButton, deleteEntryButton;

    @FXML
    TextArea notesTextArea;

    public PoolChemicalDataFXMLController poolDataController;
    public DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Get the selected entry based on the entry ID
     *
     * @param id
     */
    public void getEntry(String id) {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            PoolData poolData = new PoolData();
            HashMap<String, String> data = poolData.getEntry(id);
            Platform.runLater(() -> {
                idLabel.setText(id);
                timeField.setValue(LocalTime.parse(data.get("TIME_CHECKED"), DateTimeFormatter.ISO_TIME));
                chlorineTextField.setText(data.get("CHLORINE"));
                phTextField.setText(data.get("PH"));
                calciumTextField.setText(data.get("CALCIUM"));
                alkalinityTextField.setText(data.get("ALKALINITY"));
                airTempTextField.setText(data.get("AIR_TEMP"));
                waterTempTextField.setText(data.get("WATER_TEMP"));
                weatherComboBox.getSelectionModel().select(data.get("WEATHER"));
                batherLoadTextField.setText(data.get("BATHER_LOAD"));
                notesTextArea.setText(data.get("NOTES"));
                facilityComboBox.getSelectionModel().select(data.get("FACILITY"));
                progressIndicator.setVisible(false);
                progressIndicator.setProgress(0);
            });
            executor.shutdown();
        });
    }

    /**
     * Saves/updates the entry. If there is an ID associated with the entry,
     * then we update the existing entry. Otherwise we are creating a new entry.
     */
    public void saveEntry() {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            String id = idLabel.getText();
            boolean isNew = id.equals("...");

            LocalDate date = poolDataController.datePicker.getValue();
            LocalTime time = timeField.getValue();
            String timeString = timeFormat.format(time);
            String dateTimeString = dateTimeFormat.format(LocalDateTime.of(date, time));
            String chlorine = chlorineTextField.getText();
            String ph = phTextField.getText();
            String calcium = calciumTextField.getText();
            String alkalinity = alkalinityTextField.getText();
            String airTemp = airTempTextField.getText();
            String waterTemp = waterTempTextField.getText();
            String weather = String.valueOf(weatherComboBox.getSelectionModel().getSelectedItem());
            String batherLoad = batherLoadTextField.getText();
            String notes = notesTextArea.getText();
            String facility = facilityComboBox.getSelectionModel().getSelectedItem().toString();

            PoolData poolData = new PoolData();
            if (isNew) {
                String newId = poolData.addNewEntry(dateTimeString, chlorine, ph, calcium, alkalinity, airTemp, waterTemp, weather, batherLoad, notes, facility);
                System.out.println(newId);
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    progressIndicator.setProgress(0.0);
                    idLabel.setText(newId);
                    poolDataController.setTableView();
                });
            } else {
                boolean updated = poolData.updateEntry(id, dateTimeString, chlorine, ph, calcium, alkalinity, airTemp, waterTemp, weather, batherLoad, notes, facility);
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    progressIndicator.setProgress(0.0);
                    poolDataController.setTableView();
                });
            }
            executor.shutdown();
        });
    }

    private void initInputs() {
        weatherComboBox.getItems().setAll("Fog", "Overcast", "Rain", "Sun", "Wind");
        facilityComboBox.getItems().setAll("Main Pool", "Wading Pool");
    }

    private void deleteEntry(String id) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            PoolData poolData = new PoolData();
            boolean removed = poolData.removeEntry(id);
            Platform.runLater(()->{
                
            });
        });
    }

    private void setActionListeners() {
        saveEntryButton.setOnAction(evt -> {
            saveEntry();
        });

        deleteEntryButton.setOnAction(evt -> {
            if (confirmWithUser()) {
                deleteEntry(idLabel.getText());
            }
        });
    }

    private boolean confirmWithUser() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Action");
        alert.setHeaderText("Please confirm entry removal.");
        alert.setContentText("Please confirm that you wish to remove this entry. Once you delete this entry it cannot be recovered. Are you sure you want to proceed?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.YES;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initInputs();
        setActionListeners();
    }

}
