package org.hybird.ui.query.selectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.JComponent;

import org.hybird.ui.query.Selector;
import org.hybird.ui.tk.HTk;

public class StyleClassSelector implements Selector
{
    public static final String EXTRACTOR = "\\.((?:[\\w\\u00c0-\\uFFFF-]|\\\\.)+)";

    private List<String> styleClasses;

    @Override
    public void init (Matcher matcher)
    {
        styleClasses = new ArrayList<String> ();

        do
        {
            styleClasses.add (matcher.group (1));
        }
        while (matcher.find ());

    }
    
    @Override
    public boolean matches (JComponent component)
    {
        String style = HTk.getProperty (component, HTk.STYLE_PROPERTY);
        
        if (style == null)
            return false;
        
        List<String> componentStyles = Arrays.asList (style.split (" "));
        List<String> selectorStyles = styleClasses;
        
        for (String selectorStyle : selectorStyles)
        {
            if (! componentStyles.contains (selectorStyle))
                return false;
        }
        
        return true;
    }
    
    public List<String> styleClasses ()
    {
        return styleClasses;
    }
    
    @Override
    public String toString ()
    {
        return "StyleClassSelector [styleClasses=" + styleClasses + "]";
    }
}