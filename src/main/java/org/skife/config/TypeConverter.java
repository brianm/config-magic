package org.skife.config;

/**
 *
 */
public interface TypeConverter<T>
{
    T convert(String value);
}
