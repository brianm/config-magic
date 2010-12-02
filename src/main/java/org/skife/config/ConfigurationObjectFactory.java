package org.skife.config;


import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConfigurationObjectFactory
{
    private static final ConcurrentMap<Class, Factory> factories = new ConcurrentHashMap<Class, Factory>();
    private final ConfigSource config;
    private final Bully bully;

    public ConfigurationObjectFactory(Properties props)
    {
        this(new SimplePropertyConfigSource(props));
    }

    public ConfigurationObjectFactory(ConfigSource config)
    {
        this.config = config;
        this.bully = new Bully();
    }

    public void addCoercible(final Coercible<?> coercible)
    {
        this.bully.addCoercible(coercible);
    }


    public <T> T buildWithReplacements(Class<T> configClass, Map<String, String> mappedReplacements)
    {
        return internalBuild(configClass, mappedReplacements);
    }

    public <T> T build(Class<T> configClass)
    {
        return internalBuild(configClass, null);
    }

    private <T> T internalBuild(Class<T> configClass, Map<String, String> mappedReplacements)
    {
        ArrayList<Callback> callbacks = new ArrayList<Callback>();
        final Map<Method, Integer> slots = new HashMap<Method, Integer>();
        callbacks.add(NoOp.INSTANCE);
        int count = 1;
        for (final Method method : configClass.getMethods()) {
            if (method.isAnnotationPresent(Config.class)) {
                final Config annotation = method.getAnnotation(Config.class);
                slots.put(method, count++);

                if (method.getParameterTypes().length > 0) {
                    if (mappedReplacements != null) {
                        throw new RuntimeException("Replacements are not supported for parameterized config methods");
                    }
                    buildParameterized(callbacks, method, annotation);
                }
                else {
                    buildSimple(callbacks, method, annotation, mappedReplacements);
                }
            }
            else if (Modifier.isAbstract(method.getModifiers())) {
                throw new AbstractMethodError(String.format("Method [%s] is abstract and lacks an @Config annotation",
                                                            method.toGenericString()));
            }
        }


        if (factories.containsKey(configClass)) {
            Factory f = factories.get(configClass);
            return (T) f.newInstance(callbacks.toArray(new Callback[callbacks.size()]));
        }
        else {
            Enhancer e = new Enhancer();
            e.setSuperclass(configClass);
            e.setCallbackFilter(new ConfigMagicCallbackFilter(slots));
            e.setCallbacks(callbacks.toArray(new Callback[callbacks.size()]));
            //noinspection unchecked
            T rt = (T) e.create();
            factories.putIfAbsent(configClass, (Factory) rt);
            return rt;
        }
    }

    private void buildSimple(ArrayList<Callback> callbacks, Method method, Config annotation,
                             Map<String, String> mappedReplacements)
    {
        String[] propertyNames = annotation.value();

        if (propertyNames == null || propertyNames.length == 0) {
            throw new IllegalArgumentException("Method " +
                                               method.toGenericString() +
                                               " declares config annotation but no field name!");
        }

        String value = null;

        for (String propertyName : propertyNames) {
            if (mappedReplacements != null) {
                propertyName = applyReplacements(propertyName, mappedReplacements);
            }
            value = config.getString(propertyName);

            // First value found wins
            if (value != null) {
                break;
            }
        }

        if (value == null && method.isAnnotationPresent(Default.class)) {
            value = method.getAnnotation(Default.class).value();
        }

        if (value == null && Modifier.isAbstract(method.getModifiers())) {
            throw new IllegalArgumentException(String.format("No value present for '%s' in [%s]",
                                                             prettyPrint(propertyNames, mappedReplacements),
                                                             method.toGenericString()));
        }
        else {
            final Object finalValue = bully.coerce(method.getReturnType(), value);
            callbacks.add(new ConfigMagicFixedValue(finalValue));
        }
    }

    private String applyReplacements(String propertyName, Map<String, String> mappedReplacements)
    {
        for (String key : mappedReplacements.keySet()) {
            String token = makeToken(key);
            String replacement = mappedReplacements.get(key);
            propertyName = propertyName.replace(token, replacement);
        }
        return propertyName;
    }

    private void buildParameterized(ArrayList<Callback> callbacks, Method method, Config annotation)
    {
        if (!method.isAnnotationPresent(Default.class)) {
            throw new IllegalArgumentException(String.format("No value present for '%s' in [%s]",
                                                             prettyPrint(annotation.value(), null), 
                                                             method.toGenericString()));
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
        final String[] annotationValues = annotation.value();

        if (annotationValues == null || annotationValues.length == 0) {
            throw new IllegalArgumentException("Method " +
                                               method.toGenericString() +
                                               " declares config annotation but no field name!");
        }

        callbacks.add(new ConfigMagicMethodInterceptor(config,
                                                       annotationValues,
                                                       paramTokenList,
                                                       bully,
                                                       bulliedDefaultValue));
    }

    private String makeToken(String temp)
    {
        return "${" + temp + "}";
    }

    private String prettyPrint(String[] values, final Map<String, String> mappedReplacements)
    {
        if (values == null || values.length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < values.length; i++) {
            sb.append(values[i]);
            if (i < (values.length - 1)) {
                sb.append(", ");
            }
        }
        sb.append(']');
        if (mappedReplacements != null && mappedReplacements.size() > 0) {
            sb.append(" translated to [");
            for (int i = 0; i < values.length; i++) {
                sb.append(applyReplacements(values[i], mappedReplacements));
                if (i < (values.length - 1)) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }

        return sb.toString();
    }

    private static final class ConfigMagicFixedValue implements MethodInterceptor
    {
        private final Handler handler;

        private ConfigMagicFixedValue(final Object finalValue)
        {
            if (finalValue == null) {
                this.handler = new InvokeSuperHandler();
            }
            else {
                handler = new FixedValueHandler(finalValue);
            }
        }

        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable
        {
            return handler.handle(methodProxy, o, objects);
        }

        private static interface Handler
        {
            Object handle(MethodProxy m, Object o, Object[] args) throws Throwable;
        }

        private static class InvokeSuperHandler implements Handler
        {
            public Object handle(MethodProxy m, Object o, Object[] args) throws Throwable
            {
                return m.invokeSuper(o, args);
            }
        }

        private static class FixedValueHandler implements Handler
        {
            private final Object finalValue;

            public FixedValueHandler(Object finalValue)
            {
                this.finalValue = finalValue;
            }

            public Object handle(MethodProxy m, Object o, Object[] args) throws Throwable
            {
                return finalValue;
            }
        }

    }


    private static final class ConfigMagicCallbackFilter implements CallbackFilter
    {
        private final Map<Method, Integer> slots;

        private ConfigMagicCallbackFilter(final Map<Method, Integer> slots)
        {
            this.slots = slots;
        }

        public int accept(Method method)
        {
            return slots.containsKey(method) ? slots.get(method) : 0;
        }
    }

    private static final class ConfigMagicMethodInterceptor implements MethodInterceptor
    {
        private final ConfigSource config;
        private final String[] properties;
        private final Bully bully;
        private final Object defaultValue;
        private final List<String> paramTokenList;

        private ConfigMagicMethodInterceptor(final ConfigSource config,
                                             final String[] properties,
                                             final List<String> paramTokenList,
                                             final Bully bully,
                                             final Object defaultValue)
        {
            this.config = config;
            this.properties = properties;
            this.paramTokenList = paramTokenList;
            this.bully = bully;
            this.defaultValue = defaultValue;
        }

        public Object intercept(final Object o,
                                final Method method,
                                final Object[] args,
                                final MethodProxy methodProxy) throws Throwable
        {
            for (String property : properties) {
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
            }
            return defaultValue;
        }
    }
}
