package org.hybird.ui.events.source;

import org.hybird.composite.Composites;

public class CompositeSwingEventSource<T extends java.util.EventListener> extends DefaultCompositeEventSource<T>
{
    public CompositeSwingEventSource (Class<T> listenerClass)
    {
        super (listenerClass);
    }

    @Override
    protected void init (Class<T> listenerClass)
    {
        sink = Composites.createEventListenerOnEDTComposite (listenerClass);
    }
}