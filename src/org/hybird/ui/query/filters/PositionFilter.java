package org.hybird.ui.query.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.JComponent;

import org.hybird.ui.query.Filter;
import org.hybird.ui.query.filters.PositionFilter.Position.Type;

public class PositionFilter implements Filter
{
    public static final String EXTRACTOR = ":(nth|eq|gt|lt|first|last|even|odd|range)(?:\\((\\d*(?:,\\s*\\d*)?)\\))?(?=[^-]|$)";
    
    private List<Position> positions;
    
    public void init (Matcher matcher)
    {
        positions = new ArrayList<Position> ();

        do
        {
            Position position = new Position (Type.from (matcher.group (1)));
            
            if (matcher.group (2) != null)
                position.parameters = matcher.group (2);
            
            positions.add (position);
        }
        while (matcher.find ());
    }
    
    @Override /** Returns true if the component needs to be removed */
    public boolean filter (JComponent component, int index, int size)
    {
        for (Position position : positions)
        {
            if (position.matches (component, index, size) == false)
                return true;
        }
        
        return false;
    }
    
    @Override
    public String toString ()
    {
        return "PositionFilter [positions=" + positions + "]";
    }

    public static class Position
    {
        private Type type;
        private String parameters;
        
        public Position (Type type)
        {
            this.type = type;
        }

        public void parameters (String parameters)
        {
            this.parameters = parameters;
        }

        public boolean matches (JComponent component, int index, int size)
        {
            return type.matches (parameters, component, index, size);
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
                    int bound = Integer.parseInt (parameters);
                    return index == bound;
                }
            },
            EQ ("eq")
            {

                @Override
                public boolean matches (String parameters, JComponent component, int index, int size)
                {
                    return N_TH.matches (parameters, component, index, size);
                }
            },
            GT ("gt")
            {

                @Override
                public boolean matches (String parameters, JComponent component, int index, int size)
                {
                    int bound = Integer.parseInt (parameters);
                    return index > bound;
                }
            },
            LT ("lt")
            {

                @Override
                public boolean matches (String parameters, JComponent component, int index, int size)
                {
                    int bound = Integer.parseInt (parameters);
                    return index < bound;
                }
            },
            RANGE ("range")
            {
                @Override
                public boolean matches (String parameters, JComponent component, int index, int size)
                {
                    String [] range = parameters.split (",");
                    int lowerBound = Integer.parseInt (range[0].trim ());
                    int upperBound = Integer.parseInt (range[1].trim ());
                    
                    return index >= lowerBound && index < upperBound;
                }
            },
            EVEN ("even")
            {

                @Override
                public boolean matches (String parameters, JComponent component, int index, int size)
                {
                    return index % 2 == 0;
                }
            },
            ODD ("odd")
            {

                @Override
                public boolean matches (String parameters, JComponent component, int index, int size)
                {
                    return index % 2 == 1;
                }
            };

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
            return "Position [type=" + type + ", parameters=" + parameters + "]";
        }
    }
}