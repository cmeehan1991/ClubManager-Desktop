/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.signin;

import com.cbmwebdevelopment.clubmanager.FXMLController;
import com.cbmwebdevelopment.user.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class UserSignInController implements Initializable {

    @FXML
    Label errorMessageLabel;

    @FXML
    TextField usernameTextField, passwordTextField;

    @FXML
    Button signInButton;

    @FXML
    Hyperlink forgotUsernamePasswordLink;

    @FXML
    ProgressIndicator loadingIndicator;

    private boolean validateFields() {
        boolean isValid = true;
        if (usernameTextField.getText() == null || usernameTextField.getText().trim().isEmpty()) {
            isValid = false;
        }

        if (passwordTextField.getText() == null || passwordTextField.getText().trim().isEmpty()) {
            isValid = false;
        }
        return isValid;
    }

    private void notifyUser() {
        usernameTextField.getStyleClass().add("required");
        passwordTextField.getStyleClass().add("required");
        errorMessageLabel.setText("The username/password combination do not match what we have on record. Please try again.");
    }

    private void signUserIn() {
        loadingIndicator.toFront();
        loadingIndicator.setVisible(true);
        loadingIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            User user = new User();
            boolean signedIn = user.signIn(usernameTextField.getText(), passwordTextField.getText());
            if (signedIn) {
                String username = user.getUsername(System.getProperty("USER_ID"));
                Platform.runLater(() -> {
                    openMainWindow(username);
                });
            } else {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    loadingIndicator.setProgress(0.0);
                    notifyUser();
                });
            }
        });
        executor.shutdown();

    }

    private void openMainWindow(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
            Parent root = loader.load();
            
            FXMLController controller = (FXMLController) loader.getController();
            controller.logOutButton.setText(username);
            
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) usernameTextField.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("Club Manager");
            stage.show();

            stage.setOnCloseRequest(evt -> {
                Platform.exit();
            });

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private void signIn() {
        if (validateFields()) {
            signUserIn();
        } else {
            notifyUser();
        }
    }

    private void setActionListeners() {
        signInButton.setOnAction(evt -> {
            signIn();
        });
        
        passwordTextField.setOnKeyPressed(evt -> {
            if(evt.getCode() == KeyCode.ENTER){
                signIn();
            }
        });
        
        usernameTextField.setOnKeyPressed(evt -> {
            if(evt.getCode() == KeyCode.ENTER){
                signIn();
            }
        });
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setActionListeners();
    }

}
