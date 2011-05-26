package org.hybird.ui.tk;

import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.hybird.ui.tk.mixins.SuffixMixin;

public class HButton extends HComponent <JButton, HButton>
{
    public static final Class<HButton> HButton = HButton.class;
    public static final Class<JButton> JButton = JButton.class;

    public HButton ()
    {
        this ("");
    }
    
    /** Not sure about this one, as it strongly links the wrapped JButton to this instance of HButton because of the action listener
        - Possibly creating a leak ?
     */
    public HButton (String label)
    {
        delegate (new JButton (new AbstractAction (label)
        {
            @Override
            public void actionPerformed (ActionEvent e)
            {
                HButton.this.actionPerformed (e);
            }
        }));
    }
    
    /** Wrapper constructor - mostly used by HTk when it wraps JButtons in composites */
    public HButton (JButton button)
    {
        delegate (button);
    }
    
    public HButton icon (Icon icon) 
    {
    	delegate.setIcon (icon);
    	return this;
    }

    public HButton icon (Image image)
    {
        delegate.setIcon (new ImageIcon (image));
        return this;
    }
    
    public HButton icon (String url) 
    {
		if (url != null && !url.isEmpty ())
    		return icon (new ImageIcon (getClass ().getResource (url)));
		
		return this;
    }
    
    public HButton margin (Insets margin)
    {
        delegate.setMargin (margin);
    	return this;
    }
    
    public Insets margin ()
    {
    	return delegate.getMargin ();
    }

    private SuffixMixin suffix;
    
    private SuffixMixin suffix()
    {
        if (suffix == null)
            suffix = new SuffixMixin (this);
        return suffix;
    }
    
    public HButton suffix (String suffix)
    {
        String text = suffix ().removeSuffix (delegate.getText ());
        
        suffix ().suffix (suffix);
        text = suffix().prepareText (text);
        
        delegate.setText (text);
        
        return this;
    }

    public String text ()
    {
        return delegate.getText ();
    }
    
    public HButton text (String text)
    {
        text = suffix ().prepareText (text);
        delegate.setText (text);
        
        return this;
    }
    
    protected void actionPerformed (ActionEvent e)
    {
    }
    
    public HButton focusPainted (boolean focusPainted)
    {
        delegate.setFocusPainted (focusPainted);
        return this;
    }
}