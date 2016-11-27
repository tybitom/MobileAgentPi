/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RPiSensors;

import ServerCommunication.ServerCommunication;
import com.pi4j.jni.I2C;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tomek
 */
public class I2Ccommunicator {

    private final static Logger logger = Logger.getLogger(ServerCommunication.class.getName());

    final static int DEVICE_ADDRESS = 0x04;

    public void communicate(String msg) {

        //String msg = "Hello from Pi\n";
        byte received[] = new byte[20];

        System.out.println("<--Pi4J--> I2C conncection.");

        int fd = I2C.i2cOpen("/dev/i2c-1");

        System.out.println("Returned " + fd);

        // send the message and receive the answer
        try {
            byte b = 1;
            // for (int i = 0; i < 10; ++i) {
            /*
             * I2C.i2cWriteByteDirect(fd, deviceAddress, b);
             * System.out.println("Message sent!"); I2C.i2cReadByteDirect(fd,
             * deviceAddress);
             */
            // wait 1 second before continuing
            //Thread.sleep(1000);
            // }
            I2C.i2cWriteAndReadBytes(fd, DEVICE_ADDRESS, msg.length(), 0,
                    msg.getBytes(), 20, 0, received);
        } catch (IllegalStateException e) {
            logger.log(Level.SEVERE, "Sending via I2C failed! {0}", e.getLocalizedMessage());
        }

        // print the received message
        for (byte c : received) {
            System.out.format("%c", (c & 0xff));
        }
        System.out.println("");

        System.out.println("The end!");

        if (fd != -1) {
            I2C.i2cClose(fd);
        }
    }
}
