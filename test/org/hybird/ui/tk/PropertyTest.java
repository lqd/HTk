package org.hybird.ui.tk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import org.hybird.ui.AbstractTest;
import org.junit.Test;

public class PropertyTest extends AbstractTest
{
    protected static class MockPropertyChangeListener implements PropertyChangeListener
    {
        private Object newValue = null;
        
        @Override
        public void propertyChange (PropertyChangeEvent e)
        {
            newValue = e.getNewValue ();
        }
        
        public void assertWasCalled (String newValue)
        {
            assertNotNull (this.newValue);
            assertEquals (newValue, this.newValue);
        }
    }
    
    private static class H extends HPanel
    {
        public H ()
        {
            this (new JPanel());
        }
        
        public H (JPanel panel)
        {
            super (panel);
        }

        @Override
        public void firePropertyChange (String property, Object oldValue, Object newValue)
        {
            super.firePropertyChange (property, oldValue, newValue);
        }
    }
    
    @Test
    public void firePropertyChange()
    {
        MockPropertyChangeListener listener = new MockPropertyChangeListener ();
        
        JPanel c = new JPanel();
        c.addPropertyChangeListener ("xxx", listener);
        
        H h = new H (c); 
        h.firePropertyChange ("xxx", null, "newValue");
        
        listener.assertWasCalled ("newValue");
    }
    
    @Test
    public void addPropertyChangeListener()
    {
        MockPropertyChangeListener listener = new MockPropertyChangeListener ();
        H h = new H();
        
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ().length);
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ("xxx").length);

        h.addPropertyChangeListener ("xxx", listener);
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ().length);
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ("xxx").length);
    }
    
    @Test
    public void addPropertyChangeListenerWithWildcards()
    {
        MockPropertyChangeListener listener = new MockPropertyChangeListener ();
        H h = new H();
        
        h.addPropertyChangeListener ("xxx", listener);
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ().length);
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ("xxx").length);
        
        h.addPropertyChangeListener ("*", listener);
        assertEquals (2, h.asComponent ().getPropertyChangeListeners ().length);
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ("xxx").length);
        
        h.addPropertyChangeListener ("", listener);
        assertEquals (3, h.asComponent ().getPropertyChangeListeners ().length);
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ("xxx").length);
        
        h.addPropertyChangeListener (null, listener);
        assertEquals (4, h.asComponent ().getPropertyChangeListeners ().length);
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ("xxx").length);
    }
    
    @Test
    public void removePropertyChangeListener()
    {
        MockPropertyChangeListener listener = new MockPropertyChangeListener ();
        H h = new H();
        
        h.addPropertyChangeListener ("xxx", listener);
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ().length);
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ("xxx").length);
        
        h.removePropertyChangeListener ("xxx", listener);
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ().length);
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ("xxx").length);
    }
    
    @Test
    public void swingAddAndRemovePropertyChangeListener()
    {
        MockPropertyChangeListener listener = new MockPropertyChangeListener ();
        H h = new H();
        
        h.asComponent ().addPropertyChangeListener (listener);
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ().length);
        
        h.asComponent ().addPropertyChangeListener ("1", listener);
        assertEquals (2, h.asComponent ().getPropertyChangeListeners ().length);
        
        h.asComponent ().removePropertyChangeListener ("1", listener);
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ().length);
        
        h.asComponent ().removePropertyChangeListener (listener);
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ().length);
        
        h.asComponent ().addPropertyChangeListener ("xxx", listener);
        h.asComponent ().removePropertyChangeListener (listener);
        
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ().length); // Good job, geniuses !
    }
    
    @Test
    public void removePropertyChangeListenerWithWildcards()
    {
        MockPropertyChangeListener listener = new MockPropertyChangeListener ();
        H h = new H();
        
        h.addPropertyChangeListener ("1", listener);
        h.addPropertyChangeListener ("2", listener);
        
        assertEquals (2, h.asComponent ().getPropertyChangeListeners ().length);
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ("1").length);
        assertEquals (1, h.asComponent ().getPropertyChangeListeners ("2").length);
        
        h.removePropertyChangeListener ("*", listener);
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ().length);
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ("1").length);
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ("2").length);
        
        h.addPropertyChangeListener ("1", listener);
        h.addPropertyChangeListener ("2", listener);
        
        h.removePropertyChangeListener ("", listener);
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ().length);
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ("1").length);
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ("2").length);
        
        h.addPropertyChangeListener ("1", listener);
        h.addPropertyChangeListener ("2", listener);
        
        h.removePropertyChangeListener (null, listener);
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ().length);
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ("1").length);
        assertEquals (0, h.asComponent ().getPropertyChangeListeners ("2").length);
    }
}
