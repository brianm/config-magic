package org.skife.config;

class Config2
{
    // optional w/ default value
    @Config("option")
    public String getOption()
    {
        return "default";
    }
}