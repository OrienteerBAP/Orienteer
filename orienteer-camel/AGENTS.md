# orienteer-camel

Apache Camel integration.
- Camel XML route definitions are stored as `OIntegrationConfig` documents.
- They run as Orienteer tasks, using the `orientdb://` Camel component.
- The README is in Russian.

- **Module:** `org.orienteer.camel.Module`, module name `camel`, v3. Registered by `org.orienteer.camel.Initializer`.
- **Packages:**
  - `tasks`: `IOIntegrationConfig` (Transponder DAO), `OCamelContext extends DefaultCamelContext`,
    `CamelEventHandler`, `OCamelTaskSessionCallback`
  - `behavior`, `web`, `widget`
- **Key deps (all EOL or vulnerable):**
  - Camel **2.25.2** (EOL end of 2020): core, jdbc, gson, xstream, csv
  - `camel-xmljson` 2.23.4, which brings json-lib 2.4
  - `camel-orientdb:1.1`, built for OrientDB 2.2
  - `xom` 1.2.5
  - **Mismatched** POI: 3.17 / 3.15 / 3.15
  - Transitively: XStream 1.4.11.1 (RCE CVEs) and gson 2.8.5
- **Tests:** 1 smoke test.

## Pitfalls

- `IOIntegrationConfig` calls `context.loadRoutesDefinition(...)`, which uses JAXB (`javax.xml.bind`).
  It works on JDK 11+ only because camel-core bundles JAXB 2.3.
- Camel 2 service descriptors live under `META-INF/services/org/apache/camel/...`. The paths differ in Camel 3/4.
- `OCamelContext` stores the session password in the context's global options.

## Upgrade risk: HIGH

The code is small (about 390 lines), but Java 21 and jakarta mean Camel 4:
- new route-loading APIs
- no xmljson
- `camel-orientdb` must be rewritten or replaced

**Parked** (profile `parked`, plan D3): `./mvnw -Pparked -pl orienteer-camel -am verify`. Keep, rewrite or drop is decided in P8.
