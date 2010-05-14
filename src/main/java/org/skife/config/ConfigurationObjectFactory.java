package org.skife.config;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public <T> T buildWithReplacements(Class<T> configClass, Map<String, String> mappedReplacements) {
        return internalBuild(configClass, mappedReplacements);
    }

    public <T> T build(Class<T> configClass) {
        return internalBuild(configClass, null);
    }

    private <T> T internalBuild(Class<T> configClass, Map<String, String> mappedReplacements) {
        ArrayList<Callback> callbacks = new ArrayList<Callback>();
        final Map<Method, Integer> slots = new HashMap<Method, Integer>();
        callbacks.add(NoOp.INSTANCE);
        int count = 1;
        for (final Method method : configClass.getMethods()) {
            if (method.isAnnotationPresent(Config.class)) {
                final Config annotation = method.getAnnotation(Config.class);
                slots.put(method, count++);

                if (method.getParameterTypes().length > 0) {
                    if ( mappedReplacements != null ) {
                        throw new RuntimeException("Replacements are not supported for parameterized config methods");
                    }
                    buildParameterized(callbacks, method, annotation);
                }
                else {
                    buildSimple(callbacks, method, annotation, mappedReplacements);
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
        //noinspection unchecked
        return (T) e.create();
    }

    private void buildSimple(ArrayList<Callback> callbacks, Method method, Config annotation,
                             Map<String, String> mappedReplacements) {
        String propertyName = annotation.value();
        if ( mappedReplacements != null ) {
            propertyName = applyReplacements(propertyName, mappedReplacements);
        }
        String value = config.getString(propertyName);

        if (value == null && method.isAnnotationPresent(Default.class)) {
            value = method.getAnnotation(Default.class).value();
        }

        if (value != null) {
            final Object finalValue = bully.coerce(method.getReturnType(), value);
            callbacks.add(new FixedValue() {
                public Object loadObject() throws Exception {
                    return finalValue;
                }
            });
        }
        else if (Modifier.isAbstract(method.getModifiers())) {
            // no default (via impl or @Default) and no configured value
            throw new RuntimeException(String.format("No value present for '%s' in [%s]",
                                                     propertyName, method.toGenericString()));
        }
        else {
            callbacks.add(NoOp.INSTANCE);
        }
    }

    private String applyReplacements(String propertyName, Map<String, String> mappedReplacements) {
        for ( String key : mappedReplacements.keySet() ) {
            String token = makeToken(key);
            String replacement = mappedReplacements.get(key);
            propertyName = propertyName.replace(token, replacement);
        }
        return propertyName;
    }

    private void buildParameterized(ArrayList<Callback> callbacks, Method method, Config annotation) {
        if (!method.isAnnotationPresent(Default.class)) {
            throw new RuntimeException(String.format("No value present for '%s' in [%s]",
                                                     annotation.value(), method.toGenericString()));
        }
        String defaultValue = method.getAnnotation(Default.class).value();

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        final List<String> paramTokenList = new ArrayList<String>();
        for (Annotation[] parameterTab : parameterAnnotations) {
            for (Annotation parameter : parameterTab) {
                if (parameter.annotationType().equals(Param.class)) {
                    Param paramAnnotation = (Param) parameter;
                    paramTokenList.add(makeToken(paramAnnotation.value()));
                    break;
                }
            }
        }

        if (paramTokenList.size() != method.getParameterTypes().length) {
            throw new RuntimeException(String.format("Method [%s] is missing one or more @Param annotations",
                                                     method.toGenericString()));
        }

        final Object bulliedDefaultValue = bully.coerce(method.getReturnType(), defaultValue);
        final String annotationValue = annotation.value();
        callbacks.add(new MethodInterceptor() {
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                String property = annotationValue;
                if (args.length == paramTokenList.size()) {
                    for (int i = 0; i < paramTokenList.size(); ++i) {
                        property = property.replace(paramTokenList.get(i), String.valueOf(args[i]));
                    }
                    String value = config.getString(property);
                    if (value != null) {
                        return bully.coerce(method.getReturnType(), value);
                    }
                }
                else {
                    throw new IllegalStateException("Argument list doesn't match @Param list");
                }

                return bulliedDefaultValue;
            }
        });
    }

    private String makeToken(String temp) {
        return "${" + temp + "}";
    }
}
