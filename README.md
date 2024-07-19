# Jessie JSON

[![Apache 2.0 License](https://img.shields.io/badge/License-Apache%202.0-brightgreen.svg)](https://github.com/sergheevdev/event-bus/blob/main/LICENSE)

## Introduction

**Jessie JSON** is a simple, lightweight and memory-safe JSON library suitable for all kind of projects.

This library offers a Java implementation of JSON (RFC 8259) with a focus on memory safety. We achieve this by utilizing ```char[]``` instead of ```String```, enhancing security through explicit clearing, mutability, and better memory control. This approach mitigates risks linked to string immutability and garbage collection.

### Features

- 🚀 **Easy Startup** ➔ you can easily integrate the library and start parsing JSON immediately.
- 📚 **Small Library** ➔ you don't have to depend upon a <u>bloated library</u> (with transitive dependencies).
- 🔒 **Memory Safety** ➔ you can explicitly clear the buffer after usage (your sensitive data is protected).

### TODO

- Add more testing and reach 100% code coverage (with JaCoCo).
- Improve the quality of the documentation.

## Getting started

1. Add the dependency to your project ```pom.xml```.

```xml
<dependency>
    <groupId>tech.tryangle</groupId>
    <artifactId>jessie-json</artifactId>
    <version>1.0.4</version>
</dependency>
```

2. Parse some JSON.

```java
// 1. Parse the chars to a JSON entity
char[] raw = "{\"token\":{\"password\":\"123456\"}}".toCharArray();
JSONEntity json = JSONParser.parseJson(raw);

// 2. Easily get the desired field
char[] password = json.resolve("token.password", char[].class);
System.out.println(String.valueOf(password));
Arrays.fill(password, (char) -1);

// 3. Clear the chars and JSON entity content
Arrays.fill(raw, (char) -1);
json.clear();
```

3. To learn how to parse all kinds of structures 📚 [check the documentation](https://github.com/tryangle-es/jessie-json/wiki/Docs).

## Sponsor

**Made with ❤ by [tryangle.tech](https://tryangle.tech)** (Software Strategy and Consulting Company).

## License

**[Apache 2.0](LICENSE) &copy; TRYANGLE**
