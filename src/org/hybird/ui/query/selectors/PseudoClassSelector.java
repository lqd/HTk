package org.hybird.ui.query.selectors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.JComponent;

import org.hybird.ui.query.Selector;
import org.hybird.ui.query.selectors.AttributeSelector.Attribute;

public class PseudoClassSelector implements Selector
{
    public static final String EXTRACTOR = ":((?:[\\w\\u00c0-\\uFFFF-]|\\\\.)+)(?:\\((['\"]?)((?:\\([^\\)]+\\)|[^\\(\\)]*)+)\\2\\))?";

    private List<PseudoClass> pseudoClasses;

    @Override
    public void init (Matcher matcher)
    {
        pseudoClasses = new ArrayList<PseudoClass> ();

        do
        {
            PseudoClass pseudoClass = new PseudoClass (matcher.group (1));
            pseudoClass.match (matcher.group (0));
            
            if (matcher.group (3) != null)
                pseudoClass.parameters (matcher.group (3));
            
            pseudoClasses.add (pseudoClass);
        }
        while (matcher.find ());
    }
    
    @Override
    public boolean matches (JComponent component)
    {
        // Swing or java specific pseudoclasses can be added here
        
        // Unknown pseudoclasses are turned into tests for boolean properties of the same name
        //  ie. the :visible pseudoclass matches: JComponent.isVisible() == true
        for (PseudoClass pseudoClass : pseudoClasses)
        {
        	Attribute ps = new Attribute (pseudoClass.name (), Attribute.Type.EQUAL_TO, true);
            if (! ps.matches (component))
                return false;
        }
        
        return true;
    }
    
    @Override
    public String toString ()
    {
        return "PseudoClassSelector [pseudoClasses=" + pseudoClasses + "]";
    }

    public List<PseudoClass> pseudoClasses ()
    {
        return pseudoClasses;
    }
    
    public static class PseudoClass
    {
        private String name;
        private String parameters;
        private String match;
        
        public PseudoClass (String name)
        {
            this.name = name;
        }

        public String name ()
        {
            return name;
        }

        public void name (String name)
        {
            this.name = name;
        }

        public String parameters ()
        {
            return parameters;
        }

        public void parameters (String parameters)
        {
            this.parameters = parameters;
        }

        public boolean hasParameters ()
        {
            return parameters != null;
        }
        
        public String match ()
        {
            return match;
        }
        
        public void match (String match)
        {
            this.match = match;
        }
        
        @Override
        public String toString ()
        {
            return "PseudoClass [name=" + name + ", parameters=" + parameters + "]";
        }
    }
}