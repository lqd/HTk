package org.hybird.ui.tk;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.hybird.ui.events.EventSource;
import org.hybird.ui.events.source.DefaultCompositeEventSource;

@SuppressWarnings({"rawtypes", "unchecked"})
public class HComponent <T extends JComponent, H extends HComponent> 
{
    public static final Class<HComponent> HComponent = HComponent.class;
    public static final Class<JComponent> JComponent = JComponent.class;
    
    /** The wrapped JComponent - the delegate has to be passed using HComponent (T) constructor or the delegate (T) method */
    protected T delegate;
    
    public HComponent (T component)
    {
        delegate (component);
    }

    protected HComponent ()
    {
    }
    
    public T asComponent()
    {
        return delegate;
    }

    /** If extenders are not using the HComponent (T delegate) constructor, the delegate has to be passed using this delegate() method */
    protected void delegate (T component)
    {
        if (component == null)
            throw new IllegalArgumentException ("The delegate can't be null!");
        delegate = component;
        addStylesForHierarchy ();
    }
    
    private void addStylesForHierarchy ()
    {
        for (Class c = getClass(); c != HComponent.class; c = c.getSuperclass ())
        {
            String name = c.getSimpleName ();
            if (name.isEmpty ())
                continue;
            
            addStyle (name);
        }
        
        addStyle (HComponent.getSimpleName ());
    }
    
    public H as (String name)
    {
        delegate.setName (name);
        return (H) this;
    }
    
    public String name ()
    {
        return delegate.getName ();
    }
    
    public H property (String key, Object value)
    {
        setProperty (key, value);
        return (H) this;
    }
    
    public <X> X property (String key)
    {
        return getProperty (key);
    }
    
    public H style (String style)
    {
        setStyle (delegate, style);
        return (H) this;
    }
    
    public boolean hasStyle (String style)
    {
        return hasStyle (delegate, style);
    }
    
    public H addStyle (String style)
    {
        addStyle (delegate, style);
        return (H) this;
    }
    
    public H removeStyle (String style)
    {
        style (style().replace (style, ""));
        return (H) this;
    }
    
    public String style ()
    {
        String style = getStyle (delegate);
        if (style == null)
        	style = "";
        return style;
    }
    
    public String [] styles()
    {
        return getStyles (delegate);
    }
    
    protected static void addStyle (JComponent component, String style)
    {
        if (hasStyle (component, style))
            return;
        
        String newStyle = getStyle (component);
        
        if (newStyle == null || newStyle.isEmpty ())
            newStyle = style;
        else
            newStyle += " " + style;
        
        setStyle (component, newStyle);
    }
    
    protected static boolean hasStyle (JComponent component, String style)
    {
        for (String s : getStyles (component))
        {
            if (style.equals (s))
                return true;
        }
        
        return false;
    }
    
    protected static void setStyle (JComponent component, String style)
    {
        HTk.setProperty (component, HTk.STYLE_PROPERTY, style);
    }
    
    protected static String[] getStyles (JComponent component)
    {
        String style = HTk.getProperty (component, HTk.STYLE_PROPERTY);
        
        if (style == null || style.isEmpty ())
            return new String[0];
        
        return style.split (" ");
    }
    
    protected static String getStyle (JComponent component)
    {
        return HTk.getProperty (component, HTk.STYLE_PROPERTY);
    }
    
    /** Equivalent to: at (x, y, preferredSize.width, preferredSize.height) */
    public H at (int x, int y)
    {
        Dimension pref = delegate.getPreferredSize ();
        return bounds (x, y, pref.width, pref.height);
    }
    
    /** Equivalent to bounds () */
    public H at (int x, int y, int width, int height)
    {
        return bounds (x, y, width, height);
    }
    
    public H bounds (int x, int y, int width, int height)
    {
    	delegate.setBounds (x, y, width, height);
        return (H) this;
    }
    
    public Rectangle bounds ()
    {
    	return delegate.getBounds ();
    }
    
    /**
     * Creates a uniform empty border. Uses: margin (margin, margin, margin, margin); 
     */
    public H margin (int margin)
    {
        return margin (margin, margin, margin, margin);
    }

