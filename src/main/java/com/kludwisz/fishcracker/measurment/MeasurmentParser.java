package com.kludwisz.fishcracker.measurment;

import com.kludwisz.fishcracker.math.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MeasurmentParser {
    // /execute in minecraft:overworld run tp @s -553.92 64.05 -1034.76 2.62 8.41
    private static final String TP_FORMAT = "/execute in minecraft:overworld run tp @s %f %f %f %f %f";

    private boolean wasMeasurmentSuccessful = false;
    private Vec3 position;
    private Double angle;

    public Vec3 getLastPosition() {
        return wasMeasurmentSuccessful ? position : null;
    }

    public Double getLastAngle() {
        return wasMeasurmentSuccessful ? angle : null;
    }

    public String getMeasurmentAsString() {
        return wasMeasurmentSuccessful ? String.format("[x=%.2f y=%.2f z=%.2f theta=%.2f]", position.getX(), position.getY(), position.getZ(), angle) : "";
    }

    public boolean parseAngleMeasurment(String clipboardContent) {
        // parse the clipboard content and display the angle
        try {
            List<Double> values = extractValues(clipboardContent);
            wasMeasurmentSuccessful = values.size() == 5;

            if (wasMeasurmentSuccessful) {
                position = new Vec3(values.get(0), values.get(1), values.get(2)); // x y z
                angle = values.get(3); // yaw
            }

            return wasMeasurmentSuccessful;
        }
        catch (Exception e) {
            return false;
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
