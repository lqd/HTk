package org.hybird.ui.query;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.hybird.ui.AbstractTest;
import org.hybird.ui.query.collectors.FilteringCollector;
import org.hybird.ui.query.collectors.FullHierarchyCollector;
import org.hybird.ui.query.selectors.*;
import org.hybird.ui.query.selectors.AttributeSelector.Attribute;
import org.hybird.ui.tk.HLabel;
import org.hybird.ui.tk.HPanel;
import org.hybird.ui.tk.HTk;
import org.junit.Test;

public class QueryTest extends AbstractTest
{
    @Test
    public void expressionParsing ()
    {
        Query q = new Query (":hover");

        assertEquals (1, q.rawExpressions ().size ());
        assertListContainsOnly (q.rawExpressions (), ":hover");

        Expression p = q.expressions ().get (0);
        assertEquals (1, p.selectors ().size ());
        assertTrue (p.selectors ().get (0) instanceof PseudoClassSelector);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void expressionWithMultipleSelectors ()
    {
        Query q = new Query ("#link:hover");

        Expression e = q.expressions ().get (0);
        assertEquals (2, e.selectors ().size ());

        assertThat (e.selectors ().get (0), anyOf
        (
            is (PseudoClassSelector.class),
            is (IdSelector.class))
        );

        assertThat (e.selectors ().get (1), anyOf
        (
            is (PseudoClassSelector.class),
            is (IdSelector.class))
        );
    }

    @Test
    public void idSelector ()
    {
        Query q = new Query ("#link");

        Expression e = q.expressions ().get (0);
        assertEquals (1, e.selectors ().size ());

        Selector selector = e.selectors ().get (0);
        assertTrue (selector instanceof IdSelector);

        IdSelector s = (IdSelector) selector;
        assertEquals ("link", s.ids ().get (0));
    }

    @Test
    public void multipleStyleClasses ()
    {
        Query q = new Query (".blog.link");

        Expression e = q.expressions ().get (0);
        assertEquals (1, e.selectors ().size ());

        Selector selector = e.selectors ().get (0);
        assertTrue (selector instanceof StyleClassSelector);

        StyleClassSelector s = (StyleClassSelector) selector;
        assertListContainsOnly (s.styleClasses (), "blog", "link");
    }

    @Test
    public void typeSelector ()
    {
        Query q = new Query ("div");

        Expression e = q.expressions ().get (0);
        assertEquals (1, e.selectors ().size ());

        Selector selector = e.selectors ().get (0);
        assertTrue (selector instanceof TypeSelector);

        TypeSelector s = (TypeSelector) selector;
        assertEquals ("div", s.type ());
    }

    @Test
    public void attributeSelector ()
    {
        Query q = new Query ("[name][x $= bobi]");

        Expression e = q.expressions ().get (0);
        assertEquals (1, e.selectors ().size ());

        Selector selector = e.selectors ().get (0);
        assertTrue (selector instanceof AttributeSelector);

        AttributeSelector s = (AttributeSelector) selector;
        assertEquals (2, s.attributes ().size ());

        assertEquals ("name", s.attributes ().get (0).property ());

        assertEquals ("x", s.attributes ().get (1).property ());
        assertEquals ("bobi", s.attributes ().get (1).value ());
    }

    @Test
    public void pseudoClassSelector ()
    {
        Query q = new Query (":hover");

        Expression e = q.expressions ().get (0);
        assertEquals (1, e.selectors ().size ());

        Selector selector = e.selectors ().get (0);
        assertTrue (selector instanceof PseudoClassSelector);

        PseudoClassSelector s = (PseudoClassSelector) selector;
        assertEquals (1, s.pseudoClasses ().size ());
        assertEquals ("hover", s.pseudoClasses ().get (0).name ());
    }

    @Test
    public void specialPseudoClassSelector ()
    {
        Query q = new Query (":not(#id)");

        Expression e = q.expressions ().get (0);
        assertEquals (1, e.selectors ().size ());

        Selector selector = e.selectors ().get (0);
        assertTrue (selector instanceof SpecialPseudoClassSelector);

        SpecialPseudoClassSelector s = (SpecialPseudoClassSelector) selector;
        assertEquals (1, s.specialPseudoClasses ().size ());
        assertEquals ("not", s.specialPseudoClasses ().get (0).name ());
        assertEquals ("#id", s.specialPseudoClasses ().get (0).parameters ());
    }

    @Test
    public void queryParsing ()
    {
        Query q = new Query ("a.links#link:hover + a:hover:enabled p:nth(1)");

        assertEquals (3, q.rawExpressions ().size ());
        assertListContainsOnly (q.rawExpressions (), "a.links#link:hover", "a:hover:enabled", "p:nth(1)");

        assertEquals (Combinator.ADJACENT_SIBLING, q.expressions ().get (1).combinator ());
        assertEquals (Combinator.DESCENDANT, q.expressions ().get (2).combinator ());
    }

    @Test
    public void multipleQueryParsing ()
    {
        Query q = new Query ("a + a, code > a, a.links#link:hover + a:hover:enabled p:nth(1),"
                + " p ~ a[href ^= 'http://www'] > option:not([id^='opt']:nth-child(-n+3)) "
                + "input[type=text]");

        assertEquals (11, q.rawExpressions ().size ());
        assertListContainsOnly (q.rawExpressions (), "a", "a", "code", "a", "a.links#link:hover",
                "a:hover:enabled", "p:nth(1)", "p", "a[href ^= 'http://www']",
                "option:not([id^='opt']:nth-child(-n+3))", "input[type=text]");

        Combinator [] combinators = 
        {
            // a + a:
            null, Combinator.ADJACENT_SIBLING,

            // code > a:
            null, Combinator.CHILD,

            // a.links#link:hover + a:hover:enabled p:nth(1):
            null, Combinator.ADJACENT_SIBLING, Combinator.DESCENDANT,

            // p ~ a[href ^= 'http://www'] >
            // option:not([id^='opt']:nth-child(-n+3)) input[type=text]:
            null, Combinator.GENERAL_SIBLING, Combinator.CHILD, Combinator.DESCENDANT
        };

        List<Expression> expressions = q.expressions ();
        assertEquals (combinators.length, expressions.size ());

        for (int i = 0, size = expressions.size (); i < size; ++i)
            assertEquals ("Mismatch at index " + i, combinators[i], expressions.get (i).combinator ());
    }

    @Test
    public void expressionMatchers ()
    {
        Query q = new Query ("#link");

        List<Expression> expressions = q.expressions ();
        assertEquals (1, expressions.size ());

        Selector s = expressions.get (0).selectors ().get (0);
        assertThat (s, is (IdSelector.class));

        JComponent match = new JPanel ();
        match.setName ("link");

        JComponent noName = new JPanel ();

        JComponent noMatch = new JPanel ();
        noMatch.setName ("noMatch");

        assertTrue (s.matches (match));
        assertFalse (s.matches (noName));
        assertFalse (s.matches (noMatch));
    }

    @Test
    public void classAndId ()
    {
        Query q = new Query ("JPanel#link");
        assertEquals (1, q.expressions ().size ());
        assertEquals (2, q.expressions ().get (0).selectors ().size ());

        JComponent match = new JPanel ();
        match.setName ("link");

        JComponent noName = new JPanel ();
        JComponent noMatch = new JLabel ();

        assertListContainsOnly (q.matches (match, noName, noMatch), match);
    }

    @Test
    public void classAndAttribute ()
    {
        Query q = new Query ("JPanel[name=link]");
        assertEquals (1, q.expressions ().size ());
        assertEquals (2, q.expressions ().get (0).selectors ().size ());

        JComponent match = new JPanel ();
        match.setName ("link");

        JComponent noName = new JPanel ();
        JComponent noMatch = new JLabel ();

        assertListContainsOnly (q.matches (match, noName, noMatch), match);
    }

    @Test
    public void classAndAttributeAndId ()
    {
        Query q = new Query ("JPanel#link[visible=false]");
        assertEquals (1, q.expressions ().size ());
        assertEquals (3, q.expressions ().get (0).selectors ().size ());

        JComponent match = new JPanel ();
        match.setName ("link");
        match.setVisible (false);

        JComponent noName = new JPanel ();
        JComponent noMatch = new JLabel ();

        assertListContainsOnly (q.matches (match, noName, noMatch), match);
    }

    @Test
    public void styleClass ()
    {
        Query q = new Query (".bobi");
        assertEquals (1, q.expressions ().size ());

        JComponent match = new JPanel ();
        match.putClientProperty (HTk.STYLE_PROPERTY, "bobi");

        JComponent noStyleClass = new JPanel ();
        JComponent noMatch = new JLabel ();
        noMatch.putClientProperty (HTk.STYLE_PROPERTY, "notBobi");

        assertListContainsOnly (q.matches (match, noStyleClass, noMatch), match);
    }

    @Test
    public void styleClasses ()
    {
        Query q = new Query (".bobi.joe");
        assertEquals (1, q.expressions ().size ());

        JComponent match = new JPanel ();
        match.putClientProperty (HTk.STYLE_PROPERTY, "bobi joe");

        JComponent noStyleClass = new JPanel ();
        JComponent noMatch = new JLabel ();
        noMatch.putClientProperty (HTk.STYLE_PROPERTY, "bobi notJoe");

        assertListContainsOnly (q.matches (match, noStyleClass, noMatch), match);
    }

    @Test
    public void booleanPropertiesArePseudoClasses ()
    {
        Query q = new Query (":visible");
        assertEquals (1, q.expressions ().size ());

        JComponent match = new JPanel ();

        JComponent noMatch = new JLabel ();
        noMatch.setVisible (false);

        assertListContainsOnly (q.matches (match, noMatch), match);
    }

    @Test
    public void hComponentsStyles ()
    {
        HPanel h = new HPanel ().as ("h1");
        h.add (new HPanel ().as ("h2"));

        JPanel p = new JPanel ();
        p.setName ("h3");
        h.add (p);

        assertQueryMatches (".HPanel", h, "h1", "h2");
        assertQueryMatches ("JPanel", h, "h1", "h2", "h3");
    }

    @Test
    public void descendantCombinator ()
    {
        HPanel h = new HPanel ().as ("parent");
        h.add (new HPanel ().as ("match"));
        h.add (new HPanel ());

        assertQueryMatches ("#parent #match", h, "match");
        
        HPanel h2 = new HPanel();
        h2.add (new HPanel().add (new HPanel().add (new HPanel().add (h))));
        
        assertQueryMatches ("#parent #match", h2, "match");
    }

    @Test
    public void deeperDescendantCombinator ()
    {
        HPanel h = new HPanel ().as ("parent");
        h.add (new HPanel ().add (new HPanel ().add (new HPanel ().as ("match"))));

        assertQueryMatches ("#parent #match", h, "match");
    }

    @Test
    public void childCombinator ()
    {
        HPanel h = new HPanel ().as ("1");
        h.add (new HPanel ().as ("1.1").add (new HLabel ().as ("1.1.1")));
        h.add (new HLabel ().as ("1.2"));

        assertQueryMatches ("JPanel > JLabel", h, "1.2", "1.1.1");
        assertQueryMatches ("JPanel > JPanel", h, "1.1");
        assertQueryMatches ("JPanel > *", h, "1.1", "1.2", "1.1.1");
    }

    @Test
    public void childCombinators ()
    {
        HPanel h = new HPanel ().as ("1");
        HPanel child1 = new HPanel ().as ("1.1").add (new HLabel ().as ("1.1.1"));
        HPanel child2 = new HPanel ().as ("1.2").add (new HLabel ().as ("1.2.1"));

        h.add (child1).add (child2).add (new HLabel ().as ("1.3"));

        assertQueryMatches ("JPanel > .HPanel > JLabel", h, "1.1.1", "1.2.1");
    }

    @Test
    public void adjacentSiblingCombinator ()
    {
        HPanel h = new HPanel ();
        h.add (new HLabel ());
        h.add (new HPanel ().as ("h2"));
        h.add (new HPanel ());

        assertQueryMatches (".HLabel + .HPanel", h, "h2");
    }

    @Test
    public void generalSiblingCombinator ()
    {
        HPanel h = new HPanel ();
        h.add (new HLabel ());
        h.add (new HPanel ().as ("h2"));
        h.add (new HPanel ().as ("h3"));

        assertQueryMatches ("JLabel ~ JPanel", h, "h2", "h3");
    }

    @Test
    public void multipleCombinators ()
    {
        HPanel h = new HPanel ().as ("h1");
        HPanel h2 = new HPanel ().as ("h2");
        HPanel h3 = new HPanel ().as ("h3");
        HLabel h4 = new HLabel ().as ("h4");

        h.add (h2);
        h2.add (h3);
        h3.add (h4);

        assertQueryMatches ("#h2 > .HPanel#h3 JLabel", h, "h4");
    }

    @Test
    public void pseudoClassMatchers ()
    {
        Query q = new Query (":has(JPanel)");
        assertEquals (1, q.expressions ().size ());

        JPanel match = new JPanel ();
        match.add (new JPanel ());

        JPanel noMatch = new JPanel ();

        Expression m = q.expressions ().get (0);
        assertFalse (m.matches (noMatch));
        assertTrue (m.matches (match));
    }

    @Test
    public void not ()
    {
        Query q = new Query (":not(:visible)");
        assertEquals (1, q.expressions ().size ());

        JComponent noMatch = new JPanel ();

        JComponent match = new JLabel ();
        match.setVisible (false);

        JComponent match2 = new JLabel ();
        match2.setVisible (false);

        match.add (match2);

        assertListContainsOnly (q.matches (match, noMatch), match, match2);
    }

    @Test
    public void multipleNot ()
    {
        Query q = new Query (":not(JLabel,JPanel)");
        assertEquals (1, q.expressions ().size ());

        JComponent noMatch = new JPanel ();
        JComponent noMatch2 = new JLabel ();

        JComponent match = new JButton ();

        assertListContainsOnly (q.matches (match, noMatch, noMatch2), match);
        assertListContainsOnly (new Query (":not(JLabel, JPanel)").matches (match, noMatch, noMatch2), match);
    }

    @Test
    public void complexNot ()
    {
        HPanel h = new HPanel ().as ("h1");
        h.add (new HPanel ().as ("h2"));
        h.add (new HLabel ().as ("h3"));

        assertQueryMatches (":not(.HPanel#h2)", h, "h1", "h3");
        assertQueryMatches (":not(#h2)", h, "h1", "h3");
        assertQueryMatches (":not(.HComponent)", h);
        assertQueryMatches (":not(JPanel)", h, "h3");
        assertQueryMatches (":not(JPanel.HComponent)", h, "h3");
    }

    @Test
    public void anotherComplexNot ()
    {
        JPanel h = new JPanel ();
        h.setName ("h1");
        h.add (new HPanel ().as ("h2").asComponent ());
        h.add (new HPanel ().as ("h3").asComponent ());

        assertQueryMatches (":not(.HPanel[name^=h])", h, "h1");
    }

    @Test
    public void wildcardTypeSelector ()
    {
        HPanel h = new HPanel ().as ("1");
        h.add (new HPanel ().as ("h2"));
        h.add (new HPanel ().as ("h3"));

        assertQueryMatches ("*", h, "1", "h2", "h3");
        assertQueryMatches ("*[name]", h, "1", "h2", "h3");
        assertQueryMatches ("*[name^=h]", h, "h2", "h3");
        assertQueryMatches ("*#h2", h, "h2");
        assertQueryMatches ("*.HPanel", h, "1", "h2", "h3");
    }

    @Test
    public void attributesCanBeClientPropertiesOnJComponents ()
    {
        HPanel h = new HPanel ().as ("h1").property ("foo", "baz");
        h.add (new HPanel ().as ("h2").property ("foo", "bar"));
        h.add (new HPanel ().as ("h3"));

        assertQueryMatches ("[foo]", h, "h1", "h2");
        assertQueryMatches ("[foo=bar]", h, "h2");
    }

    @Test
    public void convertedAttribute ()
    {
        HPanel h = new HPanel ().background (Color.blue);

        Query q = new Query ("[background=Color.blue]");
        assertEquals (1, q.expressions ().size ());
        assertEquals (1, q.matches (h).size ());

        assertQueryMatches (1, "[background=#0000FF]", h);
        assertQueryMatches (1, "[background=#00F]", h);
        assertQueryMatches (1, "[background=blue]", h);
        assertQueryMatches (1, "[background=Color.blue]", h);
        assertQueryMatches (1, "[background=BLUE]", h);
        assertQueryMatches (1, "[background=Color.BLUE]", h);
        assertQueryMatches (1, "[background = Color.blue]", h);
        assertQueryMatches (1, "[background =Color.blue]", h);
        assertQueryMatches (1, "[background= Color.blue]", h);
        assertQueryMatches (1, "[background=rgb(0,0,255)]", h);
        assertQueryMatches (1, "[background = rgb (0, 0, 255)]", h);
        assertQueryMatches (1, "[background= rgb(0,0,255)]", h);
        assertQueryMatches (1, "[background=Color.rgb(0,0,255)]", h);
        assertQueryMatches (1, "[background = Color.rgb (0, 0, 255)]", h);
        assertQueryMatches (1, "[background= Color.rgb(0,0,255)]", h);

        assertQueryMatches (1, "[background=rgba(0,0,255,255)]", h);
        assertQueryMatches (1, "[background = rgba (0, 0, 255, 255)]", h);
        assertQueryMatches (1, "[background= rgba (0,0,255,255)]", h);
        assertQueryMatches (1, "[background=Color.rgba(0,0,255,255)]", h);
        assertQueryMatches (1, "[background = Color.rgba (0, 0, 255, 255)]", h);
        assertQueryMatches (1, "[background= Color.rgba(0,0,255,255)]", h);

        assertQueryMatches (0, "[background = #FF0000]", h);
        assertQueryMatches (0, "[background = #FF0]", h);
        assertQueryMatches (0, "[background = red]", h);
        assertQueryMatches (0, "[background = rgb(1,2,3)]", h);

        assertQueryMatches (1, "[background != #FF0000]", h);
        assertQueryMatches (1, "[background != #FF0]", h);
        assertQueryMatches (1, "[background != red]", h);
        assertQueryMatches (1, "[background != rgb(1,2,3)]", h);

        assertQueryMatches (0, ":not([background=#0000FF])", h);
        assertQueryMatches (0, ":not([background=#00F])", h);
        assertQueryMatches (0, ":not([background=blue])", h);
        assertQueryMatches (0, ":not([background=Color.blue])", h);
        assertQueryMatches (0, ":not([background=rgb(0,0,255)])", h);
        assertQueryMatches (0, ":not([background=Color.rgb(0,0,255)])", h);
        assertQueryMatches (0, ":not([background=rgba(0,0,255,255)])", h);
        assertQueryMatches (0, ":not([background = Color.rgba (0, 0, 255, 255)])", h);
    }

    public static class ComponentWithDate extends JComponent
    {
        private Calendar date;

        public ComponentWithDate ()
        {
            date = new GregorianCalendar (2010, Calendar.FEBRUARY, 10);
            date.set (Calendar.HOUR_OF_DAY, 12);
            date.set (Calendar.MINUTE, 45);
            date.set (Calendar.SECOND, 12);
        }

        public Date getDate ()
        {
            return date.getTime ();
        }
    }

    @Test
    public void convertedAttributeComparisons ()
    {
        final Calendar date = new GregorianCalendar (2010, Calendar.FEBRUARY, 10);
        date.set (Calendar.HOUR_OF_DAY, 12);
        date.set (Calendar.MINUTE, 45);
        date.set (Calendar.SECOND, 12);

        JComponent c = new ComponentWithDate ();
        assertEquals (date.getTime (), Attribute.getProperty (c, "date"));

        assertQueryMatches (1, "[date]", c);

        assertQueryMatches (1, "[date!='2009.12.01 12:45:12']", c);
        assertQueryMatches (1, "[date!= '2009.12.01 12:45:12']", c);
        assertQueryMatches (1, "[date !='2009.12.01 12:45:12']", c);
        assertQueryMatches (1, "[date != '2009.12.01 12:45:12']", c);

        assertQueryMatches (0, "[date='2009.12.01 12:45:12']", c);
        assertQueryMatches (0, "[date= '2009.12.01 12:45:12']", c);
        assertQueryMatches (0, "[date ='2009.12.01 12:45:12']", c);
        assertQueryMatches (0, "[date = '2009.12.01 12:45:12']", c);

        assertQueryMatches (1, "[date=2010.02.10 12:45:12]", c);
        assertQueryMatches (1, "[date= 2010.02.10 12:45:12]", c);
        assertQueryMatches (1, "[date =2010.02.10 12:45:12]", c);
        assertQueryMatches (1, "[date = 2010.02.10 12:45:12]", c);

        assertQueryMatches (1, "[date!=2009.12.01 12:45:12]", c);
        assertQueryMatches (1, "[date!= 2009.12.01 12:45:12]", c);
        assertQueryMatches (1, "[date !=2009.12.01 12:45:12]", c);
        assertQueryMatches (1, "[date != 2009.12.01 12:45:12]", c);

        assertQueryMatches (1, "[date>2009.12.01 12:45:12]", c);
        assertQueryMatches (1, "[date> 2009.12.01 12:45:12]", c);
        assertQueryMatches (1, "[date >2009.12.01 12:45:12]", c);
        assertQueryMatches (1, "[date > 2009.12.01 12:45:12]", c);

        assertQueryMatches (1, "[date>\"2009.12.01 12:45:12\"]", c);
        assertQueryMatches (1, "[date> \"2009.12.01 12:45:12\"]", c);
        assertQueryMatches (1, "[date >\"2009.12.01 12:45:12\"]", c);
        assertQueryMatches (1, "[date > \"2009.12.01 12:45:12\"]", c);

        assertQueryMatches (0, "[date<\"2009.12.01 12:45:12\"]", c);
        assertQueryMatches (0, "[date< \"2009.12.01 12:45:12\"]", c);
        assertQueryMatches (0, "[date <\"2009.12.01 12:45:12\"]", c);
        assertQueryMatches (0, "[date < \"2009.12.01 12:45:12\"]", c);
    }

    @Test
    public void positionSelector ()
    {
        HPanel h = new HPanel ().as ("h1");
        h.add (new HPanel ().as ("h2"));
        h.add (new HPanel ().as ("h3"));
        h.add (new HPanel ().as ("h4"));

        assertQueryMatches (".HPanel:first", h, "h1");
        assertQueryMatches (".HPanel:last", h, "h4");

        assertQueryMatches (".HPanel:lt(0)", h);
        assertQueryMatches (".HPanel:lt(1)", h, "h1");
        assertQueryMatches (".HPanel:lt(2)", h, "h1", "h2");
        assertQueryMatches (".HPanel:lt(4)", h, "h1", "h2", "h3", "h4");

        assertQueryMatches (".HPanel:gt(4)", h);
        assertQueryMatches (".HPanel:gt(0)", h, "h2", "h3", "h4");
        assertQueryMatches (".HPanel:gt(2)", h, "h4");

        assertQueryMatches (".HPanel:eq(2)", h, "h3");
        assertQueryMatches (".HPanel:nth(0)", h, "h1");

        assertQueryMatches (".HPanel:range(0,2)", h, "h1", "h2");
        assertQueryMatches (".HPanel:range(1,2)", h, "h2");
        assertQueryMatches (".HPanel:range(2, 5)", h, "h3", "h4");

        assertQueryMatches (".HPanel:even", h, "h1", "h3");
        assertQueryMatches (".HPanel:odd", h, "h2", "h4");

        assertQueryMatches (".HPanel:lt(3):gt(1)", h, "h3");
    }

    @Test
    public void childCombinatorAndFilter ()
    {
        HPanel h = new HPanel ().as ("h1");
        h.add (new HLabel ().as ("h2"));
        h.add (new HLabel ().as ("h3"));

        assertQueryMatches ("JPanel > JLabel:eq(1)", h, "h3");
    }

    @Test
    public void childCombinatorAndFilters ()
    {
        HPanel h = new HPanel ().as ("1");
        HPanel h1 = new HPanel ().as ("1.1").add (new HLabel ().as ("1.1.1"))
                .add (new HLabel ().as ("1.1.2"));
        HPanel h2 = new HPanel ().as ("1.2").add (new HLabel ().as ("1.2.1"));

        h.add (h1).add (h2).add (new HLabel ().as ("1.3"));

        assertQueryMatches ("JPanel", h, "1", "1.1", "1.2");

        assertQueryMatches ("JPanel:eq(1)", h, "1.1");
        assertQueryMatches (".HPanel:eq(1)", h, "1.1");

        assertQueryMatches ("JPanel > JPanel:eq(1)", h, "1.1");
        assertQueryMatches ("JPanel > .HPanel:eq(1)", h, "1.1");

        assertQueryMatches ("JPanel > .HPanel:eq(1) > JLabel", h, "1.1.1", "1.1.2");
        assertQueryMatches ("JPanel.HPanel:eq(0) > JPanel.HPanel:eq(1) > JLabel.HLabel:last-child", h, "1.1.2");
    }

    @Test
    public void filtersInComplexQuery ()
    {
        HPanel h = new HPanel ().as ("h1");
        h.add (new HPanel ().as ("h2"));
        h.add (new HPanel ().as ("h3"));
        h.add (new HPanel ().as ("h4"));

        assertQueryMatches (".HPanel:eq(0) > .HPanel:eq(1)", h, "h2");
        assertQueryMatches (".HPanel:eq(0) > .HPanel:eq(2)", h, "h3");
    }

    @Test
    public void childSelectors ()
    {
        HPanel h = new HPanel ().as ("h1");
        h.add (new HPanel ().as ("h2").add (new HPanel ().as ("h6")));
        h.add (new HPanel ().as ("h3"));
        h.add (new HPanel ().as ("h4"));
        h.add (new HPanel ().as ("h5"));

        assertQueryMatches ("#h1 > .HPanel:last-child", h, "h5");
        assertQueryMatches ("#h1 > .HPanel:first-child", h, "h2");

        assertQueryMatches ("#h1 > .HPanel:nth-child(1)", h, "h2"); // indices start at 1
        assertQueryMatches ("#h1 > .HPanel:nth-child(2)", h, "h3");

        assertQueryMatches ("#h1 > .HPanel:nth-child(odd)", h, "h2", "h4");
        assertQueryMatches ("#h1 > .HPanel:nth-child(even)", h, "h3", "h5");

        assertQueryMatches ("#h1 > .HPanel:nth-child(2n)", h, "h3", "h5"); // even
        assertQueryMatches ("#h1 > .HPanel:nth-child(3n)", h, "h4"); // every 3rd

        assertQueryMatches ("#h1 > .HPanel:nth-child(2n+1)", h, "h2", "h4"); // odd

        assertQueryMatches ("#h1 > .HPanel:nth-child(-n+2)", h, "h2", "h3"); // first 2
        assertQueryMatches ("#h1 > .HPanel:nth-child( - n  + 2 )", h, "h2", "h3");

        assertQueryMatches (".HPanel:nth-child(1)", h, "h2", "h6");
        assertQueryMatches (".HPanel:only-child", h, "h6");
        assertQueryMatches (":nth-child(odd)", h, "h2", "h4", "h6");
    }

    @Test
    public void multipleChildSelectors ()
    {
        HPanel h = new HPanel ().as ("h1");
        h.add (new HPanel ().as ("h2").add (new HPanel ().as ("h6")));
        h.add (new HPanel ().as ("h3").add (new HPanel ().as ("h7")));
        h.add (new HPanel ().as ("h4"));
        h.add (new HPanel ().as ("h5"));

        assertQueryMatches (":first-child:last-child", h, "h6", "h7");
        assertQueryMatches (":only-child", h, "h6", "h7");
        assertQueryMatches (":first-child:last-child:only-child", h, "h6", "h7");

        assertQueryMatches (".HPanel:first-child > .HPanel#h6:only-child", h, "h6");
        assertQueryMatches (".HPanel:nth-child(1) > :only-child", h, "h6");

        assertQueryMatches (".HPanel:nth-child(2) > :only-child", h, "h7");
        assertQueryMatches (".HPanel:nth-child(2) > .HPanel", h, "h7");
        assertQueryMatches (".HPanel:nth-child(2) > .HPanel:last-child:first-child", h, "h7");
        assertQueryMatches (".HPanel:nth-child(2) > .HPanel:only-child", h, "h7");

        assertQueryMatches ("JPanel.HPanel[name^=h] > JPanel.HPanel[name^=h] > JPanel.HPanel[name^=h]:first-child:last-child:only-child", h, "h6", "h7");
    }

    @Test
    public void complexMatchingAndNotMatchingCombinationOfNots ()
    {
        HPanel div = div ("theid").addStyle ("class test");
        assertQueryMatches (".div:not(#other).class:not(.test).test#theid#theid", div);
    }

    @Test
    public void multipleFilters ()
    {
        HPanel h = new HPanel ().as ("h1");
        h.add (new HPanel ().as ("h2").add (new HPanel ().as ("h4")));
        h.add (new HPanel ().as ("h3").add (new HPanel ().as ("h5")));

        assertQueryMatches (".HPanel:eq(1) JPanel:only-child", h, "h4");

        assertQueryMatches (".HPanel:gt(0) JPanel:only-child", h, "h4", "h5");
        assertQueryMatches (".HPanel:gt(0) > #h5", h, "h5");

        assertQueryMatches (".HPanel:eq(0) > .HPanel:eq(2) > .HPanel:eq(4)", h, "h5");

        assertQueryMatches (":eq(0)", h, "h1");
        assertQueryMatches (":eq(0) :eq(1) :gt(2)", h, "h4");

        assertQueryMatches (":nth-child(1) :nth-child(1)", h, "h4");

        assertQueryMatches ("JPanel.HPanel:gt(0) JPanel.HPanel:first-child:last-child:only-child", h, "h4", "h5");
        assertQueryMatches ("JPanel.HPanel:eq(2) JPanel.HPanel#h5:first-child:last-child:only-child", h, "h5");
    }

    @Test
    public void heavyDuty ()
    {
        HPanel h = new HPanel ().as ("h1");
        JPanel p = new JPanel ();
        p.setName ("p");

        h.add (new HPanel ().as ("h2")).add (p);

        HPanel h3 = new HPanel ().as ("h3");
        p.add (h3.asComponent ());

        HLabel l1 = new HLabel ("Bonjour").as ("l1");
        HLabel l2 = new HLabel ("Hello").as ("l2").visible (false);
        HLabel l3 = new HLabel ("Hello").as ("l3").visible (false);
        HLabel l4 = new HLabel ("Hello").as ("4");
        HLabel l5 = new HLabel ("Ola").as ("l5");

        h3.add (new HPanel ().as ("h4").add (l1).add (new HPanel ().as ("h9")));
        JPanel p2 = new JPanel ();
        p2.setName ("h5");
        p2.add (l2.asComponent ());
        h3.add (p2);
        h3.add (new HPanel ().as ("h6").add (new HPanel ().as ("h10").add (l3)));
        h3.add (new HPanel ().as ("h7").add (l4));
        h3.add (new HPanel ().as ("h8").add (l5));
        h3.add (new HPanel ().as ("h11").add (new HPanel ().as ("h12")));

        /*
        .HPanel#h1
           .HPanel#h2
           #p
              .HPanel#h3
                 .HPanel#h4
                    l1
                    .HPanel#h9
                 JPanel#h5
                    l2 
                 .HPanel#h6
                    .HPanel#h10
                       l3 
                 .HPanel#h7
                    4 
                 .HPanel#h8
                    l5
                 .HPanel#h11
                    .HPanel#h12
        */

        assertQueryMatches ("JLabel", h, "l1", "l2", "l3", "4", "l5");
        assertQueryMatches ("JLabel[text^=Hel]", h, "l2", "l3", "4");

        assertQueryMatches (".HPanel JLabel", h, "l1", "l2", "l3", "4", "l5");
        assertQueryMatches (".HPanel JLabel[text^=Hel]", h, "l2", "l3", "4");

        assertQueryMatches ("#h6 JLabel", h, "l3");
        assertQueryMatches (".HPanel #p #h3 #h6 #h10 JLabel", h, "l3");

        assertQueryMatches ("#h1 JLabel", h, "l1", "l2", "l3", "4", "l5");
        assertQueryMatches (":not(.HPanel) :not(.HPanel) JLabel", h, "l2");

        assertQueryMatches (".HPanel + .HPanel", h, "h7", "h8", "h11");
        assertQueryMatches (".HPanel + .HPanel JLabel", h, "4", "l5");
        assertQueryMatches (".HPanel + .HPanel JPanel", h, "h12");

        assertQueryMatches (".HPanel + .HPanel #l3", h);
        assertQueryMatches (".HPanel + .HPanel #4", h, "4");

        assertQueryMatches (".HPanel + .HPanel + .HPanel", h, "h8", "h11");
        assertQueryMatches (".HPanel + .HPanel + .HPanel + .HPanel", h, "h11");

        assertQueryMatches ("#h11 > #h12", h, "h12");
        assertQueryMatches ("#h3 > #h12", h);

        assertQueryMatches (".HPanel + .HPanel > #h12", h, "h12");
        assertQueryMatches (".HPanel + .HPanel JLabel[text^=Hel]", h, "4");

        assertQueryMatches (":not(.HPanel) + .HPanel > * > JLabel", h, "l3");

        assertQueryMatches ("JPanel ~ JPanel", h, "h5", "p", "h6", "h7", "h8", "h11");
        assertQueryMatches (".HPanel ~ .HPanel", h, "h6", "h7", "h8", "h11");
        
        assertQueryMatches ("JPanel:not(.HPanel) ~ .HPanel", h, "h6", "h7", "h8", "h11");
        assertQueryMatches (".HPanel ~ JPanel:not(.HPanel)", h, "h5", "p");
        assertQueryMatches ("JPanel:not(.HPanel) ~ JPanel:not(.HPanel)", h);

        assertQueryMatches ("JPanel.HPanel#h1[name^=h] > JPanel:not(.HPanel):nth-child(2) JLabel.HLabel[name^=l][visible!=true][text*=Hello]:only-child:first-child:last-child", h, "l2", "l3");

        assertQueryMatches (".HPanel:eq(10)", h, "h12");
        assertQueryMatches (".HPanel:eq(6)", h, "h8");

        assertQueryMatches (".HPanel + .HPanel:eq(6)", h, "h8");
    }

    @Test
    public void notAndFilters ()
    {
        HPanel h = new HPanel ().as ("h1");
        h.add (new HPanel ().as ("h2"));
        h.add (new HPanel ().as ("h3").add (new HLabel ().as ("h5")));
        h.add (new HPanel ().as ("h4"));

        assertQueryMatches (":first-child", h, "h2", "h5");
        assertQueryMatches ("#h1 > .HPanel:not(:first-child)", h, "h3", "h4");
        assertQueryMatches ("#h1 > .HPanel:not(:first-child) :not(.HPanel):only-child", h, "h5");
    }

    @Test
    public void nonDeterministicMatchingOfDescendantAndChildCombinators ()
    {
        HPanel h = new HPanel ().add (blockquote ().add (div ().add (div ().add (p ("h1")))));
        assertQueryMatches (".blockquote > .div .p", h, "h1");
    }

    @Test
    public void idCollision ()
    {
        HPanel h = new HPanel ().as ("h1");
        h.add (new HPanel ().as ("h2").add (new HLabel ().as ("h1")));

        assertQueryMatches ("#h1", h, "h1", "h1");

        assertQueryMatches ("JPanel#h1", h, "h1");
        assertQueryMatches (".HPanel#h1", h, "h1");

        assertQueryMatches ("JLabel#h1", h, "h1");
        assertQueryMatches (".HLabel#h1", h, "h1");
    }

    @Test
    public void defaultCollector()
    {
    	Query q = new Query ("");
    	assertThat (q.collector(), is (FullHierarchyCollector.class));
    }
    
    @Test
    public void fullHierarchyFlattening()
    {
    	HPanel h = new HPanel ().as ("h1");
        h.add (new HPanel ().as ("h2"));
        h.add (new HPanel ().as ("h3").add (new HLabel ().as ("h5")));
        h.add (new HPanel ().as ("h4"));
    	
    	FullHierarchyCollector collector = new FullHierarchyCollector();
    	assertEquals (5, collector.collect (h.asComponent()).size());
    }
    
    @Test // collectors should really be generators, the flattened component list should be removed too
    public void filteredHierarchyFlattening()
    {
    	HPanel h = new HPanel ().as ("h1");
        h.add (new HPanel ().as ("h2"));
        h.add (new HPanel ().as ("h3").add (new HLabel ().as ("h5")));
        h.add (new HPanel ().as ("h4"));
    	
    	FilteringCollector collector = new FilteringCollector();
    	collector.init (new Query (".HLabel"));
    	assertEquals (1, collector.collect (h.asComponent()).size());
    	assertListContainsNames (collector.collect (h.asComponent()), "h5");
    }
    
    @Test
    public void anonymousClassMatching()
    {
        HPanel h = new HPanel ().as ("h");
        JPanel p = new JPanel() {};
        p.setName ("p");
        h.add (p);
        
        assertQueryMatches ("JPanel", h, "h", "p");
    }
    
    // should "JComponent" actually match every component that is an instanceof JComponent ?
    
    // selectors should probably be case insensitive ?
    // :contains ? seulement pour certains elements par ex
    //  propri�t�s/pseudoclasses/etc qui d�pendent du type d'�l�ments ? comme :checked pour les checkbox, ou :contains pour les text elements
    // virtual properties ? implementees avec les client properties ?
    // :enabled et :disabled
    // "unroller" des s�lecteurs avec leur �quivalent ? genre la :disabled c'est �quivalent � [enabled=false]
    // pseudo elements comme ::before
    // pseudo elements swing pour par exemple r�cuperer toutes les lignes d'une table, ou d'un textfield
    // :root ? filtre qui fait un test sur egal au premier des elements ds la liste des composants
    // ::before
    
    // compiler les queries
	// filter les resultats a la place de tout collecter durant la premiere phase
    
    // creer des profiles pour representer les selecteurs acceptes ds: CSS1, CSS2, CSS3, la version etendue pour nous, les selecteurs ds jquery
    // calculer la specificite des selecteurs ?
    // constantes java ?
    // namespaces css ? 
    
    // finir le color converter, l'extraire en code reutilisable pr parser les fonctions et genre faire un example de vec, vec2, vec3 etc
    // copier bcp des tests d'ici ds hquerytest
    
    // 1) ca serait bien de pvr utiliser l'attribute selector pour les client properties
    // 2) ca serait bien de pvr faire des requetes sur les hcomponent (et pour �a utiliser le 1, avec une facon unifiee de stocker les properties
    //   dans les JComponent délégués
    // -> l'attribute selector verifiera donc: 1) le get, 2) la pr�sence d'un token de la classe HComponent + la property, 3) la presence de la 
    //   client property telle quelle?

}
