package org.usfirst.frc.team3238.robot.subsystems;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import org.usfirst.frc.team3238.robot.Constants;
import org.usfirst.frc.team3238.robot.utils.Utils;

/**
 * Controls all movements of the drive base. Controlled by Autonomous during auto, then by Robot during teleop.
 */
public class Chassis
{
    private CANTalon r, rb, l, lb;
    private CANTalon.TalonControlMode lastOpenMode;

    private double rMotionTarget, lMotionTarget;

    /**
     * Sets up talon objects
     */
    public Chassis()
    {
        r = new CANTalon(Constants.Chassis.RIGHT_A_TALON_ID);
        rb = new CANTalon(Constants.Chassis.RIGHT_B_TALON_ID);
        l = new CANTalon(Constants.Chassis.LEFT_A_TALON_ID);
        lb = new CANTalon(Constants.Chassis.LEFT_B_TALON_ID);

        rb.changeControlMode(CANTalon.TalonControlMode.Follower);
        rb.set(r.getDeviceID());
        lb.changeControlMode(CANTalon.TalonControlMode.Follower);
        lb.set(l.getDeviceID());

        r.setCurrentLimit(Constants.Chassis.CURRENT_LIMIT_AMPS);
        l.setCurrentLimit(Constants.Chassis.CURRENT_LIMIT_AMPS);

        r.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
        l.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);

        r.setMotionMagicAcceleration(Constants.Chassis.MOTION_MAGIC_ACCEL);
        r.setMotionMagicCruiseVelocity(Constants.Chassis.MOTION_MAGIC_VEL);
        l.setMotionMagicAcceleration(Constants.Chassis.MOTION_MAGIC_ACCEL);
        l.setMotionMagicCruiseVelocity(Constants.Chassis.MOTION_MAGIC_VEL);

        enableCurrentLimit();

