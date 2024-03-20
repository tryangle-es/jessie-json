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
package tech.tryangle.jessie;

import tech.tryangle.jessie.json.JSONEntity;
import tech.tryangle.jessie.json.JSONParser;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertArrayEquals;

public class JSONParserTest extends TestCase {

    private static final String FILE_NAME = "samples.txt";

    public void testParse() throws Exception {
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(FILE_NAME);
        assertNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                char[] chars = line.trim().toCharArray();
                JSONEntity json = JSONParser.parseJson(chars);
                assertArrayEquals(chars, json.toCharArray());
            }
        } finally {
            inputStream.close();
        }
    }

}
