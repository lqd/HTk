package org.hybird.ui.hquery;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.hybird.ui.events.Event;
import org.hybird.ui.events.EventListener;
import org.hybird.ui.events.EventSource;
import org.hybird.ui.tk.HComponent;
import org.hybird.ui.tk.HPanel;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DelegatedHQuery implements HQuery
{
    private HQuery delegate;

    public DelegatedHQuery (HQuery delegate)
    {
        this.delegate = delegate;
    }

    // ---- Delegate methods -----

    public List<JComponent> contents ()
    {
        return delegate.contents ();
    }

    public HQuery addStyle (String style)
    {
        return delegate.addStyle (style);
    }

    public <T extends HComponent> T as (Class<T> klass)
    {
        return delegate.as (klass);
    }

    public <T extends HComponent> T asComposite (Class<T> klass)
    {
        return delegate.asComposite (klass);
    }

    public <T extends JComponent> T asJComposite (Class<T> klass)
    {
        return delegate.asJComposite (klass);
    }
    
    public <T extends HComponent> T asComposite ()
    {
        return delegate.asComposite ();
    }

    public HComponent asHComponent ()
    {
        return delegate.asHComponent ();
    }

    public <T extends HComponent> T asHComponent (Class<T> klass)
    {
        return delegate.asHComponent (klass);
    }

    public List<HComponent> asHComponents ()
    {
        return delegate.asHComponents ();
    }

    public <T extends HComponent> List<T> asHComponents (Class<T> klass)
    {
        return delegate.asHComponents (klass);
    }

    public JComponent asJComponent ()
    {
        return delegate.asJComponent ();
    }

    public <T extends JComponent> T asJComponent (Class<T> klass)
    {
        return delegate.asJComponent (klass);
    }

    public List<JComponent> asJComponents ()
    {
        return delegate.asJComponents ();
    }

    public <T extends JComponent> List<T> asJComponents (Class<T> klass)
    {
        return delegate.asJComponents (klass);
    }

    public <T extends JComponent> T asJComposite ()
    {
        return (T) delegate.asJComposite ();
    }

    public int count ()
    {
        return delegate.count ();
    }

    public HQuery find (String selector)
    {
        return delegate.find (selector);
    }

    @Override
    public HQuery parents ()
    {
        return delegate.parents ();
    }
    
    @Override
    public HQuery parents (String selector)
    {
        return delegate.parents (selector);
    }
    
    public HQuery removeStyle (String style)
    {
        return delegate.removeStyle (style);
    }

    public String selector ()
    {
        return delegate.selector ();
    }

    public HQuery toggleStyle (String styleA, String styleB)
    {
        return delegate.toggleStyle (styleA, styleB);
    }

    public final <T extends DelegatedHQuery> T with (Class<T> plugin)
    {
        return delegate.with (plugin);
    }

    public <E> HQuery connect (Event<?, E> event, EventListener<E> listener)
    {
        return delegate.connect (event, listener);
    }

    public <L> HQuery connect (EventSource<L> source, L listener)
    {
        return delegate.connect (source, listener);
    }

    public HQuery disconnect (EventSource<?> source)
    {
        return delegate.disconnect (source);
    }

    public <E> HQuery disconnect (Event<?, E> event, EventListener<E> listener)
    {
        return delegate.disconnect (event, listener);
    }
    
    public HQuery addTo (JPanel panel)
    {
        return delegate.addTo (panel);
    }

    @Override
    public HQuery addTo (HPanel panel)
    {
        return delegate.addTo (panel);
    }
    
    public HQuery removeFromParent ()
    {
        return delegate.removeFromParent ();
    }
    
    @Override
    public HQuery enabled (boolean enabled)
    {
        return delegate.enabled (enabled);
    }
    
    @Override
    public HQuery visible (boolean visible)
    {
        return delegate.visible (visible);
    }
}
