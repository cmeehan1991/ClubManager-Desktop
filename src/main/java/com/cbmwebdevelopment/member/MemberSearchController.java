/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.member;

import com.cbmwebdevelopment.tablecontrollers.MemberSearchTableController;
import com.cbmwebdevelopment.tablecontrollers.MemberSearchTableController.MemberSearch;
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
public class MemberSearchController implements Initializable {

    @FXML
    public ScrollPane scrollPane;

    @FXML
    Button selectMemberButton;

    @FXML
    TextField memberSearchTextField;

    @FXML
    TableView<MemberSearch> memberSearchTableView;

    private MemberSearchTableController memberSearchTableController;

    protected MemberController memberController;

    @FXML
    protected void memberSearchAction(ActionEvent event) {
        setTableData(memberSearchTextField.getText());
    }

    @FXML
    protected void selectMemberAction(ActionEvent event) {
        MemberSearch selectedItems = memberSearchTableView.getSelectionModel().getSelectedItem();
        String id = String.valueOf(selectedItems.getId());
        if (memberController != null) {
            memberController.isNew = false;
            memberController.memberIdTextField.setText(id);
            memberController.getMemberInfo(id);
            closeStage();
        } else {
            MemberMain memberMain = new MemberMain();
            memberMain.isNew = false;
            memberMain.memberId = id;
            memberMain.scrollPane = scrollPane;
            try {
                memberMain.start(new Stage());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void closeStage() {
        Stage stage = (Stage) memberSearchTableView.getScene().getWindow();
        stage.close();
    }

    private void setTableData(String terms) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            MemberData memberData = new MemberData();
            ObservableList<MemberSearch> data = memberData.getMembers(terms);
            Platform.runLater(() -> {
                memberSearchTableView.getItems().setAll(data);
            });
            executor.shutdown();

        });
    }

    private void initTableView() {
        memberSearchTableController = new MemberSearchTableController();
        memberSearchTableController.tableController(memberSearchTableView);

        setTableData(null);
    }

    private void setListeners() {

        // Listen for double click on table row. 
        memberSearchTableView.setRowFactory(tv -> {
            TableRow<MemberSearch> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    MemberSearch selectedItems = row.getItem();
                    String id = String.valueOf(selectedItems.getId());

                    if (memberController != null) {
                        memberController.isNew = false;
                        memberController.memberIdTextField.setText(id);
                        memberController.getMemberInfo(id);
                        closeStage();
                    } else {
                        MemberMain memberMain = new MemberMain();
                        memberMain.isNew = false;
                        memberMain.memberId = id;
                        memberMain.scrollPane = scrollPane;
                        try {
                            memberMain.start(new Stage());
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            });
            return row;
        });

        memberSearchTextField.setOnKeyReleased((event) -> {
            System.out.println(memberSearchTextField.getText());
            setTableData(memberSearchTextField.getText());
        });
    }

    /**
     * Initializes the controller class.
     */
    @Override

    public void initialize(URL url, ResourceBundle rb) {
        initTableView();
        setListeners();
    }

}
