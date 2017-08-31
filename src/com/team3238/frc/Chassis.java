package com.team3238.frc;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;

class Chassis
{
    private CANTalon r, rb, l, lb;
    private CANTalon.TalonControlMode lastOpenMode;
    
    private double rMotionTarget, lMotionTarget;
    
    Chassis()
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
    void loop() {
        monitorTalons();
    }
    
    /**
     * Init method to prepare for teleop mode.
     */
    void teleopInit()
    {
        setVoltageMode();
    }
    
    /**
     * Main drive method must be called for any motion to take place.
     *
     * @param y     y power, -1 - 1
     * @param twist twist power, -1 to 1
     */
    void drive(double y, double twist)
    {
        CANTalon.TalonControlMode rMode = r.getControlMode();
        CANTalon.TalonControlMode lMode = l.getControlMode();
        
        if(rMode != lMode)
        {
            DriverStation.reportWarning(
                    "Drive Talon Control modes out of sync. Changing to "
                            + rMode.name(), false);
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
                rSpeed = rSpeed * (Constants.Chassis.MAX_WHEEL_SPEED
                        - Constants.Chassis.MIN_WHEEL_SPEED)
                        + Constants.Chassis.MIN_WHEEL_SPEED;
                lSpeed = lSpeed * (Constants.Chassis.MAX_WHEEL_SPEED
                        - Constants.Chassis.MIN_WHEEL_SPEED)
                        + Constants.Chassis.MIN_WHEEL_SPEED;
                break;
            default:
                DriverStation.reportError(
                        "Drive loop missing case for control mode: " + rMode
                                .name(), false);
        }
        r.set(rSpeed);
        l.set(lSpeed);
    }
    
    void setPIDMode()
    {
        lastOpenMode = CANTalon.TalonControlMode.Speed;
        setControlMode(CANTalon.TalonControlMode.Speed);
    }
    
    void setPercentMode()
    {
        lastOpenMode = CANTalon.TalonControlMode.PercentVbus;
        setControlMode(CANTalon.TalonControlMode.PercentVbus);
    }
    
    void setVoltageMode()
    {
        lastOpenMode = CANTalon.TalonControlMode.Voltage;
        setControlMode(CANTalon.TalonControlMode.Voltage);
    }
    
    void move(double distance)
    {
        // rMotionTarget = distance (mapped);
        // lMotionTarget = distance (mapped);
        
        setControlMode(CANTalon.TalonControlMode.MotionMagic);
    }
    
    private boolean isMotionMagicFinished()
    {
        return (Math.abs(r.getPosition() - rMotionTarget)
                < Constants.Chassis.MOTION_MAGIC_ERROR) && (
                Math.abs(l.getPosition() - lMotionTarget)
                        < Constants.Chassis.MOTION_MAGIC_ERROR);
    }
    
    private void setControlMode(CANTalon.TalonControlMode mode)
    {
        r.changeControlMode(mode);
        l.changeControlMode(mode);
    }
    
    void enableCurrentLimit()
    {
        setCurrentLimitState(true);
    }
    
    void disableCurrentLimit()
    {
        setCurrentLimitState(false);
    }
    
    private void setCurrentLimitState(boolean state)
    {
        r.EnableCurrentLimit(state);
        l.EnableCurrentLimit(state);
    }
    
    private void monitorTalons()
    {
        monitorTalonState(r);
        monitorTalonState(rb);
        monitorTalonState(l);
        monitorTalonState(lb);
    }
    
    private void monitorTalonState(CANTalon talon)
    {
        double temp = talon.getTemperature();
        double current = talon.getOutputCurrent();
        
        if(temp > 100)
        {
            DriverStation.reportError(
                    "TALON" + talon.getDeviceID() + " TEMP IS HIGH: " + temp
                            + " DEGREES C", false);
        }
        if(current > 40)
        {
            DriverStation.reportError(
                    "TALON" + talon.getDeviceID() + " AMPS IS HIGH: " + temp
                            + " AMPS", false);
        }
    }
}
