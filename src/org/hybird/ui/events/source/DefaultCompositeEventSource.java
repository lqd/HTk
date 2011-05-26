package org.hybird.ui.events.source;

import org.hybird.composite.Composites;
import org.hybird.composite.IComposite;
import org.hybird.ui.events.CompositeEventSource;

public class DefaultCompositeEventSource<T> implements CompositeEventSource<T>
{
    protected IComposite<T> sink;

    public DefaultCompositeEventSource (Class<T> listenerClass)
    {
        init (listenerClass);
    }

    protected void init (Class<T> listenerClass)
    {
        sink = Composites.createArrayListComposite (listenerClass);
    }

    public T delegate ()
    {
        return sink.delegate ();
    }

    public CompositeEventSource<T> add (T listener)
    {
        sink.add (listener);
        return this;
    }

    public CompositeEventSource<T> remove (T listener)
    {
        sink.remove (listener);
        return this;
    }

    public T [] listeners ()
    {
        return sink.components ();
    }

    @Override
    public Class<T> listenerClass ()
    {
        return sink.delegateClass ();
    }
}