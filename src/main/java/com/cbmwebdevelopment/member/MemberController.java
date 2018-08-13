/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.member;

import static com.cbmwebdevelopment.clubmanager.MainApp.STATE_LIST;
import com.cbmwebdevelopment.tablecontrollers.MemberGroupTableController;
import com.cbmwebdevelopment.tablecontrollers.MemberGroupTableController.MemberGroup;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.imageio.ImageIO;
import org.controlsfx.control.PrefixSelectionComboBox;

/**
 *
 * @author cmeehan
 */
public class MemberController implements Initializable {

    @FXML
    ScrollPane primaryScrollPane;

    @FXML
    ProgressIndicator progressIndicator;

    @FXML
    AnchorPane memberInformationAnchorPane;

    @FXML
    TextField memberIdTextField, surnameTextField, nameTextField, genderTextField, streetTextField, aptSuiteTextField, cityTextField, zipCodeTextField, emailAddressTextField, homePhoneTextField, mobilePhoneTextField, notesTextField, groupIdTextField;

    @FXML
    DatePicker birthDatePicker;

    @FXML
    PrefixSelectionComboBox stateComboBox;

    @FXML
    ImageView profilePictureImageView;

    @FXML
    TableView<MemberGroup> familyTableView;

    @FXML
    Button getMemberButton;

    @FXML
    TabPane tabPane;

    private String membershipId, memberId, surname, name, birthDate, gender, street, apt, city, state, postalCode, emailAddress, homePhone, mobilePhone, notes;
    private File profilePicture;
    protected boolean isNew;
    private MemberGroupTableController memberGroupTableController;
    ArrayList<String> missingItems;
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private final DateTimeFormatter toLocalDateFormat = DateTimeFormatter.ofPattern("M/d/uuuu");

    private void checkRequiredFields() {
        missingItems = new ArrayList<>();
        assignValues();

        if (surname == null || surname.trim().isEmpty()) {
            missingItems.add("Surname");
        }

        if (name == null || name.trim().isEmpty()) {
            missingItems.add("Name");
        }

        if (street == null || street.trim().isEmpty()) {
            missingItems.add("Street");
        }

        if (city == null || city.trim().isEmpty()) {
            missingItems.add("City");
        }

        if (emailAddress == null || emailAddress.trim().isEmpty()) {
            missingItems.add("Email Address");
        }
    }

