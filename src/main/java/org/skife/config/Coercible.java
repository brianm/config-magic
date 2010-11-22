package org.skife.config;

import java.net.URI;

public interface Coercible<T>
{
    T coerce(String value);

    Coercible<Boolean> BOOLEAN_COERCIBLE = new Coercible<Boolean>() {
        public Boolean coerce(final String value) {
            return Boolean.valueOf(value);
        }
    };

    Coercible<Byte> BYTE_COERCIBLE = new Coercible<Byte>() {
        public Byte coerce(final String value) {
            return Byte.valueOf(value);
        }
    };

    Coercible<Short> SHORT_COERCIBLE = new Coercible<Short>() {
        public Short coerce(final String value) {
            return Short.valueOf(value);
        }
    };

    Coercible<Integer> INTEGER_COERCIBLE = new Coercible<Integer>() {
        public Integer coerce(final String value) {
            return Integer.valueOf(value);
        }
    };

    Coercible<Long> LONG_COERCIBLE = new Coercible<Long>() {
        public Long coerce(final String value) {
            return Long.valueOf(value);
        }
    };

    Coercible<Float> FLOAT_COERCIBLE = new Coercible<Float>() {
        public Float coerce(final String value) {
            return Float.valueOf(value);
        }
    };

    Coercible<Double> DOUBLE_COERCIBLE = new Coercible<Double>() {
        public Double coerce(final String value) {
            return Double.valueOf(value);
        }
    };

    Coercible<String> STRING_COERCIBLE = new Coercible<String>() {
        public String coerce(final String value) {
            return value;
        }
    };

    Coercible<URI> URI_COERCIBLE = new Coercible<URI>() {
        public URI coerce(final String value) {
            return URI.create(value);
        }
    };
}
