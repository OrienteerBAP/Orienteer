# orienteer-tours — PARKED

**Not in the default build.** It was commented out of the root `<modules>` in `b39de728` (2024-02-08); now it's in the
opt-in profile `parked` (plan D3): `./mvnw -Pparked -pl orienteer-tours -am verify`.
The same commit bumped `jersey-media-*` 2.32 → 3.0.2. Jersey 3.x is jakarta-based, which conflicts with the module's
javax code and with `wicket-jersey:1.0` (Jersey 2.32).

Guided tours of the UI.
- `IOTour` and `IOTourStep` are stored through DAO interfaces and served over a JAX-RS endpoint (`OToursRestResources`, via wicket-jersey).
- Tours are rendered with pluggable JS engines.

- **Module:** `org.orienteer.tours.OToursModule`, module name `tours`, v1. Registered by `org.orienteer.tours.Initializer`.
  - An `IComponentInstantiationListener` injects the tour JS into every page.
  - Plugins: `ITourPlugin`, `DriverJsPlugin`, `BootstrapTouristPlugin`.
- **Packages:** `component`, `model`, `rest`.
- **Key deps:**
  - `org.orienteer.wicket-jersey:wicket-jersey:1.0`: external, javax, Wicket 8.10
  - `jersey-media-json-jackson` / `jersey-media-jaxb` 3.0.2
  - `org.webjars.npm:driver.js:0.9.8`. Current driver.js 1.x is an API rewrite.
- **Frontend:** a **vendored** Bootstrap Tourist 0.3.0 (`bootstrap-tourist.js`, about 2.3k lines, abandoned), plus plugin JS and `tours.js`.
- **Tests:** 1 smoke test.

## Pitfalls

- Imports `javax.ws.rs`, `javax.xml.bind` (JAXB is not in the JDK since 11) and `javax.inject`.
- The pom has a long list of commented-out dependencies on other Orienteer modules, including the removed `orienteer-object`.

## Upgrade risk: HIGH

Already broken. P8 options:
1. drop it
2. rewrite without JAX-RS, serving tour data through a Wicket resource, with driver.js 1.x (Bootstrap Tourist is Bootstrap 4-bound)
