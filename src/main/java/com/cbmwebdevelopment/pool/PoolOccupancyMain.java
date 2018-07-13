/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.pool;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class PoolOccupancyMain extends Application {

    public ScrollPane primaryScrollPane;

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PoolOccupancyFXML.fxml"));
        try {
            BorderPane borderPane = (BorderPane) loader.load();
            primaryScrollPane.setContent(borderPane);
        } catch (IOException ex) {
            System.out.println("Error getting pool occupancy view: " + ex.getMessage());
            System.out.println(ex.getLocalizedMessage());
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
