package com.kludwisz.fishcracker.cracker;

import com.kludwisz.fishcracker.math.Line;
import com.kludwisz.fishcracker.math.Vec2;
import com.seedfinding.mcbiome.source.BiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.structure.OceanRuin;
import com.seedfinding.mcfeature.structure.Shipwreck;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Cracker {
    private final ArrayList<Line> measuredLines = new ArrayList<>();
    private StructureModel currentModel;

    public Cracker() {}

    public void reset() {
        measuredLines.clear();
        currentModel = null;
    }

    public void addLineConstraint(Line line) {
        currentModel = null;
        if (line != null) {
            measuredLines.add(line);
        }
    }

    public StructureModel getStructureModel() {
        if (currentModel != null)
            return currentModel;

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
        final double maxSq = 6.0 * 6.0;

        // sort intersesctions by how many neighbors they have (descending)
        ArrayList<Vec2> sortedIntersections = new ArrayList<>(intersections);
        sortedIntersections.sort((a, b) -> Integer.compare(
            (int) intersections.stream().filter(i -> i.distanceToSq(b) < maxSq).count(),
            (int) intersections.stream().filter(i -> i.distanceToSq(a) < maxSq).count()
        ));

        boolean[] usedPoints = new boolean[intersections.size()];
        for (int i = 0; i < sortedIntersections.size(); i++) {
            Vec2 centralIntersection = sortedIntersections.get(i);
            if (usedPoints[sortedIntersections.indexOf(centralIntersection)])
                continue;

            ArrayList<Vec2> closeIntersections = new ArrayList<>();
            for (int j = 0; j < sortedIntersections.size(); j++) {
                Vec2 otherIntersection = sortedIntersections.get(j);
                if (usedPoints[sortedIntersections.indexOf(otherIntersection)])
                    continue;

                if (centralIntersection.distanceToSq(otherIntersection) < maxSq) {
                    closeIntersections.add(otherIntersection);
                }
            }
            if (closeIntersections.size() <3)
                continue;

            LikelyStructure newStructure = LikelyStructure.fromPoints(closeIntersections);
            if (newStructure == null) {
                continue;
            }


            likelyStructures.add(newStructure);
            for (Vec2 closeIntersection : closeIntersections) {
                usedPoints[sortedIntersections.indexOf(closeIntersection)] = true;
            }
        }

        // 3. order the structures by bit yield (descending)
        likelyStructures.sort((a, b) -> Double.compare(b.type().getBits(), a.type().getBits()));

        // 4. calculate total bit yield
        double totalBits = 0.0;
        for (LikelyStructure structure : likelyStructures) {
            totalBits += structure.type().getBits();
        }

        this.currentModel = new StructureModel(likelyStructures, totalBits);
        return this.currentModel;
    }

    public List<Long> getStructreSeeds() throws CrackingFailedException {
        if (currentModel == null)
            this.getStructureModel();
        if (currentModel.bits() < 32.0D)
            throw new CrackingFailedException("Not enough information to crack the seed");

        // the faster part of the process, no need for multithreading here
        List<Integer> shortSeeds = IntStream.range(0, 1 << 19).boxed().parallel()
                .filter(seed -> {
                    ShortStateRand rand = new ShortStateRand();
                    for (LikelyStructure structure : currentModel.structures()) {
                        if (!structure.lift(seed, rand))
                            return false;
                    }
                    return true;
                }).toList();

        // now we need to bruteforce the remaining 48-19 = 29 bits
        // (potentially several times, there can sometimes be multiple short seeds)

        // safeguard, we don't want the code to run forever
        if (shortSeeds.size() >= 8)
            throw new CrackingFailedException("Got too many results from lifting: " + shortSeeds.size());
        if (shortSeeds.isEmpty())
            throw new CrackingFailedException("Got no results from lifting");

        ArrayList<Long> structureSeeds = new ArrayList<>();
        for (int shortSeed : shortSeeds) {
            structureSeeds.addAll(
                LongStream.range(0L, 1L << 29).parallel()
                        .map(upper -> (upper << 19) | shortSeed)
                        .filter(structureSeed -> {
                            ChunkRand rand = new ChunkRand();
                            for (LikelyStructure structure : currentModel.structures()) {
                                if (!structure.checkExactPos(structureSeed, rand))
                                    return false;
                            }
                            return true;
                        })
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
            );
        }

        if (structureSeeds.isEmpty())
            throw new CrackingFailedException("Got no results from post-lifting filter");
        return structureSeeds;
    }

    public boolean testFullWorldSeed(long seed) {
        BiomeSource obs = BiomeSource.of(Dimension.OVERWORLD, MCVersion.v1_16_1, seed);
        Shipwreck shipwreck = new Shipwreck(MCVersion.v1_16_1);
        OceanRuin oceanRuin = new OceanRuin(MCVersion.v1_16_1);

        for (LikelyStructure structure : currentModel.structures()) {
            if (structure.type() == LikelyStructure.Type.SHIPWRECK) {
                if (!shipwreck.canSpawn(structure.pos(), obs))
                    return false;
            }
            else if (structure.type() == LikelyStructure.Type.OCEAN_RUIN) {
                if (!oceanRuin.canSpawn(structure.pos(), obs))
                    return false;
            }
            else {
                if (!shipwreck.canSpawn(structure.pos(), obs) && !oceanRuin.canSpawn(structure.pos(), obs))
                    return false;
            }
        }

        return true;
    }

    public void removeStructureFromModel(int index) {
        if (currentModel != null) {
            currentModel.structures().remove(index);
            // not setting the model to null to actually use the remaining information
        }
    }

    public void removeLastLineConstraint() {
        if (!measuredLines.isEmpty()) {
            measuredLines.removeLast();
            currentModel = null;
        }
    }

    public record StructureModel(List<LikelyStructure> structures, double bits) {}
}
