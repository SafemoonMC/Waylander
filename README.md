# Waylander

Waylander is the backbone of all custom Mooncraft plugins.

## Building

Waylander uses Gradle to handle dependencies and building.

**Requirements:**

- Java 16 JDK
- Git

**Compiling from source:**

```sh
git clone https://github.com/SafemoonMC/Waylander.git
cd Waylander/
./gradlew buildAll
```

You can find the output artifacts in the `/COMPILED_JARS` directory.

**Other Gradle custom tasks:**

- **buildBase**, it gets just the jar without any dependency in;
- **buildSources**, it gets just the jar with project files in;
- **buildJavadoc**, it gets just the final JavaDoc;
- **buildShadowjar**, it gets just the final jar with all necessary dependencies;

## Contributing

Waylander follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
Generally, you can import the style from the `java-google-style.xml` file you can find at the root of
the project.
