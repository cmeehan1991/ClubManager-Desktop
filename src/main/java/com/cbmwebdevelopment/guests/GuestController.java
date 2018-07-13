/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.guests;

import static com.cbmwebdevelopment.clubmanager.MainApp.STATE_LIST;
import com.cbmwebdevelopment.tablecontrollers.GuestAttendanceTableController;
import com.cbmwebdevelopment.tablecontrollers.GuestAttendanceTableController.GuestAttendance;
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
public class GuestController implements Initializable {

    @FXML
    ScrollPane primaryScrollPane;

    @FXML
    ProgressIndicator progressIndicator;

    @FXML
    AnchorPane guestInformationAnchorPane;

    @FXML
    TextField guestIdTextField, surnameTextField, nameTextField, genderTextField, streetTextField, aptSuiteTextField, cityTextField, zipCodeTextField, emailAddressTextField, homePhoneTextField, mobilePhoneTextField, notesTextField, groupIdTextField;

    @FXML
    DatePicker birthDatePicker;

    @FXML
    PrefixSelectionComboBox stateComboBox;

    @FXML
    ImageView profilePictureImageView;

    @FXML
    TableView attendanceTableView;

    @FXML
    Button getGuestButton;

    @FXML
    TabPane tabPane;

    private String guestId, surname, name, birthDate, gender, street, apt, city, state, postalCode, emailAddress, homePhone, mobilePhone, notes;
    private File profilePicture;
    protected boolean isNew;
    private GuestAttendanceTableController guestAttendanceTableController;
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
        guestId = null;
        guestIdTextField.clear();
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
        attendanceTableView.getItems().clear();
        Image image = new Image(getClass().getResource("/icons/avatar.png").toString());
        profilePictureImageView.setImage(image);

    }

    @FXML
    protected void saveGuestInformation() {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        assignValues();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            System.out.println("submit");
            GuestData guestData = new GuestData();
            String id = guestData.saveGuestData(isNew, guestId, surname, name, birthDate, gender, street, apt, city, state, postalCode, emailAddress, homePhone, mobilePhone, notes, profilePicture);
            if (isNew) {
                String group = guestData.createNewGuestGroup(id);
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    progressIndicator.setProgress(0.0);
                    guestIdTextField.setText(id);
                    groupIdTextField.setText(group);
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
    protected void getGuestAction(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GuestSearchFXML.fxml"));
        try {
            AnchorPane root = (AnchorPane) loader.load();
            GuestSearchController guestSearchController = (GuestSearchController) loader.getController();
            guestSearchController.guestController = this;

            Window currentWindow = guestIdTextField.getScene().getWindow();
            double x = (currentWindow.getX() + currentWindow.getWidth()) / 2;
            double y = currentWindow.getY() + 40;

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Guest Search");
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
     * Get the guest information
     *
     * @param id
     */
    protected void getGuestInfo(String id) {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        ExecutorService executor = Executors.newCachedThreadPool();
        guestId = id;
        executor.submit(() -> {
            GuestData guestData = new GuestData();
            HashMap<String, String> data = guestData.guestData(guestId);

            // Get the guest's profile picture
            Image image = data.get("PROFILE_PICTURE") != null ? new Image(data.get("PROFILE_PICTURE")) : new Image(getClass().getResource("/icons/avatar.png").toString());

            Platform.runLater(() -> {
                if (!data.isEmpty()) {
                    guestIdTextField.setText(id);
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
                }
                progressIndicator.setVisible(false);
                progressIndicator.setProgress(0);
            });
        });
    }

    /**
     * Set the key, click, and other listeners
     */
    private void setListeners() {

        // Search for the guest on enter
        guestIdTextField.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                getGuestInfo(guestIdTextField.getText());
            }
        });

        // Listen for double click on table
        attendanceTableView.setRowFactory(tv -> {
            TableRow<GuestAttendance> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    GuestAttendance data = row.getItem();
                    guestId = row.getId();
                    getGuestInfo(guestId);
                }
            });
            return row;
        });
    }

    private void initTableViews() {
        guestAttendanceTableController = new GuestAttendanceTableController();
        guestAttendanceTableController.tableController(attendanceTableView);
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
        guestIdTextField.setEditable(enable);
        getGuestButton.setDisable(!enable);
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
