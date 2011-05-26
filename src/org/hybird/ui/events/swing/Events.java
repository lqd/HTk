package org.hybird.ui.events.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.hybird.ui.events.Event;

public class Events
{
	public static final Event<ActionListener, ActionEvent> action = event (ActionListener.class, ActionEvent.class);
	
	public static final Event<MouseListener, MouseEvent> mouse = MouseEvents.all;
	public static final Event<MouseMotionListener, MouseEvent> mouseMotion = event (MouseMotionListener.class, MouseEvent.class);
	public static final Event<MouseWheelListener, MouseWheelEvent> mouseWheel = event (MouseWheelListener.class, MouseWheelEvent.class);

	private static <L, E> Event<L, E> event (Class<L> listener, Class<E> event)
    {
        return new DefaultEvent<L, E> (listener, event);
    }
	
	private Events () {}
}
