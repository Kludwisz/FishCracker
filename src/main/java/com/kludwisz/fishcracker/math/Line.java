package com.kludwisz.fishcracker.math;

public class Line {
    private final double angle;
    private final Vec2 start;

    public Line(Vec2 start, double angle) {
        this.start = start;
        this.angle = (angle + 90.0) * Math.PI / 180.0; // convert to radians and set up properly
    }

    public Vec2 getStart() {
        return start;
    }

    public double getAngle() {
        return angle;
    }

    public Vec2 intersection(Line other) {
        double x1 = start.getX();
        double z1 = start.getZ();
        double x2 = other.getStart().getX();
        double z2 = other.getStart().getZ();
        double m1 = Math.tan(angle);
        double m2 = Math.tan(other.getAngle());
        if (m1 == m2) return null; // parallel lines

        double x = (m1 * x1 - m2 * x2 + z2 - z1) / (m1 - m2);
        double z = m1 * (x - x1) + z1;

        return new Vec2(x, z);
    }

    public String toString() {
        return String.format(
                "[x=%.2f z=%.2f theta=%.2f]", getStart().getX(), getStart().getZ(), getAngle()
        );
    }
}
