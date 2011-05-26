package org.hybird.ui.query.selectors;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;

import org.hybird.conversion.Converter;
import org.hybird.ui.query.Selector;
import org.hybird.ui.query.selectors.AttributeSelector.Attribute.Type;

@SuppressWarnings("rawtypes")
public class AttributeSelector implements Selector
{
	// CSS3 selectors + "> < >= <= != !== =="
    private static final String EXTENDED_EXTRACTOR = "\\[\\s*((?:[\\w\\u00c0-\\uFFFF-]|\\.)+)\\s*(?:(\\S?=?[=!><])\\s*(['\"]*)(.*?)\\3|)\\s*\\]";

    // pure CSS3 selectors
	private static final String PURE_CSS_EXTRACTOR = "\\[\\s*((?:[\\w\\u00c0-\\uFFFF-]|\\.)+)\\s*(?:(\\S?=)\\s*(['\"]*)(.*?)\\3|)\\s*\\]";

    public static final String EXTRACTOR = EXTENDED_EXTRACTOR;

    private List<Attribute> attributes;

    @Override
    public void init (Matcher matcher)
    {
    	attributes = new ArrayList<Attribute>();

        List<String> found = new ArrayList<String>();
        
        do
        {
            for (int i = 0; i < matcher.groupCount(); ++i)
                found.add (matcher.group (i + 1));
        }
        while (matcher.find ());
        
        for (int i = 0; i < found.size(); i += 4)
        {
            String property = found.get (i);
            Type operator = Type.from (found.get (i + 1));
            String value = found.get (i + 3);
            
            attributes.add (new Attribute (property, operator, value));
        }
    }
    
    @Override
    public boolean matches (JComponent component)
    {
        for (Attribute a : attributes)
        {
            if (!a.matches (component))
                return false;
        }

        return true;
    }

    public List<Attribute> attributes ()
    {
        return attributes;
    }

    private static boolean isPureCSS ()
	{
	    return EXTRACTOR.equals (PURE_CSS_EXTRACTOR);
	}
    
    @Override
	public String toString()
	{
		return "AttributeSelector [attributes=" + attributes + "]";
	}

	/** Matches Java properties to css attribute queries */
    public static class Attribute
    {
    	private String property;
    	private Type type;

    	public Attribute (String property, Type type)
    	{
    		this.property = property;
    		this.type = type;
    	}

    	private Object value;
    	
    	public Attribute (String property, Type type, Object value)
    	{
    		this.property = property;
    		this.type = type;
    		this.value = value;
    	}

    	private String match;
    	
    	public String property ()
        {
            return property;
        }

        public Type type ()
        {
            return type;
        }

        public Object value ()
        {
            return value;
        }

        public String match ()
        {
            return match;
        }
        
        public boolean matches (Object source)
    	{
    		return matches (source, value);
    	}
    	
    	public boolean matches (Object source, Object value)
    	{
    		try
            {
                Object propertyValue = getProperty (source, property);
                return type.matches (propertyValue, value);
            }
    		catch (IllegalStateException e)
    		{
    		    throw e;
    		}
            catch (Exception e)
            {
                if (type == Type.NOT_HAS_ATTRIBUTE)
                    return true;

                if (type == Type.HAS_ATTRIBUTE)
                    return false;
                
            	return false;
            }
    	}

    	// property descriptors could be cached for better performance
    	public static Object getProperty (Object o, String property)
    	{
    		try
    		{
    		    // don't specify the setter, otherwise it will try to find it even if you're not using it...
    			PropertyDescriptor descriptor = new PropertyDescriptor (property, o.getClass(),
    			        "is" + property.substring (0, 1).toUpperCase () + property.substring(1),
    			        null);
    			return descriptor.getReadMethod().invoke (o);
    		}
    		catch (Exception e)
    		{
    		    if (o instanceof JComponent)
    		    {
    		        JComponent c = (JComponent) o;
    		        Object value = c.getClientProperty (property);
                    if (value != null)
    		            return value;
    		    }
    		    
    		    throw new RuntimeException (e);
    		}
    	}
    	    	
