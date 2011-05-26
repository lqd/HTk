package org.hybird.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.hybird.ui.events.swing.Events;
import org.hybird.ui.hquery.HQuery;
import org.hybird.ui.hquery.plugins.jxlayer.JXLayerPlugin;
import org.hybird.ui.query.Expression;
import org.hybird.ui.query.Query;
import org.hybird.ui.tk.HPanel;

public class LiveEventsTest
{
    public static void main (String [] args)
    {
    	Query q = new Query ("JButton");
    	final Expression expression = q.expressions().get (0);  	
    	
    	final long allEvents = AWTEvent.ACTION_EVENT_MASK | AWTEvent.ADJUSTMENT_EVENT_MASK
		| AWTEvent.COMPONENT_EVENT_MASK | AWTEvent.CONTAINER_EVENT_MASK
		| AWTEvent.FOCUS_EVENT_MASK | AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK | 
		AWTEvent.HIERARCHY_EVENT_MASK | AWTEvent.INPUT_METHOD_EVENT_MASK |
		AWTEvent.INVOCATION_EVENT_MASK | AWTEvent.ITEM_EVENT_MASK |
		AWTEvent.MOUSE_EVENT_MASK
		| AWTEvent.MOUSE_MOTION_EVENT_MASK
		| AWTEvent.MOUSE_WHEEL_EVENT_MASK | AWTEvent.PAINT_EVENT_MASK;
    	
		Toolkit.getDefaultToolkit().addAWTEventListener (new AWTEventListener()
		{
			@Override
			public void eventDispatched (AWTEvent e)
			{
				Object source = e.getSource();
				if (source instanceof JComponent && expression.matches ((JComponent) source))
				{
//					System.out.println(".eventDispatched(): " + e);
				}
//				else if (source instanceof JFrame)
//					System.out.println("check on content pane");
				//System.out.println("on: " + e.getSource());
				
				
			}
		},
		allEvents
		);
    	
//    	Toolkit.getDefaultToolkit().getSystemEventQueue().push (new EventQueue()
//    	{
//    		@Override
//    		protected void dispatchEvent (AWTEvent e)
//    		{
//    			super.dispatchEvent (e);
//    			
//    			if (e instanceof MouseEvent)
//    			System.out.println(e);
//    		}
//    	});
    	
        SwingUtilities.invokeLater (new Runnable()
        {
            @Override
            public void run ()
            {
                JFrame f = new JFrame("Box Layout Test");
                f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
                
                main = new HPanel (new BorderLayout ());
                JPanel buttons = new JPanel();
                
                final Box content = Box.createVerticalBox ();

                content.add (Box.createVerticalGlue ());
                
                buttons.add (new JButton (new AbstractAction ("Add canvas")
                {
                    Color [] colors = {Color.orange, Color.yellow};
                    int i = 0;
                    
                    @Override
                    public void actionPerformed (ActionEvent e)
                    {
                        JPanel canvas = new JPanel()
                        {{
                        	enableEvents (allEvents);
                        }};
                        
                        canvas.setBackground (colors[i++ % colors.length]);
                        canvas.setPreferredSize (new Dimension (640, 100));
                        canvas.setMaximumSize (new Dimension (640, 100));
                        
                        content.add (canvas);
                        content.validate ();
                    }
                }));
                
                content.setMinimumSize (new Dimension (640, 480));
                content.setPreferredSize (new Dimension (640, 480));
                
                main.add (buttons, BorderLayout.NORTH);
                main.add (content, BorderLayout.CENTER);
                
                f.setContentPane (main.asComponent ());
                f.pack ();
                f.setLocationRelativeTo (null);
                f.setVisible (true);
                
                // should probably be nicer as a pluggin that hijacks add calls
                $ ("JButton").connect (Events.action, new ActionListener()
                {
                    @Override
                    public void actionPerformed (ActionEvent e)
                    {
                        $ ("JButton").with (JXLayerPlugin.class).wrap ();
                    }
                });
            }
        });
    }
    
    private static HPanel main;
    
    public static HQuery $ (String query)
    {
        return main.query (query);
    }
}