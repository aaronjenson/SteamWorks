package org.usfirst.frc.team3238.robot.utils;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;

public class Utils
{
    public static void say(String s)
    {
        DriverStation.reportWarning(s, false);
    }

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
