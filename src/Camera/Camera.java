/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Camera;

import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.enums.Exposure;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Tomek
 */
// The camera should be firstly configured on RPi
// https://www.raspberrypi.org/documentation/configuration/camera.md
// Example use and the library can be found here
// https://github.com/Hopding/JRPiCam/blob/master/README.md
public class Camera {
    
    RPiCamera piCamera;
    
    public Camera() {
        configureCamera();
    }
    
    private void configureCamera() {
        try {
            //Create a Camera that saves images to the Pi's Pictures directory.
            piCamera = new RPiCamera("/home/pi/Pictures");

            //Set Camera to produce 500x500 images.
            piCamera.setWidth(500);
            piCamera.setHeight(500);

            //Adjust Camera's brightness setting.
            piCamera.setBrightness(75);

            //Set Camera's exposure.
            piCamera.setExposure(Exposure.AUTO);

            //Set Camera's timeout.
            piCamera.setTimeout(2);

            //Add Raw Bayer data to image files created by Camera.
            piCamera.setAddRawBayer(true);

            //Sets all Camera options to their default settings, overriding any changes previously made.
            piCamera.setToDefaults();
        } catch (FailedToRunRaspistillException ex) {
            Logger.getLogger(Monitoring.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public BufferedImage takeAPicture() {
        try {
            File takeStill = piCamera.takeStill("Pic001.jpg");
            return ImageIO.read(takeStill);

            //BufferedImage image = piCamera.takeBufferedStill();
        } catch (IOException ex) {
            Logger.getLogger(Monitoring.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Monitoring.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
