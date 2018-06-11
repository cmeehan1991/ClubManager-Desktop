/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.member;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class MemberMain extends Application {
    public ScrollPane scrollPane;
    public boolean isNew;
    public String membershipId, memberId;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MemberFXML.fxml"));
        AnchorPane anchorPane = (AnchorPane) loader.load();
        
        MemberController memberController = loader.getController();        
        memberController.disableInputs(isNew);
        memberController.memberIdTextField.setEditable(!isNew);
        memberController.getMemberButton.setDisable(isNew);
        memberController.isNew = isNew;
        memberController.primaryScrollPane = scrollPane;
        if(membershipId != null){
            memberController.getMembershipGroup(membershipId);
            memberController.groupIdTextField.setText(membershipId);
        }
        
        if(memberId != null){
            memberController.getMemberInfo(memberId);
        }
        
        scrollPane.setContent(anchorPane);        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
