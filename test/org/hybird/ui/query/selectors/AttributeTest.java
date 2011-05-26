package org.hybird.ui.query.selectors;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.hybird.conversion.Converter;
import org.hybird.ui.query.Query;
import org.hybird.ui.query.selectors.AttributeSelector.Attribute;
import org.hybird.ui.query.selectors.AttributeSelector.Attribute.Type;
import org.junit.BeforeClass;
import org.junit.Test;

public class AttributeTest
{
    @Test
    public void getValues ()
    {
        Dummy source = new Dummy (1);
        assertEquals (1, Attribute.getProperty (source, "i"));
        
        assertEquals ("id", Attribute.getProperty (component1, "name"));
        assertEquals (10, Attribute.getProperty (component1, "x"));
        assertEquals (0.5f, Attribute.getProperty (component2, "alignmentX"));
    }

    @Test
    public void operatorsEqual ()
    {
        Dummy source = new Dummy (1);

        Attribute selector = new Attribute ("i", Attribute.Type.EQUAL_TO);
        assertTrue (selector.matches (source, 1));
        assertTrue (selector.matches (source, "1"));
        assertTrue (selector.matches (source, "1.0"));
        assertTrue (selector.matches (source, (char) 1));
        assertFalse (selector.matches (source, 2));
        assertFalse (selector.matches (source, "1.2"));

        selector = new Attribute ("i", Type.NOT_EQUAL_TO);
        assertTrue (selector.matches (source, 2));
        assertTrue (selector.matches (source, "2"));
        assertFalse (selector.matches (source, 1));
        
        source = new Dummy ('a');
        selector = new Attribute ("c", Type.EQUAL_TO);
        assertTrue (selector.matches (source, 'a'));
        assertTrue (selector.matches (source, "a"));
        assertFalse (selector.matches (source, "ab"));
        
        source = new Dummy (1.3);
        selector = new Attribute ("o", Type.EQUAL_TO);
        assertTrue (selector.matches (source, 1.3));
        assertTrue (selector.matches (source, 1.3f));
        assertTrue (selector.matches (source, "1.3"));
        assertFalse (selector.matches (source, "1"));
        assertFalse (selector.matches (source, "ab"));
        assertFalse (selector.matches (source, "1.2"));
        
        source = new Dummy (new Float (1.3f));
        selector = new Attribute ("o", Type.EQUAL_TO);
        assertTrue (selector.matches (source, 1.3));
        assertTrue (selector.matches (source, 1.3f));
        assertTrue (selector.matches (source, "1.3"));
        
        source = new Dummy (1.0);
        selector = new Attribute ("o", Type.EQUAL_TO);
        assertTrue (selector.matches (source, "1"));
        
        source = new Dummy (1f);
        selector = new Attribute ("f", Type.EQUAL_TO);
        assertTrue (selector.matches (source, 1));
        assertTrue (selector.matches (source, (char) 1));
        assertTrue (selector.matches (source, 1l));
        assertTrue (selector.matches (source, (byte) 1));
        assertTrue (selector.matches (source, (short) 1));
        assertTrue (selector.matches (source, 1f));
        assertTrue (selector.matches (source, 1.0));
        assertTrue (selector.matches (source, "1.0"));
        assertTrue (selector.matches (source, "1"));
        assertFalse (selector.matches (source, "ab"));
        assertFalse (selector.matches (source, "1.2"));
        
        source = new Dummy (new Long (Long.MAX_VALUE));
        selector = new Attribute ("o", Type.EQUAL_TO);
        assertTrue (selector.matches (source, "" + Long.MAX_VALUE));
        
        source = new Dummy (new Long (1));
        selector = new Attribute ("o", Type.EQUAL_TO);
        assertTrue (selector.matches (source, "1"));
        assertTrue (selector.matches (source, "1.0"));
        
        source = new Dummy (new Short ((short) 1));
        selector = new Attribute ("o", Type.EQUAL_TO);
        assertTrue (selector.matches (source, 1));
        assertTrue (selector.matches (source, 1f));
        assertTrue (selector.matches (source, 1.0));
        assertTrue (selector.matches (source, "1"));
        assertTrue (selector.matches (source, "1.0"));
        
        source = new Dummy (new Byte ((byte) 1));
        selector = new Attribute ("o", Type.EQUAL_TO);
        assertTrue (selector.matches (source, 1));
        assertTrue (selector.matches (source, 1f));
        assertTrue (selector.matches (source, 1.0));
        assertTrue (selector.matches (source, "1"));
        assertTrue (selector.matches (source, "1.0"));
        
        source = new Dummy ("a");
        selector = new Attribute ("s", Type.EQUAL_TO);
        assertTrue (selector.matches (source, 'a'));
        assertTrue (selector.matches (source, "a"));
        assertFalse (selector.matches (source, "ab"));
        
        source = new Dummy ("1");
        selector = new Attribute ("s", Type.EQUAL_TO);
        assertTrue (selector.matches (source, 1));
        assertTrue (selector.matches (source, 1f));
        assertTrue (selector.matches (source, 1.0));
        assertTrue (selector.matches (source, '1'));
        assertTrue (selector.matches (source, "1"));
        assertFalse (selector.matches (source, "12"));
        
        source = new Dummy ("1.0");
        selector = new Attribute ("s", Type.EQUAL_TO);
        assertTrue (selector.matches (source, 1.0));
        assertTrue (selector.matches (source, 1.0f));
        assertTrue (selector.matches (source, "1.0"));
        assertFalse (selector.matches (source, 1.2));
    }

