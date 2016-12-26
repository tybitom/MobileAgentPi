/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AgentContol;

import static ArduinoCommunication.ArduinoCommunication.closeArduinoCommunication;
import static ArduinoCommunication.ArduinoCommunication.writeToArduino;
import ServerCommunication.AgentMsgSender;
import ServerCommunication.MessageType;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Tomek
 */
public final class MobileAgentContoller implements Runnable {

    static AgentMsgSender agentMsgSender;

    // controls the agent movement, sending steering commands to Arduino
    public MobileAgentContoller(AgentMsgSender agentMsgSender) {
        MobileAgentContoller.agentMsgSender = agentMsgSender;
        configureArduino();
    }

    // sends the configuration to Arduino basing on data read from file
    public void configureArduino() {
        try {
            InputStream inputStream = new FileInputStream("ArduinoConfig.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String configurationString;
            System.out.println("Initial Arduino configuration: ");
            try {
                while ((configurationString = br.readLine()) != null) {
                    System.out.println("\t" + configurationString);
                    try {
                        JSONObject jsonObject = new JSONObject(configurationString);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MobileAgentContoller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        writeToArduino(configurationString + "\r\n"); // jsonObject.toString()); //configurationString + "\r\n");
                    } catch (JSONException ex) {
                        Logger.getLogger(MobileAgentContoller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(MobileAgentContoller.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MobileAgentContoller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // listens for streering commands in the program console
    @Override
    public void run() {
        agentMsgSender.send("Starting MA Controller Thread!", MessageType.LOG_MSG);
        final Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            final String line = in.nextLine();
            agentMsgSender.send("Input line: " + line, MessageType.LOG_MSG);
            if ("end".equalsIgnoreCase(line)) {
                closeArduinoCommunication();
                break;
            } else {
                String commandToSend = isCommandCorrect(line);
                if (!"".equals(commandToSend)) {
                    Logger.getLogger(MobileAgentContoller.class.getName()).log(
                            Level.INFO, "Sending to Arduino: {0}", commandToSend);
                    writeToArduino(commandToSend);
                }
            }
        }
        agentMsgSender.send("Ending Mobile Agent Controller thread", MessageType.LOG_MSG);
    }

    // checks if command is less than 100 chars and in JSON format
    // if string parameter in not in JSON format
    // returns empty string "", otherwise the command
    public String isCommandCorrect(String command) {
        String result = "";
        // where 100 is the max length of arduino buffer
        if (command.length() < 100) {
            try {
                JSONObject jsonObject = new JSONObject(command);
                result = jsonObject.toString();
            } catch (JSONException ex) {
                Logger.getLogger(MobileAgentContoller.class.getName()).log(Level.SEVERE,
                        "Provided command for Arduino has not JSON format!");
            }
        }
        return result;
    }
}
