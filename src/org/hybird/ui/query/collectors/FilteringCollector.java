package org.hybird.ui.query.collectors;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.hybird.ui.query.Collector;
import org.hybird.ui.query.Expression;
import org.hybird.ui.query.Query;

/** Not done yet */
public class FilteringCollector implements Collector
{
	private Query query;
	
	@Override
	public void init (Query query)
	{
		this.query = query;
	}
	
	private boolean matches (JComponent component)
	{
		for (Expression e : query.expressions())
		{
			if (e.matches (component))
				return true;
		}
		
		return false;
	}
	
	@Override
	public List<JComponent> collect (JComponent root)
	{
		List<JComponent> components = new ArrayList<JComponent> ();
		collect (root, components);
			
		return components;
	}
	
	public void collect (Container container, List<JComponent> results)
	{
		if (container instanceof JComponent)
        {
            JComponent c = (JComponent) container;
            
            if (matches (c) && results.contains (c) == false)
            	results.add (c);
        }
		
		Component [] children = container.getComponents ();
        
        for (Component c : children)
        {
            if (results.contains (c))
                continue;
            if (c instanceof JComponent == false)
                continue;
            if (matches ((JComponent) c))
                results.add ((JComponent) c);
        }

        for (int i = 0; i < children.length; i++)
        {
            Component c = children[i];
            
            if (c instanceof Container)
                collect ((Container) c, results);
        }
	}
}
