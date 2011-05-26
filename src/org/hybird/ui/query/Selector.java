package org.hybird.ui.query;

import java.util.regex.Matcher;

import javax.swing.JComponent;

public interface Selector
{
    void init (Matcher matcher);
    boolean matches (JComponent component);
}