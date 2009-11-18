package org.skife.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.util.Properties;

/**
 *
 */
public class TestConfigurationObjectFactory
{
    @Test
    public void testFoo() throws Exception {
        ConfigurationObjectFactory c = new ConfigurationObjectFactory(new Properties() {{
            setProperty("hello", "world");
            setProperty("theValue", "value");
        }});
        Thing t = c.build(Thing.class);
        assertEquals("world", t.getName());
    }

    @Test
    public void testDefaultValue() throws Exception {
        ConfigurationObjectFactory c = new ConfigurationObjectFactory(new Properties());
        Thing t = c.build(Thing.class);
        assertEquals("woof", t.getName());
    }

    @Test
    public void testDefaultViaImpl() throws Exception
    {
        ConfigurationObjectFactory c = new ConfigurationObjectFactory(new Properties());
        Config2 config = c.build(Config2.class);
        assertEquals("default", config.getOption());
    }

    @Test
    public void testMemoization() throws Exception
    {
        ConfigurationObjectFactory c = new ConfigurationObjectFactory(new Properties());
        Config2 config = c.build(Config2.class);
        assertEquals("default", config.getOption());
        assertEquals("default", config.getOption());
        assertEquals(1, config.getInvocationCount());
    }

    @Test
    public void testProvidedOverridesDefault() throws Exception
    {
        ConfigurationObjectFactory c = new ConfigurationObjectFactory(new Properties() {{
            setProperty("option", "provided");
        }});
        
        Config2 config = c.build(Config2.class);
        assertEquals("provided", config.getOption());
    }

    @Test
    public void testMissingDefault() throws Exception
    {
        ConfigurationObjectFactory c = new ConfigurationObjectFactory(new Properties());
        try {
            c.build(Config3.class);
            fail("Expected exception due to missing value");
        }
        catch (Exception e) {
        }
    }

    @Test
    public void testDetectsAbstractMethod() throws Exception
    {
        ConfigurationObjectFactory c = new ConfigurationObjectFactory(new Properties());
        try {
            c.build(Config4.class);
            fail("Expected exception due to abstract method without @Config annotation");
        }
        catch (AbstractMethodError e) {
        }
    }

    @Test
    public void testTypes()
    {
        ConfigurationObjectFactory c = new ConfigurationObjectFactory(new Properties() {{
            setProperty("stringOption", "a string");
            setProperty("booleanOption", "true");
            setProperty("boxedBooleanOption", "true");
            setProperty("byteOption", Byte.toString(Byte.MAX_VALUE));
            setProperty("boxedByteOption", Byte.toString(Byte.MAX_VALUE));
            setProperty("shortOption", Short.toString(Short.MAX_VALUE));
            setProperty("boxedShortOption", Short.toString(Short.MAX_VALUE));
            setProperty("integerOption", Integer.toString(Integer.MAX_VALUE));
            setProperty("boxedIntegerOption", Integer.toString(Integer.MAX_VALUE));
            setProperty("longOption", Long.toString(Long.MAX_VALUE));
            setProperty("boxedLongOption", Long.toString(Long.MAX_VALUE));
            setProperty("floatOption", Float.toString(Float.MAX_VALUE));
            setProperty("boxedFloatOption", Float.toString(Float.MAX_VALUE));
            setProperty("doubleOption", Double.toString(Double.MAX_VALUE));
            setProperty("boxedDoubleOption", Double.toString(Double.MAX_VALUE));
        }});

        Config1 config = c.build(Config1.class);
        assertEquals("a string", config.getStringOption());
        assertEquals(true, config.getBooleanOption());
        assertEquals(Boolean.TRUE, config.getBoxedBooleanOption());
        assertEquals(Byte.MAX_VALUE, config.getByteOption());
        assertEquals(Byte.valueOf(Byte.MAX_VALUE), config.getBoxedByteOption());
        assertEquals(Short.MAX_VALUE, config.getShortOption());
        assertEquals(Short.valueOf(Short.MAX_VALUE), config.getBoxedShortOption());
        assertEquals(Integer.MAX_VALUE, config.getIntegerOption());
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), config.getBoxedIntegerOption());
        assertEquals(Long.MAX_VALUE, config.getLongOption());
        assertEquals(Long.valueOf(Long.MAX_VALUE), config.getBoxedLongOption());
        assertEquals(Float.MAX_VALUE, config.getFloatOption(), 0);
        assertEquals(Float.valueOf(Float.MAX_VALUE), config.getBoxedFloatOption());
        assertEquals(Double.MAX_VALUE, config.getDoubleOption(), 0);
        assertEquals(Double.valueOf(Double.MAX_VALUE), config.getBoxedDoubleOption());
    }

    public abstract static class Thing
    {
        @Config("hello")
        @Default("woof")
        public abstract String getName();
    }
}