    	@Override
        public String toString ()
        {
            return "Attribute [property=" + property + ", type=" + type + ", value=" + value + "]";
        }

    	public static boolean SAME_AS_OPERATOR_FAILS_WHEN_NOT_SAME_BUT_EQUAL_TO = true;
    	
    	@SuppressWarnings("unchecked")
        public static enum Type
        {
            EQUAL_TO ("=")
            {
                @Override
                public boolean matches (Object source, Object target)
                {
    			    if (target == null)
    			        return source == null;
    			    
    			    if (source == null)
                        return target.equals ("null"); // could be handy, but it's not really "pure" from the spec
    			    
                    if (source instanceof Comparable && source.getClass ().isAssignableFrom (target.getClass ()))
                    {
                        Comparable c = (Comparable) source;
                        return c.compareTo (target) == 0;
                    }
                    
                    if (source instanceof Boolean)
                    {
                        boolean b = (Boolean) source;
                        
                        if (target instanceof Boolean)
                            return b == (Boolean) target;
                        if (target instanceof String)
                            return b == Boolean.parseBoolean ((String) target);
                    }
                    
                    if (source instanceof Number || source instanceof Character)
                    {
                        if (source instanceof Integer)
                        {
                            int i = (Integer) source;
                            
                            if (target instanceof Integer)
                                return i == (Integer) target;
                            if (target instanceof Long)
                                return i == (Long) target;
                            if (target instanceof Short)
                                return i == (Short) target;
                            if (target instanceof Byte)
                                return i == (Byte) target;
                            if (target instanceof Double)
                                return i == (Double) target;
                            if (target instanceof Float)
                                return i == (Float) target;
                            if (target instanceof Character)
                                return i == (Character) target;
                            if (target instanceof String)
                            {
                                try
                                {
                                    return i == Long.parseLong ((String) target);
                                }
                                catch (NumberFormatException e)
                                {
                                    return i == Double.parseDouble ((String) target);
                                }
                            }
                        }
                        
                        if (source instanceof Long)
                        {
                            long l = (Long) source;
                            
                            if (target instanceof Integer)
                                return l == (Integer) target;
                            if (target instanceof Long)
                                return l == (Long) target;
                            if (target instanceof Short)
                                return l == (Short) target;
                            if (target instanceof Byte)
                                return l == (Byte) target;
                            if (target instanceof Double)
                                return l == (Double) target;
                            if (target instanceof Float)
                                return l == (Float) target;
                            if (target instanceof Character)
                                return l == (Character) target;
                            if (target instanceof String)
                            {
                                try
                                {
                                    return l == Long.parseLong ((String) target);
                                }
                                catch (NumberFormatException e)
                                {
                                    return l == Double.parseDouble ((String) target);
                                }
                            }
                        }
                        
                        if (source instanceof Double)
                        {
                            double d = (Double) source;
                            
                            if (target instanceof Integer)
                                return d == (Integer) target;
                            if (target instanceof Long)
                                return d == (Long) target;
                            if (target instanceof Short)
                                return d == (Short) target;
                            if (target instanceof Byte)
                                return d == (Byte) target;
                            if (target instanceof Double)
                                return d == (Double) target;
                            if (target instanceof Float)
                                return Math.abs (d - (Float) target) < 0.000001;
                            if (target instanceof Character)
                                return d == (Character) target;
                            if (target instanceof String)
                                return d == Double.parseDouble ((String) target);
                        }
                        
                        if (source instanceof Float)
                        {
                            float f = (Float) source;
                            
                            if (target instanceof Integer)
                                return f == (Integer) target;
                            if (target instanceof Long)
                                return f == (Long) target;
                            if (target instanceof Short)
                                return f == (Short) target;
                            if (target instanceof Byte)
                                return f == (Byte) target;
                            if (target instanceof Double)
                                return Math.abs (f - (Double) target) < 0.000001;
                            if (target instanceof Float)
                                return f == (Float) target;
                            if (target instanceof Character)
                                return f == (Character) target;
                            if (target instanceof String)
                                return f == Float.parseFloat ((String) target);
                        }
                        
                        if (source instanceof Short)
                        {
                            short s = (Short) source;
                            
                            if (target instanceof Integer)
                                return s == (Integer) target;
                            if (target instanceof Long)
                                return s == (Long) target;
                            if (target instanceof Short)
                                return s == (Short) target;
                            if (target instanceof Byte)
                                return s == (Byte) target;
                            if (target instanceof Double)
                                return s == (Double) target;
                            if (target instanceof Float)
                                return s == (Float) target;
                            if (target instanceof Character)
                                return s == (Character) target;
                            if (target instanceof String)
                            {
                                try
                                {
                                    return s == Short.parseShort ((String) target);
                                }
                                catch (NumberFormatException e)
                                {
                                    return s == Double.parseDouble ((String) target);
                                }
                            }
                        }
                        
                        if (source instanceof Byte)
                        {
                            byte b = (Byte) source;
                            
                            if (target instanceof Integer)
                                return b == (Integer) target;
                            if (target instanceof Long)
                                return b == (Long) target;
                            if (target instanceof Short)
                                return b == (Short) target;
                            if (target instanceof Byte)
                                return b == (Byte) target;
                            if (target instanceof Double)
                                return b == (Double) target;
                            if (target instanceof Float)
                                return b == (Float) target;
                            if (target instanceof Character)
                                return b == (Character) target;
                            if (target instanceof String)
                            {
                                try
                                {
                                    return b == Byte.parseByte ((String) target);
                                }
                                catch (NumberFormatException e)
                                {
                                    return b == Double.parseDouble ((String) target);
                                }
                            }
                        }
                        
                        if (source instanceof Character)
                        {
                            char c = (Character) source;
                            
                            if (target instanceof Integer)
                                return c == (Integer) target;
                            if (target instanceof Long)
                                return c == (Long) target;
                            if (target instanceof Short)
                                return c == (Short) target;
                            if (target instanceof Byte)
                                return c == (Byte) target;
                            if (target instanceof Double)
                                return c == (Double) target;
                            if (target instanceof Float)
                                return c == (Float) target;
                            if (target instanceof Character)
                                return c == (Character) target;
                            if (target instanceof String)
                            {
                                String s = (String) target;
                                if (s.length () == 1)
                                    return c == s.charAt (0);
                            }
                        }
                    }
                    
                    if (source instanceof String)
                    {
                        String s = source.toString ();
                        if (target instanceof Character)
                        {
                            if (s.length () == 1)
                                return s.charAt (0) == (Character) target;
                        }
                        if (target instanceof Float)
                            return Float.parseFloat ((String) source) == (Float) target;
                        if (target instanceof Double)
                            return Double.parseDouble ((String) source) == (Double) target;
                    }
                    
    				Converter converter = Converter.find (target.getClass(), source.getClass());
    				if (converter != null)
    				{
    					Object convertedTarget = converter.convert (target);
    					if (convertedTarget != null)
    						return source.equals (convertedTarget);
    				}
    				
                    return source.equals (target) || source.equals (target.toString ());
                }
            },
            /** Useful for Java only, not a CSS3 selector */        
            NOT_EQUAL_TO ("!=", EQUAL_TO),
            
