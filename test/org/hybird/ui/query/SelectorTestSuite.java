package org.hybird.ui.query;

import org.hybird.ui.AbstractTest;
import org.hybird.ui.tk.HButton;
import org.hybird.ui.tk.HLabel;
import org.hybird.ui.tk.HPanel;
import org.junit.Before;
import org.junit.Test;

// "Port" of a supported subset of the test suite located at http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/index.html
public class SelectorTestSuite extends AbstractTest
{
    private HPanel body;
    
    @Before
    public void setUp()
    {
        body = styledHPanel ("body", "body");
    }
    
    public void assertQueryMatches (String query, String... names)
    {
        assertQueryMatches (query, body, names);
    }
    
    @Test // #1 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-1.html
    public void groupsOfSelectors()
    {
        HPanel ul = ul ("h1");
        
        ul.add (li ("h2"))
          .add (li ("h3"));
        
        body.add (ul);
        body.add (p ("h4"));
        
        assertQueryMatches (".ul,.li", "h1", "h2", "h3");
        assertQueryMatches (".ul, .li", "h1", "h2", "h3");
        
        assertQueryMatches (".ul, .p", "h1", "h4");
        assertQueryMatches (".ul,.p", "h1", "h4");
        
        assertQueryMatches (".li, .p", "h2", "h3", "h4");
        assertQueryMatches (".ul, .p, .li", "h1", "h2", "h3", "h4");
        
        assertQueryMatches (".body, .ul, .p, .li", "body", "h1", "h2", "h3", "h4");
        assertQueryMatches (".body,.ul,.p,.li", "body", "h1", "h2", "h3", "h4");
    }
    
    @Test // #2 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-2.html
    public void typeElementSelectors()
    {
        body.add (new HPanel().as ("h1"));
        body.add (new HLabel().as ("h2"));
        body.add (new HPanel().as ("h3"));
        
        assertQueryMatches ("JLabel", "h2");
    }
    
    @Test // #3a - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-3a.html
    public void universalSelector()
    {
        HPanel p = p ("h1");
        body.add (p);
        
        p.add (span ("h2").addStyle ("t1"));
        
        HPanel li = li ("h4").addStyle ("t1");
        body.add (ul ("h3").add (li));
        
        assertQueryMatches ("*", "body", "h1", "h2", "h3", "h4");
        assertQueryMatches (".ul, .p", "h1", "h3");
        assertQueryMatches ("*.t1","h2", "h4");
    }
    
    @Test // #4 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-4.html
    public void omittedUniversalSelector()
    {
        body.add (p ("foo"));
        
        assertQueryMatches ("#foo", "foo");
        assertQueryMatches (".p", "foo");
    }
    
    @Test // #5 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-5.html
    // The attribute existence in CSS applies to its value as well, if it's set manually in HTML, then it will "exist"
    //  Here we check 1) if the (bean) property exists 2) if the JComponent client property is set
    public void attributeExistenceSelector()
    {
        body.group ("p").add (new JPanelWithTitle ("h1", "title1"));
        body.add (p ("h2").property ("title", "title2"));
        body.add (p ("h3"));
        body.add (new JPanelWithTitle ("h4", "title3"));
        
        assertQueryMatches (".p", "h1", "h2", "h3");
        assertQueryMatches (".p[title]", "h1", "h2");
        assertQueryMatches ("[title]", "h1", "h2", "h4");
    }
 
    @Test // #6 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-6.html
    public void attributeValueSelector()
    {
        HPanel address = address ("h1").property ("title", "foo");
        body.add (address);
        
        address.add (span ("h2").property ("title", "b"));
        address.add (span ("h3").property ("title", "aa"));
        
        assertQueryMatches (".address", "h1");
        assertQueryMatches (".address[title='foo']", "h1");
        assertQueryMatches (".address[title=foo]", "h1");
        assertQueryMatches (".address[title=\"foo\"]", "h1");
        assertQueryMatches (".span[title=a]");
    }
    
