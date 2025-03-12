package com.kludwisz.fishcracker;

import com.kludwisz.fishcracker.math.Line;
import com.kludwisz.fishcracker.measurment.MeasurmentParser;

public class Tests {
    public static void triangulationTest() {
        MeasurmentParser parser = new MeasurmentParser();
        Line ln1 = parser.parseMeasurment("/execute in minecraft:overworld run tp @s 1472.36 85.36 195.53 50.93 10.65");
        Line ln2 = parser.parseMeasurment("/execute in minecraft:overworld run tp @s 1513.49 85.36 274.22 80.62 8.61");

        System.out.println(ln1.intersection(ln2));
    }
}
