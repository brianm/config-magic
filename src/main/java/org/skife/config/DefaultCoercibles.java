package org.skife.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public final class DefaultCoercibles
{
    private DefaultCoercibles()
    {
    }

    /**
     * A Coercible that accepts any type with a static <code>valueOf(String)</code> method.
     */
    public static final Coercible<?> VALUE_OF_COERCIBLE = new Coercible<Object>() {

        private Map<Class<?>, Coercer<Object>> coercerMap = new HashMap<Class<?>, Coercer<Object>>();

        public Coercer<Object> accept(final Class<?> type)
        {
            if (coercerMap.containsKey(type)) {
                // If a key exists, the value always gets returned. If a null value is in the map,
                // the type was seen before and deemed not worthy.
                return coercerMap.get(type);
            }

            Coercer<Object> coercer = null;
            try {
                // Method must be 'static valueOf(String)' and return the type in question.
                Method candidate = type.getMethod("valueOf", String.class);
                if (!Modifier.isStatic(candidate.getModifiers())) {
                    // not static.
                    candidate = null;
                }
                else if (candidate.getReturnType() != type) {
                    // does not return the right type.
                    candidate = null;
                }

                if (candidate != null) {
                    final Method valueOfMethod = candidate;

                    coercer = new Coercer<Object>() {
                        public Object coerce(final String value)
                        {
                            try {
                                return valueOfMethod.invoke(null, value);
                            }
                            catch (Exception e) {
                                throw convertException(e);
                            }
                        }
                    };
                }
            }
            catch(NoSuchMethodException nsme) {
                // Don't do anything, the class does not have a method.
            }

            coercerMap.put(type, coercer);
            return coercer;
        }
    };

    /**
     * A Coercible that accepts any type with a c'tor that takes a single string parameter.
     */
    public static final Coercible<?> STRING_CTOR_COERCIBLE = new Coercible<Object>() {

        private Map<Class<?>, Coercer<Object>> coercerMap = new HashMap<Class<?>, Coercer<Object>>();

        public Coercer<Object> accept(final Class<?> type)
        {
            if (coercerMap.containsKey(type)) {
                // If a key exists, the value always gets returned. If a null value is in the map,
                // the type was seen before and deemed not worthy.
                return coercerMap.get(type);
            }


            Coercer<Object> coercer = null;
            try {
                final Constructor<?> ctor = type.getConstructor(String.class);

                coercer = new Coercer<Object>() {
                    public Object coerce(final String value)
                    {
                        try {
                            return ctor.newInstance(value);
                        }
                        catch (Exception e) {
                            throw convertException(e);
                        }
                    }
                };
            }
            catch(NoSuchMethodException nsme) {
                // Don't do anything, the class does not have a matching c'tor
            }

            coercerMap.put(type, coercer);
            return coercer;
        }
    };

    /**
     * A Coercible that accepts any type with a c'tor that takes a single Object parameter.
     *
     * This one was lovingly prepared and added for Jodatime DateTime objects.
     */
    public static final Coercible<?> OBJECT_CTOR_COERCIBLE = new Coercible<Object>() {

        private Map<Class<?>, Coercer<Object>> coercerMap = new HashMap<Class<?>, Coercer<Object>>();

        public Coercer<Object> accept(final Class<?> type)
        {
            if (coercerMap.containsKey(type)) {
                // If a key exists, the value always gets returned. If a null value is in the map,
                // the type was seen before and deemed not worthy.
                return coercerMap.get(type);
            }


            Coercer<Object> coercer = null;
            try {
                final Constructor<?> ctor = type.getConstructor(Object.class);

                coercer = new Coercer<Object>() {
                    public Object coerce(final String value)
                    {
                        try {
                            return ctor.newInstance(value);
                        }
                        catch (Exception e) {
                            throw convertException(e);
                        }
                    }
                };
            }
            catch(NoSuchMethodException nsme) {
                // Don't do anything, the class does not have a matching c'tor
            }

            coercerMap.put(type, coercer);
            return coercer;
        }
    };

    private static final RuntimeException convertException(final Throwable t)
    {
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        }
        else if (t instanceof InvocationTargetException) {
            return convertException(((InvocationTargetException)t).getTargetException());
        }
        else {
            return new RuntimeException(t);
        }
    }
}
