package org.nik.circuitbreaker;

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

    public int getVal() {
        return val;
    }
}
