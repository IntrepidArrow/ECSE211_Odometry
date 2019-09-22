package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;
import com.sun.org.apache.bcel.internal.Const;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class OdometryCorrection implements Runnable {
  private static final long CORRECTION_PERIOD = 10;

  //Variables and values to operate color sensor
  //if amplified color intensity value read by sensor is less than this value then black line. 

  //Color sensor values for black line were [0.15,0.29] when tested. Demo floor value = [0.51,0.56]
  private final int THRESHOLD = 35; 
  //max(sensor value for black line test)/max(Demo floor value) = 0.339285
  //private final double INTENSITY_DIFFERENCE_FACTOR = 0.580; 

  //colorSensor already available from resources
  private SampleProvider color_sensor = colorSensor.getRedMode();
  private float[] sensor_data = new float[color_sensor.sampleSize()]; //array of sensor readings 
  private int current_color_value = 0;

  /*   
   * Here is where the odometer correction code should be run.
   */
  public void run() {
    long correctionStart, correctionEnd;
    int tileCount_X = 0;
    int tileCount_Y = 0;
    while (true) {
      correctionStart = System.currentTimeMillis();
      if (blackLineTrigger()) {
        Sound.beep();
        if(odometer.getXYT()[2] > 340 || odometer.getXYT()[2] < 20) {
          tileCount_Y++;
          odometer.setY(TILE_SIZE*tileCount_Y);

        } else if(odometer.getXYT()[2] > 70 && odometer.getXYT()[2] < 110) {
          tileCount_X++;
          odometer.setX(TILE_SIZE*tileCount_X);

        } else if(odometer.getXYT()[2] > 160 && odometer.getXYT()[2] < 200) {
          odometer.setY(TILE_SIZE*tileCount_Y);
          tileCount_Y--;

        } else if(odometer.getXYT()[2] > 250 && odometer.getXYT()[2] < 290) {
          odometer.setX(TILE_SIZE*tileCount_X);
          tileCount_X--;
        }
      }

      //Main Tasks to complete for the method:
      // TODO Trigger correction (When do I have information to correct?)
      // TODO Calculate new (accurate) robot position
      // TODO Update odometer with new calculated (and more accurate) values, eg:
      //odometer.setXYT(0.3, 19.23, 5.0);

      // this ensures the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
      }
    }
  }

  //TODO: Documentation
  private boolean blackLineTrigger() {
    color_sensor.fetchSample(sensor_data, 0);
    current_color_value = (int)(sensor_data[0]*100);    //sensor data read in 2dp - convert to integer value to compare
    //System.out.println("Recorded Intensity: " + current_color_value);
    //when color intensity is below threshold
    if(current_color_value < THRESHOLD) {
      return true;
    }
    else {
      return false;
    }

    //    last_color_value = current_color_value; //Update most recent color intensity value detected 
    //
    //    return status;

  }

}