    @Test
    public void operatorsStartsWith ()
    {
        Dummy source = new Dummy ("abc");

        Attribute selector = new Attribute ("s", Type.EQUAL_TO);
        assertTrue (selector.matches (source, "abc"));

        selector = new Attribute ("s", Type.STARTS_WITH);
        assertTrue (selector.matches (source, "a"));
        assertTrue (selector.matches (source, "ab"));
        assertTrue (selector.matches (source, "abc"));
        assertFalse (selector.matches (source, "x"));

        selector = new Attribute ("s", Type.NOT_STARTS_WITH);
        assertFalse (selector.matches (source, "a"));
        assertFalse (selector.matches (source, "ab"));
        assertFalse (selector.matches (source, "abc"));
        assertTrue (selector.matches (source, "x"));
    }

    @Test
    public void operatorsContains ()
    {
        Dummy source = new Dummy ("abcd");

        Attribute selector = new Attribute ("s", Type.CONTAINS);
        assertTrue (selector.matches (source, "a"));
        assertTrue (selector.matches (source, "ab"));
        assertTrue (selector.matches (source, "abc"));
        assertTrue (selector.matches (source, "d"));
        assertFalse (selector.matches (source, "x"));
        assertFalse (selector.matches (source, "ad"));

        selector = new Attribute ("s", Type.NOT_CONTAINS);
        assertFalse (selector.matches (source, "a"));
        assertFalse (selector.matches (source, "ab"));
        assertFalse (selector.matches (source, "abc"));
        assertFalse (selector.matches (source, "d"));
        assertTrue (selector.matches (source, "x"));
        assertTrue (selector.matches (source, "ad"));
    }

    @Test
    public void operatorsEndsWith ()
    {
        Dummy source = new Dummy ("abc");

        Attribute selector = new Attribute ("s", Type.ENDS_WITH);
        assertTrue (selector.matches (source, "c"));
        assertTrue (selector.matches (source, "bc"));
        assertTrue (selector.matches (source, "abc"));
        assertFalse (selector.matches (source, "x"));

        selector = new Attribute ("s", Type.NOT_ENDS_WITH);
        assertFalse (selector.matches (source, "c"));
        assertFalse (selector.matches (source, "bc"));
        assertFalse (selector.matches (source, "abc"));
        assertTrue (selector.matches (source, "x"));
    }

    @Test
    public void selectorCanContainEverything ()
    {
        Dummy source = new Dummy ("abc");
        Dummy source2 = new Dummy ("xyzc");

        Attribute selector = new Attribute ("s", Type.EQUAL_TO, "abc");
        assertTrue (selector.matches (source));
        assertFalse (selector.matches (source2));

        selector = new Attribute ("s", Type.ENDS_WITH, "bc");
        assertTrue (selector.matches (source));
        assertFalse (selector.matches (source2));

        selector = new Attribute ("s", Type.ENDS_WITH, "c");
        assertTrue (selector.matches (source));
        assertTrue (selector.matches (source2));
    }

    @Test
    public void operatorSame ()
    {
        Object o = new Object ();
        Dummy source = new Dummy (o);
        Dummy source2 = new Dummy (new Object ());

        Attribute selector = new Attribute ("o", Type.SAME_AS, o);
        assertTrue (selector.matches (source));
        assertFalse (selector.matches (source2));

        selector = new Attribute ("o", Type.NOT_SAME_AS, o);
        assertFalse (selector.matches (source));
        assertTrue (selector.matches (source2));
    }

