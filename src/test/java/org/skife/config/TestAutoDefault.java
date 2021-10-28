package org.skife.config;

import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestAutoDefault
{
    private ConfigurationObjectFactory cof = null;

    @Before
    public void setUp()
    {
        final Properties p = new Properties();
        p.setProperty("value", "auto");
    	cof = new ConfigurationObjectFactory(p);
    }

    @After
    public void tearDown()
    {
        cof = null;
    }

    @Test
    public void testAutoClassDefault()
    {
        EmptyClass ec = cof.build(EmptyClass.class);

        Assert.assertEquals("auto-value", ec.getValue());
    }

    @Test
    public void testAbstractClassAutoDefault()
    {
    	EmptyAbstractClass ec = cof.build(EmptyAbstractClass.class);

        Assert.assertEquals("auto-value", ec.getValue());
    }

    
    public static class EmptyClass
    {
        @Config("value")
        @AutoDefault("auto-value")
        public String getValue()
        {
            return "value-auto";
        }
    }


    public static abstract class EmptyAbstractClass
    {
        @Config("value")
        @AutoDefault("auto-value")
        public String getValue()
        {
            return "value-auto";
        }
    }
}
