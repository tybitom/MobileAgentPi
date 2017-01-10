/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OtherAgentsCommunication;

import ActivityInformations.AgentInformation;
import Camera.Monitoring;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import ServerCommunication.AgentMsgHandler;

/**
 *
 * @author Tomek
 */
public class InternalAgentsCommunicator {

    AgentMsgHandler agentMsgHandler;

    static final Logger logger = Logger.getLogger(InternalAgentsCommunicator.class.getName());

    public InternalAgentsCommunicator(AgentMsgHandler agentMsgHandler) {
        this.agentMsgHandler = agentMsgHandler;
    }

    public void communicate() {
        String internalMessage = "";

        agentMsgHandler.send(internalMessage, "acl_assessments");
    }

    private void registerNewInteractionProtocol(String protocolName, String protocolDescription) {
        JSONObject JSONmessage = new JSONObject();
        try {
            JSONmessage.put("actionTime",
                    new Timestamp(System.currentTimeMillis()));
            JSONmessage.put("protocolName", protocolName);
            JSONmessage.put("protocolDescription", protocolDescription);
            JSONmessage.put("language",
                    AgentInformation.getInstance().getLanguage());

            JSONmessage.put("action_protocol",
                    AgentInformation.getInstance().getAction_protocol());
            JSONmessage.put("action_requesttype",
                    AgentInformation.getInstance().getAction_requesttype());
            JSONmessage.put("action_receiver",
                    AgentInformation.getInstance().getAction_receiver());
            JSONmessage.put("action_sender",
                    AgentInformation.getInstance().getAction_sender());

            agentMsgHandler.send(JSONmessage.toString(), "acl_interactionprotocols");
        } catch (JSONException ex) {
            Logger.getLogger(Monitoring.class.getName()).log(Level.SEVERE, null, ex);
        }
        agentMsgHandler.send(JSONmessage.toString(), "acl_features");
    }
}
