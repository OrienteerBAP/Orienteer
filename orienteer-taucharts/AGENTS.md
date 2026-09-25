# orienteer-taucharts

Chart widgets (line, bar, scatter and others, with plugins) built on Taucharts.
- Driven by an SQL query, run either through REST or directly.
- Users can supply JS for the chart config and for post-processing.

- **Module:** `org.orienteer.taucharts.Module`, module name `taucharts`, v4. Depends on the widgets module.
  Registered by `org.orienteer.taucharts.Initializer`.
- **Packages:** `component` (`AbstractTauchartsPanel`, `TauchartsConfig`), `component.widget`, `web`.
- **Frontend:** `src/main/resources/org/orienteer/taucharts/component/taucharts.tmpl.js`.
- **Key deps:** `org.webjars.bowergithub.targetprocess:taucharts:1.2.2` (about 2017), which pulls in d3 v3.
  **Taucharts is effectively abandoned.**
- **Tests:** 1 smoke test.

## Pitfalls

- The template uses `eval("${config}")` and `eval(${data})` and injects the stored `postProcess` JS verbatim.
  This is a security issue, and it needs `'unsafe-eval'` under Wicket 10's CSP (P10).
- Uses the legacy `OSQLSynchQuery` (`AbstractTauchartsPanel`).
- A dead duplicate `IInitializer` sits under `src/main/resources/org/orienteer/taucharts/META-INF/services/`.

## Upgrade risk: LOW–MEDIUM

- The Java side is trivial, but the JS library is dead.
- The P8 decision is probably to **replace** it with a maintained chart library such as ECharts or Chart.js, or to drop it.
- **Parked** (profile `parked`, plan D3): `./mvnw -Pparked -pl orienteer-taucharts -am verify`.
