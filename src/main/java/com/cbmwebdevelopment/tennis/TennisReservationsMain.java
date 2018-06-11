/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tennis;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.popover.EntryDetailsView;
import com.calendarfx.view.popover.EntryHeaderView;
import com.calendarfx.view.popover.PopOverContentPane;
import java.time.LocalDate;
import java.time.LocalTime;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import org.controlsfx.control.PrefixSelectionComboBox;

/**
 *
 * @author cmeehan
 */
public class TennisReservationsMain extends Application {

    public ScrollPane scrollPane;
    private CalendarView calendarView;
    private PrefixSelectionComboBox membersComboBox = new CustomPopOver().membersComboBox();

    @Override
    public void start(Stage primaryStage) {
        calendarView = new CalendarView();
        EventHandler<CalendarEvent> eventHandler = evt -> handleEvent(evt);
        
                
        Calendar reservations = new Calendar("Court Reservations");
        Calendar maintenance = new Calendar("Maintenance");
        Calendar clinics = new Calendar("Clinics");


        // Set the event handler
        reservations.addEventHandler(eventHandler);
        maintenance.addEventHandler(eventHandler);
        clinics.addEventHandler(eventHandler);
        
        
        calendarView.setEntryDetailsPopOverContentCallback(param -> {         
            
            PopOverContentPane popOverContentPane = new PopOverContentPane();
           // popOverContentPane.setHeader(new CustomPopOver().headerContent());
            
            EntryHeaderView entryHeaderView = new EntryHeaderView(param.getEntry(), param.getDateControl().getCalendars());
            PrefixSelectionComboBox reservationType = new CustomPopOver().reservationType();
            entryHeaderView.add(membersComboBox, 0, 3);
            
            EntryDetailsView entryDetailsView = new EntryDetailsView(param.getEntry());
            popOverContentPane.setCenter(entryDetailsView);
            
            param.getEntry().titleProperty().set("Hello, world");
            
            popOverContentPane.setHeader(entryHeaderView);
            
            return popOverContentPane;
        });

        
        
        //calendarView.addEventHandler(CalendarEvent.ANY, eventHandler);

        // Set calendar styles
        reservations.setStyle(Style.STYLE1);
        maintenance.setStyle(Style.STYLE2);
        clinics.setStyle(Style.STYLE3);

        CalendarSource myCalendarSource = new CalendarSource("Calendars");
        myCalendarSource.getCalendars().setAll(reservations, maintenance, clinics);

        calendarView.getCalendarSources().add(myCalendarSource);

        calendarView.setRequestedTime(LocalTime.now());

        Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
            @Override
            public void run() {
                while (true) {
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    });

                    try {
                        // update every 10 seconds
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        ;
        };

        updateTimeThread.setPriority(Thread.MIN_PRIORITY);
        updateTimeThread.setDaemon(true);
        updateTimeThread.start();

        Scene scene = new Scene(calendarView);
        scrollPane.setContent(calendarView);
    }

    public void handleEvent(CalendarEvent event) {
        if (event.getEventType() == CalendarEvent.ENTRY_CALENDAR_CHANGED) {
            System.out.println("Event: " + event);
        }
    }

    public void customizePopover() {
        calendarView.getEntryDetailsPopOverContentCallback();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
