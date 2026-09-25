# orienteer-birt

Eclipse BIRT report widgets.
- You upload a `.rptdesign` into a widget on a document or list page, and it renders as HTML, with PDF and Excel export.
- The report's datasource can be pointed at the current Orienteer DB and user.

- **Module:** `org.orienteer.birt.Module`, module name `orienteer-birt`, v1. Depends on the widgets module.
  Registered by `org.orienteer.birt.Initializer`.
  - Starts the BIRT (OSGi-less) `Platform` in `onInitialize`.
  - Calls the Eclipse-internal `RegistryProviderFactory` on destroy.
- **Packages:** `component` (panels), `component.resources` (HTML/PDF/Excel), `component.service`, `component.widget`, `web`, `widget`.
- **Key deps:** `org.orienteer:org.orienteer.birt.orientdb:2.0-SNAPSHOT`. It is **external and unresolvable**, built against OrientDB 3.1.
  It pulls in BIRT runtime **4.4.2** (2015), which brings many CVEs through POI 3.9, Batik 1.6, iText 2.1.7, Derby 10.5 and Rhino 1.7.2.
- **Tests:** 1 (`TestModule`). It renders `test.rptdesign` and compares the output **byte-for-byte** with `test.rptdesign.result.html`.

## Pitfalls

- The golden-file test breaks on any BIRT or OrientDB change. Regenerate the file deliberately.
- Report HTML, including inline `<script>`/`<style>`, is injected with `setEscapeModelStrings(false)`. This conflicts with Wicket's CSP.
- `AbstractBirtReportPanel.updateDBUriToLocal` writes the session username and password into the report datasource.
- `new_report.rptdesign` sits oddly under `src/main/resources/META-INF/services/org/orienteer/birt/`.
  A dead duplicate `IInitializer` sits under `src/main/resources/org/orienteer/birt/META-INF/services/`.
- The module pom copies the driver jar at the `install` phase (maven-dependency-plugin, version from the root pom).

## Upgrade risk: HIGH

- Modern BIRT (4.1x, Java 17+) is poorly packaged for Maven.
- The OrientDB ODA driver must be rebuilt.
- The dependency tree is CVE-heavy.

**Parked** (profile `parked`, plan D3): `./mvnw -Pparked -pl orienteer-birt -am verify`. Upgrade, replace (e.g. JasperReports) or drop is decided in P8.
