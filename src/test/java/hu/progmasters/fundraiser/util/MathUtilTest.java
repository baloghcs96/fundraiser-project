package hu.progmasters.fundraiser.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MathUtilTest {

    @Test
    void testRoundToTwoDecimalPlaces() {
        assertEquals(3.14, MathUtil.roundToTwoDecimalPlaces(3.14159));
        assertEquals(-3.14, MathUtil.roundToTwoDecimalPlaces(-3.14159));
        assertEquals(3.0, MathUtil.roundToTwoDecimalPlaces(3));
        assertEquals(3.14, MathUtil.roundToTwoDecimalPlaces(3.136));
    }

    @Test
    void testRoundToDecimalPlaces() {
        assertEquals(3.142, MathUtil.roundToDecimalPlaces(3.14159, 3));
        assertEquals(-3.142, MathUtil.roundToDecimalPlaces(-3.14159, 3));
        assertEquals(3.0, MathUtil.roundToDecimalPlaces(3.14159, 0));
        assertEquals(3.1, MathUtil.roundToDecimalPlaces(3.14159, 1));
    }

    @Test
    void testRoundToTwoDecimal() {
        assertEquals(3.14, MathUtil.roundToTwoDecimal(3.14159));
        assertEquals(-3.14, MathUtil.roundToTwoDecimal(-3.14159));
        assertEquals(3.0, MathUtil.roundToTwoDecimal(3));
        assertEquals(3.14, MathUtil.roundToTwoDecimal(3.136));
    }

}