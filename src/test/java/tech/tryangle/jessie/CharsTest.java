package tech.tryangle.jessie;

import junit.framework.TestCase;
import tech.tryangle.jessie.util.Chars;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CharsTest extends TestCase {

    private static final String SAMPLE_TEXT = "9%ÄáÑ9;-æµ¨c'ªDÉ~@½DwjúL'þ,#áßÍí";

    public void testToBytesNull() {
        assertNull(Chars.toBytes(null));
    }

    public void testToBytesEmpty() {
        byte[] output = Chars.toBytes(new char[0]);
        assertNotNull(output);
        assertEquals(0, output.length);
    }

    public void testToBytesNullCharsetFallbacksToUtf8() {
        char[] input = SAMPLE_TEXT.toCharArray();
        byte[] expected = SAMPLE_TEXT.getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(expected, Chars.toBytes(input, null));
    }

    public void testToBytesUtf8() {
        char[] input = SAMPLE_TEXT.toCharArray();
        byte[] expected = SAMPLE_TEXT.getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(expected, Chars.toBytes(input));
    }

    public void testToBytesUtf16() {
        char[] input = SAMPLE_TEXT.toCharArray();
        byte[] expected = SAMPLE_TEXT.getBytes(StandardCharsets.UTF_16);
        assertArrayEquals(expected, Chars.toBytes(input, StandardCharsets.UTF_16));
    }

    public void testToBytesIso8859_1() {
        char[] input = SAMPLE_TEXT.toCharArray();
        byte[] expected = SAMPLE_TEXT.getBytes(StandardCharsets.ISO_8859_1);
        assertArrayEquals(expected, Chars.toBytes(input, StandardCharsets.ISO_8859_1));
    }

    public void testToBytesIsInvalid() {
        char[] input = SAMPLE_TEXT.toCharArray();
        byte[] expected = SAMPLE_TEXT.getBytes(StandardCharsets.UTF_16);
        assertFalse(Arrays.equals(expected, Chars.toBytes(input, StandardCharsets.UTF_8)));
    }

    public void testToCharsNull() {
        assertNull(Chars.toChars(null));
    }

    public void testToCharsEmpty() {
        char[] output = Chars.toChars(new byte[0]);
        assertNotNull(output);
        assertEquals(0, output.length);
    }

    public void testToCharsUtf8() {
        byte[] input = SAMPLE_TEXT.getBytes(StandardCharsets.UTF_8);
        char[] expected = SAMPLE_TEXT.toCharArray();
        assertArrayEquals(expected, Chars.toChars(input));
    }

    public void testToCharsUtf16() {
        byte[] input = SAMPLE_TEXT.getBytes(StandardCharsets.UTF_16);
        char[] expected = SAMPLE_TEXT.toCharArray();
        assertArrayEquals(expected, Chars.toChars(input, StandardCharsets.UTF_16));
    }

    public void testToCharsIso8859_1() {
        byte[] input = SAMPLE_TEXT.getBytes(StandardCharsets.ISO_8859_1);
        char[] expected = SAMPLE_TEXT.toCharArray();
        assertArrayEquals(expected, Chars.toChars(input, StandardCharsets.ISO_8859_1));
    }

    public void testCharsToInt() {
        for (int expected : List.of(Integer.MIN_VALUE, -1500, -1, 0, 1, 1500, Integer.MAX_VALUE)) {
            char[] array = String.valueOf(expected).toCharArray();
            assertEquals(expected, Chars.charsToInt(array));
        }
    }

    public void testCharsToIntOverflow() {
        assertEquals(-2147483648, Chars.charsToInt("2147483648".toCharArray()));
    }

    public void testCharsToIntInvalid() {
        assertThrows(NumberFormatException.class, () -> Chars.charsToInt("123b321".toCharArray()));
    }

    public void testCharsToLong() {
        for (long expected : List.of(Long.MIN_VALUE, -1500L, -1L, 0L, 1L, 1500L, Long.MAX_VALUE)) {
            char[] array = String.valueOf(expected).toCharArray();
            assertEquals(expected, Chars.charsToLong(array));
        }
    }

    public void testCharsToLongOverflow() {
        assertEquals(-9223372036854775808L, Chars.charsToLong("9223372036854775808".toCharArray()));
    }

    public void testCharsToLongInvalid() {
        assertThrows(NumberFormatException.class, () -> Chars.charsToLong("123b321".toCharArray()));
    }

    public void testCharsToBoolean() {
        assertTrue(Chars.charsToBoolean("true".toCharArray()));
        assertFalse(Chars.charsToBoolean("false".toCharArray()));
    }

    public void testCharsToBooleanInvalid() {
        assertThrows(Exception.class, () -> Chars.charsToBoolean("falsee".toCharArray()));
        assertThrows(Exception.class, () -> Chars.charsToBoolean("fals".toCharArray()));
        assertThrows(Exception.class, () -> Chars.charsToBoolean("trueee".toCharArray()));
        assertThrows(Exception.class, () -> Chars.charsToBoolean("tru".toCharArray()));
    }

    public void testIntToChars() {
        for (int number : List.of(Integer.MIN_VALUE, -1500, -1, 0, 1, 1500, Integer.MAX_VALUE)) {
            char[] expected = String.valueOf(number).toCharArray();
            char[] given = Chars.intToChars(number);
            assertArrayEquals(expected, given);
        }
    }

    public void testLongToChars() {
        for (long number : List.of(Long.MIN_VALUE, -1500L, -1L, 0L, 1L, 1500L, Long.MAX_VALUE)) {
            char[] expected = String.valueOf(number).toCharArray();
            char[] given = Chars.longToChars(number);
            assertArrayEquals(expected, given);
        }
    }

    public void testBooleanToChars() {
        assertArrayEquals("true".toCharArray(), Chars.booleanToChars(true));
        assertArrayEquals("false".toCharArray(), Chars.booleanToChars(false));
    }

    public void testFloatToChars() {
        for (float number : List.of(Float.MIN_VALUE, 341.4325324f, 2.0f, 0.0f, -345.46f, 78786454.45612f, Float.MAX_VALUE)) {
            char[] expected = String.valueOf(number).toCharArray();
            assertArrayEquals(expected, Chars.floatToChars(number));
        }
    }

    public void testDoubleToChars() {
        for (double number : List.of(Double.MIN_VALUE, 341.4325324d, 2.0d, 0.0d, -345.46d, 78786454.45612d, Double.MAX_VALUE)) {
            char[] expected = String.valueOf(number).toCharArray();
            assertArrayEquals(expected, Chars.doubleToChars(number));
        }
    }

    public void testNullToChars() {
        assertArrayEquals("null".toCharArray(), Chars.nullToChars());
    }

}
