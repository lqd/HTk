package org.hybird.ui.query.collectors;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;

import org.hybird.ui.tk.HComponent;

public class Collectors
{
	public static List<JComponent> getAllChildren (HComponent<?,?> component)
    {
        return getAllChildren (component.asComponent ());
    }
    
    public static List<JComponent> getAllChildren (Container container)
    {
        List<JComponent> children = new ArrayList<JComponent> ();
        getAllChildren (container, children);
        return children;
    }

    public static void getAllChildren (Container container, Collection<JComponent> collection)
    {
        if (container instanceof JComponent)
        {
            if (collection.contains (container) == false)
                collection.add ((JComponent) container);
        }

        Component [] children = container.getComponents ();
        if (children == null)
            return;
        
        for (Component c : children)
        {                
            if (c instanceof JComponent)
                collection.add ((JComponent) c);
        }

        for (Component c : children)
        {
            if (c instanceof Container)
                getAllChildren ((Container) c, collection);
        }
    }
	
    public static List<JComponent> getAllParents (HComponent<?,?> component)
    {
        return getAllParents (component.asComponent ());
    }
    
    public static List<JComponent> getAllParents (HComponent<?,?>... components)
    {
        List<JComponent> parents = new ArrayList<JComponent> ();
        for (HComponent<?,?> component : components)
            getAllParents (component.asComponent (), parents);
        return parents;
    }
    
    public static List<JComponent> getAllParents (Component component)
    {
        List<JComponent> parents = new ArrayList<JComponent> ();
        getAllParents (component, parents);
        return parents;
    }
    
    public static List<JComponent> getAllParents (List<JComponent> components)
    {
        List<JComponent> parents = new ArrayList<JComponent> ();
        for (JComponent component : components)
            getAllParents (component, parents);
        return parents;
    }
    
    public static void getAllParents (Component component, Collection<JComponent> parents)
    {
        Container parent = component.getParent ();
        while (parent != null)
        {
            if (parent instanceof JComponent == false)
                break;
            
            if (parents.contains (parent) == false)
                parents.add ((JComponent) parent);
            
            parent = parent.getParent ();
        }
    }
    
	private Collectors() {}
}