            /** Useful for Java only, not a CSS3 selector */
            SAME_AS ("==")
            {
                @Override
                public boolean matches (Object source, Object target)
                {
                    boolean same = source == target;
                    
                    if (SAME_AS_OPERATOR_FAILS_WHEN_NOT_SAME_BUT_EQUAL_TO && !same)
                    {
                        if (EQUAL_TO.matches (source, target))
                            throw new IllegalStateException ("The SAME_AS_OPERATOR_FAILS_WHEN_NOT_SAME_BUT_EQUAL_TO setting is set\n" +
                            		"Problem using the == selector: source != target but source.equals (target)!");
                    }
                    
                    return same;
                }
            },
            /** Useful for Java only, not a CSS3 selector */
            NOT_SAME_AS ("!==", SAME_AS),
            
            /** Useful for Java only, not a CSS3 selector */
            GREATER_THAN (">")
            {
                @Override
                public boolean matches (Object source, Object target)
                {
                    if (source == null)
                        throw new IllegalArgumentException ("Source is null"); // the selector will return false
                    
                    if (source instanceof Comparable == false) // matches all base type wrappers
                        throw new IllegalArgumentException ("Source not a Comparable: '" + source + "'"); // the selector will return false
                    
                    if (source.getClass ().isAssignableFrom (target.getClass ()))  
                    {
                        Comparable c = (Comparable) source;
                        return c.compareTo (target) > 0;
                    }
                    
                    if (source instanceof Integer)
                    {
                        int i = (Integer) source;
                        
                        if (target instanceof Integer)
                            return i > (Integer) target;
                        if (target instanceof Long)
                            return i > (Long) target;
                        if (target instanceof Short)
                            return i > (Short) target;
                        if (target instanceof Byte)
                            return i > (Byte) target;
                        if (target instanceof Double)
                            return i > (Double) target;
                        if (target instanceof Float)
                            return i > (Float) target;
                        if (target instanceof Character)
                            return i > (Character) target;
                        if (target instanceof String)
                        {
                            try
                            {
    							return i > Long.parseLong ((String) target);
    						}
                            catch (NumberFormatException e)
                            {
                            	return i > Double.parseDouble ((String) target);
    						}
                        }
                    }
                    
                    if (source instanceof Long)
                    {
                        long l = (Long) source;
                        
                        if (target instanceof Integer)
                            return l > (Integer) target;
                        if (target instanceof Long)
                            return l > (Long) target;
                        if (target instanceof Short)
                            return l > (Short) target;
                        if (target instanceof Byte)
                            return l > (Byte) target;
                        if (target instanceof Double)
                            return l > (Double) target;
                        if (target instanceof Float)
                            return l > (Float) target;
                        if (target instanceof Character)
                            return l > (Character) target;
                        if (target instanceof String)
                        {
                            try
                            {
    							return l > Long.parseLong ((String) target);
    						}
                            catch (NumberFormatException e)
                            {
                            	return l > Double.parseDouble ((String) target);
    						}
                        }
                    }
                    
                    if (source instanceof Double)
                    {
                        double d = (Double) source;
                        
                        if (target instanceof Integer)
                            return d > (Integer) target;
                        if (target instanceof Long)
                            return d > (Long) target;
                        if (target instanceof Short)
                            return d > (Short) target;
                        if (target instanceof Byte)
                            return d > (Byte) target;
                        if (target instanceof Double)
                            return d > (Double) target;
                        if (target instanceof Float)
                            return d > (Float) target;
                        if (target instanceof Character)
                            return d > (Character) target;
                        if (target instanceof String)
                            return d > Double.parseDouble ((String) target);
                    }
                    
                    if (source instanceof Float)
                    {
                        float f = (Float) source;
                        
                        if (target instanceof Integer)
                            return f > (Integer) target;
                        if (target instanceof Long)
                            return f > (Long) target;
                        if (target instanceof Short)
                            return f > (Short) target;
                        if (target instanceof Byte)
                            return f > (Byte) target;
                        if (target instanceof Double)
                            return f > (Double) target;
                        if (target instanceof Float)
                            return f > (Float) target;
                        if (target instanceof Character)
                            return f > (Character) target;
                        if (target instanceof String)
                            return f > Float.parseFloat ((String) target);
                    }
                    
                    if (source instanceof Short)
                    {
                        short s = (Short) source;
                        
                        if (target instanceof Integer)
                            return s > (Integer) target;
                        if (target instanceof Long)
                            return s > (Long) target;
                        if (target instanceof Short)
                            return s > (Short) target;
                        if (target instanceof Byte)
                            return s > (Byte) target;
                        if (target instanceof Double)
                            return s > (Double) target;
                        if (target instanceof Float)
                            return s > (Float) target;
                        if (target instanceof Character)
                            return s > (Character) target;
                        if (target instanceof String)
                        {
                            try
                            {
    							return s > Short.parseShort ((String) target);
    						}
                            catch (NumberFormatException e)
                            {
                            	return s > Double.parseDouble ((String) target);
    						}
                        }
                    }
                    
                    if (source instanceof Byte)
                    {
                        byte b = (Byte) source;
                        
                        if (target instanceof Integer)
                            return b > (Integer) target;
                        if (target instanceof Long)
                            return b > (Long) target;
                        if (target instanceof Short)
                            return b > (Short) target;
                        if (target instanceof Byte)
                            return b > (Byte) target;
                        if (target instanceof Double)
                            return b > (Double) target;
                        if (target instanceof Float)
                            return b > (Float) target;
                        if (target instanceof Character)
                            return b > (Character) target;
                        if (target instanceof String)
                        {
                            try
                            {
    							return b > Byte.parseByte ((String) target);
    						}
                            catch (NumberFormatException e)
                            {
                            	return b > Double.parseDouble ((String) target);
    						}
                        }
                    }
                    
                    if (source instanceof Character)
                    {
                        char c = (Character) source;
                        
                        if (target instanceof Integer)
                            return c > (Integer) target;
                        if (target instanceof Long)
                            return c > (Long) target;
                        if (target instanceof Short)
                            return c > (Short) target;
                        if (target instanceof Byte)
                            return c > (Byte) target;
                        if (target instanceof Double)
                            return c > (Double) target;
                        if (target instanceof Float)
                            return c > (Float) target;
                        if (target instanceof Character)
                            return c > (Character) target;
                        if (target instanceof String)
                        {
                            String s = (String) target;
                            if (s.length () == 1)
                                return c > s.charAt (0);
                            throw new IllegalArgumentException ("Comparing a character '" + c + "' to a long String "
                                    + "' " + s + "'"); // the selector will return false
                        }
                    }
                    
                    Converter converter = Converter.find (target.getClass(), source.getClass());
                    if (converter != null)
                    {
                        Object convertedTarget = converter.convert (target);
                        if (convertedTarget != null)
                        {
                            Comparable c = (Comparable) source;
                            return c.compareTo (convertedTarget) > 0;
                        }
                    }
                    
                    throw new IllegalStateException ("Not implemented for these types"
                            + " - source: " + source.getClass ().getName () + " [" + source + "]"
                            + ", target: " + target.getClass () + "[" + target + "]");
                }
            },
            /** Useful for Java only, not a CSS3 selector */
            GREATER_THAN_OR_EQUAL_TO (">=", GREATER_THAN, EQUAL_TO),
            
