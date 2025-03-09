package com.kludwisz.fishcracker.measurment;

import com.kludwisz.fishcracker.math.Vec3i;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeasurmentParser {
    private static final String TP_FORMAT = "/tp @p %f %f %f %f %f";

    private Vec3i position;
    private double angle;

    public Vec3i getLastPosition() {
        return position;
    }

    public double getLastAngle() {
        return angle;
    }

    public boolean parseAngleMeasurment(String clipboardContent) {
        // parse the clipboard content and display the angle
        try {
            List<Double> values = extractValues(clipboardContent);
            return values.size() == 5;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static List<Double> extractValues(String command) throws Exception {
        // Convert format into a regex pattern, replacing %f with "([-+]?\\d*\\.?\\d+)"
        String regex = TP_FORMAT.replaceAll("%f", "([-+]?\\d*\\.?\\d+)");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(command);

        List<Double> values = new ArrayList<>();

        if (matcher.matches()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                values.add(Double.parseDouble(matcher.group(i)));
            }
        }

        return values;
    }
}
