package com.team3238.frc;

class Constants
{
    class Robot
    {
        static final int MAIN_STICK_PORT = 0;
        
        static final int PID_MODE_BUTTON = 7;
        static final int VOLT_MODE_BUTTON = 8;
        static final int PERC_MODE_BUTTON = 9;
        static final int ENABLE_CURRENT_LIMIT_BUTTON = 10;
        static final int DISABLE_CURRENT_LIMIT_BUTTON = 11;
        static final int MOVE_BUTTON = 1;
        
        static final double MOVE_DISTANCE = 50;
    }
    
    class Chassis
    {
        static final int RIGHT_A_TALON_ID = 1;
        static final int RIGHT_B_TALON_ID = 2;
        static final int LEFT_A_TALON_ID = 3;
        static final int LEFT_B_TALON_ID = 4;
        
        static final int CURRENT_LIMIT_AMPS = 40;
        
        static final int MAX_WHEEL_SPEED = 50;
        static final int MIN_WHEEL_SPEED = 1;
        
        static final double P_VALUE = 0.1;
        static final double I_VALUE = 0.0;
        static final double D_VALUE = 0.0;
        static final double F_VALUE = 0.0;
        
        static final double MOTION_MAGIC_ACCEL = 0;
        static final double MOTION_MAGIC_VEL = 0;
        static final double MOTION_MAGIC_ERROR = 50;
    }
    
    class Climber
    {
    
    }
    
    class Collector
    {
    
    }
    
    class Shooter
    {
    
    }
}
