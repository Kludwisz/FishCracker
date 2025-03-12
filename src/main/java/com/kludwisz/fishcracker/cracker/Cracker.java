package com.kludwisz.fishcracker.cracker;

import com.kludwisz.fishcracker.math.Line;
import com.kludwisz.fishcracker.math.Vec2;

import java.util.ArrayList;
import java.util.List;

public class Cracker {
    private final ArrayList<Line> measuredLines = new ArrayList<>();

    public Cracker() {}

    public void reset() {
        measuredLines.clear();
    }

    public void addLineConstraint(Line line) {
        measuredLines.add(line);
    }

    public StructureModel getStructureModel() {
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
        //    fall within the circle then there's likely a structure there), create likely structures
        ArrayList<LikelyStructure> likelyStructures = new ArrayList<>();
        final double maxSq = 10.0 * 10.0;

        // sort intersesctions by how many neighbors they have (descending)
        ArrayList<Vec2> sortedIntersections = new ArrayList<>(intersections);
        sortedIntersections.sort((a, b) -> Integer.compare(
            (int) intersections.stream().filter(i -> i.distanceToSq(b) < maxSq * maxSq).count(),
            (int) intersections.stream().filter(i -> i.distanceToSq(a) < maxSq * maxSq).count()
        ));

        boolean[] usedPoints = new boolean[intersections.size()];
        for (Vec2 intersection : sortedIntersections) {
            if (usedPoints[sortedIntersections.indexOf(intersection)])
                continue;
            ArrayList<Vec2> closeIntersections = new ArrayList<>();

            for (Vec2 otherIntersection : sortedIntersections) {
                if (intersection == otherIntersection || usedPoints[sortedIntersections.indexOf(otherIntersection)])
                    continue;

                if (intersection.distanceToSq(otherIntersection) < maxSq) {
                    closeIntersections.add(otherIntersection);
                }
            }
            if (closeIntersections.size() >= 3) {
                LikelyStructure newStructure = LikelyStructure.fromPoints(closeIntersections);
                if (newStructure == null)
                    continue;

                likelyStructures.add(newStructure);
                for (Vec2 closeIntersection : closeIntersections) {
                    usedPoints[intersections.indexOf(closeIntersection)] = true;
                }
            }
        }

        // 3. order the structures by bit yield (descending)
        likelyStructures.sort((a, b) -> Double.compare(b.type().getBits(), a.type().getBits()));

        // 4. calculate total bit yield
        double totalBits = 0.0;
        for (LikelyStructure structure : likelyStructures) {
            totalBits += structure.type().getBits();
        }

        return new StructureModel(likelyStructures, totalBits);
    }

    public record StructureModel(List<LikelyStructure> structures, double bits) {}
}
