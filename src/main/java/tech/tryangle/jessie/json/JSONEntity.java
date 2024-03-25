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
package tech.tryangle.jessie.json;

import tech.tryangle.jessie.util.CharArrayBuilder;
import tech.tryangle.jessie.util.Chars;

import java.util.*;
import java.util.regex.Pattern;

/* This object represents a JSON entity */
public class JSONEntity {

    /* The available JSON entity types */
    private static final int OBJECT = 0, PAIR = 1, LIST = 2, VALUE = 3;

    /* Create a JSON object */
    public static JSONEntity newObject() { return new JSONEntity(OBJECT); }

    /* Create a JSON pair */
    public static JSONEntity newPair() { return new JSONEntity(PAIR); }

    /* Create a JSON list */
    public static JSONEntity newList() { return new JSONEntity(LIST); }

    /* Create a JSON value */
    public static JSONEntity newValue() { return new JSONEntity(VALUE); }

    /* The type of JSON entity */
    private final int type;

    /* The field that holds extra data (parse metadata) */
    private int bit = 0;

    /* The key for the JSON pair */
    private String key;

    /* The value for the JSON pair or JSON value */
    private Object value;

    /* The list for the JSON list */
    private List<Object> list = null;

    /* The map for the JSON object */
    private Map<String, Object> map = null;

    /* As we have factory methods, there is no need to expose the constructor */
    private JSONEntity(int type) {
        boolean typeIsInvalid = type != OBJECT && type != PAIR && type != LIST && type != VALUE;
        if (typeIsInvalid) throw new IllegalStateException(String.format("unknown type: %s", type));
        this.type = type;
        if (type == OBJECT) this.map = new LinkedHashMap<>();
        if (type == LIST) this.list = new ArrayList<>();
    }

    // General methods

    public int getBit() { return bit; }

    public void setBit(int bit) { this.bit = bit; }

    public boolean isObject() { return type == OBJECT; }

    public boolean isPair() { return type == PAIR; }

    public boolean isList() { return type == LIST; }

    public boolean isValue() { return type == VALUE; }

    public int size() {
        if (type == OBJECT) return map.size();
        if (type == LIST) return list.size();
        return 0;
    }

    // JSON pair and JSON value methods

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if (key == null) throw new NullPointerException("key must not be null");
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public <T> T getValue(Class<T> type) {
        if (type == null) throw new NullPointerException("type must not be null");
        return parseObject(type, value);
    }

    public void setValue(Object value) {
        this.value = value;
    }

    // JSON object methods

    public boolean hasObjectValue(String key) {
        if (key == null) throw new NullPointerException("key must not be null");
        if (map == null) return false;
        return map.containsKey(key);
    }

    public Object getObjectValue(String key) {
        if (key == null) throw new NullPointerException("key must not be null");
        if (map == null) return null;
        return map.get(key);
    }

    public <T> T getObjectValue(String key, Class<T> type) {
        if (key == null) throw new NullPointerException("key must not be null");
        if (type == null) throw new NullPointerException("type must not be null");
        if (map == null) return null;
        return parseObject(type, map.get(key));
    }

    public <T> T resolveObjectValueOrDefault(String pattern, Class<T> type, T fallback) {
        T result = resolveObjectValue(pattern, type);
        if (result == null) return fallback;
        return result;
    }

    public <T> T resolveObjectValue(String pattern, Class<T> type) {
        if (pattern == null) throw new NullPointerException("pattern must not be null");
        if (type == null) throw new NullPointerException("type must not be null");
        T result = null;
        JSONEntity root = this;
        String[] split = pattern.split(Pattern.quote("."));
        int current = 0;
        while (current != split.length) {
            if (!root.hasObjectValue(split[current])) {
                break;
            }
            if (current == split.length - 1) {
                result = root.getObjectValue(split[current], type);
            } else {
                root = root.getObjectValue(split[current], JSONEntity.class);
            }
            current++;
        }
        return result;
    }

    public void addObjectPair(String key, Object value) {
        if (key == null) throw new NullPointerException("key must not be null");
        if (map.containsKey(key)) throw new JSONException("duplicate key");
        map.put(key, value);
    }

    public void setObjectPair(String key, Object value) {
        if (key == null) throw new NullPointerException("key must not be null");
        map.put(key, value);
    }

    public Map<String, Object> getPairs() {
        return Collections.unmodifiableMap(map);
    }

    // JSON list methods

    public List<Object> getList() {
        return type == LIST ? list : null;
    }

    public <T> List<T> getList(Class<T> type) {
        if (type == null) throw new NullPointerException("type must not be null");
        List<T> result = new ArrayList<>();
        for (Object object : getList()) {
            if (type == JSONEntity.class) {
                if (object == null) {
                    result.add(type.cast(JSONEntity.newValue()));
                } else if (object instanceof JSONEntity) {
                    result.add(type.cast(object));
                } else {
                    JSONEntity json = JSONEntity.newValue();
                    json.setValue(object);
                    result.add(type.cast(json));
                }
            } else if (object == null) {
                result.add(null);
            } else if (type == String.class) {
                result.add(type.cast(String.valueOf(object)));
            } else if (char[].class == object.getClass() && type.isAssignableFrom(String.class)) {
                char[] chars = (char[]) object;
                result.add(type.cast(String.valueOf(chars)));
            } else if (type.isAssignableFrom(object.getClass())) {
                result.add(type.cast(object));
            }
        }
        return result;
    }

    public void addToList(Object object) {
        list.add(object);
    }

    public void removeFromList(Object object) {
        list.remove(object);
    }

    // Casting

