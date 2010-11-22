package org.skife.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class Bully
{
    /** All explicit type conversions that config magic knows about. Every new bully will know about those. */
    private static final List<Coercible<?>> TYPE_COERCIBLES;

    /** Catchall converters. These will be run if not specific type coercer was found. */
    private static final List<Coercible<?>> DEFAULT_COERCIBLES;

    static {
        final List<Coercible<?>> typeCoercibles = new ArrayList<Coercible<?>>();
        final List<Coercible<?>> defaultCoercibles = new ArrayList<Coercible<?>>();

        typeCoercibles.add(Coercible.BOOLEAN_COERCIBLE);
        typeCoercibles.add(Coercible.BYTE_COERCIBLE);
        typeCoercibles.add(Coercible.SHORT_COERCIBLE);
        typeCoercibles.add(Coercible.INTEGER_COERCIBLE);
        typeCoercibles.add(Coercible.LONG_COERCIBLE);
        typeCoercibles.add(Coercible.FLOAT_COERCIBLE);
        typeCoercibles.add(Coercible.DOUBLE_COERCIBLE);
        typeCoercibles.add(Coercible.STRING_COERCIBLE);

        // Look Brian, now it groks URIs. ;-)
        typeCoercibles.add(Coercible.URI_COERCIBLE);

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
    private final Map<Class<?>, Coercible.Coercer<?>> mappings = new HashMap<Class<?>, Coercible.Coercer<?>>();

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

    public synchronized Object coerce(Class<?> type, String value) {
        Coercible.Coercer<?> coercer = getCoercerFor(coercibles, type);
        if (coercer == null) {
            coercer = getCoercerFor(DEFAULT_COERCIBLES, type);

            if (coercer == null) {
                throw new IllegalStateException(String.format("Don't know how to handle a '%s' type for value '%s'", type.getName(), value));
            }
        }
        return coercer.coerce(value);
    }

    private Coercible.Coercer<?> getCoercerFor(final List<Coercible<?>> coercibles, final Class<?> type)
    {
        Coercible.Coercer<?> typeCoercer = mappings.get(type);
        if (typeCoercer == null) {
            for (Coercible<?> coercible : coercibles) {
                Coercible.Coercer<?> coercer = coercible.accept(type);
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
