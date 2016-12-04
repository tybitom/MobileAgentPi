/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RPiSensors;

import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author Tomek
 */
public abstract class Sensor {

    String name;
    double accuracy;
    String unit;
    String type;
    List<Pair> measurements;

    /*public Sensor(String name, double accuracy, String unit, String type) {
        this.name = name;
        this.accuracy = accuracy;
        this.unit = unit;
        this.type = type;
    }*/

    public void measure() {
    }

    public List getMeasurements() {
        return measurements;
    }
}