    private <T> T parseObject(Class<T> type, Object value) {
        if (value instanceof char[] chars) return parseObject(type, chars);
        if (value instanceof Integer casted && type == Long.class) return type.cast(Long.valueOf(casted));
        return type.cast(value);
    }

    private <T> T parseObject(Class<T> type, char[] value) {
        if (type == String.class) {
            String result = String.valueOf(value);
            return type.cast(result);
        }
        if (type == Integer.class) {
            int result = Chars.charsToInt(value);
            return type.cast(result);
        }
        if (type == Long.class) {
            long result = Chars.charsToLong(value);
            return type.cast(result);
        }
        if (type == Boolean.class) {
            boolean result = Chars.charsToBoolean(value);
            return type.cast(result);
        }
        if (type == char[].class) {
            char[] result = value.clone();
            return type.cast(result);
        }
        throw new UnsupportedOperationException();
    }

    // String printing

    @Override
    public String toString() {
        return "Secure:XYZ";
    }

    public void clear() {
        if (type == VALUE) {
            if (value != null) {
                clear(value);
                value = null;
            }
        } else if (type == LIST) {
            if (list != null && !list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    Object object = list.get(i);
                    clear(object);
                    if (!(object instanceof JSONEntity)) {
                        list.set(i, null);
                    }
                }
            }
        } else if (type == OBJECT) {
            if (map != null && !map.isEmpty()) {
                map.values().forEach(this::clear);
                map.keySet().stream().filter(k -> !(map.get(k) instanceof JSONEntity)).forEach(k -> map.put(k, null));
            }
        }
    }

    public void clear(Object object) {
        if (object == null) {
            return;
        }
        if (object instanceof char[] chars) {
            Arrays.fill(chars, (char) -1);
        }
        if (object instanceof byte[] bytes) {
            Arrays.fill(bytes, (byte) -1);
        }
        if (object instanceof int[] ints) {
            Arrays.fill(ints, -1);
        }
        if (object instanceof Object[] bytes) {
            Arrays.fill(bytes, null);
        }
        if (object instanceof JSONEntity entity) {
            entity.clear();
        }
    }

    public char[] toCharArray() {
        if (type == VALUE) {
            if (value == null) {
                return new char[]{'n','u','l','l'};
            } else {
                return toCharArray(value);
            }
        } else if (type == LIST) {
            if (list == null || list.isEmpty()) {
                return new char[] { '[', ']' };
            } else {
                CharArrayBuilder builder = new CharArrayBuilder();
                builder.add('[');
                for (int i = 0; i < list.size(); i++) {
                    Object object = list.get(i);
                    char[] array = toCharArray(object);
                    builder.add(array);
                    Arrays.fill(array, '\0');
                    if (i < list.size() - 1) {
                        builder.add(',');
                    }
                }
                builder.add(']');
                char[] result = builder.asCharArray();
                builder.clear();
                return result;
            }
        } else if (type == OBJECT) {
            if (map == null || map.isEmpty()) {
                return new char[] { '{', '}' };
            } else {
                CharArrayBuilder builder = new CharArrayBuilder();
                builder.add('{');
                Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    char[] keyChars = toCharArray(entry.getKey());
                    builder.add(keyChars);
                    Arrays.fill(keyChars, '\0');
                    builder.add(':');
                    char[] valueChars = toCharArray(entry.getValue());
                    builder.add(valueChars);
                    Arrays.fill(valueChars, '\0');
                    if (iterator.hasNext()) {
                        builder.add(',');
                    }
                }
                builder.add('}');
                char[] result = builder.asCharArray();
                builder.clear();
                return result;
            }
        } else {
            return new char[0];
        }
    }

    public char[] toCharArray(Object object) {
        if (object == null) {
            return Chars.nullToChars();
        }
        if (object instanceof char[] value) {
            char[] escaped = escapeChars(value);
            char[] quoted = quoteText(escaped);
            Arrays.fill(escaped, '\0');
            return quoted;
        }
        if (object instanceof Integer value) {
            return Chars.intToChars(value);
        }
        if (object instanceof Long value) {
            return Chars.longToChars(value);
        }
        if (object instanceof Boolean value) {
            return Chars.booleanToChars(value);
        }
        if (object instanceof Float value) {
            return Chars.floatToChars(value);
        }
        if (object instanceof Double value) {
            return Chars.doubleToChars(value);
        }
        if (object instanceof String value) {
            char[] chars = Chars.stringToChars(value);
            char[] escaped = escapeChars(chars);
            Arrays.fill(chars, '\0');
            char[] quoted = quoteText(escaped);
            Arrays.fill(escaped, '\0');
            return quoted;
        }
        if (object instanceof JSONEntity value) {
            return value.toCharArray();
        }
        return null;
    }

    private char[] escapeChars(char[] chars) {
        CharArrayBuilder builder = new CharArrayBuilder();
        int i = 0;
        while (i < chars.length) {
            char current = chars[i];
            if (current == '"' || current == '\\') {
                builder.add('\\');
                builder.add(current);
            } else if (current == '\b') {
                builder.add('\\');
                builder.add('b');
            } else if (current == '\f') {
                builder.add('\\');
                builder.add('f');
            } else if (current == '\n') {
                builder.add('\\');
                builder.add('n');
            } else if (current == '\r') {
                builder.add('\\');
                builder.add('r');
            } else if (current == '\t') {
                builder.add('\\');
                builder.add('t');
            } else {
                builder.add(current);
            }
            i++;
        }
        char[] result = builder.asCharArray();
        builder.clear();
        return result;
    }

    private char[] quoteText(char[] chars) {
        CharArrayBuilder builder = new CharArrayBuilder();
        builder.add('"');
        builder.add(chars);
        builder.add('"');
        char[] result = builder.asCharArray();
        builder.clear();
        return result;
    }

}
