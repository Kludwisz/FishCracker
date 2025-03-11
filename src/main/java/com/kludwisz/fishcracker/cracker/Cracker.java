package com.kludwisz.fishcracker.cracker;

import com.kludwisz.fishcracker.math.Line;
import com.kludwisz.fishcracker.math.Vec2;
import com.seedfinding.mccore.util.pos.CPos;

import java.util.ArrayList;

public class Cracker {
    private final ArrayList<Line> measuredLines = new ArrayList<>();

    public Cracker() {}

    public void reset() {
        measuredLines.clear();
    }

    public void addLineConstraint(Line line) {
        measuredLines.add(line);
    }

    public ArrayList<CPos> getStructurePositions() {
        // TODO implement the actual algo:

        // 1. calculate the intersection points of all lines
        ArrayList<Vec2> intersections = new ArrayList<>();
        for (Line line1 : measuredLines) {
            for (Line line2 : measuredLines) {
                if (line1 == line2) continue; // comparing refs should be enough here
                Vec2 intersection = line1.intersection(line2);
                if (intersection != null) {
                    intersections.add(intersection);
                }
            }
        }

        // 2. find all 3-line intersection points (draw a circle around each, if 2 more intersections
        //    fall within the circle then there's likely a structure there)
        ArrayList<LikelyStructure> likelyStructures = new ArrayList<>();

        final double maxDistanceSq = 10.0 * 10.0;
        for (Vec2 intersection : intersections) {
            ArrayList<Vec2> closeIntersections = new ArrayList<>();

            for (Vec2 otherIntersection : intersections) {
                if (intersection == otherIntersection)
                    continue;

                if (intersection.distanceToSq(otherIntersection) < maxDistanceSq) {
                    closeIntersections.add(otherIntersection);
                }
            }
            if (closeIntersections.size() >= 3) {
                likelyStructures.add(LikelyStructure.fromPoints(closeIntersections));
            }
        }

        // 3. calculate which structures could generate in the resulting positions
        // 4. order the structures by bit yield (descending)
        // 5. calculate total bit yield
        return new ArrayList<>();
    }
}