    @Test // #7 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-7.html
          // #7b - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-7b.html
    public void attributeMultivalueSelector()
    {
        body.add (p ("h1").addStyle ("a b c"));
        
        HPanel address = address ("h2").property ("title", "tot foo bar");
        body.add (address);
        
        address.add (span ("h3").addStyle ("a c"));
        address.add (span ("h4").addStyle ("a bb c"));
        
        assertQueryMatches (".p", "h1");
        assertQueryMatches (".p[HTk.style ~= b]", "h1"); // using the internal client property is okay here because 
        // regular people won't usually query the style like this, but with .b
        
        assertQueryMatches (".address", "h2");
        assertQueryMatches (".address[title ~= foo]", "h2");
        
        assertQueryMatches ("span[class ~= b]"); // ~= is the 'contains word' operator, *= would have matched (being 'contains')
        
        body.add (p ("h5").property ("title", "hello world")); // #7b
        assertQueryMatches ("[title~='hello world']");
    }
    
    @Test // #8 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-8.html
    public void attributeValueSelectorsWithHyphenSeparatedAttributes()
    {
        body.add (p ("h1").property ("lang", "en-gb"));
        
        HPanel address = address ("h2").property ("lang", "fi");
        body.add (address);
        
        address.add (span ("h3").property ("lang", "en-us"));
        address.add (span ("h4").property ("lang", "en-fr"));
        
        assertQueryMatches (".p[lang|=en]", "h1");
        assertQueryMatches (".address[lang=fi]", "h2");
        assertQueryMatches (".span[lang|=en]", "h3", "h4");
        assertQueryMatches (".span[lang|=fr]");
    }
    
    @Test // #9 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-9.html
    // #10 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-10.html
    // #11 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-11.html
    public void substringMatchingAttributeSelector ()
    {
        body.add (p ("h1").property ("title", "foobar"));
        assertQueryMatches (".p[title^='foo']", "h1");
        assertQueryMatches (".p[title$=bar]", "h1");
        
        body.asComponent ().removeAll ();
        
        body.add (p ("h2").property ("title", "foobarufoo"));
        assertQueryMatches (".p[title*=\"bar\"]", "h2");
    }
    
    @Test // #13 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-13.html    
    public void classSelectors ()
    {
        HPanel ul = ul ("h1");
        body.add (ul);
        
        ul.add (new HLabel().as ("h2").style ("t1"));
        ul.add (new HPanel().as ("h3").style ("t2"));
        ul.add (new HPanel().as ("h4").style ("t2").add (new HLabel ("h5").style ("t33")));
        
        assertQueryMatches (".t1", "h2");
        assertQueryMatches ("JPanel.t2", "h3", "h4");
        assertQueryMatches (".t3");
    }
    
    @Test // #14 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-14.html
    public void moreThanOneClassSelector()
    {
        body.add (p ("h1").addStyle ("t1").addStyle ("t2"));
        body.add (div ("h2").addStyle ("test"));
        
        assertQueryMatches ("JPanel.t1", "h1");
        assertQueryMatches ("JPanel.t2", "h1");
        
        assertQueryMatches (".div", "h2");
        assertQueryMatches (".div.teST");
        assertQueryMatches (".div.te");
        assertQueryMatches (".div.st");
        assertQueryMatches (".div.te.st");
        
        assertQueryMatches ("JPanel.test", "h2");
        assertQueryMatches ("JPanel.teST");
        assertQueryMatches ("JPanel.te");
        assertQueryMatches ("JPanel.st");
        assertQueryMatches ("JPanel.te.st");
    }
    
