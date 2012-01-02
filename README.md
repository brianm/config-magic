# Example

Create an interface for your config object:

    public interface MyConfig
    {
        @Config("foo")
        String getFoo();

        @Config("blah")
        int getBlah();

        @Config("what")
        @Default("none")
        String getWhat();
    }

Set the properties that we mapped with @Config above (or simply call System.getProperties()):

    Properties props = new Properties();
    props.setProperty("foo", "hello");
    props.setProperty("blah", "123");

Then create the config object from the properties:

    ConfigurationObjectFactory factory = new ConfigurationObjectFactory(props);
    MyConfig conf = factory.build(MyConfig.class);

# Default values

    Using @Default() can set arbitrary default values. To set 'null' as the default value, use the @DefaultNull annotation.

# Advanced usage

        @Config({"what1", "what2"})
        @Default("none")
        String getWhat();

   will look at 'what1' first, then at 'what2' and finally fall back to the default.

# Maven dependency

To use config-magic in Maven projects:

    <dependency>
        <groupId>org.skife.config</groupId>
        <artifactId>config-magic</artifactId>
        <version>0.10</version>
    </dependency>

# Mailing List

We have a [mailing list](http://groups.google.com/group/config-magic) for development and users.
