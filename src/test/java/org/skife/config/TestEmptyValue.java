package org.skife.config;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class TestEmptyValue
{
    @Test(expected = IllegalArgumentException.class)
    public void testClass()
    {
        ConfigurationObjectFactory cof = new ConfigurationObjectFactory(new Properties());
        
        cof.build(EmptyClass.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInterface()
    {
        ConfigurationObjectFactory cof = new ConfigurationObjectFactory(new Properties());
        
        cof.build(EmptyInterface.class);
    }

    @Test
    public void testDefaultClass()
    {
        ConfigurationObjectFactory cof = new ConfigurationObjectFactory(new Properties());
        
        EmptyDefaultClass ec = cof.build(EmptyDefaultClass.class);

        Assert.assertEquals("default-value", ec.getValue());
    }

    @Test
    public void testAbstractDefaultClass()
    {
        ConfigurationObjectFactory cof = new ConfigurationObjectFactory(new Properties());
        
        EmptyAbstractClass ec = cof.build(EmptyAbstractClass.class);

        Assert.assertEquals("default-value", ec.getValue());
    }

    public static interface EmptyInterface
    {
        @Config("value")
        String getValue();
    }

    public static abstract class EmptyClass
    {
        @Config("value")
        public abstract String getValue();
    }


    public static abstract class EmptyAbstractClass
    {
        @Config("value")
        public String getValue()
        {
            return "default-value";
        }
    }


    public static class EmptyDefaultClass
    {
        @Config("value")
        public String getValue()
        {
            return "default-value";
        }
    }
}