        //      Uncomment next lines once pidf values are set and correct in constants class
        //        if(r.getP() != Constants.Chassis.P_VALUE)
        //        {
        //            r.setP(Constants.Chassis.P_VALUE);
        //        }
        //        if(r.getI() != Constants.Chassis.I_VALUE)
        //        {
        //            r.setI(Constants.Chassis.I_VALUE);
        //        }
        //        if(r.getD() != Constants.Chassis.D_VALUE)
        //        {
        //            r.setD(Constants.Chassis.D_VALUE);
        //        }
        //        if(r.getF() != Constants.Chassis.F_VALUE)
        //        {
        //            r.setF(Constants.Chassis.F_VALUE);
        //        }
        //        if(l.getP() != Constants.Chassis.P_VALUE)
        //        {
        //            l.setP(Constants.Chassis.P_VALUE);
        //        }
        //        if(l.getI() != Constants.Chassis.I_VALUE)
        //        {
        //            l.setI(Constants.Chassis.I_VALUE);
        //        }
        //        if(l.getD() != Constants.Chassis.D_VALUE)
        //        {
        //            l.setD(Constants.Chassis.D_VALUE);
        //        }
        //        if(l.getF() != Constants.Chassis.F_VALUE)
        //        {
        //            l.setF(Constants.Chassis.F_VALUE);
        //        }
    }

    /**
     * Loop to check talons, call in robotPeriodic.
     */
    public void loop()
    {
        monitorTalons();
    }

    /**
     * Init method to prepare for teleop mode.
     */
    public void teleopInit()
    {
        setVoltageMode();
    }

    /**
     * Main drive method must be called for any motion to take place.
     *
     * @param y     y power, -1 - 1
     * @param twist twist power, -1 to 1
     */
    public void drive(double y, double twist)
    {
        CANTalon.TalonControlMode rMode = r.getControlMode();
        CANTalon.TalonControlMode lMode = l.getControlMode();

        if(rMode != lMode)
        {
            DriverStation.reportWarning("Drive Talon Control modes out of sync. Changing to " + rMode.name(), false);
            setControlMode(rMode);
        }

        double rSpeed = y - twist;
        double lSpeed = -y - twist;

        switch(rMode)
        {
            case MotionProfile:
                return;
            case MotionMagic:
                rSpeed = rMotionTarget;
                lSpeed = lMotionTarget;

                if(isMotionMagicFinished())
                {
                    setControlMode(lastOpenMode);
                }
                break;
            case Voltage:
                rSpeed *= 12.0;
                lSpeed *= 12.0;
            case PercentVbus:
                break;
            case Speed:
                rSpeed = rSpeed * (Constants.Chassis.MAX_WHEEL_SPEED - Constants.Chassis.MIN_WHEEL_SPEED) +
                         Constants.Chassis.MIN_WHEEL_SPEED;
                lSpeed = lSpeed * (Constants.Chassis.MAX_WHEEL_SPEED - Constants.Chassis.MIN_WHEEL_SPEED) +
                         Constants.Chassis.MIN_WHEEL_SPEED;
                break;
            default:
                Utils.say("Default case in chassis class, case is " + rMode.name());
        }
        r.set(rSpeed);
        l.set(lSpeed);
    }

    /**
     * Set drive mode to closed-loop speed
     */
    public void setPIDMode()
    {
        lastOpenMode = CANTalon.TalonControlMode.Speed;
        setControlMode(CANTalon.TalonControlMode.Speed);
    }

    /**
     * Set drive mode to basic percentage
     */
    public void setPercentMode()
    {
        lastOpenMode = CANTalon.TalonControlMode.PercentVbus;
        setControlMode(CANTalon.TalonControlMode.PercentVbus);
    }

    /**
     * Set drive mode to compensate for battery voltage, open-loop
     */
    public void setVoltageMode()
    {
        lastOpenMode = CANTalon.TalonControlMode.Voltage;
        setControlMode(CANTalon.TalonControlMode.Voltage);
    }

    /**
     * Begin a motion magic motion profile in a straight line for a specified distance
     *
     * @param distance distance to move, inches
     */
    public void moveMagic(double distance)
    {
        rMotionTarget = r.getPosition() + (distance / (Constants.Chassis.WHEEL_DIAMETER * Math.PI));
        lMotionTarget = l.getPosition() + (distance / (Constants.Chassis.WHEEL_DIAMETER * Math.PI));

        setControlMode(CANTalon.TalonControlMode.MotionMagic);
    }

    /**
     * Begin a motion magic motion profile to turn on the center point for specified degrees
     *
     * @param degrees degrees to turn, positive is clockwise
     */
    public void turnMagic(double degrees)
    {
        rMotionTarget = r.getPosition() - (Constants.Chassis.WHEEL_BASE_WIDTH * Math.PI * (degrees / 360));
        lMotionTarget = l.getPosition() + (Constants.Chassis.WHEEL_BASE_WIDTH * Math.PI * (degrees / 360));

        setControlMode(CANTalon.TalonControlMode.MotionMagic);
    }

    /**
     * Enable limiting the current of each talon to 40 amps
     */
    public void enableCurrentLimit()
    {
        setCurrentLimitState(true);
    }

    /**
     * Disable limiting the current of talons
     */
    public void disableCurrentLimit()
    {
        setCurrentLimitState(false);
    }

    /**
     * Determine if motion magic has finished. Will return true only if robot is still in same position as it ended the routine in.
     *
     * @return true if motion magic routine has finished
     */
    public boolean isMotionMagicFinished()
    {
        return (Math.abs(r.getPosition() - rMotionTarget) < Constants.Chassis.MOTION_MAGIC_ERROR) &&
               (Math.abs(l.getPosition() - lMotionTarget) < Constants.Chassis.MOTION_MAGIC_ERROR);
    }

    private void setControlMode(CANTalon.TalonControlMode mode)
    {
        r.changeControlMode(mode);
        l.changeControlMode(mode);
    }

    private void setCurrentLimitState(boolean state)
    {
        r.EnableCurrentLimit(state);
        l.EnableCurrentLimit(state);
    }

    private void monitorTalons()
    {
        Utils.monitorTalon(r);
        Utils.monitorTalon(rb);
        Utils.monitorTalon(l);
        Utils.monitorTalon(lb);
    }
}
