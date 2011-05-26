package org.hybird.ui.events.swing;

import org.hybird.ui.events.Event;


public class DefaultEvent<Listener, EventType> implements Event<Listener, EventType>
{
	private Class<Listener> listenerClass;
	private Class<EventType> eventClass;
	
	public DefaultEvent (Class<Listener> listenerClass, Class<EventType> eventClass)
	{
		this.listenerClass = listenerClass;
		this.eventClass = eventClass;
	}

	@Override
	public Class<EventType> eventClass()
	{
		return eventClass;
	}
	
	public Class<Listener> listenerClass()
	{
		return listenerClass;
	}

	@Override
	public String toString()
	{
		return "DefaultEvent [eventClass=" + eventClass.getName() + ", listenerClass="
				+ listenerClass.getName() + "]";
	}
}
