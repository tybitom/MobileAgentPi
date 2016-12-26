/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ActivityInformations;

import ServerCommunication.AgentMsgSender;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Tomek
 */
public final class AgentConfigurator {

    AgentMsgSender agentMsgSender;

    static final Logger logger = Logger.getLogger(AgentConfigurator.class.getName());

    // reads an agent configuration from a file and sends it to the server
    public AgentConfigurator(AgentMsgSender agentMsgSender) {
        this.agentMsgSender = agentMsgSender;
    }

    // reads the information for registration in the Mobile Agent system
    // and sends them to the server
    public boolean configureRPi() {
        boolean result = false;
        JSONObject configurationJSON = readConfiguration("RPiConfig.txt");
        if (configurationJSON != null) {
            try {
                AgentInformations.getInstance().setName((String) configurationJSON.get("Name"));
                AgentInformations.getInstance().setLanguage((String) configurationJSON.get("Language"));

                AgentInformations.getInstance().setAction_protocol(configurationJSON.get("Action_protocol"));
                AgentInformations.getInstance().setAction_requesttype((int) configurationJSON.get("Action_requesttype"));
                AgentInformations.getInstance().setAction_receiver(configurationJSON.get("Action_receiver"));
                AgentInformations.getInstance().setAction_sender(configurationJSON.get("Action_sender"));

                AgentInformations.getInstance().setSensors((JSONArray) (configurationJSON.get("Sensors")));
                
                registerAgent(configurationJSON.toString());

                ArrayList<String> features = readFeaturesConfiguration("Features.txt");
                for(String c : features) {
                    AgentInformations.getInstance().setFeature(c);
                    reportFeature(c);
                }                    
                result = true;
            } catch (JSONException ex) {
                logger.log(Level.SEVERE,
                        "Error trying to retrive configuration information for RPi", ex);
            }
        }
        return result;
    }

    // reads the information for registration from file
    private JSONObject readConfiguration(String fileName) {
        JSONObject configurationJSON = null;
        try {
            InputStream inputStream = new FileInputStream(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String configurationString = "";
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    //System.out.println(line);
                    configurationString += line;
                }
                try {
                    configurationJSON = new JSONObject(configurationString);
                    configurationJSON = configurationJSON.put("actionTime",
                            new Timestamp(System.currentTimeMillis()));
                } catch (JSONException ex) {
                    configurationJSON = null;
                    logger.log(Level.SEVERE,
                            "Not able to create JSON. Probably RPi config file is invalid! ", ex);
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Exception during RPi file reading", ex);
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE,
                    "Unable to find RPi configuration file!", ex);
        }
        return configurationJSON;
    }

    // sends a registration message
    private void registerAgent(String configuration) {
        agentMsgSender.send(configuration, "acl_agents");
    }

    // reads agent configuration regarding features from file
    private ArrayList<String> readFeaturesConfiguration(String fileName) {
        ArrayList<String> features = new ArrayList<>();
        try {
            InputStream inputStream = new FileInputStream(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    //System.out.println(line);
                    features.add(line);
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Exception during agent features file reading", ex);
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE,
                    "Unable to find agent features file!", ex);
        }
        return features;
    }

    // reports features to the server
    private void reportFeature(String featureName) {
        JSONObject JSONmessage = new JSONObject();
        try {
            JSONmessage.put("actionTime",
                    new Timestamp(System.currentTimeMillis()));
            JSONmessage.put("featureName", featureName);
            JSONmessage.put("language",
                    AgentInformations.getInstance().getLanguage());
            JSONmessage.put("action_protocol",
                    AgentInformations.getInstance().getAction_protocol());
            JSONmessage.put("action_receiver",
                    AgentInformations.getInstance().getAction_receiver());
        } catch (JSONException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        agentMsgSender.send(JSONmessage.toString(), "acl_features");
    }
}
