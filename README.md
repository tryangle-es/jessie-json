# Jessie JSON

[![Apache 2.0 License](https://img.shields.io/badge/License-Apache%202.0-brightgreen.svg)](https://github.com/sergheevdev/event-bus/blob/main/LICENSE)

## Introduction

**Jessie JSON** is a simple, lightweight and memory-safe JSON library suitable for all kind of projects.

This library offers a Java implementation of JSON (RFC 8259) with a focus on memory safety. We achieve this by utilizing ```char[]``` instead of ```String```, enhancing security through explicit clearing, mutability, and better memory control. This approach mitigates risks linked to string immutability and garbage collection.

### Features

- 🚀 **Easy Startup** ➔ you can easily integrate the library and parsing JSON right now.
- 📚 **Small Library** ➔ you don't have to depend upon a <u>bloated library</u> (with transitive dependencies).
- 🔒 **Memory Safety** ➔ you can explicitly clear the buffer after usage (your sensitive data is protected).

### TODO

- Add more testing and reach 100% code coverage (with JaCoCo).
- Improve the quality of the documentation.
- Publish the library to maven central.

## Getting started

1. Make sure you have **Maven** and **Java** installed.
2. Compile the library with ```mvn clean install```.
3. Publish the JAR to your artifact repository.
4. Add the dependency to your project ```pom.xml```.

```xml
<dependency>
    <groupId>tech.tryangle</groupId>
    <artifactId>jessie-json</artifactId>
    <version>1.0.0</version>
</dependency>
```

5. Parse some JSON.

```java
// Parse the JSON
char[] raw = "{\"location\":{\"country\":\"US\"}}".toCharArray();
JSONEntity json = JSONParser.parseJson(raw);

// Get the data
String country = json.resolveObjectValue("location.country", String.class);
System.out.println(country);

// Clear the memory
Arrays.fill(raw, (char) -1);
json.clear();
```

## Sponsor

**Made with ❤ by [tryangle.tech](https://tryangle.tech)** (Software Strategy and Consulting Company).

## License

**[Apache 2.0](LICENSE) &copy; TRYANGLE**
