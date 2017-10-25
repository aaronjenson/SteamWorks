package org.usfirst.frc.team3238.robot.subsystems;

import com.ctre.CANTalon;
import org.usfirst.frc.team3238.robot.Constants;
import org.usfirst.frc.team3238.robot.utils.POVState;
import org.usfirst.frc.team3238.robot.utils.Utils;

/**
 * Controls all movements of the collector. Controlled by Autonomous during auto, then by Robot during teleop.
 */
public class Collector
{
    private CANTalon lift, left, right;

    private String state;

    /**
     * Sets up collector object with talons
     */
    public Collector()
    {
        lift = new CANTalon(Constants.Collector.LIFT_TALON_ID);
        left = new CANTalon(Constants.Collector.LEFT_TALON_ID);
        right = new CANTalon(Constants.Collector.RIGHT_TALON_ID);

        left.enableLimitSwitch(false, true);

        right.changeControlMode(CANTalon.TalonControlMode.Follower);
        right.reverseOutput(true);
        right.set(left.getDeviceID());

        lift.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
        lift.configEncoderCodesPerRev(1044);
        lift.enableZeroSensorPositionOnReverseLimit(true);
        lift.enableZeroSensorPositionOnForwardLimit(true);

        state = "inactive";
    }

    /**
     * Gets string of state value, for checking if a task has finished
     *
     * @return state of collector, as a string
     */
    public String getState()
    {
        return state;
    }

    /**
     * Checks that each talon is within acceptable temperature and current. Call during robotPeriodic
     */
    public void loop()
    {
        monitorTalons();
    }

    /**
     * Resets collector state to inactive
     */
    public void init()
    {
        state = "inactive";
    }

    /**
     * Begins process of collecting a gear
     */
    public void pickup()
    {
        state = "collecting";
    }

    /**
     * Begins process of placing a gear
     */
    public void place()
    {
        state = "placing";
    }

    /**
     * Cancels any current process begins lifting collector up
     */
    public void stop()
    {
        state = "inactive";
    }

    /**
     * Main collector method, must be called for any movement to occur
     *
     * @param pov pov state object to control manual mode
     */
    public void run(POVState pov)
    {
        switch(state)
        {
            case "inactive":
                set(Constants.Collector.RAISE_POWER, 0.0, pov);
                break;
            case "collecting":
                set(Constants.Collector.LOWER_POWER, Constants.Collector.INTAKE_POWER, pov);
                if(left.isRevLimitSwitchClosed())
                {
                    state = "inactive";
                }
                break;
            case "placing":
                set(Constants.Collector.PLACE_GEAR_POWER, 0.0, pov);
                if(lift.getEncPosition() < Constants.Collector.ENCODER_BOTTOM_LIMIT)
                {
                    state = "inactive";
                }
                break;
            case "manual":
                set(0.0, 0.0, pov);
                break;
            default:
                Utils.say("Default case in collector class");
        }
    }

    private void set(double liftPower, double intakePower, POVState pov)
    {
        if(pov.isNone())
        {
            lift.set(liftPower);
            left.set(intakePower);
        }
        else if(pov.isTop())
        {
            state = "manual";
            left.set(0.0);
            lift.set(Constants.Collector.LOWER_POWER);
        }
        else if(pov.isTopRight())
        {
            state = "manual";
            left.set(Constants.Collector.INTAKE_POWER);
            lift.set(Constants.Collector.LOWER_POWER);
        }
        else if(pov.isRight())
        {
            state = "manual";
            left.set(Constants.Collector.INTAKE_POWER);
            lift.set(0.0);
        }
        else if(pov.isBottomRight())
        {
            state = "manual";
            left.set(Constants.Collector.INTAKE_POWER);
            lift.set(Constants.Collector.RAISE_POWER);
        }
        else if(pov.isBottom())
        {
            state = "manual";
            left.set(0.0);
            lift.set(Constants.Collector.RAISE_POWER);
        }
        else if(pov.isBottomLeft())
        {
            state = "manual";
            left.set(-Constants.Collector.INTAKE_POWER);
            lift.set(Constants.Collector.RAISE_POWER);
        }
        else if(pov.isLeft())
        {
            state = "manual";
            left.set(-Constants.Collector.INTAKE_POWER);
            lift.set(0.0);
        }
        else if(pov.isTopLeft())
        {
            state = "manual";
            left.set(-Constants.Collector.INTAKE_POWER);
            lift.set(Constants.Collector.LOWER_POWER);
        }
    }

    private void monitorTalons()
    {
        Utils.monitorTalon(lift);
        Utils.monitorTalon(left);
        Utils.monitorTalon(right);
    }
}
