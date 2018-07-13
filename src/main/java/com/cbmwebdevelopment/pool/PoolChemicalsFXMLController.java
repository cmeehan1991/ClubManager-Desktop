/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.pool;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class PoolChemicalsFXMLController implements Initializable {

    @FXML
    ToolBar poolChemicalsToolBar;

    @FXML
    BorderPane poolChemicalsBorderPane;

    @FXML
    protected void displayPoolChemicalsInputAction(ActionEvent event) {
        try {
            AnchorPane poolChemicalData = (AnchorPane) FXMLLoader.load(getClass().getResource("/fxml/PoolChemicalDataFXML.fxml"));
            poolChemicalsBorderPane.centerProperty().set(poolChemicalData);
            changeButtonClasses((Button) event.getSource());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    protected void displayPoolChemicalsDashboardAction(ActionEvent event) {
        try {
            AnchorPane poolDashboard = (AnchorPane) FXMLLoader.load(getClass().getResource("/fxml/PoolDashboardFXML.fxml"));
            poolDashboard.setPrefWidth(poolChemicalsBorderPane.getWidth());
            poolDashboard.setPrefHeight(poolChemicalsBorderPane.getHeight());
            poolChemicalsBorderPane.centerProperty().set(poolDashboard);
            changeButtonClasses((Button) event.getSource());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void changeButtonClasses(Node activeNode) {
        ObservableList<Node> childElements = poolChemicalsToolBar.getItems();
        childElements.forEach((node) -> {
            ObservableList<String> styles = node.getStyleClass();
            if (styles.contains("active")) {
                node.getStyleClass().remove("active");
            }
        });

        activeNode.getStyleClass().add("active");

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            AnchorPane poolDashboard = (AnchorPane) FXMLLoader.load(getClass().getResource("/fxml/PoolDashboardFXML.fxml"));
            poolChemicalsBorderPane.centerProperty().set(poolDashboard);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
