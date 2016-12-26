/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArduinoCommunication;

import java.util.logging.Level;

/**
 *
 * @author Tomek
 */
public class ArduinoMessageInterpreter {

    // interpretes a message from Arduino
    public ArduinoMessage interpreteArduinoMessage(String msg) {
        ArduinoMessage intepretedMessage = new ArduinoMessage();
        msg = msg.trim(); // to remove new line character at the end of message

        // interprete Log message
        if ((msg.length() > 2) && (msg.charAt(1) == '|')) {
            String s = interpreteLogMessage(msg);
            if ("".equals(s)) { // if interpreteLogMessage(msg) did not return the value
                s = interpreteLogMessageWithParameters(msg);
            }
            if (!"".equals(s)) { // if interpreteLogMessageWithParameters(msg) did not return the value
                intepretedMessage.setIsLog(true);
                switch (msg.charAt(0)) {
                    case 'I': {
                        intepretedMessage.setLogLevel(Level.INFO);
                        break;
                    }
                    case 'W': {
                        intepretedMessage.setLogLevel(Level.WARNING);
                        break;
                    }
                    case 'S': {
                        intepretedMessage.setLogLevel(Level.SEVERE);
                        break;
                    }
                }
                intepretedMessage.setMessage(s);
            }
        }
        // interprete other messages types
        else {
            intepretedMessage.setIsLog(false);
            intepretedMessage.setMessage(msg);
        }
        return intepretedMessage;
    }

    // when a message type is a log message then this function is called
    // converts short Arduino messages into legible, long messages
    public String interpreteLogMessage(String msg) {
        switch (msg) {
            case "I|MA|sE|OK": {
                return "Message has been read successfully.";
            }
            case "S|MA|sE|NOK": { // not ok
                return "Reading message on Arduino failed!";
            }
            //Encoder:
            // Encoder(uint8_t encoderPinA, uint8_t encoderPinB, volatile unsigned long &cv)
            case "S|E|E|nme": { // no more encoders
                return "No more encoders can be added!";
            }
            // Motor
            case "I|MSC|iC|succ": {
                return "Motor controller was successfully initialized!";
            }
            case "W|MSC|cS|dth": { // dt to long
                return "dt for control speed was higher than (1,5 * sampleTime).";
            }
            case "I|MSC|sPID|succ": { // parameters set successfully
                return "Parameters set successfully.";
            }
            //PinController:
            case "S|PC|sPU|wt": { // wrong type
                return "No function was assigned to the pin. Wrong type. setPinUsage failed";
            }
            case "I|PC|sPU|succ": { // success
                return "Pin value was set successfully.";
            }
            case "S|PC|sPU|pne": { // pin not exist
                return "Wrong pin number. There are not so many pins are available. setPinUsage failed!";
            }
            case "S|PC|gPU|pne": { // pin not exist
                return "Wrong pin number. There are not so many pins are available. getPinUsage failed!";
            }
            case "S_PC_sPS_pne": { // pin not exist
                return "Wrong pin number. There are not so many pins are available. setPinState failed!";
            }
            case "S|PC|sPWM|pne": { // pin not exist
                return "Wrong pin number. There are not so many pins are available. setPWM failed!";
            }
            // TaskManager:
            case "I|TM|aT|tu": { // Task has been updated
                return "Task has been updated.";
            }
            case "I|TM|ct|nf": {
                return "No task with provided id was found!";
            }
//CommonFunctions:
//bool interpreteMessage(String &json)
            case "S|SI|iM|te": { // type empty
                return "JSON could not be interpreted. msgType empty! Probably lack of memory on Arduino.";
            }
            case "S|SI|iM|tu": { // type unknown
                return "JSON type unknown! ";
            }
            case "S|SI|iM|pf": { // parsing failed
                return "Parsing JSON message failed";
            }
//bool interpretePinCTRL(JsonObject& jsonObject)
            case "S|SI|iPC|fe": { // fun empty
                return "JSON could not be interpreted. fun empty! Probably lack of memory on Arduino.";
            }
            case "S|SI|iPC|pTe": { // pinType empty
                return "JSON could not be interpreted. pinType empty! Probably lack of memory on Arduino.";
            }
            case "S|SI|iPC|pus": { // Pin Usage could not be set
                return "Pin Usage could not be set!";
            }
            case "S|SI|iPC|pss": { // Pin State could not be set
                return "Pin State could not be set!";
            }
            case "S|SI|iPC|PWMs": { // PWM could not be set
                return "PWM value could not be set!";
            }
            case "S|SI|iPC|cu": { // command unknown
                return "Pin control command unknown!";
            }
//bool interpreteTask(JsonObject& jsonObject)
            case "S|SI|iT|fe": { // fun empty
                return "JSON could not be interpreted. fun empty! Probably lack of memory on Arduino.";
            }
            case "S|SI|iT|af": { // Adding task failed!
                return "Adding task failed!";
            }
            case "S|SI|iT|pni": { // pin not input
                return "SEVERE! Pin has not been previously set as input!";
            }
            case "S|SI|iT|fu": { // function unknown
                return "Function for task unknown!";
            }
//bool interpreteAgentCTRL(JsonObject& jsonObject)
            case "S|SI|iAC|ce": { // cmd empty
                return "JSON could not be interpreted. cmd empty! Probably lack of memory on Arduino.";
            }
            case "S|SI|iAC|cu": { // command unknown
                return "Motor control command unknown!";
            }
// bool interpreteMotorCTRL(JsonObject& jsonObject, bool leftMotor)
            case "S|SI|iMC|ce": { // cmd empty
                return "JSON could not be interpreted. cmd empty! Probably lack of memory on Arduino.";
            }
            case "S|SI|iMC|cu": { // command unknown
                return "Motor control command unknown!";
            }
//bool interpreteQuestion(JsonObject& jsonObject) 
            case "S|SI|iQ|fe": { // fun empty
                return "JSON could not be interpreted. fun empty! Probably lack of memory on Arduino.";
            }
//void serialEvent() 
            case "S|MA|sE|tl": { // to long
                return "JSON message too long! Over 100 chars!";
            }
        }
        return "";
    }

