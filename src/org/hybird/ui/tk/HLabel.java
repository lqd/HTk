package org.hybird.ui.tk;

import java.awt.Color;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.hybird.ui.tk.mixins.SuffixMixin;

public class HLabel extends HComponent <JLabel, HLabel>
{
    public static final Class<HLabel> HLabel = HLabel.class;
    public static final Class<JLabel> JLabel = JLabel.class;
    
    public HLabel ()
    {
        this ("");
    }
    
    public HLabel (String text)
    {
        this (new JLabel (text));
    }

    public HLabel (JLabel label)
    {
        super (label);
    }
    
    @Override
    public HLabel background (Color color)
    {
        if (! opaque())
            opaque (true);
        return super.background (color);
    }
    
    private SuffixMixin suffix;
    
    private SuffixMixin suffixMixin()
    {
        if (suffix == null)
            suffix = new SuffixMixin (this);
        return suffix;
    }
    
    public String suffix ()
    {
        return suffixMixin().suffix ();
    }
    
    public HLabel suffix (String suffix)
    {
        String text = suffixMixin ().removeSuffix (delegate.getText ());
        
        suffixMixin ().suffix (suffix);
        text = suffixMixin().prepareText (text);
        
        delegate.setText (text);
        return this;
    }
    
    public HLabel text (String text)
    {
        text = suffixMixin().prepareText (text);
        delegate.setText (text);
        
        return this;
    }
    
    public HLabel text (String format, Object... args)
    {
        return text (String.format (format, args));
    }
    
    public String text ()
    {
        return delegate.getText ();
    }
    
    public HLabel icon (Icon icon)
    {
        delegate.setIcon (icon);
        return this;
    }
    
    public HLabel icon (Image image)
    {
        return icon (new ImageIcon (image));
    }
    
    public HLabel horizontalAlignment (HAlignment horizontalAlignment)
    {
        delegate.setHorizontalAlignment (horizontalAlignment.value ());
        return this;
    }
    
    public HLabel verticalAligment (HAlignment verticalAligment)
    {
        delegate.setVerticalAlignment (verticalAligment.value ());
        return this;
    }
}