package org.hybird.ui.query.selectors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.JComponent;

import org.hybird.ui.query.Selector;

public class IdSelector implements Selector
{
    public static final String EXTRACTOR = "#((?:[\\w\\u00c0-\\uFFFF-]|\\\\.)+)";

    private List<String> ids;

    @Override
    public void init (Matcher matcher)
    {
        ids = new ArrayList<String> ();

        do
        {
        	if (ids.contains (matcher.group (1)) == false)
        		ids.add (matcher.group (1));
        }
        while (matcher.find ());
    }
    
    @Override
    public boolean matches (JComponent component)
    {
        for (String id : ids)
        {
            if (id.equals (component.getName ()) == false)
                return false;
        }
        
        return true;
    }
    
    public List<String> ids ()
    {
        return ids;
    }
    
    @Override
    public String toString ()
    {
        return "IdSelector [ids=" + ids + "]";
    }
}