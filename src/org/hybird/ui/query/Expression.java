package org.hybird.ui.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;

public class Expression
{
    private String text;
    private Combinator combinator;
    
    private List<Selector> selectors;
    private List<Filter> filters;

    public Expression (String text, Combinator combinator)
    {
        this.text = text;
        this.combinator = combinator;

        String expression = text;
        expression = findFilters (expression);
        expression = findSelectors (expression);
    }

    public String text ()
    {
        return text;
    }
    
    public List<Selector> selectors ()
    {
        return selectors;
    }

    public Combinator combinator ()
    {
        return combinator;
    }
    
    // ---- Element matching methods -----
    
    public void filter (List<JComponent> components, List<JComponent> previousExpressionMatches, List<JComponent> matches)
    {
        collectResults (components, matches);
        
        // cut up filters into general filters and set filters (which need an index and size) ?
        applyFilters (matches);
        
        combineAndFilterResults (previousExpressionMatches, matches);
    }

    private void collectResults (List<JComponent> components, List<JComponent> matches)
    {
        for (JComponent component : components)
        {
            if (matches (component) == false)
                continue;
            
            matches.add (component);
        }
    }
    
    public boolean matches (JComponent component)
    {
        if (selectors == null)
            return true;
        
        for (Selector s : selectors)
        {
            if (s.matches (component) == false)
                return false;
        }

        return true;
    }
    
    public void applyFilters (List<JComponent> matches)
    {
        if (filters == null)
            return;
        
        for (Filter filter : filters)
        {
            for (int size = matches.size (), i = size; i-- > 0;)
            {
                if (filter.filter (matches.get (i), i, size))
                    matches.remove (i);
            }
        }
    }
    
    private void combineAndFilterResults (List<JComponent> previousExpressionMatches, List<JComponent> matches)
    {
        for (int i = matches.size (); i-- > 0;)
        {
            JComponent component = matches.get (i);
            
            if (matchesHierarchy (component, previousExpressionMatches))
                continue;
            
            matches.remove (i);
        }
    }
    
    private boolean matchesHierarchy (JComponent component, List<JComponent> hierarchy)
    {
        if (hierarchy.isEmpty () || combinator == null)
            return true;
        
        for (JComponent ascendant : hierarchy)
        {
            if (combinator.matches (component, ascendant))
                return true;
        }
        
        return false;
    }
    
    // ---- Selector and filter creation -----
    
 // generalize finding selectors and filters in one method, this is ugly
    private String findSelectors (String expression)
    {
        for (Entry<Pattern, Class<? extends Selector>> entry : Query.registeredSelectors().entrySet ())
        {
            Pattern pattern = entry.getKey ();
            Matcher matcher = pattern.matcher (expression);
            
            if (matcher.find () == false)
                continue;
            
            Class<? extends Selector> selectorClass = entry.getValue ();

            Selector selector = null;
            
            try
            {
                selector = selectorClass.newInstance ();
            }
            catch (Exception e)
            {
                System.out.println ("Unexpected exception caught while creating new Selector: " + selectorClass.getName ());
                e.printStackTrace();
                continue;
            }
            
            selector.init (matcher);

            if (selectors == null)
                selectors = new ArrayList<Selector> ();
            selectors.add (selector);
            
            expression = matcher.replaceAll ("");
        }
        
        return expression;
    }
    
    private String findFilters (String expression)
    {
        for (Entry<Pattern, Class<? extends Filter>> entry : Query.registeredFilters().entrySet ())
        {
            Pattern pattern = entry.getKey ();
            Matcher matcher = pattern.matcher (expression);
            
            if (matcher.find () == false)
                continue;
            
            Class<? extends Filter> filterClass = entry.getValue ();

            Filter filter = null;
            
            try
            {
                filter = filterClass.newInstance ();
            }
            catch (Exception e)
            {
                System.out.println ("Unexpected exception caught while creating new Filter: " + filterClass.getName ());
                e.printStackTrace();
                continue;
            }
            
            filter.init (matcher);
            
            if (filters == null)
                filters = new ArrayList<Filter> ();
            filters.add (filter);
            
            expression = matcher.replaceAll ("");
        }
        
        return expression;
    }
    
    // ---- Blabla -----
    
	@Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((combinator == null) ? 0 : combinator.hashCode ());
        result = prime * result + ((text == null) ? 0 : text.hashCode ());
        result = prime * result + ((filters == null) ? 0 : filters.hashCode ());
        result = prime * result + ((selectors == null) ? 0 : selectors.hashCode ());
        return result;
    }

    @Override
    public boolean equals (Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass () != obj.getClass ())
            return false;
        Expression other = (Expression) obj;
        if (combinator == null)
        {
            if (other.combinator != null)
                return false;
        }
        else if (!combinator.equals (other.combinator))
            return false;
        if (text == null)
        {
            if (other.text != null)
                return false;
        }
        else if (!text.equals (other.text))
            return false;
        if (filters == null)
        {
            if (other.filters != null)
                return false;
        }
        else if (!filters.equals (other.filters))
            return false;
        if (selectors == null)
        {
            if (other.selectors != null)
                return false;
        }
        else if (!selectors.equals (other.selectors))
            return false;
        return true;
    }

    @Override
    public String toString ()
    {
        return "Expression [text=" + text + ", combinator=" + combinator + ", selectors=" + selectors + ", filters=" + filters + "]";
    }
}