    /**
     * Creates an empty border with equally sized top/bottom (horizontal) margins and left/right (vertical) margins.
     */
    public H margin (int topAndBottom, int leftAndRight)
    {
        return margin (topAndBottom, leftAndRight, topAndBottom, leftAndRight);
    }
    
    /**
     * Creates an empty border. CSS-ordered (clockwise: top, right, bottom, left) 
     *  instead of the counter-intuitive Swing ordering (counter-clockwise: top, left, bottom, right)
     * If you want to use the Swing ordering use border for instance.
     */
    public H margin (int top, int right, int bottom, int left)
    {
        return border (new EmptyBorder (top, left, bottom, right));
    }
    
    /**
     * Creates a uniform matte border. Similar to: border (new MatteBorder (margin, margin, margin, margin, color)); 
     */
    public H margin (int margin, Color color)
    {
        return border (new MatteBorder (margin, margin, margin, margin, color));
    }
    
    /**
     * Creates a matte border. Similar to: border (new MatteBorder (top, left, bottom, right, color));
     *  CSS-ordered (clockwise: top, right, bottom, left) instead of the counter-intuitive
     *  Swing ordering (counter-clockwise: top, left, bottom, right)
     * If you want to use the Swing ordering use border for instance. 
     */
    public H margin (int top, int right, int bottom, int left, Color color)
    {
        return border (new MatteBorder (top, left, bottom, right, color));
    }
    
    public H border (Border border, Border... borders)
    {
        Border result = border;
        for (Border b : borders)
            result = new CompoundBorder (result, b);
        
        delegate.setBorder (result);
    	return (H) this;
    }
    
    public Border border ()
    {
    	return delegate.getBorder ();
    }
    
    public Insets insets ()
    {
        return delegate.getInsets ();
    }
    
    public H font (Font font)
    {
        delegate.setFont (font);
    	return (H) this;
    }
    
    public Font font ()
    {
    	return delegate.getFont ();
    }
    
    public H enabled (boolean value)
    {
        delegate.setEnabled (value);
    	return (H) this;
    }
    
    public boolean enabled()
    {
        return delegate.isEnabled ();
    }
    
    public H cursor (Cursor cursor)
    {
        delegate.setCursor (cursor);
        return (H) this;
    }
    
    public H addTo (JPanel panel)
    {
        panel.add (delegate);
        return (H) this;
    }
    
    public H addTo (HPanel panel)
    {
        return addTo (panel.asComponent ());
    }
    
    // add a parents() methods, with an optional selector, to get a query filled with the parents matching some rules 
    
    public HPanel parent ()
    {
        Container parent = delegate.getParent ();
        if (parent == null)
            return null;
        if (parent instanceof JPanel == false)
            throw new IllegalStateException ("Parent is not a JPanel");
        return new HPanel ((JPanel) parent);
    }
    
    public H removeFromParent ()
    {
        Container parent = delegate.getParent ();
        if (parent == null)
            throw new IllegalStateException ("No parent");
        parent.remove (delegate);
        return (H) this;
    }
    
    public boolean visible ()
    {
        return delegate.isVisible ();
    }
    
    public H visible (boolean visible)
    {
        delegate.setVisible (visible);
        return (H) this;
    }
    
    public boolean opaque ()
    {
        return delegate.isOpaque ();
    }
    
    public H opaque (boolean opaque)
    {
        delegate.setOpaque (opaque);
        return (H) this;
    }
    
    protected void setProperty (String key, Object value)
    {
        HTk.setProperty (delegate, key, value);
    }
    
    protected <X> X getProperty (String key)
    {
        return (X) HTk.getProperty (delegate, key);
    }
    
    public H background (Color color)
    {
        delegate.setBackground (color);
        return (H) this;
    }

    public H foreground (Color color)
    {
        delegate.setForeground (color);
        return (H) this;
    }

    public H preferredSize (int width, int height)
    {
        return preferredSize (new Dimension (width, height));
    }
    
    public H preferredSize (Dimension preferred)
    {
        delegate.setPreferredSize (preferred);
        return (H) this;
    }

