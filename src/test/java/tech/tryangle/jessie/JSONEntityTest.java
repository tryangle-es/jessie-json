package tech.tryangle.jessie;

import junit.framework.TestCase;
import tech.tryangle.jessie.json.JSONEntity;
import tech.tryangle.jessie.json.JSONParser;

public class JSONEntityTest extends TestCase {

    /* Hide string value for security */
    public void testToString() {
        char[] raw = "{\"token\":{\"password\":\"123456\"}}".toCharArray();
        JSONEntity json = JSONParser.parseJson(raw);
        assertEquals("Secure:XYZ", json.toString());
    }

    /* Up-cast int to long (expect no error) */
    public void testUpCastIntToLong() {
        JSONEntity json = JSONEntity.newObject();
        json.addObjectPair("age", (int) 18);
        long value = json.getObjectValue("age", Long.class);
        assertEquals(18L, value);
    }

}
