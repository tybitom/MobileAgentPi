/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RPiSensors;

import ActivityInformations.AgentInformation;
import ServerCommunication.MessageType;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import ServerCommunication.AgentMsgHandler;

/**
 * 
 * @author Tomek
 */
public class SensorManager implements Runnable {

    ArrayList<SensorMeasurementTask> sensors;
    boolean isRunOnPi;

    boolean runFurther = true;

    static AgentMsgHandler agentMsgHandler;

    // initializes all sensores that are attached and specified in a configuration file
    public SensorManager(AgentMsgHandler agentMsgHandler) {
        SensorManager.agentMsgHandler = agentMsgHandler;
        isRunOnPi = true; //checkIfRunningOnPi();
        if (isRunOnPi) {
            sensors = new ArrayList<>();
            for (int i = 0; i < AgentInformation.getInstance().getSensors().length(); i++) {
                try {
                    JSONObject sensorJSON = (JSONObject) AgentInformation.getInstance().getSensors().get(i);
                    System.out.println(sensorJSON.toString());
                    Sensor s;
                    s = createSensorBasingOnName((String) sensorJSON.get("Name"),
                            (double) sensorJSON.get("Accuracy"),
                            (String) sensorJSON.get("Unit"), (String) sensorJSON.get("Type"));
                    if (s != null) {
                        sensors.add(new SensorMeasurementTask(s));
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(SensorManager.class.getName()).log(Level.SEVERE,
                            "Trying to access outbound index of JSON sensors array. ", ex);
                }
            }
        }
    }

    @Override
    public void run() {
        agentMsgHandler.send("Starting Sensor Manager Thread!", MessageType.LOG_MSG);
        if (isRunOnPi) {
            while (runFurther) {
                for (SensorMeasurementTask sensor : sensors) {
                    sensor.measure();
                }
            }
        }
        agentMsgHandler.send("Ending Sensor Manager thread", MessageType.LOG_MSG);
    }

    // creates sensor instance basing on name from a configuration file
    // and returnes the created sensor
    private Sensor createSensorBasingOnName(String name, double accuracy, String unit, String type) {
        Sensor resultSensor = null;
        if ("positionSensor".equals(type)) {
            resultSensor = new PositionSensor(name, accuracy, unit, type);
        }
        if (resultSensor != null) {
            Logger.getLogger(SensorManager.class
                    .getName()).log(Level.INFO,
                            "Adding sensor: {0}, {1}, {2}, {3}.",
                            new Object[]{name, accuracy, unit, type});
        } else {
            Logger.getLogger(SensorManager.class
                    .getName()).log(Level.SEVERE,
                            "No sensor could be created basing on this data: {0}, {1}, {2}, {3}. "
                            + "Please check RiConfig file.",
                            new Object[]{name, accuracy, unit, type});
        }
        return resultSensor;
    }

    // verifies if the program is running on Raspberry Pi basing on 
    // system name "linux" and username "pi"
    private boolean checkIfRunningOnPi() {
        boolean result = false;
        String osName = System.getProperty("os.name");
        //System.out.println(property);
        if (osName.toLowerCase().contains("linux")) {
            Map<String, String> env = System.getenv();
            if (env.containsKey("user") && env.get("user").contains("pi")) {
                //System.out.println(env.get("user"));
                result = true;
            } else {
                Logger.getLogger(SensorManager.class.getName()).log(Level.SEVERE,
                        "The host is not Raspberry Pi. This thread will not work!");
            }
        }
        return result;
    }

    // sets the flag runFurther to exit from while loop in the function run
    public void stopThread() {
        System.out.println("Closing SensorManager thread!");
        this.runFurther = false;
    }
}
