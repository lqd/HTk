package org.hybird.ui.tk.custom;

import java.awt.*;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.hybird.ui.events.EventSource;
import org.hybird.ui.events.source.DefaultCompositeEventSource;
import org.hybird.ui.tk.HComponent;
import org.hybird.ui.tk.HPanel;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GPanel<T extends HPanel> extends HPanel
{
    @Override
    public T add (HComponent component, Object constraints)
    {
        return (T) super.add (component, constraints);
    }

    @Override
    public T add (HComponent component, HComponent... components)
    {
        return (T) super.add (component);
    }

    @Override
    public T add (JComponent component, Object constraints)
    {
        return (T) super.add (component, constraints);
    }

    @Override
    public T add (JComponent component, JComponent... components)
    {
        return (T) super.add (component);
    }

    @Override
    public T layout (LayoutManager layout)
    {
        return (T) super.layout (layout);
    }

    @Override
    public T remove (HComponent component)
    {
        return (T) super.remove (component);
    }

    @Override
    public T remove (JComponent component)
    {
        return (T) super.remove (component);
    }

    @Override
    public T removeAll ()
    {
        return (T) super.removeAll ();
    }

    @Override
    protected <L> void addListenerToEventSource (DefaultCompositeEventSource<L> source, L listener)
    {
        super.addListenerToEventSource (source, listener);
    }

    @Override
    public T addPropertyChangeListener (String property, PropertyChangeListener listener)
    {
        return (T) super.addPropertyChangeListener (property, listener);
    }

    @Override
    public T addStyle (String style)
    {
        return (T) super.addStyle (style);
    }

    @Override
    public T addTo (JPanel panel)
    {
        return (T) super.addTo (panel);
    }

    @Override
    public T addTo (HPanel panel)
    {
        return (T) super.addTo (panel);
    }

    @Override
    public T as (String name)
    {
        return (T) super.as (name);
    }

    @Override
    public T at (int x, int y)
    {
        return (T) super.at (x, y);
    }

    @Override
    public T at (int x, int y, int width, int height)
    {
        return (T) super.at (x, y, width, height);
    }

    @Override
    public T background (Color color)
    {
        return (T) super.background (color);
    }

    @Override
    public T border (Border border, Border... borders)
    {
        return (T) super.border (border, borders);
    }

    @Override
    public T bounds (int x, int y, int width, int height)
    {
        return (T) super.bounds (x, y, width, height);
    }

    @Override
    public <L> T connect (EventSource<L> source, L listener)
    {
        return (T) super.connect (source, listener);
    }

    @Override
    public T cursor (Cursor cursor)
    {
        return (T) super.cursor (cursor);
    }

    @Override
    public T enabled (boolean value)
    {
        return (T) super.enabled (value);
    }

    @Override
    public T focusable (boolean focusable)
    {
        return (T) super.focusable (focusable);
    }

    @Override
    public T font (Font font)
    {
        return (T) super.font (font);
    }

    @Override
    public T foreground (Color color)
    {
        return (T) super.foreground (color);
    }

    @Override
    public T margin (int margin)
    {
        return (T) super.margin (margin);
    }

    @Override
    public T margin (int topAndBottom, int leftAndRight)
    {
        return (T) super.margin (topAndBottom, leftAndRight);
    }

    @Override
    public T margin (int margin, Color color)
    {
        return (T) super.margin (margin, color);
    }

    @Override
    public T margin (int top, int right, int bottom, int left)
    {
        return (T) super.margin (top, right, bottom, left);
    }

    @Override
    public T margin (int top, int right, int bottom, int left, Color color)
    {
        return (T) super.margin (top, right, bottom, left, color);
    }

    @Override
    public T opaque (boolean opaque)
    {
        return (T) super.opaque (opaque);
    }

    @Override
    public T parent ()
    {
        return (T) super.parent ();
    }

    @Override
    public T preferredHeight (int height)
    {
        return (T) super.preferredHeight (height);
    }

    @Override
    public T preferredSize (Dimension preferred)
    {
        return (T) super.preferredSize (preferred);
    }

    @Override
    public T preferredSize (int width, int height)
    {
        return (T) super.preferredSize (width, height);
    }

    @Override
    public T preferredWidth (int width)
    {
        return (T) super.preferredWidth (width);
    }

    @Override
    public T property (String key, Object value)
    {
        return (T) super.property (key, value);
    }

    @Override
    public T removeFromParent ()
    {
        return (T) super.removeFromParent ();
    }

    @Override
    public T removePropertyChangeListener (String property, PropertyChangeListener listener)
    {
        return (T) super.removePropertyChangeListener (property, listener);
    }

    @Override
    public T removeStyle (String style)
    {
        return (T) super.removeStyle (style);
    }

    @Override
    public T style (String style)
    {
        return (T) super.style (style);
    }

    @Override
    public T tooltip (String tooltip)
    {
        return (T) super.tooltip (tooltip);
    }

    @Override
    public T visible (boolean visible)
    {
        return (T) super.visible (visible);
    }
}