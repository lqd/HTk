package org.hybird.ui.query;

import java.util.regex.Matcher;

import javax.swing.JComponent;

public interface Filter
{
    void init (Matcher matcher);
	boolean filter (JComponent component, int index, int size);
}