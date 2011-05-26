package org.hybird.ui.tk;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class StyleTest
{
    // set style
    // get style
    // add style
    // remove style
    // automatic style per component
    
    @Test
    public void autoStyle()
    {
        assertHasStyles (new HPanel(), "HPanel", "HComponent");
        assertHasStyles (new HButton(), "HButton", "HComponent");
    }
    
    private static void assertHasStyles (HComponent<?,?> component, String... styles)
    {
        for (String style : styles)
            assertTrue (component.hasStyle (style));
    }
}
