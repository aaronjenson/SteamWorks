package org.usfirst.frc.team3238.robot.utils;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Class to hold the state of the top hat on a logitech extreme 3d pro joystick.
 * Used for data transfer.
 */
public class POVState
{

    private boolean[] vals = new boolean[9];

    /**
     * Create new state based off of joystick object, data is populated accordingly
     *
     * @param joy joystick to base data off of
     */
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

    /**
     * Create new state without a joystick, none is automatically selected
     */
    public POVState()
    {
        vals[0] = true;
        for(int i = 1; i < vals.length; i++)
        {
            vals[i] = false;
        }
    }

    /**
     * is top hat in center
     *
     * @return true if top hat is in center
     */
    public boolean isNone()
    {
        return vals[0];
    }

    /**
     * is top hat at top
     *
     * @return true if top hat is at top
     */
    public boolean isTop()
    {
        return vals[1];
    }

    /**
     * is top hat in top right
     *
     * @return true if top hat is in top right
     */
    public boolean isTopRight()
    {
        return vals[2];
    }

    /**
     * is top hat at right
     *
     * @return true if top hat is at right
     */
    public boolean isRight()
    {
        return vals[3];
    }

    /**
     * is top hat in bottom right
     *
     * @return true if top hat is in bottom right
     */
    public boolean isBottomRight()
    {
        return vals[4];
    }

    /**
     * is top hat at bottom
     *
     * @return true if top hat is at bottom
     */
    public boolean isBottom()
    {
        return vals[5];
    }

    /**
     * is top hat in bottom left
     *
     * @return true if top hat is in bottom left
     */
    public boolean isBottomLeft()
    {
        return vals[6];
    }

    /**
     * is top hat at left
     *
     * @return true if top hat is at left
     */
    public boolean isLeft()
    {
        return vals[7];
    }

    /**
     * is top hat in top left
     *
     * @return true if top hat is in top left
     */
    public boolean isTopLeft()
    {
        return vals[8];
    }
}
