package org.hybird.ui.tk.mixins;

import static org.hybird.ui.tk.HTk.getProperty;
import static org.hybird.ui.tk.HTk.setProperty;

import javax.swing.JComponent;

import org.hybird.ui.tk.HComponent;

public class SuffixMixin
{
    private static final String SUFFIX_PROPERTY = "HTk.SuffixMixin.suffix";
    
    private JComponent component;
    
    public SuffixMixin (JComponent component)
    {
        this.component = component;
    }

    @SuppressWarnings("rawtypes")
    public SuffixMixin (HComponent component)
    {
        this (component.asComponent ());
    }
    
    public String suffix()
    {
        return getProperty (component, SUFFIX_PROPERTY);
    }
    
    public void suffix (String suffix)
    {
        setProperty (component, SUFFIX_PROPERTY, suffix);
    }
    
    public String prepareText (String text)
    {
        String suffix = suffix ();
        if (suffix != null && suffix.isEmpty () == false)
            text += suffix;
        return text;
    }

    public String removeSuffix (String text)
    {
        String suffix = suffix ();
        if (suffix != null && suffix.isEmpty () == false)
            text = text.replace (suffix, "");
        return text;
    }
}
