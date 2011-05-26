package org.hybird.ui.events.swing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.hybird.ui.events.Event;

public class MouseEvents
{
	// to do: should contain mouse listener creation and delegation on click event
	public static final Event<MouseListener, MouseEvent> click = event (MouseListener.class, MouseEvent.class);

	public static final Event<MouseListener, MouseEvent> all = event (MouseListener.class, MouseEvent.class);

	private static <L, E> Event<L, E> event (Class<L> listener, Class<E> event)
	{
	    return new DefaultEvent<L, E> (listener, event);
	}
	
	private MouseEvents() {}
}
