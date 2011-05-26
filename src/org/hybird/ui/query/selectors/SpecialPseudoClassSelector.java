package org.hybird.ui.query.selectors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.JComponent;

import org.hybird.ui.query.Expression;
import org.hybird.ui.query.Query;
import org.hybird.ui.query.Selector;
import org.hybird.ui.query.collectors.Collectors;
import org.hybird.ui.query.selectors.PseudoClassSelector.PseudoClass;


/** Handles :not and :has selectors */
public class SpecialPseudoClassSelector implements Selector
{
    public static final String EXTRACTOR = ":(has|not)(?:\\((['\"]?)((?:\\([^\\)]+\\)|[^\\(\\)]*)+)\\2\\))?";
    
    private List<PseudoClass> specialPseudoClasses;
    
    @Override
    public void init (Matcher matcher)
    {
        specialPseudoClasses = new ArrayList<PseudoClass> ();

        do
        {
            PseudoClass pseudoClass = new PseudoClass (matcher.group (1));
            pseudoClass.match (matcher.group (0));
            
            if (matcher.group (3) != null)
                pseudoClass.parameters (matcher.group (3));
            
            specialPseudoClasses.add (pseudoClass);
        }
        while (matcher.find ());
    }
    
    public List<PseudoClass> specialPseudoClasses ()
    {
        return specialPseudoClasses;
    }
    
    @Override
    public boolean matches (JComponent component)
    {
        for (PseudoClass pseudoClass : specialPseudoClasses)
        {
            if ("has".equals (pseudoClass.name ()))
            {
                if (has (component, pseudoClass.parameters ()) == false)
                	return false;
            }
            
            if ("not".equals (pseudoClass.name ()))
            {
                if (not (component, pseudoClass.parameters ()) == false)
                	return false;
            }
        }
        
        return true;
    }
    
    private static boolean has (JComponent component, String subQuery)
    {
        Query q = new Query (subQuery);
        List<JComponent> components = Collectors.getAllChildren (component);
        components.remove (component);
        
        List<JComponent> matches = q.matches (components);
        
        return !matches.isEmpty ();
    }

    private static boolean not (JComponent component, String subQuery)
    {
        // to do: if the selector is simple, we can filter the results
        // if not then we can do another query
        
        Query q = new Query (subQuery);
        
    	for (Expression expression : q.expressions())
        {
            if (expression.matches (component))
                return false;
        }
        
        return true;
    }

	@Override
	public String toString()
	{
		return "SpecialPseudoClassSelector [specialPseudoClasses=" + specialPseudoClasses + "]";
	}
}