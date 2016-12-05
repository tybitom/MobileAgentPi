/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Camera;

import ActivityInformations.AgentInformations;
import ServerCommunication.AgentMsgSender;
import ServerCommunication.MessageType;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Tomek
 */
public class Monitoring implements Runnable {

    static AgentMsgSender agentMsgSender;
    boolean isRunOnPi;
    boolean runFurther;

    Camera camera;

    public Monitoring(AgentMsgSender agentMsgSender) {
        Monitoring.agentMsgSender = agentMsgSender;

        isRunOnPi = checkIfRunningOnPi();
        if (isRunOnPi) {
            camera = new Camera();
        }
    }

    private void reportObject(String objectName, String objectDescription) {
        JSONObject JSONmessage = new JSONObject();
        try {
            JSONmessage.put("actionTime",
                    new Timestamp(System.currentTimeMillis()));
            JSONmessage.put("objectName", objectName);
            JSONmessage.put("objectDescription", objectDescription);

            agentMsgSender.send(JSONmessage.toString(), "acl_registeredobjects");
        } catch (JSONException ex) {
            Logger.getLogger(Monitoring.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void assessObject(Object object, double assessmentValue, String featureName) {
        JSONObject JSONmessage = new JSONObject();
        try {
            JSONmessage.put("actionTime",
                    new Timestamp(System.currentTimeMillis()));
            JSONmessage.put("featureName", featureName);
            JSONmessage.put("assessmentValue", assessmentValue);
            JSONmessage.put("object", object);
            JSONmessage.put("language",
                    AgentInformations.getInstance().getLanguage());
            JSONmessage.put("action_protocol",
                    AgentInformations.getInstance().getAction_protocol());
            JSONmessage.put("action_receiver",
                    AgentInformations.getInstance().getAction_receiver());

            agentMsgSender.send(JSONmessage.toString(), "acl_assessments");
        } catch (JSONException ex) {
            Logger.getLogger(Monitoring.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void registerFeature(String featureName) {
        JSONObject JSONmessage = new JSONObject();
        try {
            JSONmessage.put("actionTime",
                    new Timestamp(System.currentTimeMillis()));
            JSONmessage.put("featureName", featureName);
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

            agentMsgSender.send(JSONmessage.toString(), "acl_features");
        } catch (JSONException ex) {
            Logger.getLogger(Monitoring.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        agentMsgSender.send("Starting Monitoring Thread!", MessageType.LOG_MSG);
        if (isRunOnPi) {
            while (runFurther) {
                BufferedImage takeAPicture = camera.takeAPicture();
                if (takeAPicture != null) {
                    agentMsgSender.send(takeAPicture.toString(), "acl_graphicalreadouts");
                }
            }
        }
        agentMsgSender.send("Ending Monitoring thread", MessageType.LOG_MSG);
    }

    public void stopThread() {
        this.runFurther = false;
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
            } else {
                Logger.getLogger(Monitoring.class.getName()).log(Level.SEVERE,
                        "The host is not Raspberry Pi. This thread will not work!");
            }
        }
        return result;
    }
}