            /** Useful for Java only, not a CSS3 selector */
            LESS_THAN ("<", GREATER_THAN_OR_EQUAL_TO),
            /** Useful for Java only, not a CSS3 selector */
            LESS_THAN_OR_EQUAL_TO ("<=", GREATER_THAN),
            
            STARTS_WITH ("^=")
            {
                @Override
                public boolean matches (Object source, Object target)
                {
                    return source.toString().startsWith (target.toString());
                }
            },
            /** Useful for Java only, not a CSS3 selector */
            NOT_STARTS_WITH (STARTS_WITH),
            
            ENDS_WITH ("$=")
            {
                @Override
                public boolean matches (Object source, Object target)
                {
                    return source.toString().endsWith (target.toString());
                }
            },
            /** Useful for Java only, not a CSS3 selector */
            NOT_ENDS_WITH (ENDS_WITH),
            
            CONTAINS ("*=")
            {
                @Override
                public boolean matches (Object source, Object target)
                {
                    return source.toString().contains (target.toString());
                }
            },
            /** Useful for Java only, not a CSS3 selector */
            NOT_CONTAINS (CONTAINS),
            
//            MATCHES, NOT_MATCHES (MATCHES), // having a regex operator could be cool
//            IS, NOT_IS, // comparing classes: getClass() == value
//            IS_A, NOT_IS_A, // comparing classes: similar to instanceof
            
