package com.kludwisz.fishcracker.cracker;

import com.kludwisz.fishcracker.math.Vec2;

import java.util.List;

public record LikelyStructure(Vec2 pos, int intersectionCount) {
    public static LikelyStructure fromPoints(List<Vec2> intersections) {
        // calculate center as simple average
        double sumX = 0.0;
        double sumZ = 0.0;
        for (Vec2 intersection : intersections) {
            sumX += intersection.getX();
            sumZ += intersection.getZ();
        }
        int n = intersections.size();
        return new LikelyStructure(new Vec2(sumX / n, sumZ / n), n);
    }
}