    @Test
    public void operatorBiggerThan ()
    {
        Dummy source = new Dummy (10);

        Attribute selector = new Attribute ("i", Type.GREATER_THAN);
        assertTrue (selector.matches (source, 1));
        assertTrue (selector.matches (source, 2L));
        assertTrue (selector.matches (source, 3.5));
        assertTrue (selector.matches (source, "3.5"));
        assertTrue (selector.matches (source, 4.2f));
        assertTrue (selector.matches (source, (byte) 5));
        assertTrue (selector.matches (source, (short) 6));
        assertTrue (selector.matches (source, "2"));
        assertFalse (selector.matches (source, 10));
        assertFalse (selector.matches (source, "13"));
        assertFalse (selector.matches (source, "" + Long.MAX_VALUE));

        selector = new Attribute ("i", Type.LESS_THAN_OR_EQUAL_TO);
        assertFalse (selector.matches (source, 1));
        assertFalse (selector.matches (source, 2L));
        assertFalse (selector.matches (source, 3.5));
        assertFalse (selector.matches (source, "3.5"));
        assertFalse (selector.matches (source, 4.2f));
        assertFalse (selector.matches (source, (byte) 5));
        assertFalse (selector.matches (source, (short) 6));
        assertTrue (selector.matches (source, 10));
        assertTrue (selector.matches (source, (char) 20));

        source = new Dummy (0.8f);
        selector = new Attribute ("f", Type.LESS_THAN_OR_EQUAL_TO);
        assertTrue (selector.matches (source, 0.8f));
        assertTrue (selector.matches (source, "0.8"));
        assertTrue (selector.matches (source, 1));
        assertTrue (selector.matches (source, 2L));
        assertTrue (selector.matches (source, 3.5));
        assertTrue (selector.matches (source, 4.2f));
        assertTrue (selector.matches (source, (byte) 5));
        assertTrue (selector.matches (source, (short) 6));
        assertTrue (selector.matches (source, (char) 20));
        assertFalse (selector.matches (source, -1));
        assertFalse (selector.matches (source, -5.3));
        assertFalse (selector.matches (source, "-5.3"));

        source = new Dummy (0.5);
        selector = new Attribute ("o", Type.LESS_THAN_OR_EQUAL_TO);
        assertTrue (selector.matches (source, 0.5));
        assertTrue (selector.matches (source, 1));
        assertTrue (selector.matches (source, "1"));
        assertTrue (selector.matches (source, 2L));
        assertTrue (selector.matches (source, 3.5));
        assertTrue (selector.matches (source, "3.5"));
        assertTrue (selector.matches (source, 4.2f));
        assertTrue (selector.matches (source, (byte) 5));
        assertTrue (selector.matches (source, (short) 6));
        assertTrue (selector.matches (source, (char) 20));
        assertFalse (selector.matches (source, -1));
        assertFalse (selector.matches (source, "-1"));
        assertFalse (selector.matches (source, -5.3));

        source = new Dummy (new Short ((short) 1));
        selector = new Attribute ("o", Type.LESS_THAN_OR_EQUAL_TO);
        assertTrue (selector.matches (source, 1));
        assertTrue (selector.matches (source, "1"));
        assertTrue (selector.matches (source, 2L));
        assertTrue (selector.matches (source, 3.5));
        assertTrue (selector.matches (source, 4.2f));
        assertTrue (selector.matches (source, "4.2"));
        assertTrue (selector.matches (source, (byte) 5));
        assertTrue (selector.matches (source, (short) 6));
        assertTrue (selector.matches (source, (char) 20));
        assertFalse (selector.matches (source, 0));
        assertFalse (selector.matches (source, -1));
        assertFalse (selector.matches (source, -5.3));

        source = new Dummy (new Byte ((byte) 1));
        selector = new Attribute ("o", Type.LESS_THAN_OR_EQUAL_TO);
        assertTrue (selector.matches (source, 1));
        assertTrue (selector.matches (source, 2L));
        assertTrue (selector.matches (source, "2"));
        assertTrue (selector.matches (source, 3.5));
        assertTrue (selector.matches (source, 4.2f));
        assertTrue (selector.matches (source, "4.2"));
        assertTrue (selector.matches (source, (byte) 5));
        assertTrue (selector.matches (source, (short) 6));
        assertTrue (selector.matches (source, (char) 20));
        assertFalse (selector.matches (source, 0));
        assertFalse (selector.matches (source, -1));
        assertFalse (selector.matches (source, -5.3));

        source = new Dummy (new Long (1));
        selector = new Attribute ("o", Type.LESS_THAN_OR_EQUAL_TO);
        assertTrue (selector.matches (source, 1));
        assertTrue (selector.matches (source, 2L));
        assertTrue (selector.matches (source, "2"));
        assertTrue (selector.matches (source, 3.5));
        assertTrue (selector.matches (source, 4.2f));
        assertTrue (selector.matches (source, "4.2"));
        assertTrue (selector.matches (source, (byte) 5));
        assertTrue (selector.matches (source, (short) 6));
        assertTrue (selector.matches (source, (char) 20));
        assertFalse (selector.matches (source, 0));
        assertFalse (selector.matches (source, -1));
        assertFalse (selector.matches (source, -5.3));
        
        source = new Dummy (new Character ('b'));
        selector = new Attribute ("o", Type.LESS_THAN_OR_EQUAL_TO);
        assertTrue (selector.matches (source, 'b'));
        assertTrue (selector.matches (source, "b"));
        assertTrue (selector.matches (source, (int) 'b'));
        assertTrue (selector.matches (source, (long) 'c'));
        assertTrue (selector.matches (source, (float) 'c'));
        assertTrue (selector.matches (source, (byte) 'd'));
        assertTrue (selector.matches (source, (short) 'e'));
        assertTrue (selector.matches (source, (char) 150));
        assertFalse (selector.matches (source, 'a'));
        assertFalse (selector.matches (source, -'b'));
        assertFalse (selector.matches (source, "ac"));
        assertFalse (selector.matches (source, "150"));
        assertFalse (selector.matches (source, "150.2"));
    }

