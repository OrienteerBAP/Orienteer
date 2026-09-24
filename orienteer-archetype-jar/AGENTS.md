# orienteer-archetype-jar

Maven archetype that generates a new **Orienteer module jar**.

- **Templates:** `src/main/resources/archetype-resources/`. Descriptor: `src/main/resources/META-INF/maven/archetype-metadata.xml`.
  - `orienteer.version`, `wicket.orientdb.version` and `jetty.version` are required properties.
    Their defaults are filtered in from the parent pom at build time.
- **Generates:**
  - `Module extends AbstractOrienteerModule`
  - `Initializer implements IInitializer`, plus its `META-INF/services` entry
  - `web/` and `component/` packages, `Initializer.properties`, `log4j2.xml`
  - `TestModule` (uses `OrienteerTestRunner`)
- **Integration test:** `src/test/resources/projects/simple-test/` generates a project and runs its tests.
  The `skip-integration-tests` profile is used for releases.
- **Build:** `maven-archetype-plugin` and the `archetype-packaging` extension, both 3.0.1.

## Pitfalls

- Plugin versions are **hardcoded** in the template `pom.xml`: compiler 3.7.0, bundle 2.3.6, eclipse 2.9,
  jar 3.0.2, surefire 2.22.1. So are junit 4.11, javax.servlet 3.0.1 and Java 1.8.
  Every plan phase that bumps these in the root pom must also update the template.
- The template lists commented-out optional modules, including `orienteer-object`, which no longer exists.
  Its only repository is the Apache snapshots repo.
- Files under `archetype-resources` are Velocity templates. `${...}` and `#` have special meaning there.

## Upgrade risk: LOW–MEDIUM

Template-only changes, but they must mirror each phase. Tracked in plan P9.
