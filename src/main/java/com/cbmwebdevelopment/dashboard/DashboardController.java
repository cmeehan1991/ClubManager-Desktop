/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.dashboard;

import com.cbmwebdevelopment.charts.Charts;
import com.cbmwebdevelopment.pool.PoolData;
import com.cbmwebdevelopment.tablecontrollers.DashboardCourtTableController;
import com.cbmwebdevelopment.tablecontrollers.DashboardCourtTableController.CourtOccupancy;
import com.cbmwebdevelopment.tennis.TennisData;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class DashboardController implements Initializable {

    @FXML
    LineChart tennisOccupancyLineChart, poolOccupancyLineChart;
    @FXML
    StackedBarChart poolChemicalsStackedBarChart;
    @FXML
    TableView outdoorCourtsUsageTableView, indoorCourtsUsageTableView;

    private DashboardCourtTableController dashboardCourtTableController;

    /**
     * Sets the data for the tennis occupancy charts and the pool occupancy
     * and chemical charts. 
     */
    private void setChartData() {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            TennisData tennisData = new TennisData();
            HashMap<String, Double> indoorData = tennisData.getOccupancy("INDOOR");
            HashMap<String, Double> outdoorData = tennisData.getOccupancy("OUTDOOR");
            Charts charts = new Charts();
            Series series1 = charts.chartSeries(indoorData);
            series1.setName("Indoor");
            
            Series series2 = charts.chartSeries(outdoorData);
            series2.setName("Outdoor");

            Platform.runLater(() -> {
                tennisOccupancyLineChart.getData().addAll(series1, series2);
            });
            executor.shutdown();
        });

        executor.submit(() -> {
            PoolData poolData = new PoolData();
            HashMap<String, Double> poolOccupancy = poolData.getOccupancy();
            HashMap<String, Double> chlorine = poolData.getChlorine(new Date());
            HashMap<String, Double> ph = poolData.getPH(new Date());

            Charts charts = new Charts();
            Series occupancySeries = charts.chartSeries(poolOccupancy);
            occupancySeries.setName("Total people");

            Series chlorineSeries = charts.chartSeries(chlorine);
            chlorineSeries.setName("Cl");

            Series phSeries = charts.chartSeries(ph);
            phSeries.setName("pH");

            Platform.runLater(() -> {
                poolOccupancyLineChart.getData().add(occupancySeries);
                poolChemicalsStackedBarChart.getData().addAll(chlorineSeries, phSeries);
            });
            executor.shutdown();
        });
    }

    private void getTableData() {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            TennisData tennisData = new TennisData();
            ObservableList<CourtOccupancy> indoorCourtOccupancyData = tennisData.getCourtOccupancy(new Date(), "INDOOR");

            Platform.runLater(() -> {
                indoorCourtsUsageTableView.getItems().setAll(indoorCourtOccupancyData);
            });

            executor.shutdown();
        });

        executor.submit(() -> {
            TennisData tennisData = new TennisData();
            ObservableList<CourtOccupancy> outdoorCourtOccupancyData = tennisData.getCourtOccupancy(new Date(), "OUTDOOR");

            Platform.runLater(() -> {
                outdoorCourtsUsageTableView.getItems().setAll(outdoorCourtOccupancyData);
            });

            executor.shutdown();
        });
    }

    private void initializeTableview() {
        dashboardCourtTableController = new DashboardCourtTableController();
        dashboardCourtTableController.tableView(indoorCourtsUsageTableView);
        dashboardCourtTableController.tableView(outdoorCourtsUsageTableView);

        getTableData();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setChartData();
        initializeTableview();
    }

}