            HAS_ATTRIBUTE ("")
            {
                @Override
                public boolean matches (Object source, Object target)
                {
                    // Attribute/property existence is handled at the selector level, if we're here everything's fine 
                    return true;
                }
            },
            /** Useful for Java only, not a CSS3 selector */
            NOT_HAS_ATTRIBUTE (HAS_ATTRIBUTE) /* maybe asString: [!attr] ? */,
            
            ONE_WORD_IS ("~=")
            {
                @Override
                public boolean matches (Object source, Object target)
                {
                    String[] words = source.toString().split (" ");
                    
                    for (String word : words)
                    {
                        if (word.equals (target))
                            return true;
                    }
                    
                    return false;
                }
            },
            /** Useful for Java only, not a CSS3 selector */
            NOT_ONE_WORD_IS (ONE_WORD_IS),
            
            LANGUAGE_SUBCODE ("|=")
            {
                @Override
                public boolean matches (Object source, Object target)
                {
                    String value = source.toString();
                    return value.equals (target) || value.startsWith (target.toString () + "-");
                }
            },
            /** Useful for Java only, not a CSS3 selector */
            NOT_LANGUAGE_SUBCODE (LANGUAGE_SUBCODE);
            
            private Type()
            {
            }
            
            private Type inverse;
            
