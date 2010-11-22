package org.skife.config;

import java.net.URL;

interface BadCoercionConfig
{
    @Config("the-url")
    URL getURL();
}