    @Test // #14b - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-14b.html
    public void moreThanOneClassSelectorB()
    {
        body.add (p ("h1").addStyle ("t1"));
        body.add (p ("h2").addStyle ("t1 t2"));
        
        assertQueryMatches (".p", "h1", "h2");
        assertQueryMatches (".t1.fail");
        assertQueryMatches (".fail.t1");
        assertQueryMatches (".t2.fail");
        assertQueryMatches (".fail.t2");
    }
    
    @Test // #14c - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-14c.html
    public void moreThanOneClassSelectorC()
    {
        body.add (new HPanel ().as ("h1").style ("t1 t2")); // p
        body.add (new HButton ().as ("h2").style ("t3")); // div
        body.add (new HLabel ().as ("h3").style ("t4 t5 t6")); // address
        
        assertQueryMatches ("JPanel.t1.t2", "h1");
        assertQueryMatches ("JButton", "h2");
        assertQueryMatches ("JButton.t1");
        assertQueryMatches ("JLabel.t5.t5", "h3");
    }
    
    @Test // 14d - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-14d.html
    public void negatedMoreThanOneClassSelectorD()
    {
        body.add (p ("h1").style ("t1 t2"));
        
        assertQueryMatches (".t1", "h1");
        assertQueryMatches (".t2", "h1");
        
        assertQueryMatches (".t1:not(.t2)");
        assertQueryMatches (":not(.t2).t1");
        assertQueryMatches (".t2:not(.t1)");
        assertQueryMatches (":not(.t1).t2");
    }
    
    @Test // #14e - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-14e.html
    public void negatedMoreThanOneClassSelectorE()
    {
        body.add (p ("h1").addStyle ("t1 t2"));
        body.add (div ("h2").addStyle ("t3"));
        body.add (address ("h3").addStyle ("t4 t5 t6"));
        
        assertQueryMatches (".p:not(.t1):not(.t2)");
        assertQueryMatches (".div:not(.t1)", "h2");
        assertQueryMatches (".address:not(.t5):not(.t5)");
    }
    
    @Test // #15 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-15.html
    public void idSelectors()
    {
        HPanel ul = ul ("h1");
        body.add (ul);
        
        ul.add (li ("t1"));
        ul.add (li ("t2"));
        ul.add (li ("t3").add (span ("t44")));
        
        assertQueryMatches ("#t1", "t1");
        assertQueryMatches (".li#t2", "t2");
        assertQueryMatches (".li#t3", "t3");
        assertQueryMatches ("#t4");
    }
    
    @Test // #15b - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-15b.html
    public void multipleIdSelectors()
    {
        body.add (p ("test"));
        body.add (div ("pass"));
        
        assertQueryMatches ("#test#fail");
        assertQueryMatches ("#fail#test");
        assertQueryMatches ("#fail");
        assertQueryMatches ("#pass#pass", "pass");
    }
    
    @Test // #28 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-28.html
    public void nthChildPseudoClass()
    {
        HPanel ul = ul ();
        body.add (ul);
        
        ul.add (li ("h1"));
        ul.add (li ());
        ul.add (li ("h2"));
        ul.add (li ());
        ul.add (li ("h3"));
        ul.add (li ());
        
        assertQueryMatches (".ul > .li:nth-child(odd)", "h1", "h2", "h3");
        
        HPanel ol = ol ();
        body.add (ol);
        
        ol.add (li ());
        ol.add (li ("h4"));
        ol.add (li ());
        ol.add (li ("h5"));
        ol.add (li ());
        ol.add (li ("h6"));
        
        assertQueryMatches (".ol > .li:nth-child(even)", "h4", "h5", "h6");
        
        HPanel t1 = table ().addStyle ("t1");
        body.add (t1);
        
        t1.add (tr ("h7").add (td ()).add (td ()).add (td ()));
        t1.add (tr ("h8").add (td ()).add (td ()).add (td ()));
        t1.add (tr ("h9").add (td ()).add (td ()).add (td ()));
        t1.add (tr ("h10").add (td ()).add (td ()).add (td ()));
        t1.add (tr ().add (td ()).add (td ()).add (td ()));
        t1.add (tr ().add (td ()).add (td ()).add (td ()));
        
        assertQueryMatches (".table.t1 .tr:nth-child(-n+4)", "h7", "h8", "h9", "h10");
        
        HPanel t2 = table ().addStyle ("t2");
        body.add (t2);
        
        t2.add (tr ().add (td ("h11")).add (td ()).add (td ()).add (td ("h12")).add (td ()).add (td ()).add (td ("h13")).add (td ()));
        t2.add (tr ().add (td ("h14")).add (td ()).add (td ()).add (td ("h15")).add (td ()).add (td ()).add (td ("h16")).add (td ()));
        t2.add (tr ().add (td ("h17")).add (td ()).add (td ()).add (td ("h18")).add (td ()).add (td ()).add (td ("h19")).add (td ()));
        
        String matches = "";
        for (int i = 11; i <= 19; ++i)
            matches += "h" + i + (i != 19 ? " " : "");
        
        assertQueryMatches (".table.t2 .td:nth-child(3n+1)", matches.split (" "));
    }
    
