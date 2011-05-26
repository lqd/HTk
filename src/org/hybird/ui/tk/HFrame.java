package org.hybird.ui.tk;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.hybird.ui.hquery.HQuery;

@SuppressWarnings("rawtypes")
public class HFrame
{
    public static final Class<HFrame> HFrame = HFrame.class;
    public static final Class<JFrame> JFrame = JFrame.class;
    
    private JFrame delegate;
    
    public HFrame ()
    {
        this ("");
    }
    
    public HFrame (String title)
    {
        JFrame frame = new JFrame (title);
        this.delegate = frame;
        
        delegate.setDefaultCloseOperation (javax.swing.JFrame.EXIT_ON_CLOSE);
    }
    
    public HFrame (JFrame frame)
    {
        this.delegate = frame;
    }
    
    public JFrame asComponent()
    {
        return delegate;
    }
    
    public void content (HComponent content)
    {
        delegate.setContentPane (content.asComponent ());
    }
    
    public void visible (boolean visible)
    {
        delegate.setVisible (visible);
    }
    
    public HQuery query (String selector)
    {
        return HPanel.query (selector, (JComponent) delegate.getContentPane ());
    }
    
    public void launch ()
    {
        delegate.pack ();
        delegate.setLocationRelativeTo (null);
        delegate.setVisible (true);
    }
    
    public void resizable (boolean resizable)
    {
        delegate.setResizable (resizable);
    }
}
