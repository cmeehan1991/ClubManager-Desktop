/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecells;

import com.cbmwebdevelopment.tablecontrollers.ChemicalDataTableController.Chemicals;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DateTimeStringConverter;

/**
 *
 * @author cmeehan
 */
public class TableCellTimePicker extends TableCell<Chemicals, String> {

    private Calendar currentTime = Calendar.getInstance();
    private TextField textField;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public TableCellTimePicker() {
        super();
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setText(Calendar.HOUR + ":" + Calendar.MINUTE);
            setGraphic(textField);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(textField.getText());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(String.valueOf(item));
                }
                setText(item);
                setGraphic(textField);
            } else {
                setText(item);
                setGraphic(null);
            }
        }
    }

    private void createTextField() {
        textField = new TextField();
        try {
            textField.setTextFormatter(new TextFormatter<>(new DateTimeStringConverter(timeFormat), timeFormat.parse(currentTime.toString())));
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
