package org.hybird.ui.hquery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.hybird.composite.Composites;
import org.hybird.composite.IComposite;
import org.hybird.ui.events.Event;
import org.hybird.ui.events.EventListener;
import org.hybird.ui.events.EventSource;
import org.hybird.ui.query.Query;
import org.hybird.ui.query.collectors.Collectors;
import org.hybird.ui.tk.HComponent;
import org.hybird.ui.tk.HPanel;
import org.hybird.ui.tk.HTk;

@SuppressWarnings({"unchecked", "rawtypes"})
public class DefaultHQuery implements HQuery
{
    private Query query;

    private List<JComponent> results;
    private JComponent result;
    
    public DefaultHQuery (String selector)
    {
        query = new Query (selector);
    }

    public DefaultHQuery (JComponent component)
    {
        result = component;
    }
    
    // ---- Querying -----

    public void query (JComponent root)
    {
        results (query.matches (root));
    }
    
    private void results (List<JComponent> results)
    {
        if (results.size () == 1)
            result = results.get (0);

        this.results = results;
    }
    
    @Override
    public HQuery find (String selector)
    {
        DefaultHQuery h = new DefaultHQuery ("* " + selector);

        if (result != null)
            h.results (h.query.matches (result));
        else
            h.results (h.query.matches (results.toArray (new JComponent [results.size ()])));

        return h;
    }

    @Override
    public HQuery parents ()
    {
        return parents ("*");
    }
    
    @Override
    public HQuery parents (String selector)
    {
        DefaultHQuery h = new DefaultHQuery (selector);

        if (result != null)
            h.results (h.query.matches (Collectors.getAllParents (result)));
        else
            h.results (h.query.matches (Collectors.getAllParents (results)));

        return h;
    }
    
    public List<JComponent> contents ()
    {
        return results;
    }
    
    @Override
    public int count ()
    {
        if (result != null)
            return 1;
        if (results == null)
            return 0;
        return results.size ();
    }
    
    @Override
    public String selector ()
    {
        if (query == null)
            return null;
        return query.text ();
    }

    // ---- Styles -----

    @Override
    public HQuery addStyle (String style)
    {
        // to do: only create the composite once, cache it in a field

        asComposite ().addStyle (style);
        return this;
    }

    @Override
    public HQuery removeStyle (String style)
    {
        asComposite ().removeStyle (style);
        return this;
    }

    @Override
    public HQuery toggleStyle (String styleA, String styleB)
    {
        asComposite ().toggleStyle (styleA, styleB);
        return this;
    }
    
    // ---- Composites -----

    @Override
    public <T extends HComponent> T asComposite (Class<T> klass)
    {
        return as (klass);
    }
    
    @Override
    /* return 1 HComponent or composite */
    public <T extends HComponent> T as (Class<T> klass)
    {
        if (result != null)
            return asHComponent (klass);

        if (composites == null)
            composites = new HashMap<Class<?>, IComposite<?>> ();

        IComposite<T> composite = (IComposite<T>) composites.get (klass);
        if (composite == null)
        {
            composite = Composites.createArrayListComposite (klass, results.size ());

            for (JComponent component : results)
                composite.add (HTk.wrap (klass, component));

            composites.put (klass, composite);
        }

        return composite.delegate ();
    }

    private Map<Class<?>, IComposite<?>> composites;

    @Override
    /* return 1 JComponent or composite */
    public <T extends JComponent> T asJComposite (Class<T> klass)
    {
        if (result != null)
            return asJComponent (klass);

        if (composites == null)
            composites = new HashMap<Class<?>, IComposite<?>> ();

        IComposite<T> composite = (IComposite<T>) composites.get (klass);
        if (composite == null)
        {
            composite = Composites.createCollectionComposite ((List<T>) results, klass);
            composites.put (klass, composite);
        }

        return composite.delegate ();
    }

    // ---- Methods accessing and wrapping HComponents -----

    @Override
    /* wrap the JComponent result and return 1 HComponent instance */
    public <T extends HComponent> T asHComponent (Class<T> klass)
    {
        return HTk.wrap (klass, asJComponent ());
    }

    @Override
    /* wrap the JComponent results and return as many HComponent instances */
    public <T extends HComponent> List<T> asHComponents (Class<T> klass)
    {
        List<JComponent> components = asJComponents ();

        List<T> hComponents = new ArrayList<T> (components.size ());
        for (JComponent component : components)
            hComponents.add (HTk.wrap (klass, component));

        return hComponents;
    }

    // ---- Methods accessing JComponents -----

    @Override
    public <T extends JComponent> T asJComponent (Class<T> klass)
    {
        if (results != null && results.size () > 1)
            throw new IllegalStateException ("There's more than one result: " + results.size ());

        return (T) result;
    }

    @Override
    public <T extends JComponent> List<T> asJComponents (Class<T> klass)
    {
        if (results == null)
        {
            if (result == null)
                return Collections.emptyList ();
            return (List<T>) Arrays.asList (result);
        }

        return (List<T>) results;
    }

    // ---- Shortcuts to HComponents and JComponents composite methods -----

    @Override
    public <T extends HComponent> T asComposite ()
    {
        return (T) as (HComponent.HComponent);
    }

    @Override
    public HComponent asHComponent ()
    {
        return asHComponent (HComponent.HComponent);
    }

    @Override
    public List<HComponent> asHComponents ()
    {
        return asHComponents (HComponent.HComponent);
    }

    @Override
    public <T extends JComponent> T asJComposite ()
    {
        return (T) asJComposite (HComponent.JComponent);
    }

    @Override
    public JComponent asJComponent ()
    {
        return asJComponent (HComponent.JComponent);
    }

    @Override
    public List<JComponent> asJComponents ()
    {
        return asJComponents (HComponent.JComponent);
    }

    // ---- Shortcuts to HComponents and JComponents methods -----
    
    public HQuery addTo (HPanel panel)
    {
        return addTo (panel.asComponent ());
    }
    
    @Override
    public HQuery addTo (JPanel panel)
    {
        asComposite().addTo (panel);
        return this;
    }
    
    @Override
    public HQuery removeFromParent ()
    {
        asComposite ().removeFromParent ();
        return this;
    }
    
    @Override
    public HQuery enabled (boolean enabled)
    {
        asComposite().enabled (enabled);
        return this;
    }
    
    @Override
    public HQuery visible (boolean visible)
    {
        asComposite().visible (visible);
        return this;
    }
    
    // ---- Plugin related methods -----

    // map contenant les instances de plugins ?
    // mini cache avec weakhashmap et weakrefs pr constructeurs de plugins ?

    @Override
    public <T extends DelegatedHQuery> T with (Class<T> plugin)
    {
        try
        {
            return plugin.getConstructor (HQuery.class).newInstance (this);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException ("Unexpected exception caught while trying to create plugin '"
                    + plugin.getName () + "'", e);
        }
    }

    // ---- Events -----

    @Override
    public <E> HQuery connect (Event<?, E> event, EventListener<E> listener)
    {
        return this;
    }

    @Override
    public <L> HQuery connect (EventSource<L> source, L listener)
    {
        asComposite ().connect (source, listener);
        return this;
    }

    @Override
    public HQuery disconnect (EventSource<?> source)
    {
        return this;
    }

    @Override
    public <E> HQuery disconnect (Event<?, E> event, EventListener<E> listener)
    {
        return this;
    }
    
    // ---- Misc -----
    
    @Override
    public String toString ()
    {
        return "hQuery '" + selector () + "': " + count() + " results";
    }
}