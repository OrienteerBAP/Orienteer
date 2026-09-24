# orienteer-archetype-war

Maven archetype that generates a **custom Orienteer-based WAR application**.

- **Templates:** `src/main/resources/archetype-resources/`. Descriptor: `src/main/resources/META-INF/maven/archetype-metadata.xml`.
- **Generates:**
  - `MyWebApplication extends OrienteerWebApplication`, which calls `registerModule(DataModel.class)`
  - `DataModel extends AbstractOrienteerModule`
  - `web/` package
  - `src/main/webapp/WEB-INF/web.xml` (Servlet 2.5, `OrienteerFilter`)
  - `orienteer.properties` and `orienteer-test.properties`
  - `Dockerfile`: builder `maven:3.6-jdk-8-alpine`, runtime `orienteer/orienteer:latest`
  - `.dockerignore`
  - `TestMyWebApplication`, plus a test keystore
- **Integration test:** `src/test/resources/projects/`.
- **Build:** `maven-archetype-plugin` **2.3** and `archetype-packaging` **2.2**. They don't match the jar archetype, which uses 3.0.1.

## Pitfalls

- The template `pom.xml` hardcodes Java 1.8, junit 4.11, javax.servlet 3.0.1, compiler 3.7.0, war 3.1.0,
  surefire 2.20, bundle 2.3.6 and eclipse 2.9. It also has a `jetty-maven-plugin` block with `jetty-all:uber`
  and the **dead** `oss.sonatype.org` snapshots repository.
- `.gitignore` exists in the resources but isn't listed in `archetype-metadata.xml`, so it isn't generated.
- Files are Velocity templates: take care with `${...}` and `#`.

## Upgrade risk: MEDIUM

Same as the jar archetype, plus the `web.xml`, Jetty plugin and Dockerfile templates. Tracked in plan P9.
