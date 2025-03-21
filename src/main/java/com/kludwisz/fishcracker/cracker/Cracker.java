package com.kludwisz.fishcracker.cracker;

import com.kludwisz.fishcracker.math.Line;
import com.kludwisz.fishcracker.math.Vec2;
import com.kludwisz.fishcracker.measurment.MeasurmentParser;
import com.seedfinding.mccore.rand.ChunkRand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Cracker {
    private final ArrayList<Line> measuredLines = new ArrayList<>();

    public Cracker() {
//        // TODO remove, test data
//        MeasurmentParser measurmentParser = new MeasurmentParser();
//        String[] commands = {
//                "/execute in minecraft:overworld run tp @s -845.48 66.24 265.35 234.75 8.22",
//                "/execute in minecraft:overworld run tp @s -846.15 76.36 225.99 337.78 14.04",
//                "/execute in minecraft:overworld run tp @s -860.75 76.36 235.74 306.35 10.06",
//                "/execute in minecraft:overworld run tp @s -810.29 76.36 225.01 243.29 14.04"
//        };
//        for (String command : commands) {
//            addLineConstraint(measurmentParser.parseMeasurment(command));
//        }
//        getStructureModel();
    }

    public void reset() {
        measuredLines.clear();
    }

    public void addLineConstraint(Line line) {
        if (line != null)
            measuredLines.add(line);
    }

    public StructureModel getStructureModel() {
        // 1. calculate the intersection points of all lines
        ArrayList<Vec2> intersections = new ArrayList<>();
        for (int i = 0; i < measuredLines.size(); i++) {
            Line line1 = measuredLines.get(i);
            for (int j = i + 1; j < measuredLines.size(); j++) {
                Line line2 = measuredLines.get(j);
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
        for (Vec2 intersection : sortedIntersections) {
            System.out.println(intersection + " " + intersections.stream().filter(i -> i.distanceToSq(intersection) < maxSq * maxSq).count());
        }

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
            if (closeIntersections.size() >= 2) {
                closeIntersections.add(intersection);
                LikelyStructure newStructure = LikelyStructure.fromPoints(closeIntersections);
                if (newStructure == null)
                    continue;

                likelyStructures.add(newStructure);
                for (Vec2 closeIntersection : closeIntersections) {
                    usedPoints[sortedIntersections.indexOf(closeIntersection)] = true;
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

    public List<Long> getStructreSeeds(StructureModel model) {
        // the faster part of the process, no need for multithreading here
        List<Integer> shortSeeds = IntStream.range(0, 1 << 19).boxed().parallel()
                .filter(seed -> {
                    ShortStateRand rand = new ShortStateRand();
                    for (LikelyStructure structure : model.structures()) {
                        if (!structure.lift(seed, rand))
                            return false;
                    }
                    return true;
                }).toList();

        // now we need to bruteforce the remaining 48-19 = 29 bits
        // (potentially several times, there can sometimes be multiple short seeds)

        // safeguard, we don't want the code to run forever
        if (shortSeeds.size() >= 8)
            return null;

        ArrayList<Long> structureSeeds = new ArrayList<>();
        for (int shortSeed : shortSeeds) {
            structureSeeds.addAll(
                LongStream.range(0L, 1L << 29).parallel()
                        .map(upper -> (upper << 19) | shortSeed)
                        .filter(structureSeed -> {
                            ChunkRand rand = new ChunkRand();
                            for (LikelyStructure structure : model.structures()) {
                                if (!structure.checkExactPos(structureSeed, rand))
                                    return false;
                            }
                            return true;
                        })
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
            );
        }

        return structureSeeds;
    }

    public record StructureModel(List<LikelyStructure> structures, double bits) {}
}
