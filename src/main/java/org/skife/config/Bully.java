package org.skife.config;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


class Bully
{
    private static final Map<Class<?>, Coercible<?>> DEFAULT_COERCIBLES;

    static {
        final Map<Class<?>, Coercible<?>> defaultCoercibles = new HashMap<Class<?>, Coercible<?>>();

        defaultCoercibles.put(Boolean.class, Coercible.BOOLEAN_COERCIBLE);
        defaultCoercibles.put(Boolean.TYPE, Coercible.BOOLEAN_COERCIBLE);
        defaultCoercibles.put(Byte.class, Coercible.BYTE_COERCIBLE);
        defaultCoercibles.put(Byte.TYPE, Coercible.BYTE_COERCIBLE);
        defaultCoercibles.put(Short.class, Coercible.SHORT_COERCIBLE);
        defaultCoercibles.put(Short.TYPE, Coercible.SHORT_COERCIBLE);
        defaultCoercibles.put(Integer.class, Coercible.INTEGER_COERCIBLE);
        defaultCoercibles.put(Integer.TYPE, Coercible.INTEGER_COERCIBLE);
        defaultCoercibles.put(Long.class, Coercible.LONG_COERCIBLE);
        defaultCoercibles.put(Long.TYPE, Coercible.LONG_COERCIBLE);
        defaultCoercibles.put(Float.class, Coercible.FLOAT_COERCIBLE);
        defaultCoercibles.put(Float.TYPE, Coercible.FLOAT_COERCIBLE);
        defaultCoercibles.put(Double.class, Coercible.DOUBLE_COERCIBLE);
        defaultCoercibles.put(Double.TYPE, Coercible.DOUBLE_COERCIBLE);
        defaultCoercibles.put(String.class, Coercible.STRING_COERCIBLE);
        defaultCoercibles.put(URI.class, Coercible.URI_COERCIBLE);

        DEFAULT_COERCIBLES = Collections.unmodifiableMap(defaultCoercibles);
    }

    private final Map<Class<?>, Coercible<?>> mappings = new HashMap<Class<?>, Coercible<?>>();

    public Bully()
    {
        mappings.putAll(DEFAULT_COERCIBLES);
    }

    public Object coerce(Class<?> type, String value) {
        final Coercible<?> coercible = mappings.get(type);
        if (coercible == null) {
            throw new IllegalStateException(String.format("Don't know how to handle a '%s' type for value '%s'", type.getName(), value));
        }
        return coercible.coerce(value);
    }
}
