/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecontrollers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author cmeehan
 */
public class PoolOccupancyTableController {

    public void tableController(TableView tableView) {
        TableColumn<PoolOccupancy, Integer> idColumn = new TableColumn<>("");
        TableColumn<PoolOccupancy, Integer> memberIdColumn = new TableColumn<>("ID");
        TableColumn<PoolOccupancy, String> surnameColumn = new TableColumn<>("Surname");
        TableColumn<PoolOccupancy, String> nameColumn = new TableColumn<>("Name");
        TableColumn<PoolOccupancy, Integer> membershipIdColumn = new TableColumn<>("Membership ID");
        TableColumn<PoolOccupancy, String> checkInOutColumn = new TableColumn<>("Check In/Out");
        TableColumn<PoolOccupancy, String> timeInColumn = new TableColumn<>("Time In");
        TableColumn<PoolOccupancy, String> timeOutColumn = new TableColumn<>("Time Out");
        TableColumn<PoolOccupancy, Boolean> isGuestColumn = new TableColumn<>("");

        // Add the nested columns
        checkInOutColumn.getColumns().addAll(timeInColumn, timeOutColumn);

        // Set the cell value factory
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        membershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("membershipId"));
        timeInColumn.setCellValueFactory(new PropertyValueFactory<>("timeIn"));
        timeOutColumn.setCellValueFactory(new PropertyValueFactory<>("timeOut"));
        isGuestColumn.setCellValueFactory(new PropertyValueFactory<>("isGuest"));

        // Set column preferred width
        idColumn.setMaxWidth(-5);
        idColumn.setResizable(false);
        idColumn.setVisible(false);
        memberIdColumn.setPrefWidth(75);
        surnameColumn.setPrefWidth(253);
        nameColumn.setPrefWidth(219);
        membershipIdColumn.setPrefWidth(103);
        timeInColumn.setPrefWidth(75);
        timeOutColumn.setPrefWidth(75);
        checkInOutColumn.setPrefWidth(150);
        isGuestColumn.setMaxWidth(-5);
        isGuestColumn.setResizable(false);
        isGuestColumn.setVisible(false);

        tableView.setPlaceholder(new Label("The table is empty"));

        tableView.getColumns().setAll(idColumn, memberIdColumn, surnameColumn, nameColumn, membershipIdColumn, checkInOutColumn, isGuestColumn);

    }

    public static class PoolOccupancy {

        private final SimpleIntegerProperty id, memberId, membershipId;
        private final SimpleStringProperty surname, name, timeIn, timeOut;
        private final SimpleBooleanProperty isGuest;

        public PoolOccupancy(Integer id, Integer memberId, String surname, String name, Integer membershipId, String timeIn, String timeOut, Boolean isGuest) {
            this.id = new SimpleIntegerProperty(id);
            this.memberId = new SimpleIntegerProperty(memberId);
            this.membershipId = new SimpleIntegerProperty(membershipId);
            this.surname = new SimpleStringProperty(surname);
            this.name = new SimpleStringProperty(name);
            this.timeIn = new SimpleStringProperty(timeIn);
            this.timeOut = new SimpleStringProperty(timeOut);
            this.isGuest = new SimpleBooleanProperty(isGuest);
        }

        public void setId(Integer newVal) {
            id.set(newVal);
        }

        public Integer getId() {
            return id.get();
        }

        public void setMemberId(Integer newVal) {
            memberId.set(newVal);
        }

        public Integer getMemberId() {
            return memberId.get();
        }

        public void setMembershipId(Integer newVal) {
            membershipId.set(newVal);
        }

        public Integer getMembershipId() {
            return membershipId.get();
        }

        public void setSurname(String newVal) {
            surname.set(newVal);
        }

        public String getSurname() {
            return surname.get();
        }

        public void setName(String newVal) {
            name.set(newVal);
        }

        public String getName() {
            return name.get();
        }

        public void setTimeIn(String newVal) {
            timeIn.set(newVal);
        }

        public String getTimeIn() {
            return timeIn.get();
        }

        public void setTimeOut(String newVal) {
            timeOut.set(newVal);
        }

        public String getTimeOut() {
            return timeOut.get();
        }

        public void setIsGuest(Boolean newVal) {
            isGuest.set(newVal);
        }

        public Boolean getIsGuest() {
            return isGuest.get();
        }
    }
}