    @Test
    public void compositeOperators ()
    {
        Dummy source = new Dummy (10);

        Attribute selector = new Attribute ("i", Type.GREATER_THAN_OR_EQUAL_TO);
        assertTrue (selector.matches (source, 3));
        assertTrue (selector.matches (source, 10.0));
        assertFalse (selector.matches (source, 11L));

        selector = new Attribute ("i", Type.LESS_THAN);
        assertTrue (selector.matches (source, 13));
        assertTrue (selector.matches (source, 15.0));
        assertFalse (selector.matches (source, 10));
        assertFalse (selector.matches (source, 8L));
        assertFalse (selector.matches (source, -(short) 3));
    }

    @Test
    public void operatorOneWordIs()
    {
        Dummy source = new Dummy ("one two three");

        Attribute selector = new Attribute ("s", Type.ONE_WORD_IS);
        assertTrue (selector.matches (source, "one"));
        assertTrue (selector.matches (source, "two"));
        assertTrue (selector.matches (source, "three"));
        assertFalse (selector.matches (source, "on"));
        assertFalse (selector.matches (source, "tw"));
        assertFalse (selector.matches (source, "one two"));
        assertFalse (selector.matches (source, ""));
        
        selector = new Attribute ("s", Type.NOT_ONE_WORD_IS);
        assertFalse (selector.matches (source, "one"));
        assertTrue (selector.matches (source, "four"));
    }
    
    @Test
    public void operatorLanguageSubcode()
    {
        Attribute selector = new Attribute ("s", Type.LANGUAGE_SUBCODE);
        assertTrue (selector.matches (new Dummy ("en"), "en"));
        assertTrue (selector.matches (new Dummy ("en-US"), "en"));
        assertTrue (selector.matches (new Dummy ("en-scouse"), "en"));
        assertFalse (selector.matches (new Dummy ("fr-FR"), "en"));
    }
    
    @Test
    public void operatorHasAttribute ()
    {
        Dummy source = new Dummy ("unused");
        
        Attribute selector = new Attribute ("s", Type.HAS_ATTRIBUTE);
        assertTrue (selector.matches (source, "whatever"));
        
        selector = new Attribute ("s", Type.NOT_HAS_ATTRIBUTE);
        assertFalse (selector.matches (source));
        
        selector = new Attribute ("nonExistingProperty", Type.HAS_ATTRIBUTE);
        assertFalse (selector.matches (source));
        
        selector = new Attribute ("nonExistingProperty", Type.NOT_HAS_ATTRIBUTE);
        assertTrue (selector.matches (source));
        
    }
    
