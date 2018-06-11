/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.charts;

import java.util.HashMap;
import javafx.scene.chart.XYChart;

/**
 *
 * @author cmeehan
 */
public class Charts {
    public XYChart.Series chartSeries(HashMap<String, Double> data){
        XYChart.Series series = new XYChart.Series();
        data.forEach((date, count)->{
            series.getData().add(new XYChart.Data(date, count));
        });
        return series;
    }
}
