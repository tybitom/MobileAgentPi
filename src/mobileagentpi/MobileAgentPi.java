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
import AgentContol.MobileAgentContoller;
import ArduinoCommunication.ArduinoCommunication;
import RPiSensors.SensorManager;
import ServerCommunication.AgentMsgSender;
import ServerCommunication.ToServerSender;

public class MobileAgentPi {

    AgentMsgSender agentMsgSender;
    ArduinoCommunication arduinoCommunicationThread;
    MobileAgentContoller agentContoller;
    SensorManager sensorManager;

    public MobileAgentPi() {
        agentMsgSender = ToServerSender.getInstance();// ToConsoleSender.getInstance();
        arduinoCommunicationThread = new ArduinoCommunication(38400, agentMsgSender);
        agentContoller = new MobileAgentContoller(agentMsgSender);
        sensorManager = new SensorManager();
    }

    public void run() {
        agentContoller.run();
        System.out.println("Exiting program!");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MobileAgentPi mobileAgentPi = new MobileAgentPi();
        mobileAgentPi.run();
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
