package org.hybird.ui.query.collectors;

import java.util.List;

import javax.swing.JComponent;

import org.hybird.ui.query.Collector;
import org.hybird.ui.query.Query;

public class FullHierarchyCollector implements Collector
{
	@Override
	public void init (Query query)
	{
	}
	
	@Override
	public List<JComponent> collect (JComponent root)
	{
		return Collectors.getAllChildren (root);
	}
}