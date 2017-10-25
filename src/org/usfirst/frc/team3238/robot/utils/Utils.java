package org.usfirst.frc.team3238.robot.utils;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Class holds methods used in many places throughout the whole project.
 */
public class Utils
{
    /**
     * Method to output a string to some form of log
     *
     * @param s string to output
     */
    public static void say(String s)
    {
        DriverStation.reportWarning(s, false);
    }

    /**
     * Checks talon for high temperature or current
     *
     * @param talon motor controller to monitor
     */
    public static void monitorTalon(CANTalon talon)
    {
        double temp = talon.getTemperature();
        double current = talon.getOutputCurrent();

        if(temp > 100)
        {
            DriverStation.reportError("TALON" + talon.getDeviceID() + " TEMP IS HIGH: " + temp + " DEGREES C", false);
        }
        if(current > 40)
        {
            DriverStation.reportError("TALON" + talon.getDeviceID() + " AMPS IS HIGH: " + temp + " AMPS", false);
        }
    }
}
