package org.hybird.ui.tk;

import javax.swing.SwingConstants;

public enum HAlignment
{
    TOP (SwingConstants.TOP),
    BOTTOM (SwingConstants.BOTTOM),
    
    LEFT (SwingConstants.LEFT),
    RIGHT (SwingConstants.RIGHT),
    
    CENTER (SwingConstants.CENTER);

    private int v;

    private HAlignment (int v)
    {
        this.v = v;
    }

    public int value ()
    {
        return v;
    }
}