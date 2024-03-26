package tech.tryangle.jessie;

import junit.framework.TestCase;
import tech.tryangle.jessie.json.JSONEntity;
import tech.tryangle.jessie.json.JSONParser;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class JSONEntityTest extends TestCase {

    /*
    ➔ When calling the JSONEntity::toString() method, then....
    ➔ Expect the string value to be hidden
    ➔ Expect no error to be thrown
     */
    public void testToString() {
        char[] raw = "{\"token\":{\"password\":\"123456\"}}".toCharArray();
        JSONEntity json = JSONParser.parseJson(raw);
        assertEquals("Secure:XYZ", json.toString());
    }

    /*
    ➔ When up-casting int to long, then....
    ➔ Expect the int type to behave as long type
    ➔ Expect no error to be thrown
     */
    public void testUpCastIntToLong() {
        JSONEntity json = JSONEntity.newObject();
        json.addObjectPair("age", (int) 18);
        long value = json.getObjectValue("age", Long.class);
        assertEquals(18L, value);
    }

    /*
    ➔ When parsing the json, then...
    ➔ Expect the expected values match the given
    ➔ Expect no error to be thrown
     */
    public void testParseStringList() {
        char[] raw = "[\"one\", \"two\", \"three\"]".toCharArray();
        JSONEntity json = JSONParser.parseJson(raw);
        List<String> list = json.getList(String.class);
        assertEquals("one", list.get(0));
        assertEquals("two", list.get(1));
        assertEquals("three", list.get(2));
    }

    /*
    ➔ When parsing the json, then...
    ➔ Expect the expected values match the given
    ➔ Expect no error to be thrown
     */
    public void testParseCharArrayList() {
        char[] raw = "[\"one\", \"two\", \"three\"]".toCharArray();
        JSONEntity json = JSONParser.parseJson(raw);
        List<char[]> list = json.getList(char[].class);
        assertArrayEquals("one".toCharArray(), list.get(0));
        assertArrayEquals("two".toCharArray(), list.get(1));
        assertArrayEquals("three".toCharArray(), list.get(2));
    }

    /*
    ➔ When parsing the json, then...
    ➔ Expect the expected values match the given
    ➔ Expect no error to be thrown
     */
    public void testParseHeterogeneousList() {
        char[] raw = "[1, {\"name\":\"peter\"}, true, null]".toCharArray();
        JSONEntity json = JSONParser.parseJson(raw);
        List<JSONEntity> list = json.getList(JSONEntity.class);
        // Parse 1st element
        Integer one = list.get(0).getValue(Integer.class);
        assertEquals(1, one.intValue());
        // Parse 2nd element
        String two = list.get(1).getObjectValue("name", String.class);
        assertEquals("peter", two);
        // Parse 3rd element
        Boolean three = list.get(2).getValue(Boolean.class);
        assertTrue(three);
        // Parse 4th element
        Object four = list.get(3).getValue();
        assertNull(four);
    }

}
