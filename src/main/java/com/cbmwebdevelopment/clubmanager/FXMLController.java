package com.cbmwebdevelopment.clubmanager;

import com.calendarfx.view.CalendarView;
import com.cbmwebdevelopment.dashboard.DashboardMain;
import com.cbmwebdevelopment.member.MemberMain;
import com.cbmwebdevelopment.member.MemberSearchController;
import com.cbmwebdevelopment.pool.PoolOccupancyMain;
import com.cbmwebdevelopment.tennis.TennisReservationsMain;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FXMLController implements Initializable {

    @FXML
    CalendarView calendarView;
    
    @FXML
    ScrollPane primaryScrollPane;

    @FXML
    public Button logOutButton, dashboardButton, newMemberButton, viewMemberButton, viewAllMembersButton, courtReservationsButton, poolOccupancyButton, poolChemicalsButton, newEmployeeButton, viewEmployeeButton, employeeSchedulingButton;
    
    @FXML
    VBox navigationVBox;
    
    @FXML
    HBox navigationHBox;
    
   
    @FXML
    private void displayDashboardWindowAction(ActionEvent event) {
        try {
            DashboardMain dashboardMain = new DashboardMain();
            dashboardMain.primaryScrollPane = primaryScrollPane;
            dashboardMain.start(new Stage());
            changeButtonClasses(dashboardButton);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    @FXML
    protected void openNewMemberWindowAction(ActionEvent event) throws IOException {
        MemberMain memberMain = new MemberMain();
        memberMain.scrollPane = primaryScrollPane;
        memberMain.isNew = true;
        memberMain.start(new Stage());
        changeButtonClasses(newMemberButton);
    }
    
    @FXML 
    protected void viewMemberWindowAction(ActionEvent event) throws IOException{
        MemberMain memberMain = new MemberMain();
        memberMain.scrollPane = primaryScrollPane;
        memberMain.isNew = false;
        
        memberMain.start(new Stage());
        changeButtonClasses(viewMemberButton);
    }
    
    @FXML
    protected void viewAllMembersWindowAction(ActionEvent event){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MemberSearchFXML.fxml"));
        try{
            AnchorPane anchorPane = (AnchorPane) loader.load();
            MemberSearchController controller = (MemberSearchController) loader.getController();
            controller.scrollPane = primaryScrollPane;
            primaryScrollPane.setContent(anchorPane);
        changeButtonClasses(viewAllMembersButton);
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }    
    
    @FXML
    protected void showReservationsWindowAction(ActionEvent event){
        TennisReservationsMain tennisReservationsMain = new TennisReservationsMain();
        tennisReservationsMain.scrollPane = primaryScrollPane;
        tennisReservationsMain.start(new Stage());
        changeButtonClasses(courtReservationsButton);
    }
    
    @FXML
    protected void poolOccupancyAction(ActionEvent event){
        PoolOccupancyMain poolOccupancyMain = new PoolOccupancyMain();
        poolOccupancyMain.primaryScrollPane = primaryScrollPane;
        poolOccupancyMain.start(new Stage());
        changeButtonClasses(poolOccupancyButton);
    }
    
    @FXML
    protected void displayPoolChemicalsAction(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PoolChemicalsFXML.fxml"));
            BorderPane borderPane = (BorderPane) loader.load();
            borderPane.prefHeight(primaryScrollPane.getHeight());
            borderPane.prefWidth(primaryScrollPane.getWidth());
            primaryScrollPane.setContent(borderPane);
            changeButtonClasses(poolChemicalsButton);
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
    
    public void changeButtonClasses(Node activeNode){
        ObservableList<Node> childElements = navigationVBox.getChildren();
        childElements.forEach((node)->{
            ObservableList<String> styles = node.getStyleClass();
            if(styles.contains("active")){
                node.getStyleClass().remove("active");
            }
        });
        
        activeNode.getStyleClass().add("active");
        
    }

    private void openLogIn(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserSignInFXML.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) logOutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException ex){
            
        }
    }
    
    private void setListeners(){
        logOutButton.setOnAction(evt -> {
            System.getProperties().clear();
            openLogIn();
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setListeners();
        
        displayDashboardWindowAction(new ActionEvent());
        primaryScrollPane.setFitToHeight(true);
        primaryScrollPane.setFitToWidth(true);
        primaryScrollPane.setMinWidth(800);
    }
}
