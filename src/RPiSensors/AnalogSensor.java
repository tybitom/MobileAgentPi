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
public abstract class AnalogSensor extends Sensor {

    int pin;

    public AnalogSensor(String name, double accuracy, String unit, String type) {
        this.name = name;
        this.accuracy = accuracy;
        this.unit = unit;
        this.type = type;
        this.measurements = new ArrayList<>();
    }

    //reads configuration from file where file name equals sensor name
    public boolean readConfigurationFromFile() {
        return true;
    }

    @Override
    public void measure() {
    }
}
