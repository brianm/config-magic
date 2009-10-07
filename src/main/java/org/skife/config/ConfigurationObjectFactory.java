package org.skife.config;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.NoOp;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class ConfigurationObjectFactory
{
    private final Configuration config;
    private final Bully bully;

    public ConfigurationObjectFactory(Properties props) {
        this(ConfigurationConverter.getConfiguration(props), new Bully());
    }

    public ConfigurationObjectFactory(Configuration config, Bully bully) {
        this.config = config;
        this.bully = bully;
    }

    public <T> T build(Class<T> configClass) {
        ArrayList<Callback> callbacks = new ArrayList<Callback>();
        final Map<Method, Integer> slots = new HashMap<Method, Integer>();
        callbacks.add(NoOp.INSTANCE);
        int count = 1;
        for (final Method method : configClass.getMethods()) {
            if (method.isAnnotationPresent(Config.class)) {
                final Config ca = method.getAnnotation(Config.class);
                slots.put(method, count++);
                callbacks.add(new FixedValue()
                {
                    public Object loadObject() throws Exception {
                        return bully.coerce(config, method, ca.value());
                    }
                });
            }
        }

        Enhancer e = new Enhancer();
        e.setSuperclass(configClass);
        e.setCallbackFilter(new CallbackFilter() {
            public int accept(Method method) {
                return slots.containsKey(method) ? slots.get(method) : 0;
            }
        });
        e.setCallbacks(callbacks.toArray(new Callback[callbacks.size()]));
        return (T) e.create();
    }
}
