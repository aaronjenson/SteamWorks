package org.usfirst.frc.team3238.robot.auto;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team3238.robot.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Vision class takes input from a camera and identifies the two pieces of retro-reflective tape next to the peg.
 * Calculates distance and angle and sends to another class.
 */
public class Vision extends Thread
{

    private static final double CAMERA_FIELD_OF_VIEW = 75;
    private static final int FRAME_WIDTH = 480;
    private static final int FRAME_HEIGHT = 320;

    private static final int CAMERA_EXPOSURE = 1;

    private static final int COLOR_MIN_H = 78;
    private static final int COLOR_MIN_S = 172;
    private static final int COLOR_MIN_V = 44;
    private static final int COLOR_MAX_H = 92;
    private static final int COLOR_MAX_S = 255;
    private static final int COLOR_MAX_V = 255;

    private static final double CONTOUR_MAX_AREA = 500;
    private static final double CONTOUR_MIN_AREA = 100;

    private static final double CONTOUR_MIN_HEIGHT = 100;
    private static final double CONTOUR_MAX_HEIGHT = 200;
    private static final double CONTOUR_MIN_WIDTH = 20;
    private static final double CONTOUR_MAX_WIDTH = 100;

    private static final double CONTOUR_MIN_RATIO = 0.2;
    private static final double CONTOUR_MAX_RATIO = 1;

    /**
     * Data transfer object for distance and angle to target
     */
    static class VisionOutput
    {
        double angle;
        double distance;

        VisionOutput(double distance, double angle)
        {
            this.angle = angle;
            this.distance = distance;
        }
    }

    /**
     * Interface used to notify other classes of new data
     */
    public interface VisionListener
    {
        void onFrameReady(VisionOutput output);
    }

    private CvSink sink;
    private VisionListener listener;
    private CvSource mask_serve;

    private Mat sourceFrame;
    private Mat mask;
    private List<MatOfPoint> contours;
    private List<MatOfPoint> filteredContours;

    private boolean isProcessing = false;

    /**
     * Sets up camera, adds listener to this class
     *
     * @param source
     * @param listener
     */
    public Vision(UsbCamera source, VisionListener listener)
    {
        super();
        this.listener = listener;

        source.setExposureManual(CAMERA_EXPOSURE);

        sink = CameraServer.getInstance().getVideo(source);
        sourceFrame = new Mat();
        mask = new Mat();

        contours = new ArrayList<>();
        filteredContours = new ArrayList<>();

        mask_serve = CameraServer.getInstance().putVideo("mask", FRAME_WIDTH, FRAME_HEIGHT);
    }

    /**
     * Enables processing until stopProcessing() is called
     */
    public void startProcessing()
    {
        isProcessing = true;
    }

    /**
     * Disables processing until startProcessing() is called
     */
    public void stopProcessing()
    {
        isProcessing = false;
    }

    /**
     * Main method, processes frames in a while loop if enabled.
     */
    @Override
    public void run()
    {
        while(!Thread.interrupted())
        {
            if(isProcessing)
            {
                double distance;
                double angle;

                double timestamp = Timer.getFPGATimestamp();

                try
                {
                    sink.grabFrame(sourceFrame);

                    Imgproc.resize(sourceFrame, sourceFrame, new Size(FRAME_WIDTH, FRAME_HEIGHT));
                    Imgproc.cvtColor(sourceFrame, sourceFrame, Imgproc.COLOR_RGB2HSV);

                    Core.inRange(sourceFrame, new Scalar(COLOR_MIN_H, COLOR_MIN_S, COLOR_MIN_V),
                                 new Scalar(COLOR_MAX_H, COLOR_MAX_S, COLOR_MAX_V), mask);

                    mask_serve.putFrame(mask);

                    contours.clear();
                    filteredContours.clear();
                    Imgproc.findContours(mask, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                    for(MatOfPoint contour : contours)
                    {
                        double contourArea = Imgproc.contourArea(contour);

                        if(contourArea < CONTOUR_MIN_AREA || contourArea > CONTOUR_MAX_AREA)
                        {
                            continue;
                        }

                        Rect bound = Imgproc.boundingRect(contour);

                        if(bound.height < CONTOUR_MIN_HEIGHT || bound.height > CONTOUR_MAX_HEIGHT)
                        {
                            continue;
                        }
                        if(bound.width < CONTOUR_MIN_WIDTH || bound.width > CONTOUR_MAX_WIDTH)
                        {
                            continue;
                        }

                        double ratio = bound.width / bound.height;

                        if(ratio < CONTOUR_MIN_RATIO || ratio > CONTOUR_MAX_RATIO)
                        {
                            continue;
                        }

                        filteredContours.add(contour);
                    }

                    Utils.say(filteredContours.size() + " contours found");

                    if(filteredContours.size() > 2)
                    {
                        filteredContours.sort((o1, o2) ->
                                              {
                                                  double area1 = Imgproc.contourArea(o1);
                                                  double area2 = Imgproc.contourArea(o2);

                                                  return Double.compare(area1, area2);

                                              });
                    }

                    if(filteredContours.size() > 1)
                    {

                        Rect bounds1 = Imgproc.boundingRect(filteredContours.get(0));
                        Rect bounds2 = Imgproc.boundingRect(filteredContours.get(1));

                        double centerX1 = bounds1.x + (0.5 * bounds1.width);
                        double centerX2 = bounds2.x + (0.5 * bounds2.width);

                        double centerXOffset = (centerX1 - centerX2) / 2;
                        double centerX = Math.min(centerX1, centerX2) + centerXOffset;

                        double height = (bounds1.height + bounds2.height) / 2;

                        angle = ((centerX - (FRAME_WIDTH / 2)) / (FRAME_WIDTH / 2)) * CAMERA_FIELD_OF_VIEW;
                        distance = 2.5 / Math.tan((height / FRAME_HEIGHT) * CAMERA_FIELD_OF_VIEW * 0.5);

                        listener.onFrameReady(new VisionOutput(distance, angle));
                    }

                    double time = Timer.getFPGATimestamp() - timestamp;
                    Utils.say("Vision FPS: " + (1 / time));

                } catch(Exception e)
                {
                    DriverStation.reportError(e.getMessage(), true);
                }
            }
        }
    }
}