    /**
     * Assign values to all of the variables
     */
    private void assignValues() {
        membershipId = groupIdTextField.getText();
        System.out.println("Membership ID: " + membershipId);
        surname = surnameTextField.getText();
        name = nameTextField.getText();
        birthDate = birthDatePicker.getValue() != null ? format.format(birthDatePicker.getValue()) : null;
        gender = genderTextField.getText();
        street = streetTextField.getText();
        apt = aptSuiteTextField.getText();
        city = cityTextField.getText();
        state = String.valueOf(stateComboBox.getSelectionModel().getSelectedItem());
        postalCode = zipCodeTextField.getText();
        emailAddress = emailAddressTextField.getText();
        homePhone = homePhoneTextField.getText();
        mobilePhone = mobilePhoneTextField.getText();
        notes = notesTextField.getText();
        Image image = profilePictureImageView.getImage();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        try {
            profilePicture = File.createTempFile("profile", ".png");
            ImageIO.write(bufferedImage, "png", profilePicture);
        } catch (IOException e) {
            System.out.println("Error saving image");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    protected void cancelAction(ActionEvent event) {
        memberId = null;
        membershipId = null;
        memberIdTextField.clear();
        surnameTextField.clear();
        nameTextField.clear();
        birthDatePicker.setValue(null);
        genderTextField.clear();
        streetTextField.clear();
        aptSuiteTextField.clear();
        cityTextField.clear();
        stateComboBox.getSelectionModel().select(-1);
        zipCodeTextField.clear();
        emailAddressTextField.clear();
        homePhoneTextField.clear();
        mobilePhoneTextField.clear();
        notesTextField.clear();
        groupIdTextField.clear();
        familyTableView.getItems().clear();
        Image image = new Image(getClass().getResource("/icons/avatar.png").toString());
        profilePictureImageView.setImage(image);

    }

    @FXML
    protected void saveMemberInformation() {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        assignValues();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            System.out.println("submit");
            MemberData memberData = new MemberData();
            String id = memberData.saveMemberData(isNew, memberId, surname, name, birthDate, gender, street, apt, city, state, postalCode, emailAddress, homePhone, mobilePhone, notes, profilePicture, membershipId);
            if (isNew && (membershipId == null || membershipId.trim().isEmpty())) {
                String group = memberData.createNewMemberGroup(id);
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    progressIndicator.setProgress(0.0);
                    memberIdTextField.setText(id);
                    groupIdTextField.setText(group);
                    isNew = false;
                });
            } else if (isNew && (membershipId == null || !membershipId.trim().isEmpty())) {
                String group = memberData.getMembershipGroup(id);
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    progressIndicator.setProgress(0.0);
                    memberIdTextField.setText(id);
                    isNew = false;
                });
            } else {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    progressIndicator.setProgress(0.0);
                });
            }
            executor.shutdown();
        });

    }

    @FXML
    protected void addFamilyAction(ActionEvent event) {
        MemberMain memberMain = new MemberMain();
        memberMain.isNew = true;
        memberMain.membershipId = membershipId;
        memberMain.scrollPane = primaryScrollPane;
        try {
            memberMain.start(new Stage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    protected void editFamilyAction(ActionEvent event) {
        MemberGroup memberGroup = familyTableView.getSelectionModel().getSelectedItem();
        if (memberGroup != null) {
            getMemberInfo(String.valueOf(memberGroup.getId()));
        }
    }

    protected void getMembershipGroup(String id) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            MemberData memberData = new MemberData();
            ObservableList<MemberGroup> data = memberData.getMemberGroup(id);
            Platform.runLater(() -> {
                familyTableView.getItems().setAll(data);
            });
            executor.shutdown();
        });
    }

    @FXML
    protected void getMemberAction(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MemberSearchFXML.fxml"));
        try {
            AnchorPane root = (AnchorPane) loader.load();
            MemberSearchController memberSearchController = (MemberSearchController) loader.getController();
            memberSearchController.memberController = this;

            Window currentWindow = memberIdTextField.getScene().getWindow();
            double x = (currentWindow.getX() + currentWindow.getWidth()) / 2;
            double y = currentWindow.getY() + 40;

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Member Search");
            stage.setX(x);
            stage.setY(y);
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    protected void uploadProfilePictureAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg");
        fc.getExtensionFilters().add(extensionFilter);
        File file = fc.showOpenDialog(new Stage());
        Image image = new Image(file.toURI().toString());
        profilePictureImageView.setImage(image);
    }

    /**
     * Get the member information
     *
     * @param id
     */
    protected void getMemberInfo(String id) {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        ExecutorService executor = Executors.newCachedThreadPool();
        memberId = id;
        executor.submit(() -> {
            MemberData memberData = new MemberData();
            HashMap<String, String> data = memberData.memberData(memberId);
            membershipId = data.get("MEMBERSHIP_ID");

            ObservableList<MemberGroup> memberGroupData = memberData.getMemberGroup(membershipId);

            // Get the member's profile picture
            Image image = data.get("PROFILE_PICTURE") != null ? new Image(data.get("PROFILE_PICTURE")) : new Image(getClass().getResource("/icons/avatar.png").toString());

            Platform.runLater(() -> {
                if (!data.isEmpty()) {
                    memberIdTextField.setText(id);
                    surnameTextField.setText(data.get("SURNAME"));
                    nameTextField.setText(data.get("NAME"));
                    birthDatePicker.setValue(LocalDate.parse(data.get("DATE_OF_BIRTH"), toLocalDateFormat));
                    genderTextField.setText(data.get("GENDER"));
                    streetTextField.setText(data.get("STREET"));
                    aptSuiteTextField.setText(data.get("APT"));
                    cityTextField.setText(data.get("CITY"));
                    stateComboBox.getSelectionModel().select(data.get("STATE"));
                    zipCodeTextField.setText(data.get("POSTAL_CODE"));
                    emailAddressTextField.setText(data.get("EMAIL_ADDRESS"));
                    homePhoneTextField.setText(data.get("HOME_PHONE"));
                    mobilePhoneTextField.setText(data.get("MOBILE_PHONE"));
                    notesTextField.setText(data.get("NOTES"));
                    groupIdTextField.setText(data.get("MEMBERSHIP_ID"));
                    profilePictureImageView.setImage(image);
                    familyTableView.getItems().setAll(memberGroupData);
                }
                progressIndicator.setVisible(false);
                progressIndicator.setProgress(0);
            });
            executor.shutdown();
        });
    }

    /**
     * Set the key, click, and other listeners
     */
    private void setListeners() {

        // Search for the member on enter
        memberIdTextField.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                getMemberInfo(memberIdTextField.getText());
            }
        });

        // Listen for double click on table
        familyTableView.setRowFactory(tv -> {
            TableRow<MemberGroup> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    MemberGroup data = row.getItem();
                    memberId = row.getId();
                    getMemberInfo(memberId);
                }
            });
            return row;
        });
    }

    private void initTableViews() {
        memberGroupTableController = new MemberGroupTableController();
        memberGroupTableController.tableController(familyTableView);
        if (groupIdTextField != null && !groupIdTextField.getText().trim().isEmpty()) {
            membershipId = groupIdTextField.getText();
            getMembershipGroup(membershipId);
        }
        
    }

    protected void initInputs() {
        stateComboBox.getItems().setAll(STATE_LIST);
    }

    protected void disableInputs(boolean enable) {
        surnameTextField.setEditable(enable);
        nameTextField.setEditable(enable);
        birthDatePicker.setEditable(enable);
        genderTextField.setEditable(enable);
        streetTextField.setEditable(enable);
        aptSuiteTextField.setEditable(enable);
        cityTextField.setEditable(enable);
        stateComboBox.setDisable(!enable);
        zipCodeTextField.setEditable(enable);
        emailAddressTextField.setEditable(enable);
        homePhoneTextField.setEditable(enable);
        mobilePhoneTextField.setEditable(enable);
        notesTextField.setEditable(enable);
        memberIdTextField.setEditable(enable);
        getMemberButton.setDisable(!enable);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(0.0);
        initInputs();
        initTableViews();
        setListeners();
    }

}
