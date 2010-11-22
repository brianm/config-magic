package org.skife.config;

import static org.hamcrest.CoreMatchers.is;

import java.net.URI;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestCoercion
{
    private ConfigurationObjectFactory c = null;

    @Before
    public void setUp()
    {
        this.c = new ConfigurationObjectFactory(new Properties() {{
            setProperty("the-url", "http://github.org/brianm/config-magic");
        }});
    }

    @After
    public void tearDown()
    {
        this.c = null;
    }

    @Test(expected=IllegalStateException.class)
    public void testBadConfig()
    {
        c.build(WibbleConfig.class);
    }

    @Test
    public void testGoodConfig()
    {
        CoercionConfig cc = c.build(CoercionConfig.class);
        Assert.assertThat(cc.getURI(), is(URI.create("http://github.org/brianm/config-magic")));
    }
}
