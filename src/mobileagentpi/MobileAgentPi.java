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
import Camera.Camera;
import Camera.Monitoring;
import RPiSensors.SensorManager;
import ServerCommunication.AgentMsgSender;
import ServerCommunication.ToConsoleSender;
import ServerCommunication.ToServerSender;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    // initialises all the instances of classes: sender (console or server),
    // agent Configurator having all the information about the agent,
    // communication with Arduino and others
    public boolean initialize() {
        initializeSender();
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
        try {
            monitoring = new Monitoring(agentMsgSender);
        } catch (FailedToRunRaspistillException ex) {
            Logger.getLogger(MobileAgentPi.class.getName()).log(Level.SEVERE, "Trying to initialize camera failed!", ex);
            monitoring = null;
        }
        return true;
    }

    // initialises the type of sender:
    // server with a provided in file ip address or console otherwise
    void initializeSender() {
        try {
            InputStream inputStream = new FileInputStream("ServerConfig.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String configurationString;
            try {
                if ((configurationString = br.readLine()) != null) {
                    // ip of the server
                    Logger.getLogger(MobileAgentPi.class.getName()).log(Level.INFO,
                            "Sender set to ServerSender: {0}", configurationString);
                    agentMsgSender = ToServerSender.getInstance(configurationString);
                } else {
                    Logger.getLogger(MobileAgentPi.class.getName()).log(Level.INFO,
                            "Sender set to ConsoleSender");
                    agentMsgSender = ToConsoleSender.getInstance();
                }
            } catch (IOException ex) {
                Logger.getLogger(MobileAgentPi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MobileAgentPi.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    // runs the threads responsible for
    // the control of an agent, sensor manager and monitoring 
    public void runThreads() {
        if (initialize()) {
            // ArduinoCommunication has event listener
            Thread agentContollerThread = new Thread(agentContoller);
            Thread sensorManagerThread = new Thread(sensorManager);

            agentContollerThread.start();
            sensorManagerThread.start();

            if (monitoring != null) {
                Thread monitoringThread = new Thread(monitoring);
                monitoringThread.start();
            }

            /////////////////experimental///////////////////
            Camera camera;
            try {
                camera = new Camera();
                camera.takeAPicture();
            } catch (FailedToRunRaspistillException ex) {
                Logger.getLogger(MobileAgentPi.class.getName()).log(Level.SEVERE, 
                        "experimental - starting camera failed", ex);
            }
            ////////////////////////////////////////////////

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
}
