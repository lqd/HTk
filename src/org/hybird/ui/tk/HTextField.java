package org.hybird.ui.tk;

import javax.swing.JTextField;

public class HTextField extends HComponent <JTextField, HTextField>
{
    public static final Class<HTextField> HTextField = HTextField.class;
    public static final Class<JTextField> JTextField = JTextField.class;
    
    public HTextField ()
    {
        this ("");
    }
    
    public HTextField (String text)
    {
        this (new JTextField (text));
    }
    
    public HTextField (JTextField textField)
    {
        delegate (textField);
    }

    public HTextField columns (int columns)
    {
        delegate.setColumns (columns);
        return this;
    }
    
    public HTextField text (String text)
    {
        delegate.setText (text);
        return this;
    }
    
    public String text()
    {
        return delegate.getText ();
    }
    
    public HTextField horizontalAlignment (HAlignment horizontalAlignment)
    {
        delegate.setHorizontalAlignment (horizontalAlignment.value ());
        return this;
    }

    public void requestFocus ()
    {
        delegate.requestFocus ();
    }
    
    public boolean hasFocus ()
    {
        return delegate.hasFocus ();
    }
    
    /** Requests focus and selects all */
    public void selectAll ()
    {
        requestFocus();
        delegate.selectAll ();
    }
}
