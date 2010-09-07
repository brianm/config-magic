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

# Maven dependency

To use config-magic in Maven projects:

    <dependency>
        <groupId>org.skife.config</groupId>
        <artifactId>config-magic</artifactId>
        <version>0.4</version>
    </dependency>
