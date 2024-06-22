package org.nik.rate_limiter;

public class Interval {
    private TimeUnit unit;
    private int val;

    public Interval(TimeUnit unit, int val) {
        this.unit = unit;
        this.val = val;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public long getGranularity() {
        switch (unit) {
            case SECOND -> {
                return 1000;
            }

            case MINUTE -> {
                return 60000;
            }

            case HOUR -> {
                return 3600000;
            }
            default -> {
                throw new IllegalArgumentException("Unsupported unit: " + unit);
            }
        }
    }

    public long getDifference(long currentTime, long lastRefillTime) {
        long timeUnitsPassedSinceLastRequest = currentTime - lastRefillTime;
        return (timeUnitsPassedSinceLastRequest / getGranularity()) * val;
    }
}
