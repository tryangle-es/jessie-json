package tech.tryangle.jessie;

import junit.framework.TestCase;
import tech.tryangle.jessie.json.JSONEntity;
import tech.tryangle.jessie.json.JSONParser;

import java.util.List;

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
    ➔ When up-cast int to long, then....
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
    ➔ When parse the json, then...
    ➔ Expect the expected values match the given
    ➔ Expect no error to be thrown
     */
    public void testMultipleTypesList() {
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
