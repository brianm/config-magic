package org.skife.config;

import org.junit.Test;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;

/**
 *
 */
public class TestConfigurationObjectFactory
{
    @Test
    public void testFoo() throws Exception {
        ConfigurationObjectFactory c = new ConfigurationObjectFactory(new Properties() {{
            setProperty("hello", "world");
        }});
        Thing t = c.build(Thing.class);
        assertEquals(t.getName(), "world");
    }

    @Test
    public void testDefaultValue() throws Exception {
        ConfigurationObjectFactory c = new ConfigurationObjectFactory(new Properties());
        Thing t = c.build(Thing.class);
        assertEquals(t.getName(), "woof");
    }

    public abstract static class Thing
    {
        @Config("hello")
        @Default("woof")
        public abstract String getName();
    }
}
