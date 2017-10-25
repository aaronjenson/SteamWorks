package org.usfirst.frc.team3238.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team3238.robot.auto.Autonomous;
import org.usfirst.frc.team3238.robot.subsystems.Chassis;
import org.usfirst.frc.team3238.robot.subsystems.Climber;
import org.usfirst.frc.team3238.robot.subsystems.Collector;
import org.usfirst.frc.team3238.robot.utils.POVState;

/**
 * Main Robot class is controlled by WPI software. All actions of the robot go through this class.
 */
public class Robot extends IterativeRobot
{
    private Joystick mainStick;

    private Chassis chassis;
    private Climber climber;
    private Collector collector;

    private Autonomous auto;

    /**
     * Called once when the robot is powered on.
     */
    @Override
    public void robotInit()
    {
        mainStick = new Joystick(Constants.Robot.MAIN_STICK_PORT);

        chassis = new Chassis();
        climber = new Climber();
        collector = new Collector();

        auto = new Autonomous(chassis, collector);

        CameraServer.getInstance().startAutomaticCapture(Constants.Robot.DRIVER_CAMERA_ID);
    }

    /**
     * Called in a loop for the entire time the robot is powered on.
     * Checks talon current and temp to ensure there are no problems.
     */
    @Override
    public void robotPeriodic()
    {
        chassis.loop();
        climber.loop();
        collector.loop();
    }

    /**
     * Called once when any mode is disabled
     */
    @Override
    public void disabledInit()
    {

    }

    /**
     * Called in a loop while no other mode is enabled
     */
    @Override
    public void disabledPeriodic()
    {

    }

    /**
     * Called once at the start of auto mode
     */
    @Override
    public void autonomousInit()
    {
        auto.init();
    }

    /**
     * Called in a loop during auto mode
     */
    @Override
    public void autonomousPeriodic()
    {
        auto.run();
    }

    /**
     * Called once at the start of teleop mode
     */
    @Override
    public void teleopInit()
    {
        chassis.teleopInit();
        climber.init();
        collector.init();
    }

    /**
     * Called in a loop during teleop mode.
     * Controls all logic of the subsystems based off of joystick inputs.
     */
    @Override
    public void teleopPeriodic()
    {
        double y = mainStick.getY();
        double twist = mainStick.getTwist();
        double throttle = mainStick.getThrottle();

        boolean pidModeButton = mainStick.getRawButton(Constants.Robot.PID_MODE_BUTTON);
        boolean voltModeButton = mainStick.getRawButton(Constants.Robot.VOLTAGE_MODE_BUTTON);
        boolean percModeButton = mainStick.getRawButton(Constants.Robot.PERCENT_MODE_BUTTON);
        boolean enableCurrent = mainStick.getRawButton(Constants.Robot.ENABLE_CURRENT_LIMIT_BUTTON);
        boolean disableCurrentLimit = mainStick.getRawButton(Constants.Robot.DISABLE_CURRENT_LIMIT_BUTTON);
        boolean placeGearButton = mainStick.getRawButton(Constants.Robot.PLACE_GEAR_BUTTON);
        boolean climberUpButton = mainStick.getRawButton(Constants.Robot.CLIMBER_UP_BUTTON) ||
                                  mainStick.getRawButton(Constants.Robot.CLIMBER_UP_BUTTON_2);
        boolean collectButton = mainStick.getRawButton(Constants.Robot.COLLECT_GROUND_BUTTON);
        boolean disableButton = mainStick.getRawButton(Constants.Robot.DISABLE_BUTTON);

        // CHASSIS LOGIC
        // -------------
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
        if(placeGearButton)
        {
            chassis.moveMagic(Constants.Robot.PLACE_GEAR_DISTANCE);
        }

        throttle = ((throttle + 1) / 4) + 0.5;

        y *= throttle;
        twist *= throttle;

        chassis.drive(y, twist);

        // CLIMBER LOGIC
        // -------------
        if(climberUpButton)
        {
            climber.up();
        }
        else
        {
            climber.stop();
        }

        climber.run();

        // COLLECTOR LOGIC
        // ---------------
        if(collectButton)
        {
            collector.pickup();
        }
        if(placeGearButton)
        {
            collector.place();
        }
        if(disableButton)
        {
            collector.stop();
        }

        collector.run(new POVState(mainStick));
    }

    /**
     * Called once at the start of test mode
     */
    @Override
    public void testInit()
    {

    }

    /**
     * Called in a loop during test mode
     */
    @Override
    public void testPeriodic()
    {

    }
}
