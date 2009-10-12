package org.skife.config;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.NoOp;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigurationObjectFactory
{
    private final Configuration config;
    private final Bully bully;

    public ConfigurationObjectFactory(Properties props) {
        this(ConfigurationConverter.getConfiguration(props));
    }

    public ConfigurationObjectFactory(Configuration config) {
        this.config = config;
        this.bully = new Bully(); 
    }

    public <T> T build(Class<T> configClass) {
        ArrayList<Callback> callbacks = new ArrayList<Callback>();
        final Map<Method, Integer> slots = new HashMap<Method, Integer>();
        callbacks.add(NoOp.INSTANCE);
        int count = 1;
        for (final Method method : configClass.getMethods()) {
            if (method.isAnnotationPresent(Config.class)) {
                final Config annotation = method.getAnnotation(Config.class);
                slots.put(method, count++);

                String value = config.getString(annotation.value());

                if (value == null && method.isAnnotationPresent(Default.class)) {
                    value = method.getAnnotation(Default.class).value();
                }

                if (value != null) {
                    final Object finalValue = bully.coerce(method.getReturnType(), value);
                    callbacks.add(new FixedValue()
                    {
                        public Object loadObject() throws Exception {
                            return finalValue;
                        }
                    });
                }
                else if (Modifier.isAbstract(method.getModifiers())) {
                    // no default (via impl or @Default) and no configured value
                    throw new RuntimeException(String.format("No value present for '%s' in [%s]",
                            annotation.value(), method.toGenericString()));
                }
                else {
                    callbacks.add(NoOp.INSTANCE);
                }
            }
            else if (Modifier.isAbstract(method.getModifiers())) {
                throw new AbstractMethodError(String.format("Method [%s] does is abstract but does not have an @Config annotation",
                        method.toGenericString()));
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