    @Test // #32 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-32.html
    public void firstChildPseudoClass()
    {
        HPanel div = div ();
        body.add (div);
        
        HPanel table = table ().addStyle ("t1");
        div.add (table);
        
        table.add (tr ().add (td ("h1")).add (td ()).add (td ()));
        table.add (tr ().add (td ("h2")).add (td ()).add (td ()));
        table.add (tr ().add (td ("h3")).add (td ()).add (td ()));
        
        HPanel p = p ();
        body.add (p);
        
        p.add (span ("h4"));
        
        assertQueryMatches (".t1 .td:first-child", "h1", "h2", "h3");
        assertQueryMatches (".p > *:first-child", "h4");
    }
    
    @Test // #33 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-33.html
    public void lastChildPseudoClass()
    {
        HPanel div = div ();
        body.add (div);
        
        HPanel table = table ().addStyle ("t1");
        div.add (table);
        
        table.add (tr ().add (td ()).add (td ()).add (td ("h1")));
        table.add (tr ().add (td ()).add (td ()).add (td ("h2")));
        table.add (tr ().add (td ()).add (td ()).add (td ("h3")));
        
        HPanel p = p ();
        body.add (p);
        
        p.add (span ("h4"));
        
        assertQueryMatches (".t1 .td:last-child", "h1", "h2", "h3");
        assertQueryMatches (".p > *:last-child", "h4");
    }
    
    @Test // #36 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-36.html
    public void onlyChildPseudoClass()
    {
        body.add (p ());
        body.add (div ().add (p ("h1")));
        
        assertQueryMatches (".p:only-child", "h1");
    }
    
    @Test // #43 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-43.html
    public void descendantCombinator()
    {
        HPanel div = div ().addStyle ("t1");
        body.add (div);
        
        div.add (p ("h1"));
        div.add (table ().add (tbody ().add (tr ().add (td ().add (p ("h2"))))));
        
        body.add (table ().add (tbody ().add (tr ().add (td ().add (p (""))))));
        
        assertQueryMatches (".div.t1 .p", "h1", "h2");
    }
    
    @Test // #44 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-44.html
    public void childCombinator()
    {
        HPanel div = div ();
        body.add (div);
        
        div.add (p ("h1").addStyle ("red test"));
        div.add (div ().add (p ("h2").addStyle ("red test")));
        
        body.add (table ().add (tbody ().add (tr ().add (td ().add (p ().addStyle ("white test"))))));
        
        assertQueryMatches (".div > .p.test", "h1", "h2");
    }
    
    @Test // #44c - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-44c.html
    public void childCombinatorAndClasses()
    {
        body.add (div ());
        body.add (div ("h1").addStyle ("control"));
        
        assertQueryMatches (".fail > .div");
        assertQueryMatches (".control", "h1");
    }
    
