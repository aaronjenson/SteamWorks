package org.usfirst.frc.team3238.robot.subsystems;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import org.usfirst.frc.team3238.robot.Constants;
import org.usfirst.frc.team3238.robot.utils.Utils;

public class Climber
{
    private CANTalon talon, slaveTalon;

    private String state;

    public Climber()
    {
        talon = new CANTalon(Constants.Climber.MASTER_TALON_ID);
        slaveTalon = new CANTalon(Constants.Climber.SLAVE_TALON_ID);

        slaveTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
        slaveTalon.set(talon.getDeviceID());

        talon.enableBrakeMode(false);
        slaveTalon.enableBrakeMode(false);
    }

    public void init()
    {
        state = "inactive";
    }

    public void loop()
    {
        monitorTalons();
    }

    public void up()
    {
        state = "up";
    }

    public void stop()
    {
        state = "inactive";
    }

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