    public Dimension preferredSize ()
    {
        return delegate.getPreferredSize ();
    }
    
    public H tooltip (String tooltip)
    {
        delegate.setToolTipText (tooltip);
        return (H) this;
    }
    
    public String tooltip ()
    {
        return delegate.getToolTipText ();
    }
    
    /**
     *  Sets the preferred size to be the existing preferred width and the specified height.
     *   Calls preferredSize (preferredSize().width, height)
     */
    public H preferredHeight (int height)
    {
        return preferredSize (preferredSize().width, height);
    }

    /**
     *  Sets the preferred size to be the specified width and the existing preferred height.
     *   Calls preferredSize (width, preferredSize().height)
     */
    public H preferredWidth (int width)
    {
        return preferredSize (width, preferredSize().height);
    }

	public void toggleStyle (String styleA, String styleB)
	{
		if (hasStyle (styleA))
		{
			removeStyle (styleA);
			addStyle (styleB);
		}
		else if (hasStyle (styleB))
		{
			removeStyle (styleB);
			addStyle (styleA);
		}
		else
		{
			addStyle (styleA);
		}
	}
	
	public <L> H connect (EventSource<L> source, L listener)
	{
	    if (source instanceof DefaultCompositeEventSource)
	    {
	        addListenerToEventSource ((DefaultCompositeEventSource) source, listener);
	    }
	    else
	    {
	        addListenerToDelegate (source, listener);
	    }
	    
		return (H) this;
	}
	
	protected <L> void addListenerToEventSource (DefaultCompositeEventSource<L> source, L listener)
	{
	    source.add (listener);
	}
	
	// add a cache for the add & remove listener methods, with a weakhashmap
	
	protected <L> void addListenerToDelegate (EventSource<L> source, L listener)
    {
	    Class<L> eventListenerClass = source.listenerClass();
	    
        String addMethod = "add" + eventListenerClass.getSimpleName();
        
        try
        {
            Method addListenerMethod = delegate.getClass().getMethod (addMethod, eventListenerClass);
            addListenerMethod.invoke (delegate, listener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
	
	/** Calls setRequestFocusEnabled() and setFocusable() with the specified 'focusable' parameter  */
    public H focusable (boolean focusable)
    {
        delegate.setRequestFocusEnabled (focusable);
        delegate.setFocusable (focusable);
        return (H) this;
    }
	
	// ---- Property change methods -----
	
	/** Adds a property change listener for the specified property.
	 *  Accepts null, an empty string, or "*" as wildcards to listen to all property change events */
	public H addPropertyChangeListener (String property, PropertyChangeListener listener)
	{
	    if (property == null || property.isEmpty () || "*".equals (property.trim ()))
	        delegate.addPropertyChangeListener (listener);
	    else
	        delegate.addPropertyChangeListener (property, listener);
	    
	    return (H) this;
	}
	
	/** Removes a property change listener for the specified property.
     *  Accepts null, an empty string, or "*" as wildcards to remove the specified listener from all property change events*/
	public H removePropertyChangeListener (String property, PropertyChangeListener listener)
	{
	    if (property == null || property.isEmpty () || "*".equals (property.trim ()))
        {
	        PropertyChangeListener [] listeners = delegate.getPropertyChangeListeners ();
	        for (PropertyChangeListener l : listeners)
	        {
	            if (l instanceof PropertyChangeListenerProxy == false)
	                continue;
	            
	            PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) l;
	            
                if (proxy.getListener () == listener)
                    delegate.removePropertyChangeListener (proxy.getPropertyName (), listener);
	        }
        }
        else
            delegate.removePropertyChangeListener (property, listener);
	    
	    return (H) this;
	}
	
	protected void firePropertyChange (String property, Object oldValue, Object newValue)
    {
        PropertyChangeListener [] listeners = delegate.getPropertyChangeListeners (property);
        
        if (listeners.length == 0)
            return;
        
        PropertyChangeEvent e = new PropertyChangeEvent (delegate, property, oldValue, newValue);
        for (PropertyChangeListener l : listeners)
            l.propertyChange (e);
    }
}