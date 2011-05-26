package org.hybird.ui.events;

public interface CompositeEventSource<T> extends EventSource<T>
{
    CompositeEventSource<T> add (T listener);
    CompositeEventSource<T> remove (T listener);
    
    T [] listeners ();
}
