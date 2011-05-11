package org.skife.config;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


class Bully
{
    /** All explicit type conversions that config magic knows about. Every new bully will know about those. */
    private static final List<Coercible<?>> TYPE_COERCIBLES;

    /** Catchall converters. These will be run if no specific type coercer was found. */
    private static final List<Coercible<?>> DEFAULT_COERCIBLES;

    static {
        final List<Coercible<?>> typeCoercibles = new ArrayList<Coercible<?>>();
        final List<Coercible<?>> defaultCoercibles = new ArrayList<Coercible<?>>();

        typeCoercibles.add(DefaultCoercibles.BOOLEAN_COERCIBLE);
        typeCoercibles.add(DefaultCoercibles.BYTE_COERCIBLE);
        typeCoercibles.add(DefaultCoercibles.SHORT_COERCIBLE);
        typeCoercibles.add(DefaultCoercibles.INTEGER_COERCIBLE);
        typeCoercibles.add(DefaultCoercibles.LONG_COERCIBLE);
        typeCoercibles.add(DefaultCoercibles.FLOAT_COERCIBLE);
        typeCoercibles.add(DefaultCoercibles.DOUBLE_COERCIBLE);
        typeCoercibles.add(DefaultCoercibles.STRING_COERCIBLE);

        // Look Brian, now it groks URIs. ;-)
        typeCoercibles.add(DefaultCoercibles.URI_COERCIBLE);

        defaultCoercibles.add(DefaultCoercibles.VALUE_OF_COERCIBLE);
        defaultCoercibles.add(DefaultCoercibles.STRING_CTOR_COERCIBLE);
        defaultCoercibles.add(DefaultCoercibles.OBJECT_CTOR_COERCIBLE);

        TYPE_COERCIBLES = Collections.unmodifiableList(typeCoercibles);
        DEFAULT_COERCIBLES = Collections.unmodifiableList(defaultCoercibles);
    }

    /**
     * The instance specific mappings from a given type to its coercer. This needs to be two-level because the
     * catchall converters will generate specific instances of their coercers based on the type.
     */
    private final Map<Class<?>, Coercer<?>> mappings = new HashMap<Class<?>, Coercer<?>>();

    /**
     * All the coercibles that this instance knows about. This list can be extended with user mappings.
     */
    private final List<Coercible<?>> coercibles = new ArrayList<Coercible<?>>();

    public Bully()
    {
        coercibles.addAll(TYPE_COERCIBLES);
    }

    /**
     * Adds a new Coercible to the list of known coercibles. This also resets the current mappings in this bully.
     */
    public void addCoercible(final Coercible<?> coercible)
    {
        coercibles.add(coercible);
        mappings.clear();
    }

    public synchronized Object coerce(Type type, String value) {
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>)type;

            if (clazz.isArray()) {
                return coerceArray(clazz.getComponentType(), value);
            }
            else {
                return coerce(clazz, value);
            }
        }
        else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type rawType = parameterizedType.getRawType();

            if (rawType instanceof Class<?>) {
                Type[] args = parameterizedType.getActualTypeArguments();

                if (args != null && args.length == 1 && args[0] instanceof Class<?>) {
                    return coerceCollection((Class<?>)rawType, (Class<?>)args[0], value);
                }
            }
        }
        throw new IllegalStateException(String.format("Don't know how to handle a '%s' type for value '%s'", type, value));
    }

    private Object coerceArray(Class<?> elemType, String value) {
        if (value == null) {
            return null;
        }
        else {
            String[] tokens = value.split("\\s*,\\s*");
            Object targetArray = Array.newInstance(elemType, tokens.length);

            for (int idx = 0; idx < tokens.length; idx++) {
                Array.set(targetArray, idx, coerce(elemType, tokens[idx]));
            }
            return targetArray;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object coerceCollection(Class<?> containerType, Class<?> elemType, String value) {
        if (value == null) {
            return null;
        }
        else {
            String[] tokens = value.split("\\s*,\\s*");
            Collection result = null;
            if (Set.class.equals(containerType)) {
                result = new HashSet(tokens.length);
            }
            else if (Collection.class.equals(containerType) || List.class.equals(containerType)) {
                result = new ArrayList(tokens.length);
            }
            else if (Collection.class.isAssignableFrom(containerType)) {
                try {
                    final Constructor<?> ctor = containerType.getConstructor();

                    if (ctor != null) {
                        result = (Collection)ctor.newInstance();
                    }
                }
                catch (Exception ex) {
                    // handled below
                }
            }
            if (result == null) {
                throw new IllegalStateException(String.format("Don't know how to handle a '%s' container type for value '%s'", containerType, value));
            }
            for (String token : tokens) {
                result.add(coerce(elemType, token));
            }
            return result;
        }
    }

    private Object coerce(Class<?> clazz, String value) {
        Coercer<?> coercer = getCoercerFor(coercibles, clazz);
        if (coercer == null) {
            coercer = getCoercerFor(DEFAULT_COERCIBLES, clazz);

            if (coercer == null) {
                throw new IllegalStateException(String.format("Don't know how to handle a '%s' type for value '%s'", clazz, value));
            }
        }
        return coercer.coerce(value);
    }

    private Coercer<?> getCoercerFor(final List<Coercible<?>> coercibles, final Class<?> type)
    {
        Coercer<?> typeCoercer = mappings.get(type);
        if (typeCoercer == null) {
            for (Coercible<?> coercible : coercibles) {
                final Coercer<?> coercer = coercible.accept(type);
                if (coercer != null) {
                    mappings.put(type, coercer);
                    typeCoercer = coercer;
                    break;
                }
            }
        }
        return typeCoercer;
    }
}