    @Test
    public void simpleAttributeDeclarationParsing ()
    {
        String [] found = AttributeSelector.parseAttributeDeclaration ("a[href $= 'org/']");
        assertEquals (4, found.length);
        assertEquals ("href", found[0]);
        assertEquals ("$=", found[1]);
        assertEquals ("'", found[2]);
        assertEquals ("org/", found[3]);
        
        found = AttributeSelector.parseAttributeDeclaration ("a[rel=bookmark]");
        assertEquals (4, found.length);
        assertEquals ("rel", found[0]);
        assertEquals ("=", found[1]);
        assertEquals ("", found[2]);
        assertEquals ("bookmark", found[3]);
        
        found = AttributeSelector.parseAttributeDeclaration ("a[rel='bookmark']");
        assertEquals (4, found.length);
        assertEquals ("rel", found[0]);
        assertEquals ("=", found[1]);
        assertEquals ("'", found[2]);
        assertEquals ("bookmark", found[3]);
        
        found = AttributeSelector.parseAttributeDeclaration ("a[rel=\"bookmark\"]");
        assertEquals (4, found.length);
        assertEquals ("rel", found[0]);
        assertEquals ("=", found[1]);
        assertEquals ("\"", found[2]);
        assertEquals ("bookmark", found[3]);
        
        found = AttributeSelector.parseAttributeDeclaration ("[rev =bookmark]");
        assertEquals (4, found.length);
        assertEquals ("rev", found[0]);
        assertEquals ("=", found[1]);
        assertEquals ("", found[2]);
        assertEquals ("bookmark", found[3]);
        
        found = AttributeSelector.parseAttributeDeclaration ("[rev*= bookmark]");
        assertEquals (4, found.length);
        assertEquals ("rev", found[0]);
        assertEquals ("*=", found[1]);
        assertEquals ("", found[2]);
        assertEquals ("bookmark", found[3]);
        
        found = AttributeSelector.parseAttributeDeclaration ("[rev ^='bookmark']");
        assertEquals (4, found.length);
        assertEquals ("rev", found[0]);
        assertEquals ("^=", found[1]);
        assertEquals ("'", found[2]);
        assertEquals ("bookmark", found[3]);

        found = AttributeSelector.parseAttributeDeclaration ("input[name='foo[bar]']");
        assertEquals (4, found.length);
        assertEquals ("name", found[0]);
        assertEquals ("=", found[1]);
        assertEquals ("'", found[2]);
        assertEquals ("foo[bar]", found[3]);
        
        found = AttributeSelector.parseAttributeDeclaration ("*[title]");
        assertEquals (4, found.length);
        assertEquals ("title", found[0]);
        assertEquals (null, found[1]);
        assertEquals (null, found[2]);
        assertEquals (null, found[3]);
    }

    @Test
    public void attributeDeclarationParsingPureCSS ()
    {
        assertAttributeMatches ("[title]", "title");
        assertAttributeMatches ("a[title]", "title");
        assertAttributeMatches ("*[title]", "title");
        assertAttributeMatches ("a[rel='bookmark']", "rel", "=", "'", "bookmark");
        assertAttributeMatches ("a[rel=\"bookmark\"]", "rel", "=", "\"", "bookmark");
        assertAttributeMatches ("a[rel=bookmark]", "rel", "=", "", "bookmark");
        assertAttributeMatches ("span[lang=ä¸­æ–‡]", "lang", "=", "", "ä¸­æ–‡");
        assertAttributeMatches ("a[href ^= 'http://www']", "href", "^=", "'", "http://www");
        assertAttributeMatches ("a[href$= 'org/']", "href", "$=", "'", "org/");
        assertAttributeMatches ("a[href $= 'org/']", "href", "$=", "'", "org/");
        assertAttributeMatches ("a[href $='org/']", "href", "$=", "'", "org/");
        assertAttributeMatches ("a[href$='org/']", "href", "$=", "'", "org/");
        assertAttributeMatches ("a[href *='google']", "href", "*=", "'", "google");
        assertAttributeMatches ("a[rel|='bookmark']", "rel", "|=", "'", "bookmark");
        assertAttributeMatches ("a[rel~=bookmark]", "rel", "~=", "", "bookmark");
    }
    
