/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tennis;

import com.cbmwebdevelopment.member.MemberData;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.control.PrefixSelectionComboBox;

/**
 *
 * @author cmeehan
 */
public class CustomPopOver {

    FilteredList<String> items;

    public PrefixSelectionComboBox reservationType(){
        PrefixSelectionComboBox reservationType = new PrefixSelectionComboBox();
        reservationType.getItems().setAll("Court Maintenance", "Clinic", "Private Lesson", "Reservation", "Walk On", "Tournament");
        
        return reservationType;
    }
    
    public GridPane headerContent() {
        GridPane gridPane = new GridPane();

        GridPane.setHgrow(gridPane, Priority.ALWAYS);
        GridPane.setVgrow(gridPane, Priority.ALWAYS);
        
        gridPane.setHgap(5);
        gridPane.setPadding(new Insets(0, 10, 0, 10));

        // Gridpane Content
        Label label = new Label("Reservation");
        label.getStyleClass().add("sectionLabel");
        PrefixSelectionComboBox reservationType = new PrefixSelectionComboBox();
        reservationType.getItems().setAll("Court Maintenance", "Clinic", "Private Lesson", "Reservation", "Walk On", "Tournament");

        PrefixSelectionComboBox members = membersComboBox();

        reservationType.valueProperty().addListener((obs, ov, nv) -> {
            if (nv.toString().equals("Court Maintenance") || nv.toString().equals("Clinic") || nv.toString().equals("Tournament")) {
                members.getSelectionModel().select(-1);
                members.setDisable(true);
            } else {
                members.setDisable(false);
            }
        });
        gridPane.add(label, 0, 0);
        gridPane.add(reservationType, 0, 1);
        gridPane.add(members, 0, 2);

        return gridPane;
    }

    public TitledPane titledPane() {
        TitledPane pane = new TitledPane();
        pane.setText("Details");
        pane.setContent(detailsContent());
        pane.setExpanded(true);
        
        return pane;
    }

    private GridPane detailsContent() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(5);

        // Start Date
        Label startLabel = new Label("Start:");
        DatePicker startDatePicker = new DatePicker();

        // All day
        CheckBox allDayCheckBox = new CheckBox("All day");

        // End Date
        Label endLabel = new Label("End:");
        DatePicker endDatePicker = new DatePicker();

        allDayCheckBox.selectedProperty().addListener((obs, ov, nv) -> {
            endLabel.setVisible(!nv);
            endDatePicker.setVisible(!nv);
        });

        gridPane.add(startLabel, 0, 0);
        gridPane.add(startDatePicker, 1, 0);
        
        GridPane.setColumnSpan(allDayCheckBox, 2);
        gridPane.add(allDayCheckBox, 0, 1);
        
        gridPane.add(endLabel, 0, 2);
        gridPane.add(endDatePicker, 1, 2);
        
        
        return gridPane;
    }

    public PrefixSelectionComboBox membersComboBox() {
        PrefixSelectionComboBox comboBox = new PrefixSelectionComboBox();
        MemberData memberData = new MemberData();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            ObservableList<String> data = memberData.membersList();
                items = new FilteredList<>(data);
                System.out.println(items);
            Platform.runLater(() -> {
                comboBox.getItems().setAll(items);
                comboBox.getEditor().textProperty().addListener(new InputFilter(comboBox, items, false));
                comboBox.setDisable(false);
            });
            executor.shutdown();
        });
        // Set the combobox to editable so the user can filter
        comboBox.setEditable(true);
        

        return comboBox;
    }

    class InputFilter implements ChangeListener<String> {

        private PrefixSelectionComboBox<String> box;
        private FilteredList<String> items;
        private boolean upperCase;
        private int maxLength;
        private String restriction;

        public InputFilter(PrefixSelectionComboBox<String> box, FilteredList<String> items, boolean upperCase, int maxLength,
                String restriction) {
            this.box = box;
            this.items = items;
            this.upperCase = upperCase;
            this.maxLength = maxLength;
            this.restriction = restriction;
        }

        public InputFilter(PrefixSelectionComboBox<String> box, FilteredList<String> items, boolean upperCase, int maxLength) {
            this(box, items, upperCase, maxLength, null);
        }

        public InputFilter(PrefixSelectionComboBox<String> box, FilteredList<String> items, boolean upperCase) {
            this(box, items, upperCase, -1, null);
        }

        public InputFilter(PrefixSelectionComboBox<String> box, FilteredList<String> items) {
            this(box, items, false);
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            StringProperty value = new SimpleStringProperty(newValue);

            // If any item is selected we save that reference.
            String selected = box.getSelectionModel().getSelectedItem() != null
                    ? box.getSelectionModel().getSelectedItem() : null;

            String selectedString = null;
            // We save the String of the selected item.
            if (selected != null) {
                selectedString = (String) selected;
            }

            if (upperCase) {
                value.set(value.get().toUpperCase());
            }

            if (maxLength >= 0 && value.get().length() > maxLength) {
                value.set(oldValue);
            }

            if (restriction != null) {
                if (!value.get().matches(restriction + "*")) {
                    value.set(oldValue);
                }
            }

            // If an item is selected and the value in the editor is the same
            // as the selected item we don't filter the list.
            if (selected != null && value.get().equals(selectedString)) {
                // This will place the caret at the end of the string when
                // something is selected.
                Platform.runLater(() -> box.getEditor().end());
            } else {
                items.setPredicate(item -> {
                    String itemString = item;
                    if (itemString.toUpperCase().contains(value.get().toUpperCase())) {
                        return true;
                    } else {
                        return false;
                    }
                });
            }

            // If the popup isn't already showing we show it.
            if (!box.isShowing()) {
                // If the new value is empty we don't want to show the popup,
                // since
                // this will happen when the combo box gets manually reset.
                if (!newValue.isEmpty() && box.isFocused()) {
                    box.show();
                }
            } // If it is showing and there's only one item in the popup, which is
            // an
            // exact match to the text, we hide the dropdown.
            else {
                if (items.size() == 1) {
                    // We need to get the String differently depending on the
                    // nature
                    // of the object.
                    String item = items.get(0);

                    // To get the value we want to compare with the written
                    // value, we need to crop the value according to the current
                    // selectionCrop.
                    String comparableItem = item;

                    if (value.get().equals(comparableItem)) {
                        Platform.runLater(() -> box.hide());
                    }
                }
            }

            box.getEditor().setText(value.get());
        }
    }
}
