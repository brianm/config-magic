package org.skife.config;

import static junit.framework.Assert.assertEquals;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.junit.Test;

import java.util.Properties;

/**
 *
 */
public class TestCommonsConfig
{
    @Test
    public void testFoo() throws Exception {
        Properties props = new Properties();
        props.setProperty("hello", "world");
        Configuration conf = ConfigurationConverter.getConfiguration(props);
        assertEquals(conf.getString("hello"), "world");
    }
}
