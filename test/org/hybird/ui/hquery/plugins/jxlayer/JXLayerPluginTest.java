package org.hybird.ui.hquery.plugins.jxlayer;

import java.awt.BorderLayout;

import org.hybird.ui.AbstractTest;
import org.hybird.ui.hquery.HQuery;
import org.hybird.ui.tk.HButton;
import org.hybird.ui.tk.HLabel;
import org.hybird.ui.tk.HPanel;
import org.junit.Before;

public class JXLayerPluginTest extends AbstractTest
{
//    @Test
//    public void getLayoutConstraints()
//    {
//        assertEquals (BorderLayout.CENTER, $("#h1").with (JXLayerPlugin).constraint());
//        assertEquals (BorderLayout.SOUTH, $("#h3").with (JXLayerPlugin).constraint());
//        assertEquals (BorderLayout.NORTH, $("#h5").with (JXLayerPlugin).constraint());
//    }
    
    // possibly remember the constraint inside the plugin (by overriding add) instead of the hpanel
    
    private HPanel h;
    
    @Before
    public void setUp()
    {
        h = new HPanel (new BorderLayout()).as ("h");
        
        HPanel h1 = new HPanel().as ("h1").add (new HLabel().as ("h2"));
        h.add (h1, BorderLayout.CENTER);
        
        HPanel h3 = new HPanel().as ("h3").add (new HLabel().as ("h4"));
        h.add (h3, BorderLayout.SOUTH);
        
        h.add (new HButton().as ("h5"), BorderLayout.NORTH);
    }
    
    public HQuery $ (String query)
    {
        return h.query (query);
    }
}
