package org.hybird.ui.query.selectors;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;

import org.hybird.ui.query.Selector;
import org.hybird.ui.query.selectors.ChildSelector.ChildPosition.Type;

public class ChildSelector implements Selector
{
    public static final String EXTRACTOR = ":(only|nth|last|first)-child(?:\\((even|odd|[ \\dn+-]*)\\))?";
    
    private List<ChildPosition> positions;
    
    public void init (Matcher matcher)
    {
        positions = new ArrayList<ChildPosition> ();

        do
        {
            ChildPosition position = new ChildPosition (Type.from (matcher.group (1)));
            
            if (matcher.group (2) != null)
            	position.parameters = matcher.group (2);
            
            positions.add (position);
        }
        while (matcher.find ());
    }
    
    @Override
    public boolean matches (JComponent component)
    {
        for (ChildPosition position : positions)
        {
            if (position.matches (component) == false)
                return false;
        }
        
        return true;
    }
    
    public List<ChildPosition> positions ()
    {
        return positions;
    }
    
    public static class ChildPosition
    {
    	private Type type;
        private String parameters;
        
        public ChildPosition (Type type)
        {
            this.type = type;
        }
        
        public boolean matches (JComponent component)
        {
            Container parent = component.getParent ();
            if (parent == null)
                return false;
            
            Component [] children = parent.getComponents ();
            
            int at = -1;
            for (int i = 0; i < children.length; ++i)
            {
                if (children[i] == component)
                {
                    at = i;
                    break;
                }
            }
            
            if (at == -1)
                return false;
            
            return type.matches (parameters, component, at, children.length);
        }
        
        public static enum Type
        {
            FIRST ("first")
            {
                @Override
                public boolean matches (String parameters, JComponent component, int index, int size)
                {
                    return index == 0;
                }
            },
            LAST ("last")
            {
                @Override
                public boolean matches (String parameters, JComponent component, int index, int size)
                {
                    return index == size - 1;
                }
            },
            N_TH ("nth")
            {
                @Override
                public boolean matches (String parameters, JComponent component, int index, int size)
                {
                	index++;

                	parameters = parameters.replace (" ", "");
                	
                	if ("odd".equals (parameters))
                		parameters = "2n+1";
                	if ("even".equals (parameters))
                		parameters = "2n";
                	
            		if (parameters.matches ("\\d+"))
            			parameters = "0n+" + parameters;

            		Matcher matcher = nthExtractor.matcher (parameters);
            		if (!matcher.find())
            			throw new IllegalArgumentException ("The child selector parameters '" + parameters + "' is invalid");
            		
            		// an+b
            		
            		String a = matcher.group (2);
            		if (a.isEmpty())
            			a = "0";
            		
					int modulo = Integer.parseInt (a);
            		
            		String b = matcher.group (3);
            		if (b.isEmpty())
            			b = "0";
            		
        			if (b.startsWith ("+"))
        				b = b.substring (1);

        			int offset = Integer.parseInt (b);
            		
            		if (matcher.group (1).equals ("-"))
            			return index <= offset;
            		
            		if (modulo == 0)
            			return index == offset;
            		
            		return index % modulo == offset;
                }
            },
            ONLY ("only")
            {
            	@Override
            	public boolean matches (String parameters, JComponent component, int index, int size)
            	{
            		return FIRST.matches (parameters, component, index, size)
            			&& LAST.matches (parameters, component, index, size);
            	}
            };

            private static final Pattern nthExtractor = Pattern.compile ("(-?)(\\d*)n((?:\\+|-)?\\d*)");
            
            private String name;

            private Type (String name)
            {
                this.name = name;
            }

            public abstract boolean matches (String parameters, JComponent component, int index, int size);

            public static Type from (String name)
            {
                for (Type type : Type.values())
                {
                    if (type.name.equals (name))
                        return type;
                }

                throw new IllegalArgumentException ("Unknown position filter: " + name);
            }
        }
        
        @Override
        public String toString ()
        {
            return "ChildPosition [type=" + type + ", parameters=" + parameters + "]";
        }
    }
}
