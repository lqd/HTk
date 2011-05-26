package org.hybird.ui.tk;

import static org.junit.Assert.assertEquals;

import org.hybird.ui.AbstractTest;
import org.hybird.ui.query.collectors.Collectors;
import org.junit.Before;
import org.junit.Test;

public class HierarchyTest extends AbstractTest
{
    private HPanel body;
    
    @Before
    public void setUp()
    {
        body = styledHPanel ("body", "body");
    }
    
    @Test
    public void parents()
    {
        HFrame f = new HFrame();
        f.content (body);
        
        body.add (new HPanel().as ("h1"));
        
        HLabel h2 = new HLabel().as ("h2");
        body.add (h2);
        
        HPanel h3 = new HPanel().as ("h3");
        body.add (h3);
        
        assertEquals ("body", h2.parent ().name ());
        assertEquals (body.asComponent (), h2.parent ().asComponent ());
        
        assertEquals (3, Collectors.getAllParents (h2).size ()); // body, jlayeredpane, jrootpanel
        assertEquals ("body", Collectors.getAllParents (h2).get (0).getName ());
        assertEquals (body.asComponent (), Collectors.getAllParents (h2).get (0));
        
        assertEquals (3, Collectors.getAllParents (h2, h3).size ());
    }
}
