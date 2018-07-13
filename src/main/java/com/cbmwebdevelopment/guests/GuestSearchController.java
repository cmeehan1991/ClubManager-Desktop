/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.guests;

import com.cbmwebdevelopment.member.MemberMain;
import com.cbmwebdevelopment.tablecontrollers.GuestSearchTableController;
import com.cbmwebdevelopment.tablecontrollers.GuestSearchTableController.GuestSearch;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class GuestSearchController implements Initializable {

    @FXML
    public ScrollPane scrollPane;

    @FXML
    Button selectMemberButton;

    @FXML
    TextField guestSearchTextField;

    @FXML
    TableView<GuestSearch> guestSearchTableView;

    private GuestSearchTableController guestSearchTableController;

    protected GuestController guestController;

    @FXML
    protected void guestSearchAction(ActionEvent event) {
        setTableData(guestSearchTextField.getText());
    }

    @FXML
    protected void selectMemberAction(ActionEvent event) {
        GuestSearch selectedItems = guestSearchTableView.getSelectionModel().getSelectedItem();
        String id = String.valueOf(selectedItems.getId());
        if (guestController != null) {
            guestController.isNew = false;
            guestController.guestIdTextField.setText(id);
            guestController.getGuestInfo(id);
            closeStage();
        } 
    }

    private void closeStage() {
        Stage stage = (Stage) guestSearchTableView.getScene().getWindow();
        stage.close();
    }

    private void setTableData(String terms) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            GuestData guestData = new GuestData();
            ObservableList<GuestSearch> data = guestData.getGuests(terms);
            Platform.runLater(() -> {
                guestSearchTableView.getItems().setAll(data);
            });
            executor.shutdown();

        });
    }

    private void initTableView() {
        guestSearchTableController = new GuestSearchTableController();
        guestSearchTableController.tableController(guestSearchTableView);

        setTableData(null);
    }

    private void setListeners() {

        // Listen for double click on table row. 
        guestSearchTableView.setRowFactory(tv -> {
            TableRow<GuestSearch> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    GuestSearch selectedItems = row.getItem();
                    String id = String.valueOf(selectedItems.getId());

                    if (guestController != null) {
                        guestController.isNew = false;
                        guestController.guestIdTextField.setText(id);
                        closeStage();
                    } 
                }
            });
            return row;
        });

        guestSearchTextField.setOnKeyReleased((event) -> {
            System.out.println(guestSearchTextField.getText());
            setTableData(guestSearchTextField.getText());
        });
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override

    public void initialize(URL url, ResourceBundle rb) {
        initTableView();
        setListeners();
    }

}
