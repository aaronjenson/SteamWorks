package org.usfirst.frc.team3238.robot.auto;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3238.robot.Constants;
import org.usfirst.frc.team3238.robot.subsystems.Chassis;
import org.usfirst.frc.team3238.robot.subsystems.Collector;
import org.usfirst.frc.team3238.robot.utils.POVState;
import org.usfirst.frc.team3238.robot.utils.Utils;

/**
 * Autonomous class, controls all actions during the 15 second start of each match with no driver control
 */
public class Autonomous implements Vision.VisionListener
{
    private Chassis drive;
    private Collector collect;

    private Vision vision;

    private SendableChooser<String> chooser;

    private int position = 0; // -1 for left, 0 for center, 1 for right, anything else for nothing
    private boolean placeAndRun = false; // true to attempt running down the field after placing

    private boolean visionDone;

    private boolean[] phaseDone = new boolean[6];
    private boolean[] phaseBegun = new boolean[6];
    private boolean autoDone;

    /**
     * Starts camera, stores subsystems in
     *
     * @param chassis
     * @param collector
     */
    public Autonomous(Chassis chassis, Collector collector)
    {
        UsbCamera cam = CameraServer.getInstance().startAutomaticCapture(Constants.Robot.VISION_CAMERA_ID);
        drive = chassis;
        collect = collector;

        vision = new Vision(cam, this);
        vision.start();
    }

    /**
     * Sends auto routine options to smart dashboard for selection
     */
    public void select()
    {
        chooser = new SendableChooser<>();
        chooser.addObject("Boiler Gear", "boilerGear");
        chooser.addObject("Boiler Gear Run", "boilerGearRun");
        chooser.addDefault("Center Gear", "centerGear");
        chooser.addObject("Retrieval Gear", "retrGear");
        chooser.addObject("Retrieval Gear Run", "retrGearRun");
        chooser.addObject("None", "none");
        SmartDashboard.putData("Auto Routine", chooser);
    }

    /**
     * Resets phase data, sets up subsystems
     */
    public void init()
    {
        String routine = chooser.getSelected();
        boolean isRedAlliance = DriverStation.getInstance().getAlliance().toString().equals("Red");
        setData(routine, isRedAlliance);

        visionDone = false;
        vision.stopProcessing();

        for(int i = 0; i < phaseDone.length; i++)
        {
            phaseDone[i] = false;
        }
        for(int i = 0; i < phaseBegun.length; i++)
        {
            phaseBegun[i] = false;
        }

        autoDone = false;

        collect.init();
    }

    /**
     * Main auto method, must be called each loop to make anything happen
     */
    public void run()
    {

        if(autoDone)
        {
            drive.setPercentMode();
            collect.stop();
        }
        else if(!phaseDone[0] && (position == -1 || position == 1 || position == 0)) // moving forward, all autos
        {
            if(!phaseBegun[0])
            {
                drive.moveMagic(73.25);
                phaseBegun[0] = true;
            }
            if(drive.isMotionMagicFinished())
            {
                phaseDone[0] = true;
            }
        }
        else if(!phaseDone[0]) // no auto running
        {
            Utils.say("No auto selected");
            autoDone = true;
        }
        else if(!phaseDone[1] && position == -1) // left gear, turning to peg
        {
            if(!phaseBegun[1])
            {
                drive.turnMagic(60);
                phaseBegun[1] = true;
            }
            if(drive.isMotionMagicFinished())
            {
                phaseDone[1] = true;
            }
        }
        else if(!phaseDone[1] && position == 1) // right gear, turning to peg
        {
            if(!phaseBegun[1])
            {
                drive.turnMagic(-60);
                phaseBegun[1] = true;
            }
            if(drive.isMotionMagicFinished())
            {
                phaseDone[1] = true;
            }
        }
        else if(!phaseDone[1] && position == 0) // center gear, ~25 inches to peg
        {
            if(!phaseBegun[2])
            {
                vision.startProcessing();
                phaseBegun[2] = true;
            }
            if(visionDone)
            {
                vision.stopProcessing();
                phaseDone[2] = true;
            }
        }
        else if(!phaseDone[2] && (position == -1 || position == 1)) // left or right gear, ~82 inches to peg, oriented
        {
            if(!phaseBegun[2])
            {
                vision.startProcessing();
                phaseBegun[2] = true;
            }
            if(visionDone)
            {
                vision.stopProcessing();
                phaseDone[2] = true;
            }
        }
        else if(!phaseDone[2] && position == 0) // center gear, at peg, placing
        {
            if(!phaseBegun[2])
            {
                drive.moveMagic(-35);
                collect.place();
                phaseBegun[2] = true;
            }
            if(drive.isMotionMagicFinished() && collect.getState().equals("inactive"))
            {
                phaseDone[2] = true;
                autoDone = true;
            }
        }
        else if(!phaseDone[3] && (position == -1 || position == 1)) // side gear at peg, placing
        {
            if(!phaseBegun[3])
            {
                drive.moveMagic(-35);
                collect.place();
                phaseBegun[3] = true;
            }
            if(drive.isMotionMagicFinished() && collect.getState().equals("inactive"))
            {
                phaseDone[3] = true;
                if(!placeAndRun)
                {
                    autoDone = true;
                }
            }
        }
        else if(!phaseDone[4] && position == -1 && placeAndRun) // side gear w/ run (left), turning
        {
            if(!phaseBegun[4])
            {
                drive.turnMagic(-60);
                phaseBegun[4] = true;
            }
            if(drive.isMotionMagicFinished())
            {
                phaseDone[4] = true;
            }
        }
        else if(!phaseDone[4] && position == 1 && placeAndRun) // side gear w/ run (right), turning
        {
            if(!phaseBegun[4])
            {
                drive.turnMagic(60);
                phaseBegun[4] = true;
            }
            if(drive.isMotionMagicFinished())
            {
                phaseDone[4] = true;
            }
        }
        else if(!phaseDone[5] && placeAndRun) // side gear w/ run, running
        {
            if(!phaseBegun[5])
            {
                drive.moveMagic(216);
                phaseBegun[5] = true;
            }
            if(drive.isMotionMagicFinished())
            {
                phaseDone[5] = true;
                autoDone = true;
            }
        }
        else
        {
            autoDone = true;
        }

        drive.drive(0.0, 0.0);
        collect.run(new POVState());
    }

    /**
     * Listener for vision data
     *
     * @param output data transfer object with angle and distance to target
     */
    @Override
    public void onFrameReady(Vision.VisionOutput output)
    {
        if(Math.abs(output.angle) > 1.0)
        {
            drive.turnMagic(output.angle);
        }
        else if(output.distance > 1.0)
        {
            drive.moveMagic(output.distance);
        }
        else
        {
            visionDone = true;
        }
    }

    private void setData(String choice, boolean isRed)
    {
        if((choice.contains("retrGear") && isRed) || (choice.contains("boilerGear") && !isRed))
        {
            position = -1;
        }
        else if(choice.equals("centerGear"))
        {
            position = 0;
        }
        else if((choice.contains("boilerGear") && isRed) || (choice.contains("retrGear") && !isRed))
        {
            position = 1;
        }
        else if(choice.equals("none"))
        {
            position = -2;
        }

        placeAndRun = choice.contains("Run");
    }
}
