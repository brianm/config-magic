package org.skife.config;

import org.apache.commons.configuration.Configuration;

import java.lang.reflect.Method;

class Bully
{
    public Object coerce(Class<?> type, String value) {
        if (String.class.isAssignableFrom(type)) {
            return value;
        }
        else if (Boolean.class.isAssignableFrom(type) || Boolean.TYPE.isAssignableFrom(type)) {
            return Boolean.valueOf(value);
        }
        else if (Byte.class.isAssignableFrom(type) || Byte.TYPE.isAssignableFrom(type)) {
            return Byte.valueOf(value);
        }
        else if (Short.class.isAssignableFrom(type) || Short.TYPE.isAssignableFrom(type)) {
            return Short.valueOf(value);
        }
        else if (Integer.class.isAssignableFrom(type) || Integer.TYPE.isAssignableFrom(type)) {
            return Integer.valueOf(value);
        }
        else if (Long.class.isAssignableFrom(type) || Long.TYPE.isAssignableFrom(type)) {
            return Long.valueOf(value);
        }
        else if (Float.class.isAssignableFrom(type) || Float.TYPE.isAssignableFrom(type)) {
            return Float.valueOf(value);
        }
        else if (Double.class.isAssignableFrom(type) || Double.TYPE.isAssignableFrom(type)) {
            return Double.valueOf(value);
        }
        
        return value;
    }
}
