package org.usfirst.frc.team3238.robot;

/**
 * Class holds all constants for each subsystem, divided into their own subclass.
 */
public class Constants
{
    /**
     * Robot subclass has all joystick buttons and values used by multiple subsystems
     */
    public class Robot
    {
        public static final int MAIN_STICK_PORT = 0;

        public static final int PLACE_GEAR_BUTTON = 1;
        public static final int COLLECT_GROUND_BUTTON = 2;
        public static final int DISABLE_BUTTON = 3;
        public static final int CLIMBER_UP_BUTTON_2 = 4;
        public static final int CLIMBER_UP_BUTTON = 6;
        public static final int PID_MODE_BUTTON = 7;
        public static final int VOLTAGE_MODE_BUTTON = 8;
        public static final int PERCENT_MODE_BUTTON = 9;
        public static final int ENABLE_CURRENT_LIMIT_BUTTON = 10;
        public static final int DISABLE_CURRENT_LIMIT_BUTTON = 11;

        public static final double PLACE_GEAR_DISTANCE = -24;

        public static final int DRIVER_CAMERA_ID = 0;
        public static final int VISION_CAMERA_ID = 1;
    }

    /**
     * Chassis subclass holds all values for the chassis
     */
    public class Chassis
    {
        public static final int RIGHT_A_TALON_ID = 1;
        public static final int RIGHT_B_TALON_ID = 2;
        public static final int LEFT_A_TALON_ID = 3;
        public static final int LEFT_B_TALON_ID = 4;

        public static final int CURRENT_LIMIT_AMPS = 40;

        public static final int MAX_WHEEL_SPEED = 50;
        public static final int MIN_WHEEL_SPEED = 1;

        public static final double P_VALUE = 0.1;
        public static final double I_VALUE = 0.0;
        public static final double D_VALUE = 0.0;
        public static final double F_VALUE = 0.0;

        public static final double MOTION_MAGIC_ACCEL = 0;
        public static final double MOTION_MAGIC_VEL = 0;
        public static final double MOTION_MAGIC_ERROR = 50;

        public static final double WHEEL_DIAMETER = 7.64;
        public static final double WHEEL_BASE_WIDTH = 23.5;
    }

    /**
     * Climber subclass holds all values for the climber
     */
    public class Climber
    {
        public static final int MASTER_TALON_ID = 8;
        public static final int SLAVE_TALON_ID = 9;

        public static final double UP_POWER = -1.0;
    }

    /**
     * Collector subclass holds all values for the collector
     */
    public class Collector
    {
        public static final int LIFT_TALON_ID = 5;
        public static final int LEFT_TALON_ID = 6;
        public static final int RIGHT_TALON_ID = 7;

        public static final double INTAKE_POWER = -0.65;
        public static final double RAISE_POWER = 0.55;
        public static final double LOWER_POWER = -0.55;
        public static final double PLACE_GEAR_POWER = -0.7;
        public static final int ENCODER_BOTTOM_LIMIT = -600;
    }
}
