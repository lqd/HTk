package org.hybird.ui.hquery.plugins.jxlayer;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.hybird.ui.hquery.DelegatedHQuery;
import org.hybird.ui.hquery.HQuery;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.BufferedLayerUI;
import org.jdesktop.jxlayer.plaf.effect.BufferedImageOpEffect;

import com.jhlabs.image.BlurFilter;

public class JXLayerPlugin extends DelegatedHQuery
{
    public static final Class<JXLayerPlugin> JXLayerPlugin = JXLayerPlugin.class;
    
    public JXLayerPlugin (HQuery delegate)
    {
        super (delegate);
    }
    
    private static final String WRAPPED_PROPERTY = "JXLayerPlugin.wrapped"; 
    
    public void wrap ()
    {
        JComponent component = this.contents ().get (0);
        
        if (component.getClientProperty (WRAPPED_PROPERTY) != null)
            return;
        
        JComponent parent = (JComponent) component.getParent ();
        
        parent.remove (component);
     
        BufferedLayerUI<JComponent> layerUI = new BufferedLayerUI<JComponent>()
        {
            @Override
            protected void paintLayer(Graphics2D g, JXLayer<? extends JComponent> l)
            {
                // this paints the layer as is
                super.paintLayer (g, l);
                // fill it with the translucent green
                g.setColor (new Color(0, 128, 0, 128));
                g.fillRect (0, 0, l.getWidth(), l.getHeight());
            }
        };
 
//        BufferedLayerUI<JComponent> layerUI = new BufferedLayerUI<JComponent>();
        layerUI.setLayerEffects (new BufferedImageOpEffect(new BlurFilter()));
        
        // create the layer for the panel using our custom layerUI
        JXLayer<JComponent> layer = new JXLayer<JComponent> (component, layerUI);
        parent.add (layer);
        
        component.putClientProperty (WRAPPED_PROPERTY, true);
        
        parent.validate ();
    }
}
