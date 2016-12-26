/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArduinoCommunication;

/**
 *
 * @author Tomek
 */
import ServerCommunication.AgentMsgSender;
import ServerCommunication.MessageType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

//example code http://playground.arduino.cc/Interfacing/Java
//libraries downloaded from http://fizzed.com/oss/rxtx-for-java
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ArduinoCommunication implements SerialPortEventListener {

    static SerialPort serialPort;

    private static final String PORT_NAMES[] = {
        "/dev/tty.usbserial-A9007UX1", // Mac OS X
        "/dev/ttyACM0", // Raspberry Pi
        "/dev/ttyUSB0", // Linux
        "COM3", // Windows
        "COM4", // Windows
    };
    /**
     * A BufferedReader which will be fed by a InputStreamReader converting the
     * bytes into characters making the displayed results codepage independent
     */
    private BufferedReader input;
    /**
     * The output stream to the port
     */
    private static OutputStream outputStream;
    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port.
     */
    private int DATA_RATE = 38400;

    ArduinoMessageInterpreter messageInterpreter;
    static AgentMsgSender agentMsgSender;

    private final static Logger logger = Logger.getLogger(ArduinoCommunication.class.getName());

    public ArduinoCommunication(int dataRate, AgentMsgSender agentMsgSender) {
        ArduinoCommunication.agentMsgSender = agentMsgSender;
        agentMsgSender.send("Creating Arduino Communication Thread!", MessageType.LOG_MSG);
        messageInterpreter = new ArduinoMessageInterpreter();
        initialize(dataRate);
    }

    // initializes communication with Arduino via serial COM port
    public void initialize(int dataRate) {
        // the next line is for Raspberry Pi and 
        // gets us into the while loop and was suggested here was suggested 
        // http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

        DATA_RATE = dataRate;
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            agentMsgSender.send("Could not find COM port.", MessageType.LOG_MSG);
            return;
        } else {
            agentMsgSender.send("Connecting to Arduino COM port " + portId.getName(), MessageType.LOG_MSG);
        }

        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            outputStream = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            agentMsgSender.send("Starting Arduino Communication Thread!", MessageType.LOG_MSG);
        } catch (PortInUseException | UnsupportedCommOperationException
                | IOException | TooManyListenersException e) {
            System.err.println(e.toString());
        }
    }

    /**
     * This should be called when you stop using the port. This will prevent
     * port locking on platforms like Linux.
     */
    public static synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     *
     * @param oEvent
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        //agentMsgSender.send("serialEvent!");
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = input.readLine();
                // agentMsgSender.send(inputLine);
                if (!"".equals(inputLine)) {
                    reactOnMessage(inputLine);
                }
            } catch (IOException e) {
                //System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    // interpretes a message from Arduino and logs it to the console or sends to server
    private void reactOnMessage(String inputLine) {
        ArduinoMessage message = messageInterpreter.interpreteArduinoMessage(inputLine);
        String s = message.getMessage();
        if (!("".equals(s))) {
            if (message.isLog) {
                int ll = message.getLogLevel().intValue();
                if (Level.INFO.intValue() == ll) {
                    logger.log(Level.INFO, "ARDUINO: {0}", s);
                } else if (Level.WARNING.intValue() == ll) {
                    logger.log(Level.WARNING, "ARDUINO: {0}", s);
                } else if (Level.SEVERE.intValue() == ll) {
                    logger.log(Level.SEVERE, "ARDUINO: {0}", s);
                }
            } else {
                agentMsgSender.send(message.getMessage(), MessageType.ARDUINO_MSG);
            }
        }
    }

    public static void closeArduinoCommunication() {
        agentMsgSender.send("Ending Arduino Communication thread", MessageType.LOG_MSG);
        close();
    }

    public static void writeToArduino(String s) {
        if (outputStream != null) {
            try {
                // write string to serial port
                outputStream.write(s.getBytes());
                outputStream.flush();

            } catch (IOException e) {
                agentMsgSender.send("Sending failed!" + e.getLocalizedMessage(), MessageType.LOG_MSG);
            }
        }
    }
}
