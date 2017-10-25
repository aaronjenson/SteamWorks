package org.usfirst.frc.team3238.robot.subsystems;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import org.usfirst.frc.team3238.robot.Constants;
import org.usfirst.frc.team3238.robot.utils.Utils;

/**
 * Controls movements of climber
 */
public class Climber
{
    private CANTalon talon, slaveTalon;

    private String state;

    /**
     * Sets up talons for climber object
     */
    public Climber()
    {
        talon = new CANTalon(Constants.Climber.MASTER_TALON_ID);
        slaveTalon = new CANTalon(Constants.Climber.SLAVE_TALON_ID);

        slaveTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
        slaveTalon.set(talon.getDeviceID());

        talon.enableBrakeMode(false);
        slaveTalon.enableBrakeMode(false);
    }

    /**
     * Reset state to start teleop
     */
    public void init()
    {
        state = "inactive";
    }

    /**
     * Checks talons for current and temperature, call in robotPeriodic
     */
    public void loop()
    {
        monitorTalons();
    }

    /**
     * Change to up state
     */
    public void up()
    {
        state = "up";
    }

    /**
     * Change to inactive state
     */
    public void stop()
    {
        state = "inactive";
    }

    /**
     * Main method, must be called each loop to run any climber functions
     */
    public void run()
    {
        switch(state)
        {
            case "inactive":
                talon.set(0.0);
                break;
            case "up":
                talon.set(Constants.Climber.UP_POWER);
                break;
            default:
                DriverStation.reportError("Default case in Climber class", false);
        }
    }

    private void monitorTalons()
    {
        Utils.monitorTalon(talon);
        Utils.monitorTalon(slaveTalon);
    }
}
