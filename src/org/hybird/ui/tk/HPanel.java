package org.hybird.ui.tk;

import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.hybird.ui.hquery.DefaultHQuery;
import org.hybird.ui.hquery.HQuery;

@SuppressWarnings({"rawtypes", "unchecked"})
public class HPanel extends HComponent <JPanel, HPanel>
{
    public static final Class<HPanel> HPanel = HPanel.class;
    public static final Class<JPanel> JPanel = JPanel.class;
    
    public HPanel (JPanel panel)
    {
        super (panel);
    }

    public HPanel (LayoutManager layout)
    {
        delegate (new JPanel (layout));
    }
    
    public HPanel ()
    {
        delegate (new JPanel ());
    }
    
    public HPanel add (HComponent component, HComponent... components)
    {
        add (component.asComponent ());
        for (HComponent c : components)
            add (c.asComponent ());
        return this;
    }
    
    public HPanel add (HComponent component, Object constraints)
    {
        add (component.asComponent (), constraints);
        return this;
    }
    
    public HPanel add (JComponent component, JComponent... components)
    {
        delegate.add (component);
        for (JComponent c : components)
            delegate.add (c);
        return this;
    }
    
    public HPanel add (JComponent component, Object constraints)
    {
        delegate.add (component, constraints);
        return this;
    }
    
    // add methods to access child components, with indexes and foreach
    // access child components as hcomponents
    
    public HPanel removeAll ()
    {
        delegate.removeAll ();
        return this;
    }
    
    public HPanel remove (HComponent component)
    {
        return remove (component.asComponent ());
    }
    
    public HPanel remove (JComponent component)
    {
        delegate.remove (component);
        return this;
    }
    
    public HPanel layout (LayoutManager layout)
    {
        delegate.setLayout (layout);
        return this;
    }
    
    public <L extends LayoutManager> L layout ()
    {
        return (L) delegate.getLayout ();
    }
    
    /** Asks the JPanel to revalidate and repaint */
    public void containerUpdated ()
    {
        delegate.revalidate();
        delegate.repaint();
    }
    
    public static interface StyleGroup
    {
        StyleGroup add (JComponent component, JComponent... components);
        StyleGroup add (HComponent component, HComponent... components);
    }
    
    /** Creates a style group: all components will be added to the HPanel and tagged with the specified style class (added to the existing style) */
    public StyleGroup group (final String style)
    {
        return new StyleGroup ()
        {
            @Override
            public StyleGroup add (HComponent component, HComponent... components)
            {
                HPanel.this.add (component.addStyle (style));
                
                for (HComponent c : components)
                    HPanel.this.add (c.addStyle (style));
                
                return this;
            }
            
            @Override
            public StyleGroup add (JComponent component, JComponent... components)
            {
                addStyle (component, style);
                HPanel.this.add (component);
                
                for (JComponent c : components)
                {
                    addStyle (c, style);
                    HPanel.this.add (c);
                }
                
                return this;
            }
        };
    }
    
    // ---- hQuery related methods --------------------------------------------
    
    public HQuery query (String selector)
    {
        return query (selector, this);
    }
    
    public static HQuery query (String selector, HComponent root)
    {
        return query (selector, root.asComponent());
    }
    
    public static HQuery query (String selector, JComponent root)
    {
    	DefaultHQuery h = new DefaultHQuery (selector);
    	h.query (root);
        return h;
    }
    
    public static HQuery query (JComponent component)
    {
        DefaultHQuery h = new DefaultHQuery (component);
        return h;
    }
}