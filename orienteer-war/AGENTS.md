# orienteer-war

Deployable `orienteer.war` (core only, no Java code). It is also the payload of the Docker image.

- `src/main/webapp/WEB-INF/web.xml` maps `org.orienteer.core.OrienteerFilter` on `/*` (REQUEST, ERROR) plus 404/403 error pages.
  It uses the Servlet **2.5** schema.
  - This descriptor is shared: the root pom's `jetty-maven-plugin` config points every module's `jetty:run` at it.
- `WEB-INF/jetty.xml` is empty (`configure_9_3.dtd`). `WEB-INF/jboss-web.xml` sets context root `/`.
- Build: `maven-war-plugin` (version from the root pom) with `finalName=orienteer`, producing `target/orienteer.war`.
- `com.spotify:dockerfile-maven-plugin` 1.4.10 is bound to `deploy` and pushes `orienteer/orienteer:latest`.
  CI skips it with `-Ddockerfile.skip`, because CI builds the image with buildx and `../Dockerfile.mvn`.
- Profile `dockerbuild` (`-Ddocker-build`) builds only core + war.

## Pitfalls

- `../mvnw jetty:run` works here too (war packaging is allowed in this module's plugin block; shared config in the root pom).

## Upgrade risk: MEDIUM

No code, but several things change:
- `web.xml` moves to the Jakarta EE 10 / Servlet 6 schema (P6).
- The Jetty plugin moves to `jetty-ee10-maven-plugin`.
- `jetty-hazelcast` sessions need rethinking (P6).
- The docker plugin should be removed and the base image changed (P9).
