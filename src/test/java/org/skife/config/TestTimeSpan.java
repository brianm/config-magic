package org.skife.config;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestTimeSpan
{
    private ConfigurationObjectFactory cof;

    @Before
    public void setUp()
    {
        cof = new ConfigurationObjectFactory(new Properties());
    }

    @After
    public void tearDown()
    {
        cof = null;
    }

    @Test
    public void testMilliSeconds()
    {
        ClassWithMilliseconds ec = cof.build(ClassWithMilliseconds.class);

        Assert.assertEquals(5, ec.getValue().getPeriod());
        Assert.assertEquals(TimeUnit.MILLISECONDS, ec.getValue().getUnit());
        Assert.assertEquals(new TimeSpan(5, TimeUnit.MILLISECONDS), ec.getValue());
        Assert.assertEquals(5, ec.getValue().getMillis());
    }

    @Test
    public void testSeconds()
    {
        ClassWithSeconds ec = cof.build(ClassWithSeconds.class);

        Assert.assertEquals(5, ec.getValue().getPeriod());
        Assert.assertEquals(TimeUnit.SECONDS, ec.getValue().getUnit());
        Assert.assertEquals(new TimeSpan(5, TimeUnit.SECONDS), ec.getValue());
        Assert.assertEquals(TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS), ec.getValue().getMillis());
    }
    
    @Test
    public void testMinutes()
    {
        ClassWithMinutes ec = cof.build(ClassWithMinutes.class);

        Assert.assertEquals(5, ec.getValue().getPeriod());
        Assert.assertEquals(TimeUnit.MINUTES, ec.getValue().getUnit());
        Assert.assertEquals(new TimeSpan(5, TimeUnit.MINUTES), ec.getValue());
        Assert.assertEquals(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES), ec.getValue().getMillis());
    }

    @Test
    public void testHours()
    {
        ClassWithHours ec = cof.build(ClassWithHours.class);

        Assert.assertEquals(5, ec.getValue().getPeriod());
        Assert.assertEquals(TimeUnit.HOURS, ec.getValue().getUnit());
        Assert.assertEquals(new TimeSpan(5, TimeUnit.HOURS), ec.getValue());
        Assert.assertEquals(TimeUnit.MILLISECONDS.convert(5, TimeUnit.HOURS), ec.getValue().getMillis());
    }

    @Test
    public void testDays()
    {
        ClassWithDays ec = cof.build(ClassWithDays.class);

        Assert.assertEquals(5, ec.getValue().getPeriod());
        Assert.assertEquals(TimeUnit.DAYS, ec.getValue().getUnit());
        Assert.assertEquals(new TimeSpan(5, TimeUnit.DAYS), ec.getValue());
        Assert.assertEquals(TimeUnit.MILLISECONDS.convert(5, TimeUnit.DAYS), ec.getValue().getMillis());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoUnit()
    {
        cof.build(ClassWithTimespanWithoutUnit.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalUnit()
    {
        cof.build(ClassWithTimespanWithIllegalUnit.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhitespace()
    {
        cof.build(ClassWithTimespanWithWhitespace.class);
    }

    public static abstract class ClassWithMilliseconds
    {
        @Config("value")
        @Default("5ms")
        public abstract TimeSpan getValue();
    }

    public static abstract class ClassWithSeconds
    {
        @Config("value")
        @Default("5s")
        public abstract TimeSpan getValue();
    }

    public static abstract class ClassWithMinutes
    {
        @Config("value")
        @Default("5m")
        public abstract TimeSpan getValue();
    }

    public static abstract class ClassWithHours
    {
        @Config("value")
        @Default("5h")
        public abstract TimeSpan getValue();
    }

    public static abstract class ClassWithDays
    {
        @Config("value")
        @Default("5d")
        public abstract TimeSpan getValue();
    }

    public static abstract class ClassWithTimespanWithoutUnit
    {
        @Config("value")
        @Default("5")
        public abstract TimeSpan getValue();
    }

    public static abstract class ClassWithTimespanWithIllegalUnit
    {
        @Config("value")
        @Default("5x")
        public abstract TimeSpan getValue();
    }

    public static abstract class ClassWithTimespanWithWhitespace
    {
        @Config("value")
        @Default("5 h")
        public abstract TimeSpan getValue();
    }
}
