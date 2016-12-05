/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileagentpi;

/**
 *
 * @author Tomek
 */
import ActivityInformations.AgentInformations;
import ActivityInformations.AgentConfigurator;
import AgentContol.MobileAgentContoller;
import ArduinoCommunication.ArduinoCommunication;
import Camera.Monitoring;
import RPiSensors.SensorManager;
import ServerCommunication.AgentMsgSender;
import ServerCommunication.ToConsoleSender;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class MobileAgentPi {

    boolean runFurther = true;

    AgentConfigurator agentConfigurator;
    AgentMsgSender agentMsgSender;
    ArduinoCommunication arduinoCommunicationThread;
    MobileAgentContoller agentContoller;
    SensorManager sensorManager;
    Monitoring monitoring;

    public MobileAgentPi() {

    }

    public boolean initialize() {
        agentMsgSender = ToConsoleSender.getInstance(); // ToServerSender.getInstance(); //
        agentConfigurator = new AgentConfigurator(agentMsgSender);
        if (!agentConfigurator.configureRPi()) {
            Logger.getLogger(MobileAgentPi.class.getName()).log(Level.SEVERE,
                    "Error in configuration for RPi.");
            return false;
        }
        reportPresence();
        arduinoCommunicationThread = new ArduinoCommunication(38400, agentMsgSender);
        agentContoller = new MobileAgentContoller(agentMsgSender);
        sensorManager = new SensorManager(agentMsgSender);
        monitoring = new Monitoring(agentMsgSender);
        return true;
    }

    private void reportPresence() {
        JSONObject presenceJSON = new JSONObject();

        try {
            presenceJSON.put("actionTime",
                    new Timestamp(System.currentTimeMillis()));
            presenceJSON.put("action_protocol",
                    AgentInformations.getInstance().getAction_protocol());
            presenceJSON.put("action_requesttype",
                    AgentInformations.getInstance().getAction_requesttype());
            presenceJSON.put("action_receiver",
                    AgentInformations.getInstance().getAction_receiver());
            presenceJSON.put("action_sender",
                    AgentInformations.getInstance().getAction_sender());

            agentMsgSender.send(presenceJSON.toString(), "acl_presencerequests");
        } catch (JSONException ex) {
            Logger.getLogger(MobileAgentPi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void runThreads() {
        if (initialize()) {
            // ArduinoCommunication has event listener
            Thread agentContollerThread = new Thread(agentContoller);
            Thread sensorManagerThread = new Thread(sensorManager);
            Thread monitoringThread = new Thread(monitoring);

            agentContollerThread.start();
            sensorManagerThread.start();
            monitoringThread.start();

            while (runFurther) {
                if (!agentContollerThread.isAlive()) {
                    sensorManager.stopThread();
                    monitoring.stopThread();
                    runFurther = false;
                }
            }
            System.out.println("Exiting program!");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MobileAgentPi mobileAgentPi = new MobileAgentPi();
        mobileAgentPi.runThreads();
    }

    /*static void tryServerConnection() {
        ServerCommunication serverCommunication = new ServerCommunication("/MobileAgentsService/api/logMessages");

        try {
            InputStream crunchifyInputStream = new FileInputStream("C:/Users/Tomek/Documents/CrunchifyJSON.txt"); // /home/pi/Documents/CrunchifyJSON.txt
            InputStreamReader crunchifyReader = new InputStreamReader(crunchifyInputStream);
            BufferedReader br = new BufferedReader(crunchifyReader);
            String line;
            String json = "";
            while ((line = br.readLine()) != null) {
                json += line + "\n";
            }

            serverCommunication.send(json);
        } catch (IOException e) {
            System.out.println("Problem with reading json file occured! " + e.getLocalizedMessage());
        }
    }*/
}
