/*
 * Copyright (C) 2024 Serghei Sergheev Botnari (under the TRYANGLE trademark)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.tryangle.jessie.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Chars {

    public static byte[] toBytes(char[] chars) {
        return toBytes(chars, StandardCharsets.UTF_8);
    }

    public static byte[] toBytes(char[] chars, Charset charset) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = charset.encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) -1);
        return bytes;
    }

    public static char[] toChars(byte[] bytes) {
        return toChars(bytes, StandardCharsets.UTF_8);
    }

    public static char[] toChars(byte[] bytes, Charset charset) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        CharBuffer charBuffer = charset.decode(byteBuffer);
        char[] chars = Arrays.copyOfRange(charBuffer.array(), charBuffer.position(), charBuffer.limit());
        Arrays.fill(charBuffer.array(), (char) -1);
        return chars;
    }

    public static int charsToInt(char[] chars) {
        return charsToInt(chars, 0, chars.length);
    }

    public static int charsToInt(char[] chars, int start, int end) throws NumberFormatException {
        int length = end - start;
        if (length < 1) throw new RuntimeException();
        int result = 0;
        int multiplier = 1;
        if (chars[start] == '+') {
            start++;
        }
        if (chars[start] == '-') {
            multiplier = -1;
            start++;
        }
        for (int i = start; i < end; i++) {
            int digit = (int) chars[i] - (int) '0';
            if ((digit < 0) || (digit > 9)) throw new NumberFormatException();
            result *= 10;
            result += (digit * multiplier);
        }
        return result;
    }

    public static long charsToLong(char[] chars) {
        return charsToLong(chars, 0, chars.length);
    }

    public static long charsToLong(char[] chars, int start, int end) throws NumberFormatException {
        int length = end - start;
        if (length < 1) throw new RuntimeException();
        long result = 0;
        long multiplier = 1;
        if (chars[start] == '+') {
            start++;
        }
        if (chars[start] == '-') {
            multiplier = -1;
            start++;
        }
        for (int i = start; i < end; i++) {
            long digit = (long) chars[i] - (long) '0';
            if ((digit < 0) || (digit > 9)) throw new NumberFormatException();
            result *= 10;
            result += (digit * multiplier);
        }
        return result;
    }

    public static boolean charsToBoolean(char[] chars) {
        return charsToBoolean(chars, 0, chars.length);
    }

    public static boolean charsToBoolean(char[] chars, int start, int end) {
        int length = end - start;
        if (length < 4 || length > 5) throw new RuntimeException();
        if (chars[start] == 't' && chars[start + 1] == 'r' && chars[start + 2] == 'u' && chars[start + 3] == 'e') return true;
        if (chars[start] == 'f' && chars[start + 1] == 'a' && chars[start + 2] == 'l' && chars[start + 3] == 's' && chars[start + 4] == 'e') return false;
        throw new RuntimeException();
    }

    public static char[] intToChars(int value) {
        if (value == 0) return new char[] { '0' };
        int sign = value < 0 ? -1 : 1;
        if (sign == -1) value *= -1;
        int length = (int) (Math.log10(value) + 1);
        if (sign == -1) length++;
        char[] chars = new char[length];
        for (int i = chars.length - 1; i >= 0; i--) {
            int modulo = value % 10;
            value = value / 10;
            chars[i] = (char) (modulo + '0');
        }
        if (sign == -1) chars[0] = '-';
        return chars;
    }

    public static char[] longToChars(long value) {
        if (value == 0L) return new char[] { '0' };
        int sign = value < 0 ? -1 : 1;
        if (sign == -1) value *= -1;
        int length = (int) (Math.log10(value) + 1);
        if (sign == -1) length++;
        char[] chars = new char[length];
        for (int i = chars.length - 1; i >= 0; i--) {
            long modulo = value % 10;
            value = value / 10;
            chars[i] = (char) (modulo + '0');
        }
        if (sign == -1) chars[0] = '-';
        return chars;
    }

    public static char[] booleanToChars(boolean value) {
        if (value) {
            return new char[] { 't', 'r', 'u', 'e' };
        } else {
            return new char[] { 'f', 'a', 'l', 's', 'e' };
        }
    }

    public static char[] stringToChars(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        char[] chars = Chars.toChars(bytes);
        Arrays.fill(bytes, (byte) -1);
        return chars;
    }

    public static char[] floatToChars(float value) {
        return String.valueOf(value).toCharArray();
    }

    public static char[] doubleToChars(double value) {
        return String.valueOf(value).toCharArray();
    }

    public static char[] nullToChars() {
        return new char[] { 'n', 'u', 'l', 'l' };
    }

}
