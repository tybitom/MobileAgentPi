/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ActivityInformations;

import java.util.ArrayList;
import org.json.JSONArray;

/**
 *
 * @author Tomek
 */
public class AgentInformation {

    String name;
    JSONArray sensors;
    String language;
    Object action_protocol;
    int action_requesttype;
    Object action_receiver;
    Object action_sender;

    ArrayList<String> features;

    private static AgentInformation instance = null;

    // serves for agent information container
    // filled with the information from configuration files
    public AgentInformation() {
        features = new ArrayList<>();
    }

    public static AgentInformation getInstance() {
        if (instance == null) {
            instance = new AgentInformation();
        }
        return instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONArray getSensors() {
        return sensors;
    }

    public void setSensors(JSONArray sensors) {
        this.sensors = sensors;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAction_protocol() {
        return action_protocol.toString();
    }

    public void setAction_protocol(Object action_protocol) {
        this.action_protocol = action_protocol;
    }

    public int getAction_requesttype() {
        return action_requesttype;
    }

    public void setAction_requesttype(int action_requesttype) {
        this.action_requesttype = action_requesttype;
    }

    public String getAction_receiver() {
        return action_receiver.toString();
    }

    public void setAction_receiver(Object action_receiver) {
        this.action_receiver = action_receiver;
    }

    public String getAction_sender() {
        return action_sender.toString();
    }

    public void setAction_sender(Object action_sender) {
        this.action_sender = action_sender;
    }

    /*public ArrayList<String> getFeatures() {
        return features;
    }*/
    public String getFeature(int index) {
        return features.get(index);
    }

    public boolean hasFeature(String feature) {
        return features.contains(feature);
    }

    public void setFeatures(ArrayList<String> features) {
        this.features = features;
    }

    public void setFeature(String feature) {
        this.features.add(feature);
    }
}
