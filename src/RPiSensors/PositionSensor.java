/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RPiSensors;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author Tomek
 */
public class PositionSensor extends I2Csensor {

    public PositionSensor(String name, double accuracy, String unit, String type) {
        super(name, accuracy, unit, type);
        this.measurements = new ArrayList<>();

        measurements.add(new Pair("xPosition", 0.0));
        measurements.add(new Pair("yPosition", 0.0));
    }

    @Override
    public void measure() {
        measurements.set(0, new Pair("xPosition", 2.45));
        measurements.set(1, new Pair("yPosition", 5.95));
    }

    @Override
    public List getMeasurements() {
        return measurements;
    }
}
