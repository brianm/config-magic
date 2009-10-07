package org.skife.config;

import org.apache.commons.configuration.Configuration;

import java.lang.reflect.Method;

/**
 *
 */
public class Bully
{
    public Object coerce(Configuration config, Method method, String key) {

        String rs = config.getString(key);
        if (rs == null) {
            if (method.isAnnotationPresent(Default.class)) {
                return method.getAnnotation(Default.class).value();
            }
            else {
                throw new RuntimeException("No value present for " + key);
            }
        }
        return rs;
    }
}
