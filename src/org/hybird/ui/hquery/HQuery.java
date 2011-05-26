package org.hybird.ui.hquery;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.hybird.ui.events.Event;
import org.hybird.ui.events.EventListener;
import org.hybird.ui.events.EventSource;
import org.hybird.ui.tk.HComponent;
import org.hybird.ui.tk.HPanel;

@SuppressWarnings("rawtypes")
public interface HQuery
{
    /** The number of results this query has */ 
    int count();
    
    /** Shortcut to asComposite */
    <T extends HComponent> T as (Class<T> klass);
    
//    /** Returns a composite (of 1 or many) JComponent instances */
//    <T extends JComponent> T as (Class<T> klass);
    
    /** Returns 1 HComponent instance */
    <T extends HComponent> T asHComponent (Class<T> klass);
    /** Returns many HComponent instances */
    <T extends HComponent> List<T> asHComponents (Class<T> klass);
    
    /** Returns 1 JComponent instance */
    <T extends JComponent> T asJComponent (Class<T> klass);
    /** Returns many JComponent instances */
    <T extends JComponent> List<T> asJComponents (Class<T> klass);
    
    /** Equivalent to asComposite (HComponent.class) */
    <T extends HComponent> T asComposite ();
    
    /** Returns a composite (of 1 or many) HComponent instances */
    <T extends HComponent> T asComposite (Class<T> klass);
    
    /** Equivalent to asJComposite (JComponent.class) */
    <T extends JComponent> T asJComposite ();
    /** Returns a composite (of 1 or many) JComponent instances */
    <T extends JComponent> T asJComposite (Class<T> klass);
    
    /** Equivalent to asComponent (HComponent.class) */
    HComponent asHComponent ();
    /** Equivalent to asComponents (HComponent.class) */
    List<HComponent> asHComponents ();
    
    /** Equivalent to asJComponent (JComponent.class) */
    JComponent asJComponent ();
    /** Equivalent to asJComponents (JComponent.class) */
    List<JComponent> asJComponents ();

    HQuery addStyle (String style);
    HQuery removeStyle (String style);

	HQuery toggleStyle (String styleA, String styleB);

	String selector();

	HQuery find (String selector);
	
	<T extends DelegatedHQuery> T with (Class<T> plugin);

	List<JComponent> contents();

	<E> HQuery connect (Event<?,E> event, EventListener<E> listener);
	<L> HQuery connect (EventSource<L> source, L listener);
	
	<E> HQuery disconnect (Event<?, E> event, EventListener<E> listener);
	HQuery disconnect (EventSource<?> source);

	HQuery addTo (HPanel panel);
	HQuery addTo (JPanel panel);
	HQuery removeFromParent();

	HQuery enabled (boolean enabled);
	HQuery visible (boolean visible);
	
	// add first and last methods ? maybe with an optional class to have $ ("").first (MyComponent.class).myMethod();
	
	HQuery parents ();
	HQuery parents (String selector);
}