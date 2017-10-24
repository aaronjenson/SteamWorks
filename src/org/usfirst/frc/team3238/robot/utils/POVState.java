package org.usfirst.frc.team3238.robot.utils;

import edu.wpi.first.wpilibj.Joystick;

public class POVState
{

    private boolean[] vals = new boolean[9];

    public POVState(Joystick joy)
    {
        int val = joy.getPOV();
        vals[0] = val == -1;

        int checkVal = 0;

        for(int i = 1; i <= 8; i++)
        {
            vals[i] = val == checkVal;
            checkVal += 45;
        }
    }

    public POVState()
    {
        vals[0] = true;
        for(int i = 1; i < vals.length; i++)
        {
            vals[i] = false;
        }
    }

    public boolean isNone()
    {
        return vals[0];
    }

    public boolean isTop()
    {
        return vals[1];
    }

    public boolean isTopRight()
    {
        return vals[2];
    }

    public boolean isRight()
    {
        return vals[3];
    }

    public boolean isBottomRight()
    {
        return vals[4];
    }

    public boolean isBottom()
    {
        return vals[5];
    }

    public boolean isBottomLeft()
    {
        return vals[6];
    }

    public boolean isLeft()
    {
        return vals[7];
    }

    public boolean isTopLeft()
    {
        return vals[8];
    }
}
