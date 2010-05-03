package org.skife.config;

public enum TestEnum
{
    ONE,
    TWO,
    THREE;

    public String toString()
    {
        return name().toLowerCase();
    }
}
