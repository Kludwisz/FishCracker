package com.kludwisz.fishcracker.cracker;

import com.kludwisz.fishcracker.math.Vec2;
import com.seedfinding.mccore.util.pos.CPos;

import java.util.List;

public record LikelyStructure(CPos pos, int intersectionCount, Type type) {
    public String toString() {
        return String.format("LikelyStructure{pos=%s, intersectionCount=%d, type=%s}", pos, intersectionCount, type.name());
    }

    public static LikelyStructure fromPoints(List<Vec2> intersections) {
        // calculate center as simple average
        double sumX = 0.0;
        double sumZ = 0.0;
        for (Vec2 intersection : intersections) {
            sumX += intersection.getX();
            sumZ += intersection.getZ();
        }
        int n = intersections.size();
        Vec2 pos = new Vec2(sumX / n, sumZ / n);
        CPos bestChunk = getBestChunkPos(pos);
        if (bestChunk == null)
            return null;

        return new LikelyStructure(bestChunk, n, LikelyStructure.typeAtPos(bestChunk));
    }

    private static final double MIN_DISTANCE_DIFF = 4.0 * 4.0; // TODO tuning
    private static CPos getBestChunkPos(Vec2 pos) {
        // find the chunk such that its origin (0,0) is closest to the pos.
        // only return the chunk if other chunks are significantly further away;
        // otherwise we'd be getting false readings all the time

        int chunkX = (int) Math.floor(pos.getX() / 16.0);
        int chunkZ = (int) Math.floor(pos.getZ() / 16.0);

        CPos currentBest = new CPos(chunkX, chunkZ);
        double currentDist = pos.distanceToSq(new Vec2(chunkX * 16.0, chunkZ * 16.0));
        boolean goodDifference = true;

        for (int dcx = -1; dcx <= 1; dcx++) {
            for (int dcz = -1; dcz <= 1; dcz++) {
                CPos chunk = new CPos(chunkX + dcx, chunkZ + dcz);
                double dist = new Vec2(chunk.getX() * 16.0, chunk.getZ() * 16.0).distanceToSq(pos);

                if (dist < currentDist) {
                    goodDifference = (currentDist - dist) > MIN_DISTANCE_DIFF;
                    currentBest = chunk;
                    currentDist = dist;
                }
                else {
                    goodDifference = goodDifference && (dist - currentDist) > MIN_DISTANCE_DIFF;
                }
            }
        }

        return goodDifference ? currentBest : null;
    }

    private static Type typeAtPos(CPos pos) {
        // ocean ruin 14357621, 20, 12
        boolean canBeRuin = true;
        int ruinX = ((pos.getX() % 20) + 20) % 20; // positive modulo
        int ruinZ = ((pos.getZ() % 20) + 20) % 20;
        if (ruinX >= 12 || ruinZ >= 12)
            canBeRuin = false;

        // shipwreck  165745295, 24, 20
        boolean canBeShipwreck = true;
        int shipX = ((pos.getX() % 24) + 24) % 24;
        int shipZ = ((pos.getZ() % 24) + 24) % 24;
        if (shipX >= 20 || shipZ >= 20)
            canBeShipwreck = false;

        if (canBeRuin && canBeShipwreck) return Type.ANY;
        if (canBeRuin) return Type.OCEAN_RUIN;
        if (canBeShipwreck) return Type.SHIPWRECK;
        return Type.NONE;
    }

    public enum Type {
        NONE(0.0),
        ANY(6.729),
        SHIPWRECK(8.644),
        OCEAN_RUIN(7.170);

        private final double bits;

        Type(double bits) {
            this.bits = bits;
        }

        public double getBits() {
            return bits;
        }
    }
}
