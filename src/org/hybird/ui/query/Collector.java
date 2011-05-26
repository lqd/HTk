package org.hybird.ui.query;

import java.util.List;

import javax.swing.JComponent;

public interface Collector
{
	void init (Query query);
	
	List<JComponent> collect (JComponent root);
}
