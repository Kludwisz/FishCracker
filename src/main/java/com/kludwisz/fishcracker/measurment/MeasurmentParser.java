package com.kludwisz.fishcracker.measurment;

import com.kludwisz.fishcracker.math.Line;
import com.kludwisz.fishcracker.math.Vec2;

import java.util.ArrayList;
import java.util.List;

public class MeasurmentParser {
    // /execute in minecraft:overworld run tp @s -553.92 64.05 -1034.76 2.62 8.41
    private static final String TP_FORMAT = "/execute in minecraft:overworld run tp @s %f %f %f %f %f";

    public Line parseMeasurment(String clipboardContent) {
        // parse the clipboard content and display the angle
        try {
            List<Double> values = extractValues(clipboardContent);
            boolean wasMeasurmentSuccessful = values.size() == 5;

            if (wasMeasurmentSuccessful) {
                return new Line(new Vec2(values.get(0), values.get(2)), values.get(3));
            }

            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    private static List<Double> extractValues(String command) {
        String[] formatParts = TP_FORMAT.split(" ");
        String[] commandParts = command.split(" ");

        List<Double> values = new ArrayList<>();

        for (int i = 0; i < formatParts.length; i++) {
            if (formatParts[i].equals("%f")) {
                values.add(Double.parseDouble(commandParts[i]));
            }
        }

        return values;
    }
}
