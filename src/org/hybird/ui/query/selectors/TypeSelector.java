package org.hybird.ui.query.selectors;

import java.util.regex.Matcher;

import javax.swing.JComponent;

import org.hybird.ui.query.Selector;

public class TypeSelector implements Selector
{
    public static final String EXTRACTOR = "^((?:[\\w\\u00c0-\\uFFFF\\*-]|\\\\.)+)";
    
    private String type;

    @Override
    public void init (Matcher matcher)
    {
        type = matcher.group (1);
    }
    
    @Override
    public boolean matches (JComponent component)
    {
        if ("*".equals (type))
            return true;
        
        String name = component.getClass().getSimpleName();

        if ("".equals (name)) // anonymous class
            name = component.getClass().getSuperclass ().getSimpleName ();
        
        return type.equals (name);
    }
    
    @Override
    public String toString ()
    {
        return "TypeSelector [type=" + type + "]";
    }

    public String type ()
    {
        return type;
    }
}