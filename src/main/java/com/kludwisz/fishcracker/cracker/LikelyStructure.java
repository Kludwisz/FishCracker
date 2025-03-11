package com.kludwisz.fishcracker.cracker;

import com.kludwisz.fishcracker.math.Vec2;

import java.util.List;

public record LikelyStructure(Vec2 pos, int intersectionCount, Type type) {
    public static LikelyStructure fromPoints(List<Vec2> intersections) {
        // calculate center as simple average
        double sumX = 0.0;
        double sumZ = 0.0;
        for (Vec2 intersection : intersections) {
            sumX += intersection.getX();
            sumZ += intersection.getZ();
        }
        int n = intersections.size();
        return new LikelyStructure(new Vec2(sumX / n, sumZ / n), n, Type.ANY);
    }

    private static Type typeAtPos(Vec2 pos) {
        // TODO implement
        // ocean_ruin  14357621, 20, 12
        // shipwreck  165745295, 24, 20
        return Type.ANY;
    }

    public enum Type {
        ANY(0.0), // TODO calculate
        SHIPWRECK(8.644),
        OCEAN_RUIN(7.170);

        private double bits;

        Type(double bits) {
            this.bits = bits;
        }

        public double getBits() {
            return bits;
        }
    }
}
