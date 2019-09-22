package ca.mcgill.ecse211.lab2;

import java.text.DecimalFormat;
import lejos.robotics.SampleProvider;
//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.lab2.Resources.*;

/**
 * This class is used to display the content of the odometer variables (x, y, Theta)
 */
public class Display implements Runnable {

  private double[] position;
  private final long DISPLAY_PERIOD = 25;
  private long timeout = Long.MAX_VALUE;

  private SampleProvider color_sensor = colorSensor.getRedMode();
  private float[] sensor_data = new float[color_sensor.sampleSize()]; //array of sensor readings 
  private int light_value;
  
  public void run() {
    
    LCD.clear();
    
    long updateStart, updateEnd;

    long tStart = System.currentTimeMillis();
    do {
      updateStart = System.currentTimeMillis();

      // Retrieve x, y and Theta information
      position = odometer.getXYT();
      
      // Print x,y, and theta information
      DecimalFormat numberFormat = new DecimalFormat("######0.00");
      LCD.drawString("X: " + numberFormat.format(position[0]), 0, 0);
      LCD.drawString("Y: " + numberFormat.format(position[1]), 0, 1);
      LCD.drawString("T: " + numberFormat.format(position[2]), 0, 2);
//      LCD.drawString("Prop Angle " + (leftMotor.getTachoCount() - rightMotor.getTachoCount())/6.953, 0, 4);
//      color_sensor.fetchSample(sensor_data, 0);
//      LCD.drawString("INTENSITY: " + sensor_data[0], 0, 4);
      
      // this ensures that the data is updated only once every period
      updateEnd = System.currentTimeMillis();
      if (updateEnd - updateStart < DISPLAY_PERIOD) {
        try {
          Thread.sleep(DISPLAY_PERIOD - (updateEnd - updateStart));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } while ((updateEnd - tStart) <= timeout);

  }
  
  /**
   * Sets the timeout in ms.
   * 
   * @param timeout
   */
  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }
  
  /**
   * Shows the text on the LCD, line by line.
   * 
   * @param strings comma-separated list of strings, one per line
   */
  public static void showText(String... strings) {
    LCD.clear();
    for (int i = 0; i < strings.length; i++) {
      LCD.drawString(strings[i], 0, i);
    }
  }

}
