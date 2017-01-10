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
public class ToConsoleSender implements AgentMsgHandler {

    private static ToConsoleSender instance = null;

    private final static Logger logger = Logger.getLogger(ToConsoleSender.class.getName());

    protected ToConsoleSender() {
    }

    public static ToConsoleSender getInstance() {
        if (instance == null) {
            instance = new ToConsoleSender();
        }
        return instance;
    }

    // prints the message to the program console as a normal message or as a log
    @Override
    public void send(String msg, MessageType msgType) {
        if (msgType == MessageType.LOG_MSG) {
            logger.log(Level.INFO, msg);
        } else {
            System.out.println(msg);
        }
    }

    // prints the message to the program console specifying the path
    // is needed for server REST communication
    @Override
    public void send(String msg, String path) {
        System.out.print(path + ": ");
        System.out.println(msg);
    }
}