    @Test // #44d - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-44d.html
    public void childCombinatorAndIds()
    {
        body.add (div ());
        body.add (p ("h1"));
        
        assertQueryMatches ("#fail > .div");
        assertQueryMatches (".p", "h1");
    }
    
    @Test // #45 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-45.html
    public void directAdjacentCombinator()
    {
        HPanel div = div ().addStyle ("stub");
        body.add (div);
        
        div.add (p ());
        div.add (p ("h1"));
        div.add (p ("h2"));
        div.add (address ());
        div.add (p ());
        
        assertQueryMatches (".div.stub > .p + .p", "h1", "h2");
    }
    
    @Test // #45c - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-45c.html
    public void directAdjacentCombinatorAndClasses()
    {
        body.add (div ());
        body.add (div ("h1").addStyle ("control"));
        
        assertQueryMatches (".fail + .div");
        assertQueryMatches (".control", "h1");
    }
    
    @Test // #46 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-46.html
    public void indirectAdjacentCombinator()
    {
        HPanel div = div ().addStyle ("stub");
        body.add (div);
        
        div.add (p ());
        div.add (p ("h1"));
        div.add (p ("h2"));
        div.add (address ());
        div.add (p ("h3"));
        
        assertQueryMatches (".div.stub > .p ~ .p", "h1", "h2", "h3");
    }
    
    @Test // #54 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-54.html
    public void negatedSubstringMatchingAttributeSelectorOnBeginning()
    {
        HPanel div = div ().addStyle ("stub");
        body.add (div);
        
        div.add (p ("h1"));
        div.add (p ("h2").property ("title", "on chante?"));
        div.add (p ().property ("title", "si on chantait").add (span ("h3").property ("title", "si il chantait")));
        
        assertQueryMatches (".div.stub *:not([title^='si on'])", "h1", "h2", "h3");
    }
    
    @Test // #55 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-55.html
    // #56 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-56.html
    public void negatedSubstringMatchingAttributeSelectorOnEnd()
    {
        HPanel div = div ().addStyle ("stub");
        body.add (div);
        
        div.add (p ("h1"));
        div.add (p ("h2").property ("title", "on chante?"));
        div.add (p ().property ("title", "si on chantait").add (span ("h3").property ("title", "si il chante")));
        
        assertQueryMatches (".div.stub *:not([title$='tait'])", "h1", "h2", "h3");
        assertQueryMatches (".div.stub *:not([title*=' on'])", "h1", "h2", "h3");
    }
    
    @Test // #59 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-59.html
    public void negatedClassSelector()
    {
        HPanel div = div ().addStyle ("stub");
        body.add (div);
        
        div.add (p ("h1"));
        div.add (p ("h2").addStyle ("bar foofoo tut"));
        div.add (p ().addStyle ("bar foo tut").add (span ("h3").addStyle ("tut foo2")));
        
        assertQueryMatches (".div.stub *:not(.foo)", "h1", "h2", "h3");
    }
    
    @Test // #60 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-60.html
    public void negatedIdSelector()
    {
        HPanel div = div ().addStyle ("stub");
        body.add (div);
        
        div.add (p ("h1"));
        div.add (p ("foo2"));
        div.add (p ("foo").add (span ("h3")));
        
        assertQueryMatches (".div.stub *:not(#foo)", "h1", "foo2", "h3");
    }
    
