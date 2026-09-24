# orienteer-pivottable

Pivot-table widget built on pivottable.js, with D3/C3 renderers.
- Data comes from the wicket-orientdb REST endpoint `/orientdb/query/db/sql/...`.
- The pivot configuration is saved in the widget document.

- **Module:** `org.orienteer.pivottable.PivotTableModule`, module name `pivottable`, v2. Depends on the widgets module.
  Registered by `org.orienteer.pivottable.Initializer`.
- **Packages:** `component` (`PivotPanel`), `component.widget`.
- **Frontend:** `src/main/resources/org/orienteer/pivottable/component/pivottable.tmpl.js`, a `PackageTextTemplate`
  rendered via `OnDomReadyHeaderItem`, plus a small CSS fix.
  - Webjars are referenced by `/webjars/<lib>/current/...` paths.
- **Key deps (webjars, all 2016):** `org.webjars.bower:pivottable:2.4.0`, `d3:3.5.17`, `c3:0.4.11`.
  jQuery and jQuery UI come from core.
- **Tests:** 1 smoke test.
- `orienteer-standalone` depends on this module.

## Pitfalls

- The template does `JSON.parse('${config}')` with the stored config **unescaped**. A quote breaks it, and it
  allows script injection (P10).

## Upgrade risk: LOW

- The Java side is trivial.
- The JS libraries are stale: pivottable 2.23 is the last release, and d3/c3 are old majors.
- Check that the webjar paths still resolve after the wicket-webjars upgrade (P5).
