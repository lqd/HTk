package org.hybird.ui.hquery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.hybird.ui.AbstractTest;
import org.hybird.ui.events.CompositeEventSource;
import org.hybird.ui.events.EventListener;
import org.hybird.ui.events.source.DefaultCompositeEventSource;
import org.hybird.ui.events.swing.Events;
import org.hybird.ui.events.swing.MouseEvents;
import org.hybird.ui.tk.HButton;
import org.hybird.ui.tk.HComponent;
import org.hybird.ui.tk.HLabel;
import org.hybird.ui.tk.HPanel;
import org.junit.Before;
import org.junit.Test;

public class HQueryTest extends AbstractTest
{
	@Test
	public void contentsAreAvailable()
	{
		assertListContainsNames ($("*").contents(), "h", "h1", "h2", "h3", "h4", "h5");
	}
	
	@Test
	public void addRemoveStyle()
	{
		assertFalse (h.hasStyle ("added"));
		
		$ ("*").addStyle ("added");
		assertTrue (h.hasStyle ("added"));
		
		$ ("*").removeStyle ("added");
		assertFalse (h.hasStyle ("added"));
	}
	
	@Test
	public void toggleStyle()
	{
		assertFalse (h.hasStyle ("a"));
		assertFalse (h.hasStyle ("b"));
		
		$ ("*").toggleStyle ("a", "b");
		assertTrue (h.hasStyle ("a"));
		assertFalse (h.hasStyle ("b"));
		
		$ ("*").toggleStyle ("a", "b");
		assertFalse (h.hasStyle ("a"));
		assertTrue (h.hasStyle ("b"));
		
		$ ("*").toggleStyle ("a", "b");
		assertTrue (h.hasStyle ("a"));
		assertFalse (h.hasStyle ("b"));
	}
	
	@Test
	public void hQueryHasSelector()
	{
		assertEquals ("*", $ ("*").selector());
		assertEquals ("JPanel[attr=bobi]", $ ("JPanel[attr=bobi]").selector());
	}
	
	@Test
	public void resultsFilteringWithFind()
	{
		assertQueryMatches (".HPanel > .HPanel", h, "h1", "h3");
		assertQueryMatches (".HPanel > .HPanel:first-child > .HLabel", h, "h2");
		
		HQuery subQuery = $ (".HPanel > .HPanel:first-child").find (".HLabel");
		assertEquals (1, subQuery.count());
		assertEquals ("h2", subQuery.asJComponent().getName());
	}
	
	@Test
	public void parents()
	{
	    assertListContainsNames ($ ("#h4").parents().contents (), "h", "h3");
	    assertListContainsNames ($ ("#h4, #h2").parents().contents (), "h", "h1", "h3");
	    
	    HPanel h6 = new HPanel().as ("h6");
        h6.addTo (h);
        
        assertListContainsNames ($ ("#h4, #h2, #h6").parents().contents (), "h", "h1", "h3");
        
        HButton h7 = new HButton ().as ("h7");
        h7.addTo (h6);
        
        assertListContainsNames ($ ("#h4, #h2, #h7").parents().contents (), "h", "h1", "h3", "h6");
        
        // there should also be no duplicates
        assertEquals (2, $ ("#h4").parents().contents ().size ());
        assertEquals (3, $ ("#h4, #h2").parents().contents ().size ());
        assertEquals (3, $ ("#h4, #h2, #h6").parents().contents ().size ());
        assertEquals (4, $ ("#h4, #h2, #h7").parents().contents ().size ());
	}
	
	@Test
	public void parentsFiltering()
	{
	    // all parents, h3 and h
	    assertListContainsNames ($ ("#h4").parents ().contents (), "h3", "h");
	    // components matching query, h3 and h1
	    assertListContainsNames ($ (".HPanel > .HPanel").contents (), "h3", "h1");

	    // triangulation
	    assertListContainsNames ($ ("#h4").parents (".HPanel > .HPanel").contents (), "h3");
	}
	
	private static final Class<Bling> Bling = Bling.class;
	
	public static class Bling extends DelegatedHQuery
	{
		public Bling (HQuery delegate)
		{
			super (delegate);
		}

		public void bling()
		{
			for (JComponent result : contents())
				result.putClientProperty ("bling", "true");
		}
		
		public boolean hasBling ()
		{
			for (JComponent result : contents())
			{
				if (! "true".equals (result.getClientProperty ("bling")))
					return false;
			}
			
			return true;
		}
	}
	
	@Test
	public void plugins()
	{
		assertEquals (0, $ ("[bling]").count());
		
		$ ("#h2").with (Bling).bling();
		assertEquals (1, $("[bling]").count());
		assertTrue ($("#h2").with (Bling).hasBling());
	}

	public static class Dummy extends DelegatedHQuery
    {
        public Dummy (HQuery delegate)
        {
            super (delegate);
        }
        
        public HQuery dummy()
        {
            return this;
        }
    }
	
