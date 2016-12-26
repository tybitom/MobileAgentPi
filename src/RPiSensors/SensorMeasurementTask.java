/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RPiSensors;

import ActivityInformations.AgentInformations;
import static RPiSensors.SensorManager.agentMsgSender;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Tomek
 */
public class SensorMeasurementTask extends Sensor {

    Sensor sensor;

    long lastExecutionTime;
    long dt = 1000;

    // wrapper class for sensor containing time difference between measurements
    public SensorMeasurementTask(Sensor sensor) {
        this.sensor = sensor;
        lastExecutionTime = System.currentTimeMillis();
    }

    @Override
    public void measure() {
        long now = System.currentTimeMillis();
        if ((now - lastExecutionTime) > dt) {
            sensor.measure();
            lastExecutionTime = now;
            reportMeasurement(sensor.getMeasurements());
        }
    }

    @Override
    public List getMeasurements() {
        return sensor.getMeasurements();
    }

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    // reports a measurement to the server
    private void reportMeasurement(List<Pair> measurements) {
        if (measurements != null && !measurements.isEmpty()) {
            JSONObject JSONmessage = new JSONObject();
            try {
                JSONmessage.put("actionTime",
                        new Timestamp(System.currentTimeMillis()));
                for (Pair measurement : measurements) {
                    JSONmessage.put((String) measurement.getKey(), measurement.getValue());
                }
                JSONmessage.put("language",
                        AgentInformations.getInstance().getLanguage());
                JSONmessage.put("action_protocol",
                        AgentInformations.getInstance().getAction_protocol());
                JSONmessage.put("action_requesttype",
                        AgentInformations.getInstance().getAction_requesttype());
                JSONmessage.put("action_receiver",
                        AgentInformations.getInstance().getAction_receiver());
                JSONmessage.put("action_sender",
                        AgentInformations.getInstance().getAction_sender());

                agentMsgSender.send(JSONmessage.toString(), "acl_readouts");

            } catch (JSONException ex) {
                Logger.getLogger(SensorMeasurementTask.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
