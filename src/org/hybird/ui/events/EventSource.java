package org.hybird.ui.events;

public interface EventSource<ListenerType>
{
    Class<ListenerType> listenerClass();
}
