/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RPiSensors;

import java.util.ArrayList;

/**
 *
 * @author Tomek
 */
public abstract class OneWireSensor extends Sensor {

    public OneWireSensor(String name, double accuracy, String unit, String type) {
        this.name = name;
        this.accuracy = accuracy;
        this.unit = unit;
        this.type = type;
        this.measurements = new ArrayList<>();
    }

    @Override
    public void measure() {

    }
}