            private Type (Type inverse)
            {
                this.inverse = inverse;
            }

            private Type[] or;
            
            private Type (Type... or)
            {
                this.or = or;
            }

            private String asString;
            
            private Type (String asString)
            {
                this.asString = asString;
            }

            private Type (String asString, Type inverse)
            {
                this.asString = asString;
                this.inverse = inverse;
            }

            private Type (String asString, Type... or)
            {
                this.asString = asString;
                this.or = or;
            }

            public boolean matches (Object source, Object target)
            {
                if (inverse != null)
                    return ! inverse.matches (source, target);
                
                if (or != null)
                {
                    boolean matches = false;
                    for (Type o : or)
                        matches = matches || o.matches (source, target);
                    
                    return matches;
                }
                
                throw new IllegalStateException ("Shouldn't happen - selector type not implemented: " + this);
            }

            public static Type[] pureCSSSelectors()
            {
                return new Type[] {EQUAL_TO, STARTS_WITH, ENDS_WITH, CONTAINS, HAS_ATTRIBUTE, ONE_WORD_IS, LANGUAGE_SUBCODE};
            }
            
            public static Type from (String asString)
            {
            	if (asString == null)
            		asString = "";
            	
                for (Type o : isPureCSS () ? Type.pureCSSSelectors () : Type.values ())
                {
                    if (o.asString != null && o.asString.equals (asString))
                        return o;
                }
                
                throw new IllegalStateException ("Unknown type for '" + asString + "'");
            }
        }
    }
    
	// testing purposes only
	public static Attribute[] from (String selector)
    {
        List<Attribute> attributes = new ArrayList<Attribute>();
        
        String [] parsed = parseAttributeDeclaration (selector);
        for (int i = 0; i < parsed.length; i += 4)
        {
            String property = parsed [i];
            Type operator = Type.from (parsed [i + 1]);
            String value = parsed [i + 3];
            
            attributes.add (new Attribute (property, operator, value));
        }
        
        return attributes.toArray (new Attribute [attributes.size ()]);
    }
	
	// testing purposes only
	static String [] parseAttributeDeclaration (String declaration)
    {
        Matcher m = Pattern.compile (EXTRACTOR).matcher (declaration);
        
        if (m.find () == false)
            return new String [0];

        List<String> found = new ArrayList<String>();
        
        do
        {
            for (int i = 0; i < m.groupCount(); ++i)
                found.add (m.group (i + 1));
        }
        while (m.find ());
        
        return found.toArray (new String [found.size ()]);
    }
}