    // when a message type is a log message with numeric parameters then this function is called
    // converts short Arduino messages into legible, long messages
    public String interpreteLogMessageWithParameters(String msg) {
        if (msg.contains("W|PC|sPS|ch")) { // PinController::setPinState(uint8_t pinNumber, uint8_t state)
            String s[] = msg.substring("W|PC|sPS|ch".length()).split("|");
            if (s.length <= 2) {
                return "Changing pin function from " + getTypeAsString(Integer.parseInt(s[0])) + " to " + getTypeAsString(Integer.parseInt(s[1]));
            } else if (s.length == 1) {
                return "Changing pin function from " + getTypeAsString(Integer.parseInt(s[0])) + " to [pin function could not be read]";
            } else {
                return "Changing pin function from [pin function could not be read] to [pin function could not be read]";
            }
        } else if (msg.contains("W|PC|sPWM|ch")) { // PinController::setPWM(uint8_t pinNumber, int value)
            String s[] = msg.substring("W|PC|sPWM|ch".length()).split("|");
            if (s.length <= 2) {
                return "Changing pin function from " + getTypeAsString(Integer.parseInt(s[0])) + " to " + getTypeAsString(Integer.parseInt(s[1]));
            } else if (s.length == 1) {
                return "Changing pin function from " + getTypeAsString(Integer.parseInt(s[0])) + " to [pin function could not be read]";
            } else {
                return "Changing pin function from [pin function could not be read] to [pin function could not be read]";
            }
        } else if (msg.contains("I|TM|aT|tc")) { // Task has been created
            String s = msg.substring("I|TM|at|nt".length()); // MAX_NUMBER_OF_TASKS
            return "Task with id " + s + " has been created.";
        } else if (msg.contains("I|TM|at|nt")) { // TaskManager::addTask(int id, unsigned long sampleTime, void (*taskFunction)(void))
            String s = msg.substring("I|TM|at|nt".length()); // MAX_NUMBER_OF_TASKS
            return "No more than MAX_NUMBER_OF_TASKS " + s + " can be added";
        } else if (msg.contains("I|TM|ct|te")) { // TaskManager::addTask(int id, unsigned long sampleTime, void (*taskFunction)(void))
            String s = msg.substring("I|TM|ct|te".length()); // task id
            return "Task exists on index " + s;
        } else if (msg.contains("S|SI|iT|tnf")) { // TaskManager::addTask(int id, unsigned long sampleTime, void (*taskFunction)(void))
            String s = msg.substring("S|SI|iT|tnf".length()); // task id
            return "No task with provided id " + s + " was found.";
        } else if (msg.contains("I|cF|fm|FM")) { // TaskManager::addTask(int id, unsigned long sampleTime, void (*taskFunction)(void))
            String s = msg.substring("I|cF|fm|FM".length()); // task id
            return "Free RAM memory on Arduino: " + s;
        }
        return "";
    }

    public String interpreteMessage(String msg) {
        return "";
    }

    String getTypeAsString(int type) {
        switch (type) {
            case 0: {
                return "NO_FUNC_PIN";
            }
            case 1: {
                return "DIGITAL_OUTPUT";
            }
            case 2: {
                return "DIGITAL_INPUT";
            }
            case 3: {
                return "DIGITAL_INPUT_NO_PULLUP";
            }
            case 4: {
                return "PWM_PIN";
            }
            case 5: {
                return "ANALOG_INPUT";
            }
            case 6: {
                return "ANALOG_OUTPUT";
            }
            default: {
                return "[unknown pin function]";
            }
        }
    }
}