	@Test
	public void pluginChaining()
	{
		assertEquals (0, $ ("[bling]").count());
		
		$ ("#h2").with (Dummy.class).dummy().with (Bling).bling();
		assertEquals (1, $("[bling]").count());
		assertTrue ($("#h2").with (Bling).hasBling());
	}
	
	@Test
	public void addAndRemove()
	{
	    assertEquals (1, $ (new JButton()).count ());
	    
	    assertEquals (3, h.asComponent ().getComponentCount ());
	    
	    JButton h6 = new JButton();
	    h6.setName ("h6");
	    
	    $ (h6).addTo (h);
	    assertEquals (4, h.asComponent ().getComponentCount ());
	    
	    $ ("#h3").removeFromParent();
	    assertEquals (3, h.asComponent ().getComponentCount ());
	    
	    assertQueryMatches ("#h > :last-child", h, "h6");
	    assertQueryMatches ("#h3", h);
	}
	
	// effects
	// validation
	// events
	// jxlayer
	// transitions
	// each() method for iteration ? useful or not ? maybe implementing Iterable directly
	// live events & live/die ?
	// compiled properties 
	// triggers
	
	@Test
	public void events()
	{
		//tmp: for API purposes
		$("#h").connect (MouseEvents.click, new EventListener<MouseEvent> ()
		{
			public void onEvent (MouseEvent e)
			{
			}
		});
		
		assertEquals (0, h.asComponent().getMouseListeners().length);
		$("#h").connect (Events.mouse, new MouseAdapter() {});
		assertEquals (1, h.asComponent().getMouseListeners().length);
		
		assertEquals (0, h.asComponent().getMouseMotionListeners().length);
		$("#h").connect (Events.mouseMotion, new MouseAdapter() {});
		assertEquals (1, h.asComponent().getMouseMotionListeners().length);
		
		assertEquals (0, h.asComponent().getMouseWheelListeners().length);
		$("#h").connect (Events.mouseWheel, new MouseAdapter() {});
		assertEquals (1, h.asComponent().getMouseWheelListeners().length);
		
		assertEquals (1, $("#h5").asJComposite (JButton.class).getActionListeners().length);
		$("#h5").connect (Events.action, new ActionListener()
		{
			@Override
			public void actionPerformed (ActionEvent e)
			{
			}
		});
		assertEquals (2, $("#h5").asJComposite (JButton.class).getActionListeners().length);
	}
	
	private static class MockMouseAdapter extends MouseAdapter
	{
	    private boolean callbackCalled = false;
	    
	    @Override
	    public void mouseClicked (MouseEvent e)
	    {
	        callbackCalled = true;
	    }
	    
	    public void verify()
	    {
	        assertTrue ("The mouseClicked() callback should have been called!", callbackCalled);
	    }
	}
	
	public static class HComponentWithEvent extends HComponent<JComponent, HComponentWithEvent>
	{
	    private final DefaultCompositeEventSource<MouseListener> click = new DefaultCompositeEventSource<MouseListener> (MouseListener.class);
	    
	    public void fireMouseClick()
        {
            click.delegate ().mouseClicked (null);
        }
	    
	    public CompositeEventSource<MouseListener> click()
        {
            return click;
        }
	    
	    public Events events()
	    {
	        return new Events();
	    }
	    
	    public class Events
        {
            public CompositeEventSource<MouseListener> click()
            {
                return click;
            }
            
            public final CompositeEventSource<MouseListener> click = HComponentWithEvent.this.click;
        }
	}
	
	@Test
	public void compositeEvents()
	{
	    MockMouseAdapter listener = new MockMouseAdapter();
	    
	    HComponentWithEvent x = new HComponentWithEvent ();
	    assertEquals (0, x.click.listeners().length);

	    x.connect (x.events ().click, listener); // public variable w/ an indirection, in order not to pollute the component API with event methods
	    assertEquals (1, x.click.listeners().length);
	    
	    x.connect (x.events ().click(), listener); // method call w/ an indirection, in order not to pollute the component API with event methods and return an instance of the EventSource interface 
        assertEquals (2, x.click.listeners().length);
        
        x.connect (x.click, listener); // public variable (can sometimes pose a problem as it offers access to the delegate/even firing from outside)
        assertEquals (3, x.click.listeners().length);
	    
        x.connect (x.click(), listener); // method call, returning an instance of the EventSource interface, which forbids access to the delegate
        assertEquals (4, x.click.listeners().length);
        
	    x.fireMouseClick();
	    
	    listener.verify ();
	}
	
	private HPanel h;
	
	@Before
	public void setUp()
	{
		h = new HPanel ().as ("h");
		h.add (new HPanel().as ("h1").add (new HLabel().as ("h2")));
		h.add (new HPanel().as ("h3").add (new HLabel().as ("h4")));
		h.add (new HButton().as ("h5"));
	}
	
	public HQuery $ (JComponent component)
    {
        return HPanel.query (component);
    }
	
	public HQuery $ (String query)
	{
		return h.query (query);
	}
}
