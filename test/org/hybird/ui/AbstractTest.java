package org.hybird.ui;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.hybird.ui.query.Query;
import org.hybird.ui.tk.HComponent;
import org.hybird.ui.tk.HPanel;

public class AbstractTest
{
    public static HPanel ul (String name)
    {
        return styledHPanel ("ul", name);
    }
    
    public static HPanel li (String name)
    {
        return styledHPanel ("li", name);
    }
    
    public static HPanel ol (String name)
    {
        return styledHPanel ("ol", name);
    }
    
    public static HPanel p (String name)
    {
        return styledHPanel ("p", name);
    }
    
    public static HPanel span (String name)
    {
        return styledHPanel ("span", name);
    }
    
    public static HPanel div (String name)
    {
        return styledHPanel ("div", name);
    }
    
    public static HPanel address (String name)
    {
        return styledHPanel ("address", name);
    }
    
    public static HPanel table (String name)
    {
        return styledHPanel ("table", name);
    }
    
    public static HPanel tbody (String name)
    {
        return styledHPanel ("tbody", name);
    }
    
    public static HPanel tr (String name)
    {
        return styledHPanel ("tr", name);
    }
    
    public static HPanel td (String name)
    {
        return styledHPanel ("td", name);
    }
    
    public static HPanel ul ()
    {
        return ul ("");
    }
    
    public static HPanel li ()
    {
        return ul ();
    }
    
    public static HPanel ol ()
    {
        return ol ("");
    }
    
    public static HPanel table ()
    {
        return table ("");
    }
    
    public static HPanel tr ()
    {
        return tr ("");
    }
    
    public static HPanel td ()
    {
        return td ("");
    }
    
    public static HPanel div ()
    {
        return div ("");
    }
    
    public static HPanel p ()
    {
        return p ("");
    }
    
    public static HPanel tbody ()
    {
        return tbody ("");
    }
    
    public static HPanel address ()
    {
        return address ("");
    }
    
    public static HPanel span ()
    {
        return span ("");
    }
    
    public static HPanel blockquote ()
    {
        return blockquote ("");
    }
    
    public static HPanel blockquote (String name)
    {
        return styledHPanel ("blockquote", name);
    }
    
    public static HPanel styledHPanel (String style, String name)
    {
        return new HPanel ().style (style).as (name);
    }
    
    public static class JPanelWithTitle extends JPanel
    {
        private String title;
        
        public JPanelWithTitle ()
        {
        }
        
        public JPanelWithTitle (String name, String title)
        {
            setName (name);
            setTitle (title);
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle (String title)
        {
            this.title = title;
        }
    }
    
    public static void assertListContainsOnly (List<?> list, Object... o)
    {
        assertNotNull (list);
        assertNotNull (o);

        if (o.length == 0 && list.isEmpty () == false)
        {
            System.out.print ("Results: ");
            for (Object x : list)
                System.out.print (x + " ");
            System.out.println ("");
        }

        assertEquals (o.length, list.size ());

        for (Object _o : o)
        {
            assertTrue ("List should contain: '" + _o + "': " + list, list.contains (_o));
        }
    }
    
    public static void assertQueryMatches (String query, HComponent<?,?> root, String... names)
    {
        assertQueryMatches (query, root.asComponent (), names);
    }

    public static void assertQueryMatches (String query, JComponent root, String... names)
    {
        Query q = new Query (query);
        List<JComponent> matches = q.matches (root);
        
        assertListContainsNames(matches, names);
    }
    
	public static void assertListContainsNames (List<JComponent> matches, String... names)
	{
		assertEquals ("Different number of results", names.length, matches.size ());
        
        for (String name : names)
            assertListContainsName (matches, name);
	}
    
    public static void assertListContainsName (List<JComponent> components, String name)
    {
        for (JComponent c : components)
        {
            if (name.equals (c.getName ()))
                return;
        }
        
        String names = "";
        for (JComponent c : components)
        	names += c.getName() + ", ";
        
        names = names.substring (0, names.length() - 2);
        
        fail ("The name '" + name + "' is not present in the list: '" + names + "'");
    }
    
    public static void assertQueryMatches (int expected, String query, HComponent<?,?> h)
    {
        assertQueryMatches (expected, query, h.asComponent ());
    }
    
    public static void assertQueryMatches (int expected, String query, JComponent c)
    {
        assertEquals (expected, new Query (query).matches (c).size ());
    }
}