    @Test
    public void attributeDeclarationParsingExtended ()
    {
        assertAttributeMatches ("[rel != 2]", "rel", "!=", "", "2");
        assertAttributeMatches ("[rel == 2]", "rel", "==", "", "2");
        assertAttributeMatches ("[rel >= 2]", "rel", ">=", "", "2");
        assertAttributeMatches ("[rel <= 2]", "rel", "<=", "", "2");
        assertAttributeMatches ("[rel > 2]", "rel", ">", "", "2");
        assertAttributeMatches ("[rel < 2]", "rel", "<", "", "2");
        assertAttributeMatches ("[rel !== 2]", "rel", "!==", "", "2");
        
        assertAttributeMatches ("[x !=2]", "x", "!=", "", "2");
        assertAttributeMatches ("[rel ==2]", "rel", "==", "", "2");
        assertAttributeMatches ("[rel >=2]", "rel", ">=", "", "2");
        assertAttributeMatches ("[rel <=2]", "rel", "<=", "", "2");
        assertAttributeMatches ("[rel >2]", "rel", ">", "", "2");
        assertAttributeMatches ("[rel <2]", "rel", "<", "", "2");
        assertAttributeMatches ("[rel !==2]", "rel", "!==", "", "2");
        
        assertAttributeMatches ("[x!= 2]", "x", "!=", "", "2");
        assertAttributeMatches ("[rel== 2]", "rel", "==", "", "2");
        assertAttributeMatches ("[rel>= 2]", "rel", ">=", "", "2");
        assertAttributeMatches ("[rel<= 2]", "rel", "<=", "", "2");
        assertAttributeMatches ("[rel> 2]", "rel", ">", "", "2");
        assertAttributeMatches ("[rel< 2]", "rel", "<", "", "2");
        assertAttributeMatches ("[rel!== 2]", "rel", "!==", "", "2");
        
        assertAttributeMatches ("[x!=2]", "x", "!=", "", "2");
        assertAttributeMatches ("[rel==2]", "rel", "==", "", "2");
        assertAttributeMatches ("[rel>=2]", "rel", ">=", "", "2");
        assertAttributeMatches ("[rel<=2]", "rel", "<=", "", "2");
        assertAttributeMatches ("[rel>2]", "rel", ">", "", "2");
        assertAttributeMatches ("[rel<2]", "rel", "<", "", "2");
        assertAttributeMatches ("[rel!==2]", "rel", "!==", "", "2");
    }

    @Test
    public void pureOperatorParsing ()
    {
        assertEquals (Type.EQUAL_TO, Type.from ("="));
        
        assertEquals (Type.STARTS_WITH, Type.from ("^="));
        assertEquals (Type.ENDS_WITH, Type.from ("$="));
        assertEquals (Type.CONTAINS, Type.from ("*="));
        
        assertEquals (Type.ONE_WORD_IS, Type.from ("~="));
        assertEquals (Type.LANGUAGE_SUBCODE, Type.from ("|="));
        
        assertEquals (Type.HAS_ATTRIBUTE, Type.from (""));
    }
    
    @Test
    public void extendedOperatorParsing ()
    {
        assertEquals (Type.NOT_EQUAL_TO, Type.from ("!="));
        assertEquals (Type.GREATER_THAN, Type.from (">"));
        assertEquals (Type.GREATER_THAN_OR_EQUAL_TO, Type.from (">="));
        assertEquals (Type.LESS_THAN, Type.from ("<"));
        assertEquals (Type.LESS_THAN_OR_EQUAL_TO, Type.from ("<="));
        
        assertEquals (Type.SAME_AS, Type.from ("=="));
        assertEquals (Type.NOT_SAME_AS, Type.from ("!=="));
    }
    
    @Test
    public void selectorParsing ()
    {
        Attribute [] selectors = AttributeSelector.from ("span[class='example']");
        assertEquals (1, selectors.length);
        
        Attribute selector = selectors[0];
        assertEquals ("class", selector.property());
        assertEquals (Type.EQUAL_TO, selector.type());
        assertEquals ("example", selector.value());
        
        selector = AttributeSelector.from ("[name$=joe]") [0];
        assertEquals ("name", selector.property());
        assertEquals (Type.ENDS_WITH, selector.type());
        assertEquals ("joe", selector.value());
    }
    
    @Test
    public void multipleAttributes()
    {
        assertAttributeMatches ("[rev=bookmark][name*= 'joe']",
                "rev", "=", "", "bookmark",
                "name", "*=", "'", "joe");
    }
    