    @Test // #73 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-73.html
    public void negatedNthChildPseudoClass()
    {
        HPanel ul = ul ();
        body.add (ul);
        
        ul.add (li ());
        ul.add (li ("h1"));
        ul.add (li ());
        ul.add (li ("h2"));
        ul.add (li ());
        ul.add (li ("h3"));
        
        assertQueryMatches (".ul > .li:not(:nth-child(odd))", "h1", "h2", "h3");
        
        HPanel ol = ol ();
        body.add (ol);
        
        ol.add (li ("h4"));
        ol.add (li ());
        ol.add (li ("h5"));
        ol.add (li ());
        ol.add (li ("h6"));
        ol.add (li ());
        
        assertQueryMatches (".ol > .li:not(:nth-child(even))", "h4", "h5", "h6");
        
        HPanel t1 = table ().addStyle ("t1");
        body.add (t1);
        
        t1.add (tr ().add (td ()).add (td ()).add (td ()));
        t1.add (tr ().add (td ()).add (td ()).add (td ()));
        t1.add (tr ().add (td ()).add (td ()).add (td ()));
        t1.add (tr ().add (td ()).add (td ()).add (td ()));
        t1.add (tr ("h7").add (td ()).add (td ()).add (td ()));
        t1.add (tr ("h8").add (td ()).add (td ()).add (td ()));
        
        assertQueryMatches (".table.t1 .tr:not(:nth-child(-n+4))", "h7", "h8");
        
        HPanel t2 = table ().addStyle ("t2");
        body.add (t2);
        
        t2.add (tr ().add (td ()).add (td ("h9")).add (td ("h10")).add (td ()).add (td ("h11")).add (td ("h12")).add (td ()).add (td ("h13")));
        t2.add (tr ().add (td ()).add (td ("h14")).add (td ("h15")).add (td ()).add (td ("h16")).add (td ("h17")).add (td ()).add (td ("h18")));
        t2.add (tr ().add (td ()).add (td ("h19")).add (td ("h20")).add (td ()).add (td ("h21")).add (td ("h22")).add (td ()).add (td ("h23")));
        
        String matches = "";
        for (int i = 9; i <= 23; ++i)
            matches += "h" + i + (i != 23 ? " " : "");
        
        assertQueryMatches (".table.t2 .td:not(:nth-child(3n+1))", matches.split (" "));
    }
    
    @Test // #77 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-77.html
    public void negatedFirsthChildPseudoClass()
    {
        HPanel div = div ();
        body.add (div);
        
        HPanel table = table ().addStyle ("t1");
        div.add (table);
        
        table.add (tr ().add (td ()).add (td ("h1")).add (td ("h2")));
        table.add (tr ().add (td ()).add (td ("h3")).add (td ("h4")));
        table.add (tr ().add (td ()).add (td ("h5")).add (td ("h6")));
        
        HPanel p = p ();
        body.add (p);
        
        p.add (span ());
        
        assertQueryMatches (".t1 .td:not(:first-child)", "h1", "h2", "h3", "h4", "h5", "h6");
        assertQueryMatches (".p > *:not(:first-child)");
    }
    
    @Test // #78 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-78.html
    public void negatedLastChildPseudoClass()
    {
        HPanel div = div ();
        body.add (div);
        
        HPanel table = table ().addStyle ("t1");
        div.add (table);
        
        table.add (tr ().add (td ("h1")).add (td ("h2")).add (td ()));
        table.add (tr ().add (td ("h3")).add (td ("h4")).add (td ()));
        table.add (tr ().add (td ("h5")).add (td ("h6")).add (td ()));
        
        HPanel p = p ();
        body.add (p);
        
        p.add (span ());
        
        assertQueryMatches (".t1 .td:not(:last-child)", "h1", "h2", "h3", "h4", "h5", "h6");
        assertQueryMatches (".p > *:not(:last-child)");
    }
    
    @Test // #81 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-81.html
    public void negatedOnlyChildPseudoClass()
    {
        body.add (p ("h1"));
        body.add (div ().add (p ()));
        
        assertQueryMatches (".p:not(:only-child)", "h1");
    }
    
    @Test // #83 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-83.html
    public void negationPseudoClassCannnotBeAnArgumentOfItself()
    {
        body.add (p());
        assertQueryMatches ("p:not(:not(p))");
    }
    
