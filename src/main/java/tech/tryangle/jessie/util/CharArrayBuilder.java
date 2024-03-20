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

import java.util.Arrays;

public class CharArrayBuilder {

    private char[] array;

    private int size;

    private int maxSize;

    public CharArrayBuilder() {
        this.array = new char[1];
        this.size = 0;
        this.maxSize = 1;
    }

    public void add(char value) {
        if (size == maxSize) {
            growSize();
        }
        array[size] = value;
        size++;
    }

    public void add(char[] value) {
        while (size + value.length > maxSize) {
            growSize();
        }
        for (int i = 0; i < value.length; i++) {
            array[size] = value[i];
            size++;
        }
    }

    public char[] asCharArray() {
        char[] copy = new char[size];
        System.arraycopy(array, 0, copy, 0, size);
        return copy;
    }

    public String asString() {
        char[] copy = new char[size];
        System.arraycopy(array, 0, copy, 0, size);
        return String.valueOf(copy);
    }

    public void growSize() {
        int newSize = ((maxSize + (maxSize << 1)) >> 1) + 1;
        char[] aux = new char[newSize];
        System.arraycopy(array, 0, aux, 0, maxSize);
        array = aux;
        maxSize = newSize;
    }

    public void clear() {
        Arrays.fill(array, '\0');
        size = 0;
    }

}
