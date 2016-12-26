/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArduinoCommunication;

import java.util.logging.Level;

/**
 * A message class from Arduino
 * @author Tomek
 */
public class ArduinoMessage {

    boolean isLog;
    Level logLevel; // Level.INFO
    String message = "";

    public boolean isIsLog() {
        return isLog;
    }

    public void setIsLog(boolean isLog) {
        this.isLog = isLog;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
