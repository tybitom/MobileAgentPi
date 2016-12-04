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

    private final static Logger logger = Logger.getLogger(ToServerSender.class.getName());

    ServerCommunication dataSender;

    protected ToServerSender() {
        dataSender = new ServerCommunication();
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
                dataSender.send(msg, "MobileAgentsService/api/arduinoMessages");
                break;
            }
            case RPI_MSG: {
                dataSender.send(msg, "MobileAgentsService/api/rpiMessages");
                break;
            }
            case LOG_MSG: {
                dataSender.send(msg, "MobileAgentsService/api/logMessages");
                break;
            }
            default:
                logger.log(Level.SEVERE, "Message not send! Unknown message type {0}.", msgType);
        }
    }

    @Override
    public void send(String msg, String path) {
        dataSender.send(msg, path);
    }

}
