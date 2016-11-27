/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RPiSensors;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Tomek
 */
public class SensorManager implements Runnable {

    ArrayList<I2Csensor> sensors;
    boolean isRunOnPi;

    public SensorManager() {
        isRunOnPi = checkIfRunningOnPi();
        if (isRunOnPi) {
            sensors.add(new I2Csensor());
        }
    }

    @Override
    public void run() {
        if (isRunOnPi) {
            sensors.forEach((sensor) -> {
                sensor.readSensorValue();
            });
        }
    }

    private boolean checkIfRunningOnPi() {
        boolean result = false;
        String osName = System.getProperty("os.name");
        //System.out.println(property);
        if (osName.toLowerCase().contains("linux")) {
            Map<String, String> env = System.getenv();
            if (env.containsKey("user") && env.get("user").contains("pi")) {
                //System.out.println(env.get("user"));
                result = true;
            }
        }
        return result;
    }
}
