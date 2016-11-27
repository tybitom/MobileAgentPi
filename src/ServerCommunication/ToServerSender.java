/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerCommunication;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tomek
 */
public class ToServerSender implements AgentMsgSender {

    private static ToServerSender instance = null;

    private final static Logger logger = Logger.getLogger(ServerCommunication.class.getName());

    ServerCommunication arduinoDataSender;
    ServerCommunication rpiDataSender;
    ServerCommunication logSender;

    protected ToServerSender() {
        arduinoDataSender = new ServerCommunication("MobileAgentsService/api/arduinoMessages");
        rpiDataSender = new ServerCommunication("MobileAgentsService/api/rpiMessages");
        logSender = new ServerCommunication("MobileAgentsService/api/logMessages");
    }

    public static ToServerSender getInstance() {
        if (instance == null) {
            instance = new ToServerSender();
        }
        return instance;
    }

    @Override
    public void send(String msg, MessageType msgType) {
        switch (msgType) {
            case ARDUINO_MSG: {
                arduinoDataSender.send(msg);
                break;
            }
            case RPI_MSG: {
                rpiDataSender.send(msg);
                break;
            }
            case LOG_MSG: {
                logSender.send(msg);
                break;
            }
            default:
                logger.log(Level.SEVERE, "Message not send! Unknown message type {0}.", msgType);
        }
    }

}
