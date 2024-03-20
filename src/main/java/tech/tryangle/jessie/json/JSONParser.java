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

import java.util.LinkedList;

public class JSONParser {

    public static JSONEntity parseJson(char[] json) {
        LinkedList<JSONEntity> stack = new LinkedList<>();
        CharArrayBuilder builder = new CharArrayBuilder();
        JSONEntity node = null;
        int curlyBrackets = 0;
        int squareBrackets = 0;
        int start = -1;
        int i = 0;
        while (i < json.length) {
            char current = json[i];
            if (current == '"') {
                if (start == -1) {
                    start = i + 1;
                } else {
                    if (node == null) {
                        node = JSONEntity.newValue();
                        node.setValue(builder.asCharArray());
                    } else {
                        if (node.isValue()) {
                            throw new JSONException("expected single value");
                        } else if (node.isObject()) {
                            stack.push(node);
                            node = JSONEntity.newPair();
                            node.setKey(builder.asString());
                        } else if (node.isPair()) {
                            if (node.getBit() != 1) throw new JSONException("no colon in key/value pair");
                            node.setValue(builder.asCharArray());
                            JSONEntity last = stack.pop();
                            last.addObjectPair(node.getKey(), node.getValue());
                            node = last;
                        } else if (node.isList()) {
                            node.addToList(builder.asCharArray());
                        }
                    }
                    start = -1;
                    builder.clear();
                }
            } else if (start == -1) {
                if (current == '{') {
                    if (node != null) {
                        if (node.isValue()) throw new JSONException("expected single value");
                        stack.push(node);
                    }
                    node = JSONEntity.newObject();
                    curlyBrackets++;
                } else if (current == '[') {
                    if (node != null) {
                        if (node.isValue()) throw new JSONException("expected single value");
                        stack.push(node);
                    }
                    node = JSONEntity.newList();
                    squareBrackets++;
                } else if (current == '}' || current == ']') {
                    if (node == null) throw new JSONException("closing an un-opened object or list");
                    if (node.size() > 1 && node.getBit() != node.size() - 1) throw new JSONException("unbalanced commas");
                    if (current == '}') {
                        if (!node.isObject()) throw new JSONException("closing an un-opened object");
                        curlyBrackets--;
                    } else {
                        if (!node.isList()) throw new JSONException("closing an un-opened list");
                        squareBrackets--;
                    }
                    if (!stack.isEmpty()) {
                        int count = 1;
                        while (count > 0) {
                            JSONEntity last = stack.pop();
                            if (node.isValue()) {
                                throw new JSONException("expected single value");
                            } else if (last.isObject()) {
                                last.addObjectPair(node.getKey(), node.getValue());
                            } else if (last.isPair()) {
                                if (last.getBit() != 1) throw new JSONException("no colon in key/value pair");
                                last.setValue(node);
                                count++;
                            } else if (last.isList()) {
                                last.addToList(node);
                            }
                            node = last;
                            count--;
                        }
                    }
                } else if (current == '-' || (current >= '0' && current <= '9')) {
                    int dots = 0;
                    int minuses = 0;
                    int j = i;
                    while (j < json.length) {
                        char traverse = json[j];
                        if (traverse == '.') {
                            dots++;
                            if (dots > 1) throw new JSONException("found decimal with multiple dots");
                        } else if (traverse == '-') {
                            minuses++;
                            if (minuses > 1) throw new JSONException("found numeric with multiple minuses");
                        } else if (traverse < '0' || traverse > '9') {
                            break;
                        }
                        j++;
                    }
                    char[] raw = substring(json, i, j);
                    Object parsed;
                    if (dots > 0) {
                        parsed = Double.parseDouble(String.valueOf(raw));
                    } else {
                        long value = Long.parseLong(String.valueOf(raw));
                        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                            parsed = value;
                        } else {
                            parsed = Integer.parseInt(String.valueOf(raw));
                        }
                    }
                    if (node == null) {
                        node = JSONEntity.newValue();
                        node.setValue(parsed);
                    } else {
                        if (node.isValue()) {
                            throw new JSONException("expected single value");
                        } else if (node.isPair()) {
                            if (node.getBit() != 1) throw new JSONException("no colon in key/value pair");
                            node.setValue(parsed);
                            JSONEntity last = stack.pop();
                            last.addObjectPair(node.getKey(), node.getValue());
                            node = last;
                        } else if (node.isList()) {
                            node.addToList(parsed);
                        }
                    }
                    i = j - 1;
                } else if (current == 't') {
                    if (i + 3 >= json.length) throw new JSONException("dangling in-between char");
                    if (json[i + 1] != 'r') throw new JSONException("dangling in-between char");
                    if (json[i + 2] != 'u') throw new JSONException("dangling in-between char");
                    if (json[i + 3] != 'e') throw new JSONException("dangling in-between char");
                    i += 3;
                    if (node == null) {
                        node = JSONEntity.newValue();
                        node.setValue(true);
                    } else {
                        if (node.isValue()) {
                            throw new JSONException("expected single value");
                        } else if (node.isPair()) {
                            if (node.getBit() != 1) throw new JSONException("no colon in key/value pair");
                            node.setValue(true);
                            JSONEntity last = stack.pop();
                            last.addObjectPair(node.getKey(), node.getValue());
                            node = last;
                        } else if (node.isList()) {
                            node.addToList(true);
                        }
                    }
                } else if (current == 'f') {
                    if (i + 4 >= json.length) throw new JSONException("dangling in-between char");
                    if (json[i + 1] != 'a') throw new JSONException("dangling in-between char");
                    if (json[i + 2] != 'l') throw new JSONException("dangling in-between char");
                    if (json[i + 3] != 's') throw new JSONException("dangling in-between char");
                    if (json[i + 4] != 'e') throw new JSONException("dangling in-between char");
                    i += 4;
                    if (node == null) {
                        node = JSONEntity.newValue();
                        node.setValue(false);
                    } else {
                        if (node.isValue()) {
                            throw new JSONException("expected single value");
                        } else if (node.isPair()) {
                            if (node.getBit() != 1) throw new JSONException("no colon in key/value pair");
                            node.setValue(false);
                            JSONEntity last = stack.pop();
                            last.addObjectPair(node.getKey(), node.getValue());
                            node = last;
                        } else if (node.isList()) {
                            node.addToList(false);
                        }
                    }
                } else if (current == 'n') {
                    if (i + 3 >= json.length) throw new JSONException("dangling in-between char");
                    if (json[i + 1] != 'u') throw new JSONException("dangling in-between char");
                    if (json[i + 2] != 'l') throw new JSONException("dangling in-between char");
                    if (json[i + 3] != 'l') throw new JSONException("dangling in-between char");
                    i += 3;
                    if (node == null) {
                        node = JSONEntity.newValue();
                        node.setValue(null);
                    } else {
                        if (node.isValue()) {
                            throw new JSONException("expected single value");
                        } else if (node.isPair()) {
                            if (node.getBit() != 1) throw new JSONException("no colon in key/value pair");
                            node.setValue(null);
                            JSONEntity last = stack.pop();
                            last.addObjectPair(node.getKey(), node.getValue());
                            node = last;
                        } else if (node.isList()) {
                            node.addToList(null);
                        }
                    }
                } else if (current == ':') {
                    if (node == null) throw new JSONException("dangling colon inside no object");
                    if (!node.isPair()) throw new JSONException("dangling colon inside no object");
                    node.setBit(1);
                } else if (current == ',') {
                    if (node == null) throw new JSONException("dangling comma inside no object or list");
                    if (!node.isObject() && !node.isList()) throw new JSONException("dangling comma inside no object or list");
                    node.setBit(node.getBit() + 1);
                } else if (current != ' ' && current != '\n' && current != '\r' && current != '\t' && !Character.isISOControl(current)) {
                    throw new JSONException("dangling unknown char");
                }
            } else {
                char next = i + 1 < json.length ? json[i + 1] : '\0';
                if (current == '\\') {
                    if (next == '\"') {
                        builder.add('"');
                    } else if (next == '\\') {
                        builder.add('\\');
                    } else if (next == '/') {
                        builder.add('/');
                    } else if (next == 'b') {
                        builder.add('\b');
                    } else if (next == 'f') {
                        builder.add('\f');
                    } else if (next == 'n') {
                        builder.add('\n');
                    } else if (next == 'r') {
                        builder.add('\r');
                    } else if (next == 't') {
                        builder.add('\t');
                    } else if (next == 'u') {
                        StringBuilder unicode = new StringBuilder();
                        int begin = i + 2;
                        for (int j = 0; j < 4; j++) {
                            char cur = begin + j < json.length ? json[begin + j] : '\0';
                            if (!Character.isLetterOrDigit(cur)) break;
                            unicode.append(cur);
                        }
                        if (unicode.length() == 4) {
                            builder.add(parseUnicode(unicode.toString()));
                            i += 4;
                        } else {
                            throw new JSONException("invalid unicode 4 hex digits");
                        }
                    } else {
                        throw new JSONException("dangling unknown escape char");
                    }
                    i++;
                } else {
                    builder.add(current);
                }
            }
            i++;
        }
        builder.clear();
        if (curlyBrackets != 0) throw new JSONException("unbalanced curly brackets");
        if (squareBrackets != 0) throw new JSONException("unbalanced square brackets");
        if (node == null) node = JSONEntity.newValue();
        return node;
    }

    private static char parseUnicode(String input) {
        try {
            return (char) Integer.parseInt(input, 16);
        } catch (Exception e) {
            throw new JSONException("invalid unicode 4 hex digits");
        }
    }

    private static char[] substring(char[] original, int from, int to) {
        char[] array = new char[to - from];
        System.arraycopy(original, from, array, 0, to - from);
        return array;
    }

}
