package hu.progmasters.fundraiser.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {

    private MathUtil() {
    }

    public static double roundToTwoDecimalPlaces(double value) {
        return roundToDecimalPlaces(value, 2);
    }

    public static double roundToDecimalPlaces(double value, int decimalPlaces) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double roundToTwoDecimal(double value) {
        return Math.round(value * Math.pow(10, 2)) / Math.pow(10, 2);
    }
}