    @Test // #86 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-86.html
    public void nonDeterministicMatchingOfDescendantAndChildCombinators()
    {
        body.add (blockquote().add (div().add (div().add (p ("h1")))));
        assertQueryMatches (".blockquote > .div .p", "h1");
    }
    
    @Test // #87 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-87.html
    public void nonDeterministicMatchingOfDirectAndIndirectAdjacentCombinators()
    {
        body.add (blockquote().add (div()));
        body.add (div());
        body.add (div());
        body.add (p ("h1"));
        assertQueryMatches (".blockquote + .div ~ .p", "h1");
    }
    
    @Test // #88 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-88.html
    public void nonDeterministicMatchingOfDirectAndDirectAdjacentCombinators()
    {
        body.add (blockquote().add (div()));
        body.add (div().add (div().add (p ("h1"))));
        assertQueryMatches (".blockquote + .div .p", "h1");
    }
    
    @Test // #89 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-89.html
    public void simpleCombinationOfDescendantAndChildCombinators()
    {
        body.add (blockquote().add (div().add (div().add (p ("h1")))));
        assertQueryMatches (".blockquote .div > .p", "h1");
    }
    
    @Test // #90 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-90.html
    public void simpleCombinationOfDirectAndIndirectAdjacentCombinators()
    {
        body.add (blockquote().add (div()));
        body.add (div());
        body.add (div());
        body.add (p ("h1"));
        assertQueryMatches (".blockquote ~ .div + .p", "h1");
    }
    
    // Maybe improve the error handling with tests #154-158
    
    @Test // #170 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-170.html
    // #170a - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-170a.html
    // #170b - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-170b.html
    public void longChainOfSelectors()
    {
        body.add (p().add (span ("h1")));
        
        String query = "";
        for (int i = 0; i <= 50; ++i)
            query += ".span" + (i != 50 ? ", " : "");
        
        assertQueryMatches (query, "h1");
        
        query = "";
        for (int i = 0; i <= 50; ++i)
            query += ".span";
        assertQueryMatches (query, "h1");
    }
    
    @Test // #170c - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-170c.html
    // #170d - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-170d.html
    public void longChainOfSelectors2()
    {
        body.add (p ("h1"));
        
        assertQueryMatches (".p:not(.span):not(.span):not(.span):not(.span):not(.span):not(.span)", "h1");
        assertQueryMatches (".p:first-child:first-child:first-child:first-child:first-child:first-child:first-child:first-child:first-child:first-child:first-child", "h1");
    }
    
    @Test // #176 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-176.html
    public void combinationsClassesAndIds()
    {
        body.add (p ("id").addStyle ("class test"));
        body.add (div ("theid").addStyle ("class test"));
        
        assertQueryMatches (".p:not(#other).class:not(.fail).test#id#id", "id");
        assertQueryMatches (".div:not(#theid).class:not(.fail).test#theid#theid");
        assertQueryMatches (".div:not(#other).notclass:not(.fail).test#theid#theid");
        assertQueryMatches (".div:not(#other).class:not(.test).test#theid#theid");
        assertQueryMatches (".div:not(#other).class:not(.fail).nottest#theid#theid");
        assertQueryMatches (".div:not(#other).class:not(.fail).nottest#theid#other");
    }
    
    @Test // #d2 - http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-d2.html
    public void staticVersionOfTestD2()
    {
        HPanel div = div ();
        body.add (div);

        div.add (p ());
        div.add (div ("stub"));
        div.add (div ("d1"));
        div.add (div ("d2").add (div ("d3").add (div ("d4")).add (div ("d5").add (div ("h1")))));

        assertQueryMatches ("#stub ~ .div", "d1", "d2");
        assertQueryMatches ("#stub ~ .div .div", "d3", "d4", "d5", "h1");
        assertQueryMatches ("#stub ~ .div .div + .div", "d5");
        assertQueryMatches ("#stub ~ .div .div + .div > .div", "h1");
    }
}
