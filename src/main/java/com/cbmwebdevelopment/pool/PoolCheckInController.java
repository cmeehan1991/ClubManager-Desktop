/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.pool;

import com.cbmwebdevelopment.member.MemberData;
import com.cbmwebdevelopment.tablecontrollers.MemberCheckInTableController;
import com.cbmwebdevelopment.tablecontrollers.MemberCheckInTableController.MemberCheckInInfo;
import com.cbmwebdevelopment.tablecontrollers.PoolOccupancyTableController.PoolOccupancy;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class PoolCheckInController implements Initializable {

    @FXML
    TextField searchTermsTextField;
    
    @FXML
    TableView<MemberCheckInInfo> memberCheckInTableView;
    
    protected TableView poolOccupantTableView;
    private MemberCheckInTableController tableController;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat dfDateOnly = new SimpleDateFormat("yyyy-MM-dd");
    
    @FXML
    protected void searchAction(ActionEvent event){
        
    }
    
    @FXML
    protected void addNewGuestAction(ActionEvent event){
        
    }
    
    @FXML
    protected void selectMemberButton(ActionEvent event){
       MemberCheckInInfo data = memberCheckInTableView.getSelectionModel().getSelectedItem();
        if(data != null){
            String memberId = String.valueOf(data.getId());
            String membershipId = String.valueOf(data.getMembershipId());
            String guestId = membershipId == null ? memberId : null;
            String date = df.format(new Date());
            checkMemberIn(memberId, membershipId, guestId, date);
        }
    }
        
    private void tableData(String terms){
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(()->{
            MemberData memberData = new MemberData();
            ObservableList<MemberCheckInInfo> data = memberData.getMemberCheckInInfo(terms);
            Platform.runLater(()->{
                memberCheckInTableView.getItems().setAll(data);
            });
            executor.shutdown();
        });
    }
    
    private void checkMemberIn(String memberId, String membershipId, String guestId, String date){
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(()->{
            PoolData poolData = new PoolData();
            MemberCheckInInfo data = memberCheckInTableView.getSelectionModel().getSelectedItem();
            boolean checkedIn = poolData.checkMemberIn(membershipId, memberId, guestId, date); 
            System.out.println("Checked In: " + checkedIn);
            if(checkedIn){
                System.out.println(date);
                ObservableList<PoolOccupancy> poolOccupancy = poolData.getOccupants(null, date);
                System.out.println(poolOccupancy);
                Platform.runLater(()->{
                    poolOccupantTableView.getItems().setAll(poolOccupancy);
                    Stage currentStage = (Stage) memberCheckInTableView.getScene().getWindow();
                    currentStage.close();
                });
            }
            executor.shutdown();
        });
    }
    
    private void initTableView(){
        tableController = new MemberCheckInTableController();
        tableController.tableController(memberCheckInTableView);
        
        // Set the initial table data to all members
        tableData(null);
    }
    
    private void setActionListeners(){    
        // Handle double click on table row
        memberCheckInTableView.setRowFactory(cb -> {
            TableRow<MemberCheckInInfo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    MemberCheckInInfo data = row.getItem();
                    String id = String.valueOf(data.getId());
                    String membershipId = String.valueOf(data.getMembershipId());
                    String guestId = membershipId == null ? id : null;
                    String date = df.format(new Date());
                    checkMemberIn(id, membershipId, guestId, date);
                }
            });
            return row;
        });
        
        // Handle enter key on search text field
        searchTermsTextField.setOnKeyPressed(evt -> {
            String terms = searchTermsTextField.getText();
            if(evt.getCode() == KeyCode.ENTER && (terms != null && !terms.trim().isEmpty())){
                tableData(terms);
            }
        });
        
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initTableView();
        setActionListeners();
    }    
    
}
