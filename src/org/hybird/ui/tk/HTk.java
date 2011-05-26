package org.hybird.ui.tk;

import java.lang.reflect.Constructor;

import javax.swing.JComponent;

@SuppressWarnings({"unchecked", "rawtypes"})
public class HTk
{
    public static final String STYLE_PROPERTY = "HTk.style";
    
    public static <T extends HComponent> T wrap (Class<T> tkClass, JComponent component)
    {
        if (tkClass == HComponent.class)
            return (T) wrap (component);
        
        Class<? extends JComponent> klass = component.getClass ();
        
        try
        {
            Constructor<?> constructor = findConstructor (tkClass, klass);
            if (constructor != null)
            {
                Object hComponent = constructor.newInstance (component);
                return (T) hComponent;
            }
        }
        catch (Exception e)
        {
        }
        
        throw new IllegalStateException ("The wrapper class '" + tkClass.getName ()
                + "' can't be used as a HComponent wrapper, as it does not have a constructor taking a '"
                + klass.getName () +  "' (or any of its superclasses) as a parameter");
    }
    
    private static <T extends HComponent> Constructor<?> findConstructor (Class<T> tkClass, Class klass)
    {
        try
        {
            Constructor<?> constructor = tkClass.getDeclaredConstructor (klass);
            return constructor;
        }
        catch (Exception e)
        {
        }

        if (klass == JComponent.class)
            return null;
        
        Class<?> superclass = klass.getSuperclass ();
        return findConstructor (tkClass, superclass);
    }
    
    private static <T extends HComponent> T wrap (JComponent component)
    {
        return (T) new HComponent (component);
    }
    
    public static <X> X getProperty (JComponent component, String key)
    {
        return (X) component.getClientProperty (key);
    }
    
    public static void setProperty (JComponent component, String key, Object value)
    {
        component.putClientProperty (key, value);
    }
    
    private HTk () {}
}
