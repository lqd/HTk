package org.hybird.ui.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;

import org.hybird.conversion.Converter;
import org.hybird.ui.query.collectors.Collectors;
import org.hybird.ui.query.collectors.FullHierarchyCollector;
import org.hybird.ui.query.filters.PositionFilter;
import org.hybird.ui.query.selectors.AttributeSelector;
import org.hybird.ui.query.selectors.ChildSelector;
import org.hybird.ui.query.selectors.IdSelector;
import org.hybird.ui.query.selectors.PseudoClassSelector;
import org.hybird.ui.query.selectors.SpecialPseudoClassSelector;
import org.hybird.ui.query.selectors.StyleClassSelector;
import org.hybird.ui.query.selectors.TypeSelector;
import org.hybird.ui.tk.HComponent;

public class Query
{
    private static final Pattern chunker = Pattern.compile ("((?:\\((?:\\([^\\(\\)]+\\)|[^\\(\\)]+)+\\)|\\[(?:\\[[^\\[\\]]+\\]|[^\\[\\]]+)+\\]|\\\\.|[^ >+~,\\(\\[]+)+|[>+~])(\\s*,\\s*)?((?:.|\r|\n)*)");
    
    private static final Map<Pattern, Class<? extends Selector>> registeredSelectors;
    private static final Map<Pattern, Class<? extends Filter>> registeredFilters;
    
    static
    {
        Converter.registerSwingConverters ();
        
        registeredSelectors = new LinkedHashMap <Pattern, Class<? extends Selector>> ();
        registeredFilters = new LinkedHashMap <Pattern, Class<? extends Filter>> ();
        
        registerDefaults ();
    }
    
    private String text;
    
    private List<Expression> expressions;
    
    private Collector collector;
    
    public Query (String text)
    {
    	this.collector = new FullHierarchyCollector();
    	this.text = text;
		parse (this.text);
    }
    
    public void parse (String query)
    {
        while (query != null)
            query = parseExpression (query);
    }

    private List<Integer> subQueries;
    
    private String parseExpression (String query)
    {
        Combinator combinator = null;

        for (Matcher matcher = chunker.matcher (query); matcher.find ();)
        {
            String expression = matcher.group (1);
            if (Combinator.isCombinator (expression))
            {
                combinator = Combinator.from (expression);
                matcher = chunker.matcher (matcher.group (3));

                continue;
            }
            
            if (expressions == null)
            {
            	expressions = new ArrayList<Expression> ();
            }
            else if (combinator == null)
        	{
            	if (subQueries == null)
            		subQueries = new ArrayList<Integer>();
            	subQueries.add (expressions.size());
        	}
            
            expressions.add (new Expression (expression, combinator));

            if (matcher.group (2) != null)
                return matcher.group (3);

            combinator = Combinator.DESCENDANT;
            matcher = chunker.matcher (matcher.group (3));
        }

        return null;
    }

    public List<JComponent> matches (List<JComponent> components)
    {
    	if (subQueries == null)
    		return matches (components, 0, expressions.size());
    	
    	Set<JComponent> matches = new LinkedHashSet<JComponent>();
    	
    	for (int i = 0, size = expressions.size(), from = 0; i < size; ++i)
    	{
    		if (expressions.get (i).combinator() != null)
    			continue;
    		
    		int to = i + 1;
    		
    		matches.addAll (matches (components, from, to));
    		from = to;
    	}
    	
    	return new ArrayList<JComponent> (matches);
    }
    
    public List<JComponent> matches (List<JComponent> components, int from, int to)
    {
        List<JComponent> matches = new ArrayList<JComponent> ();
        List<JComponent> previousExpressionMatches = new ArrayList<JComponent> ();
        
        for (int i = from; i < to; ++i)
        {
            Expression expression = expressions.get (i);
            
            expression.filter (components, previousExpressionMatches, matches);
            
            if (matches.isEmpty())
                return matches;
            
            previousExpressionMatches.clear ();
            
            List<JComponent> tmp = previousExpressionMatches;
            previousExpressionMatches = matches;
            matches = tmp;
        }

        return previousExpressionMatches;
    }
    
    public List<JComponent> matches (HComponent<?,?>... containers)
    {
        List<JComponent> allComponents = new ArrayList<JComponent>();
        for (HComponent<?,?> c : containers)
        	Collectors.getAllChildren (c.asComponent(), allComponents);
        return matches (allComponents);
    }
    
    public List<JComponent> matches (JComponent... containers)
    {
        List<JComponent> allComponents = new ArrayList<JComponent>();
        for (JComponent c : containers)
        	Collectors.getAllChildren (c, allComponents);
        return matches (allComponents);
    }
    
    public String text ()
    {
        return text;
    }
    
    public List<Expression> expressions ()
    {
        return expressions;
    }

    public List<String> rawExpressions ()
    {
        List<String> rawExpressions = new ArrayList<String> ();
        
    	for (Expression p : expressions)
    		rawExpressions.add (p.text());
        
        return rawExpressions;
    }

    public Collector collector()
	{
		return collector;
	}
    
    public void collector (Collector collector)
	{
		this.collector = collector;
	}
    
    @Override
	public String toString()
	{
		return "Query [text=" + text + ", expressions=" + expressions + "]";
	}
    
    // ---- Selector registry -----

    private static void registerDefaults ()
    {
        registerSelector (SpecialPseudoClassSelector.EXTRACTOR, SpecialPseudoClassSelector.class);
        registerSelector (ChildSelector.EXTRACTOR, ChildSelector.class);
        
        registerSelector (AttributeSelector.EXTRACTOR, AttributeSelector.class);
        registerSelector (PseudoClassSelector.EXTRACTOR, PseudoClassSelector.class);
        
        registerSelector (IdSelector.EXTRACTOR, IdSelector.class);
        registerSelector (StyleClassSelector.EXTRACTOR, StyleClassSelector.class);
        registerSelector (TypeSelector.EXTRACTOR, TypeSelector.class);
        
        registerFilter (PositionFilter.EXTRACTOR, PositionFilter.class);
    }
    
    static void registerSelector (String regex, Class<? extends Selector> selectorClass)
    {
        registeredSelectors.put (Pattern.compile (regex), selectorClass);
    }
    
    static Map<Pattern, Class<? extends Selector>> registeredSelectors ()
    {
        return registeredSelectors;
    }
    
    static void registerFilter (String regex, Class<? extends Filter> filterClass)
    {
        registeredFilters.put (Pattern.compile (regex), filterClass);
    }
    
    static Map<Pattern, Class<? extends Filter>> registeredFilters ()
    {
        return registeredFilters;
    }
}