package me.vlink102.personal.overwatch.minecraftlink;

public class TimeUnit {
    private final double msValue;

    public TimeUnit(double value, Converter converter) {
        this.msValue = switch (converter) {
            case TICK -> value * 50L;
            case SECOND -> value * 1000L;
            case MILLISECOND -> value;
        };
    }

    public int getTicks() {
        return (int) (msValue / 50);
    }

    public long getMs() {
        return (long) msValue;
    }

    public double getSeconds() {
        return msValue / 1000;
    }

    public enum Converter {
        SECOND,
        TICK,
        MILLISECOND;

        public TimeUnit of(double value) {
            return new TimeUnit(value, this);
        }
    }

    public TimeUnit merge(TimeUnit timeUnit) {
        return new TimeUnit(timeUnit.msValue + msValue, Converter.MILLISECOND);
    }

    public TimeUnit add(double value, Converter converter) {
        TimeUnit timeUnit = converter.of(value);
        return merge(timeUnit);
    }

    public TimeUnit addSysTime() {
        return new TimeUnit(msValue + System.currentTimeMillis(), Converter.MILLISECOND);
    }
}
