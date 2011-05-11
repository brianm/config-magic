package org.skife.config;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeSpan
{
    private final long period;
    private final TimeUnit unit;
    private final long millis;

    private static final Pattern SPLIT = Pattern.compile("^(\\d+)(\\w+)$");

    public TimeSpan(String spec)
    {
        Matcher m = SPLIT.matcher(spec);
        if (!m.matches()) {
            throw new IllegalArgumentException(String.format("%s is not a vlid time spec", spec));
        }
        String number = m.group(1);
        String type = m.group(2);
        period = Long.parseLong(number);
        if ("m".equals(type)) {
            unit = TimeUnit.MINUTES;
        }
        else if ("s".equals(type)) {
            unit = TimeUnit.SECONDS;
        }
        else if ("ms".equals(type)) {
            unit = TimeUnit.MILLISECONDS;
        }
        else if ("h".equals(type)) {
            unit = TimeUnit.HOURS;
        }
        else if ("d".equals(type)) {
            unit = TimeUnit.DAYS;
        }
        else {
            throw new IllegalArgumentException(String.format("%s is not a valid time unit in %s", type, spec));
        }
        millis = TimeUnit.MILLISECONDS.convert(period, unit);
    }

    public TimeSpan(long period, TimeUnit unit)
    {
        this.period = period;
        this.unit = unit;
        this.millis = TimeUnit.MILLISECONDS.convert(period, unit);
    }

    public long getMillis() {
        return millis;
    }

    public String toString()
    {
        switch (unit) {
            case SECONDS:
                return period + "s";
            case MINUTES:
                return period + "m";
            case HOURS:
                return period + "h";
            case DAYS:
                return period + "d";
            default:
                return period + "ms";
        }
    }

    @Override
    public int hashCode()
    {
        return 31 + (int)(millis ^ (millis >>> 32));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TimeSpan other = (TimeSpan)obj;

        return millis == other.millis;
    }

    public long getPeriod()
    {
        return period;
    }

    public TimeUnit getUnit()
    {
        return unit;
    }
}
