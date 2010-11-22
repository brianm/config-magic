package org.skife.config;

import java.net.URI;

public interface Coercible<T>
{
    Coercer<T> accept(Class<?> clazz);

    public interface Coercer<T>
    {
        T coerce(String value);

        Coercer<Boolean> BOOLEAN_COERCER = new Coercer<Boolean>() {
            public Boolean coerce(final String value) {
                return Boolean.valueOf(value);
            }
        };

        Coercer<Byte> BYTE_COERCER = new Coercer<Byte>() {
            public Byte coerce(final String value) {
                return Byte.valueOf(value);
            }
        };

        Coercer<Short> SHORT_COERCER = new Coercer<Short>() {
            public Short coerce(final String value) {
                return Short.valueOf(value);
            }
        };

        Coercer<Integer> INTEGER_COERCER = new Coercer<Integer>() {
            public Integer coerce(final String value) {
                return Integer.valueOf(value);
            }
        };

        Coercer<Long> LONG_COERCER = new Coercer<Long>() {
            public Long coerce(final String value) {
                return Long.valueOf(value);
            }
        };

        Coercer<Float> FLOAT_COERCER = new Coercer<Float>() {
            public Float coerce(final String value) {
                return Float.valueOf(value);
            }
        };

        Coercer<Double> DOUBLE_COERCER = new Coercer<Double>() {
            public Double coerce(final String value) {
                return Double.valueOf(value);
            }
        };

        Coercer<String> STRING_COERCER = new Coercer<String>() {
            public String coerce(final String value) {
                return value;
            }
        };

        Coercer<URI> URI_COERCER = new Coercer<URI>() {
            public URI coerce(final String value) {
                return URI.create(value);
            }
        };
    }

    Coercible<Boolean> BOOLEAN_COERCIBLE = new Coercible<Boolean>() {
        public Coercer<Boolean> accept(final Class<?> clazz) {
            if (Boolean.class.isAssignableFrom(clazz) || Boolean.TYPE.isAssignableFrom(clazz)) {
                return Coercer.BOOLEAN_COERCER;
            }
            return null;
        }
    };

    Coercible<Byte> BYTE_COERCIBLE = new Coercible<Byte>() {
        public Coercer<Byte> accept(final Class<?> clazz) {
            if (Byte.class.isAssignableFrom(clazz) || Byte.TYPE.isAssignableFrom(clazz)) {
                return Coercer.BYTE_COERCER;
            }
            return null;
        }
    };

    Coercible<Short> SHORT_COERCIBLE = new Coercible<Short>() {
        public Coercer<Short> accept(final Class<?> clazz) {
            if (Short.class.isAssignableFrom(clazz) || Short.TYPE.isAssignableFrom(clazz)) {
                return Coercer.SHORT_COERCER;
            }
            return null;
        }
    };

    Coercible<Integer> INTEGER_COERCIBLE = new Coercible<Integer>() {
        public Coercer<Integer> accept(final Class<?> clazz) {
            if (Integer.class.isAssignableFrom(clazz) || Integer.TYPE.isAssignableFrom(clazz)) {
                return Coercer.INTEGER_COERCER;
            }
            return null;
        }
    };

    Coercible<Long> LONG_COERCIBLE = new Coercible<Long>() {
        public Coercer<Long> accept(final Class<?> clazz) {
            if (Long.class.isAssignableFrom(clazz) || Long.TYPE.isAssignableFrom(clazz)) {
                return Coercer.LONG_COERCER;
            }
            return null;
        }
    };

    Coercible<Float> FLOAT_COERCIBLE = new Coercible<Float>() {
        public Coercer<Float> accept(final Class<?> clazz) {
            if (Float.class.isAssignableFrom(clazz) || Float.TYPE.isAssignableFrom(clazz)) {
                return Coercer.FLOAT_COERCER;
            }
            return null;
        }
    };

    Coercible<Double> DOUBLE_COERCIBLE = new Coercible<Double>() {
        public Coercer<Double> accept(final Class<?> clazz) {
            if (Double.class.isAssignableFrom(clazz) || Double.TYPE.isAssignableFrom(clazz)) {
                return Coercer.DOUBLE_COERCER;
            }
            return null;
        }
    };

    Coercible<String> STRING_COERCIBLE = new Coercible<String>() {
        public Coercer<String> accept(final Class<?> clazz) {
            if (String.class.equals(clazz)) {
                return Coercer.STRING_COERCER;
            }
            return null;
        }
    };

    Coercible<URI> URI_COERCIBLE = new Coercible<URI>() {
        public Coercer<URI> accept(final Class<?> clazz) {
            if (URI.class.equals(clazz)) {
                return Coercer.URI_COERCER;
            }
            return null;
        }
    };

}
