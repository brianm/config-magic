package org.skife.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

public class CommonsConfigSource implements ConfigSource
{
    private final Configuration config;

    public CommonsConfigSource(Configuration config) {
        this.config = config;
    }

    public String getString(String propertyName)
    {
        final String [] strings = config.getStringArray(propertyName);
        if (strings == null || strings.length == 0) {
            return null;
        }
        return StringUtils.join(strings, ",");
    }
}
