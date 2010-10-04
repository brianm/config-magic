package org.skife.config;

import org.apache.commons.configuration.Configuration;

public class CommonsConfigSource implements ConfigSource
{
    private final Configuration config;

    public CommonsConfigSource(Configuration config) {
        this.config = config;
    }

    public String getString(String propertyName)
    {
        return config.getString(propertyName);
    }
}
