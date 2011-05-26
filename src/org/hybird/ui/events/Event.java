package org.hybird.ui.events;

public interface Event <ListenerType, EventType> extends EventSource<ListenerType>
{
	Class<EventType> eventClass();
}
