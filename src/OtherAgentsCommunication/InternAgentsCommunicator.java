/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OtherAgentsCommunication;

import ActivityInformations.AgentInformations;
import Camera.Monitoring;
import ServerCommunication.AgentMsgSender;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Tomek
 */
public class InternAgentsCommunicator {

    AgentMsgSender agentMsgSender;

    static final Logger logger = Logger.getLogger(InternAgentsCommunicator.class.getName());

    public InternAgentsCommunicator(AgentMsgSender agentMsgSender) {
        this.agentMsgSender = agentMsgSender;
    }

    public void communicate() {
        String internalMessage = "";

        agentMsgSender.send(internalMessage, "acl_assessments");
    }

    private void registerNewInteractionProtocol(String protocolName, String protocolDescription) {
        JSONObject JSONmessage = new JSONObject();
        try {
            JSONmessage.put("actionTime",
                    new Timestamp(System.currentTimeMillis()));
            JSONmessage.put("protocolName", protocolName);
            JSONmessage.put("protocolDescription", protocolDescription);
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

            agentMsgSender.send(JSONmessage.toString(), "acl_interactionprotocols");
        } catch (JSONException ex) {
            Logger.getLogger(Monitoring.class.getName()).log(Level.SEVERE, null, ex);
        }
        agentMsgSender.send(JSONmessage.toString(), "acl_features");
    }
}
