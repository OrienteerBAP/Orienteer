# orienteer-rproxy

Built-in reverse proxy for external REST APIs.
- Each `ORProxyEndPoint` DB document defines one endpoint: base URL, mount path, basic auth, cookies, headers,
  protected parameters and an optional extension class.
- Endpoints are mounted as Wicket shared resources. `ORProxyHook` remounts them when the documents change.

- **Module:** `org.orienteer.rproxy.ORProxyModule`, module name `orienteer-rproxy`, v2. Registered by `org.orienteer.rproxy.Initializer`.
  All 7 classes are in package `org.orienteer.rproxy`.
- **Key deps:** no direct third-party dependencies.
  - `ORProxyResource` extends wicket-orientdb's `ru.ydn.wicket.wicketorientdb.rest.ReverseProxyResource`.
  - Uses okhttp3 (`HttpUrl`, `Headers`, `Credentials`), which arrives via wicket-orientdb.
- **Tests:** 1 smoke test.

## Pitfalls

- **Bug:** in the `ORProxyResource` constructor, configured headers are put into `cookies` instead of `headers` (P10).
- A stray duplicate `IInitializer` service file sits at `src/main/resources/org/orienteer/rproxy/META-INF/services/`. It has no effect.
- The pom hardcodes compiler 1.8 without `-parameters`, and surefire 2.22.1. Remove these overrides (P2).

## Upgrade risk: MEDIUM

Small code, but it fully depends on wicket-orientdb's `ReverseProxyResource` being ported to Wicket 10.
