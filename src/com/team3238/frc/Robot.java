package com.team3238.frc;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends IterativeRobot
{
    private Joystick mainStick;
    
    private Chassis chassis;
    Climber climber;
    Collector collector;
    Shooter shooter;
    
    @Override public void robotInit()
    {
        mainStick = new Joystick(Constants.Robot.MAIN_STICK_PORT);
        
        chassis = new Chassis();
    }
    
    @Override public void robotPeriodic()
    {
        chassis.loop();
    }
    
    @Override public void disabledInit()
    {
    
    }
    
    @Override public void disabledPeriodic()
    {
    
    }
    
    @Override public void autonomousInit()
    {
    
    }
    
    @Override public void autonomousPeriodic()
    {
    
    }
    
    @Override public void teleopInit()
    {
        chassis.teleopInit();
    }
    
    @Override public void teleopPeriodic()
    {
        double y = mainStick.getY();
        double twist = mainStick.getTwist();
        double throttle = mainStick.getThrottle();
        
        boolean pidModeButton = mainStick.getRawButton(Constants.Robot.PID_MODE_BUTTON);
        boolean voltModeButton = mainStick.getRawButton(Constants.Robot.VOLT_MODE_BUTTON);
        boolean percModeButton = mainStick.getRawButton(Constants.Robot.PERC_MODE_BUTTON);
        boolean enableCurrent = mainStick.getRawButton(Constants.Robot.ENABLE_CURRENT_LIMIT_BUTTON);
        boolean disableCurrentLimit = mainStick.getRawButton(Constants.Robot.DISABLE_CURRENT_LIMIT_BUTTON);
        boolean moveButton = false; // mainStick.getRawButton(Constants.Robot.MOVE_BUTTON);
        
        if(pidModeButton)
        {
            chassis.setPIDMode();
        }
        if(percModeButton)
        {
            chassis.setPercentMode();
        }
        if(voltModeButton)
        {
            chassis.setVoltageMode();
        }
        if(enableCurrent)
        {
            chassis.enableCurrentLimit();
        }
        if(disableCurrentLimit)
        {
            chassis.disableCurrentLimit();
            
        }
        if(moveButton)
        {
            chassis.move(Constants.Robot.MOVE_DISTANCE);
        }
        
        throttle = ((throttle + 1) / 4) + 0.5;
        
        y *= throttle;
        twist *= throttle;
        
        chassis.drive(y, twist);
    }
    
    @Override public void testInit()
    {
    
    }
    
    @Override public void testPeriodic()
    {
    
    }
}