    @Test
    public void multipleSelectors()
    {
        Attribute [] selectors = AttributeSelector.from ("[rev=bookmark][name*= 'joe']");
        assertEquals (2, selectors.length);
        
        Attribute selector = selectors[0];
        assertEquals ("rev", selector.property());
        assertEquals (Type.EQUAL_TO, selector.type());
        assertEquals ("bookmark", selector.value());
        
        selector = selectors[1];
        assertEquals ("name", selector.property());
        assertEquals (Type.CONTAINS, selector.type());
        assertEquals ("joe", selector.value());
    }
    
    @Test
    public void swingPureAttributes()
    {
        assertSelectorFinds ("*", 2);
        
    	assertSelectorFindsComponent1Only ("#id");
    	assertSelectorFindsComponent1Only ("JLabel#id");
    	assertSelectorFindsComponent1Only ("JLabel[name=id]");
        
        assertSelectorFindsNothing ("JLabel[name=X]");
        
        assertSelectorFindsComponent1Only ("[name^=i]");
        assertSelectorFindsComponent1Only ("[name^=id]");
        assertSelectorFindsComponent1Only ("[name$=d]");
        assertSelectorFindsComponent1Only ("[name$=id]");
        assertSelectorFindsComponent1Only ("JLabel[name=id]");
        
        assertSelectorFindsComponent1Only ("[name=id][opaque=false]");
        assertSelectorFindsNothing ("[name=id][opaque=true]");
        assertSelectorFindsComponent1Only ("*[name=id][opaque=false]");
        assertSelectorFindsNothing ("*[name=id][opaque=true]");
        
        assertSelectorFindsComponent1Only ("[x=10]");
        assertSelectorFindsComponent1Only ("[x=10][y=20][width=30][height=40]");
        assertSelectorFindsNothing ("[x=0][y=20][width=30][height=40]");
        
        assertSelectorFindsBothComponents ("[name]");
        assertSelectorFindsComponent1Only ("[name$= id]");
        assertSelectorFindsComponent1Only ("[name $=id]");
        assertSelectorFindsComponent1Only ("[name $= id]");
        
        assertSelectorFindsComponent1Only ("[x=10.0]");
        
        assertSelectorFindsComponent2Only ("[alignmentX=0.5]");
    }

    @Test
    public void swingExtendedAttributes()
    {
        assertSelectorFinds ("*", 2);
        
        assertSelectorFindsBothComponents ("[name!=X]");
        
        assertSelectorFindsComponent2Only ("[x!=10]");
        assertSelectorFindsComponent1Only ("[x!=0][y=20]");
        
        assertSelectorFindsComponent1Only ("[x<=10]");
        assertSelectorFindsBothComponents ("[x>=10]");
        assertSelectorFindsBothComponents ("[x > 9]");
        assertSelectorFindsBothComponents ("[x >9]");
        assertSelectorFindsBothComponents ("[x> 9]");
        
        assertSelectorFindsComponent1Only ("[x<=10.5]");
        assertSelectorFindsComponent2Only ("[x!=10.0]");
        assertSelectorFindsBothComponents ("[y!=20.7]");
        
        assertSelectorFindsComponent1Only ("[name!='']");
        assertSelectorFindsComponent1Only ("[name!= '']");
        assertSelectorFindsComponent1Only ("[name != '']");
        assertSelectorFindsComponent1Only ("[name !='']");
        
        assertSelectorFindsComponent2Only ("[x!=10.0]");
        assertSelectorFindsBothComponents ("[y!=20.7]");
        
        try
        {
            assertSelectorFindsNothing ("[alignmentX==0.5]");
            fail ("The SAME_AS_OPERATOR_FAILS_WHEN_NOT_SAME_BUT_EQUAL_TO setting should have made this request fail");
        }
        catch (Exception e)
        {
        }
        
        Attribute.SAME_AS_OPERATOR_FAILS_WHEN_NOT_SAME_BUT_EQUAL_TO = false;
        assertSelectorFindsNothing ("[alignmentX==0.5]");
        
        assertSelectorFindsComponent1Only ("[alignmentX != 0.5]");
    }
    
    @Test
    public void microSyntaxHook()
    {
        Converter.registerSwingConverters ();
        
    	assertSelectorFindsComponent1Only ("[background=Color.green]");
    	assertSelectorFindsComponent1Only ("[background= Color.green]");
    	assertSelectorFindsComponent1Only ("[background =Color.green]");
    	assertSelectorFindsComponent1Only ("[background = Color.green]");
    	assertSelectorFindsComponent1Only ("[background=#00FF00]");
    	assertSelectorFindsComponent1Only ("[background=#00FF00FF]");
    	
    	assertSelectorFindsComponent2Only ("[background!=Color.green]");
    	assertSelectorFindsComponent2Only ("[background != #00FF00]");
    }
    
