package org.hybird.ui.query;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public enum Combinator
{
    /** 'E F' matches an F element descendant of an E element */ 
    DESCENDANT ("")
    {
        @Override
        public boolean matches (JComponent component, JComponent potentialParent)
        {
            if (component == potentialParent)
                return false;
            
            return SwingUtilities.isDescendingFrom (component, potentialParent);
        }
    },
    
    /** 'E > F' matches an F element child of an E element */
    CHILD (">")
    {
        @Override
        public boolean matches (JComponent component, JComponent potentialParent)
        {
            return component.getParent () == potentialParent;
        }
    },
    
    /** 'E + F' matches an F element immediately preceded by an E element */
    ADJACENT_SIBLING ("+")
    {
        @Override
        public boolean matches (JComponent component, JComponent potentialSibling)
        {
            Container parent = component.getParent ();
            if (parent == null)
                return false;

            Component [] siblings = parent.getComponents ();
            for (int i = 1; i < siblings.length; i++)
            {
                if (siblings[i] == component)
                    return siblings[i - 1] == potentialSibling;
            }
            
            return false;
        }
    },
    
    /** 'E ~ F' matches an F element preceded by an E element */
    GENERAL_SIBLING ("~")
    {
        @Override
        public boolean matches (JComponent component, JComponent potentialSibling)
        {
            Container parent = component.getParent ();
            if (parent == null)
                return false;

            Component [] siblings = parent.getComponents ();
            int found = -1;
            for (int i = 0; i < siblings.length; i++)
            {
                if (found != -1)
                {
                    if (siblings[i] == component)
                        return true;
                }
                
                if (siblings[i] == potentialSibling)
                    found = i;
            }
            
            return false;
        }
    };

    private String label;

    private Combinator (String label)
    {
        this.label = label;
    }

    public String getLabel ()
    {
        return label;
    }

    public static boolean isCombinator (String part)
    {
        return part.matches ("[>+~]");
    }

    public static Combinator from (String label)
    {
        for (Combinator r : values ())
        {
            if (r.label != null && r.label.equals (label))
                return r;
        }

        throw new IllegalArgumentException ("Unknown combinator: " + label);
    }

    public abstract boolean matches (JComponent component, JComponent potentialMatch);
}