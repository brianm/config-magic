package org.skife.config;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeSpan
{
    private final long period;
    private final TimeUnit unit;
    private final long millis;

    private static final Pattern SPLIT = Pattern.compile("^(\\d+)\\s?(\\w+)$");

    private static final HashMap<String,TimeUnit> UNITS = new HashMap<String,TimeUnit>();
    static {
        UNITS.put("ms", TimeUnit.MILLISECONDS);
        UNITS.put("millisecond", TimeUnit.MILLISECONDS);
        UNITS.put("milliseconds", TimeUnit.MILLISECONDS);
        UNITS.put("s", TimeUnit.SECONDS);
        UNITS.put("second", TimeUnit.SECONDS);
        UNITS.put("seconds", TimeUnit.SECONDS);
        UNITS.put("m", TimeUnit.MINUTES);
        UNITS.put("minute", TimeUnit.MINUTES);
        UNITS.put("minutes", TimeUnit.MINUTES);
        UNITS.put("h", TimeUnit.HOURS);
        UNITS.put("hour", TimeUnit.HOURS);
        UNITS.put("hours", TimeUnit.HOURS);
        UNITS.put("d", TimeUnit.DAYS);
        UNITS.put("day", TimeUnit.DAYS);
        UNITS.put("days", TimeUnit.DAYS);
    }
    
    public TimeSpan(String spec)
    {
        Matcher m = SPLIT.matcher(spec);
        if (!m.matches()) {
            throw new IllegalArgumentException(String.format("%s is not a valid time spec", spec));
        }
        String number = m.group(1);
        String type = m.group(2);
        period = Long.parseLong(number);
        unit = UNITS.get(type);
        if (unit == null) {
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

    public long getPeriod()
    {
        return period;
    }

    public TimeUnit getUnit()
    {
        return unit;
    }

    @Override
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
}
