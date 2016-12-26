/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RPiSensors;

import java.util.ArrayList;

/**
 *
 * @author Tomek
 */
public class DS1621 extends I2Csensor {

    final static int DEVICE_ADDRESS = 0x4f;  //Sensor address
    private final int FREQ = 100000;  //Bus frequency from Datasheet
    private final int ADDRESS_SIZE = 7; //Address size from Datasheet
    private final int BUS_ID = 1; // Internal bus ID in Java ME Embedded from Getting Started Guide

    // Registers addresses and definitions of configuration parameters of DS1621 from Datasheet
    private final int REG_READ_TEMP = 0xAA;
    private final int RT_ADDR_SIZE = 0x01;
    private final int READ_TEMP_SIZE = 0x02;
    private final int READ_TEMP_VAL = 0x00;
    private final int REG_ACC_CONF = 0xAC;
    private final int ACC_CONF_VAL = 0x00;
    private final int REG_START_CONV = 0xEE;
    private final int REG_STOP_CONV = 0x22;

    public DS1621(String name, double accuracy, String unit, String type) {
        super(name, accuracy, unit, type);
        this.measurements = new ArrayList<>();
    }

    public void configure() {
        /*byte received[] = new byte[20];
        
        int fd = I2C.i2cOpen("/dev/i2c-1");
        
        System.out.println("Returned " + fd);
        
        try {
            // Config sensor to continuous temperature measuring according to Datasheet
            write(device, new byte[]{(byte) REG_ACC_CONF, (byte) ACC_CONF_VAL});
            write(device, new byte[]{(byte) REG_START_CONV});
            
            I2C.i2cWrite(fd, DEVICE_ADDRESS, msg.length(), 0,
                    new byte[]{(byte) REG_ACC_CONF, (byte) ACC_CONF_VAL}, 20, 0, received);

            // Read temperature
            ByteBuffer tempBuf = ByteBuffer.allocateDirect(READ_TEMP_SIZE);
            device.read(REG_READ_TEMP, RT_ADDR_SIZE, tempBuf);
            // Profit!  
            System.out.println("Temperature is:" + tempBuf.get(0));

        } catch (Exception ex) { // Bad practice. Only for consiceness
            ex.printStackTrace();
        } finally {
            // Close device
            if (device != null) {
                try {
                    device.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                // Destroy application
                notifyDestroyed();
            }

        }*/
    }
    @Override
    public void measure() {
    }
}
