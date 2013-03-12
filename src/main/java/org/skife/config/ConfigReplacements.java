package org.skife.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If a configuration bean is created with mapped replacement values via
 * {@link ConfigurationObjectFactory#buildWithReplacements(Class, java.util.Map)},
 * this annotation designates a method which should present the provided Map.
 * The map may not be changed and is not necessarily the same instance as the original.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConfigReplacements
{

}