    @Test // Calendars too ?
    public void microSyntaxesCanHookIntoComparable()
    {
        Calendar lastYear = new GregorianCalendar (2009, Calendar.FEBRUARY, 10);
        Calendar now = new GregorianCalendar (2010, Calendar.FEBRUARY, 10);
        
        assertTrue (now.compareTo (lastYear) > 0);
        assertTrue (now.getTime ().compareTo (lastYear.getTime ()) > 0);
        
        Dummy source = new Dummy (now.getTime ());

        Attribute selector = new Attribute ("date", Type.GREATER_THAN);
        assertTrue (selector.matches (source, "2009.12.01 12:45:12"));
        assertTrue (selector.matches (source, lastYear.getTime ()));
        assertFalse (selector.matches (source, now.getTime ()));
        
        selector = new Attribute ("date", Type.EQUAL_TO);
        assertTrue (selector.matches (source, now.getTime ()));
        assertFalse (selector.matches (source, "2009.12.01 12:45:12"));
        assertFalse (selector.matches (source, lastYear.getTime ()));
    }
    
    private void assertSelectorFinds (String selector, JComponent... expected)
    {
    	List<JComponent> results = parseSelector (selector);
    	assertEquals (expected.length, results.size());
    	for (int i = 0; i < expected.length; ++i)
    	    assertEquals ("Mismatch at index " + i, expected[i], results.get (i));
    }
    
    private void assertSelectorFindsBothComponents (String selector)
    {
        assertSelectorFinds (selector, component1, component2);
    }
    
    private void assertSelectorFinds (String selector, int count)
    {
    	List<JComponent> results = parseSelector (selector);
        assertEquals (count, results.size());
    }
    
    private void assertSelectorFindsComponent1Only (String selector)
    {
        assertSelectorFinds (selector, component1);
    }
    
    private void assertSelectorFindsComponent2Only (String selector)
    {
        assertSelectorFinds (selector, component2);
    }
    
    private void assertSelectorFindsNothing (String selector)
    {
        assertSelectorFinds (selector, 0);
    }

    private List<JComponent> parseSelector (String selector)
    {
    	Query q = new Query (selector);
    	return q.matches (component1, component2);
    }
    
    private static JComponent component1, component2;
    
    @BeforeClass
    public static void setUpClass()
    {
    	component1 = new JLabel();
        component1.setName ("id");
        assertEquals ("id", component1.getName ());
        
        component1.setOpaque (false);
        assertFalse (component1.isOpaque ());
        
        component1.setBounds (10, 20, 30, 40);
        assertEquals (10, component1.getX ());
        
        assertEquals (0f, component1.getAlignmentX ());
        
        component1.setBackground (Color.green);
        assertEquals (Color.green, component1.getBackground());
        
        component2 = new JLabel();
        component2.setName ("");
        assertEquals ("", component2.getName ());
        
        component2.setBounds (100, 200, 300, 400);
        assertEquals (100, component2.getX ());
        
        component2.setAlignmentX (0.5f);
        assertEquals (0.5f, component2.getAlignmentX ());
    }
    
    private static void assertAttributeMatches (String input, Object... expected)
    {
        String [] found = AttributeSelector.parseAttributeDeclaration (input);

        assertFalse (0 == found.length);
        
        for (int i = 0; i < expected.length; ++i)
            assertEquals (input + " didn't match at index: " + i, expected[i], found[i]);
    }

    private static class Dummy
    {
        private int i;

        public int getI ()
        {
            return i;
        }

        public Dummy (int i)
        {
            this.i = i;
        }

        private String s;

        public String getS ()
        {
            return s;
        }

        private Dummy (String s)
        {
            this.s = s;
        }

        private boolean b;

        public boolean isB ()
        {
            return b;
        }

        public Dummy (boolean b)
        {
            this.b = b;
        }

        private Object o;

        public Object getO ()
        {
            return o;
        }

        public Dummy (Object o)
        {
            this.o = o;
        }
        
        private char c;

        public char getC ()
        {
            return c;
        }

        public Dummy (char c)
        {
            this.c = c;
        }
        
        private float f;

        public float getF ()
        {
            return f;
        }

        public Dummy (float f)
        {
            this.f = f;
        }
        
        private Date date;

        public Dummy (Date date)
        {
            this.date = date;
        }

        public Date getDate ()
        {
            return date;
        }
    }
}