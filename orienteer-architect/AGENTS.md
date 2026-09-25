# orienteer-architect

Visual OrientDB schema designer built on mxGraph. It can also generate Java sources for an Orienteer module
from the diagram. User guide: the GitHub wiki page "Orienteer-Architect-User-Guide".

- **Module:** `org.orienteer.architect.OArchitectModule`, module name `architect`, v1.
  Registered by `org.orienteer.architect.Initializer`.
- **Packages:** `component(.behavior|.panel|.panel.command|.widget)`, `event`, `model(.generator)`, `service(.generator)`, `util`.
- **Frontend:** about 25 custom JS/CSS files (roughly 5.7k lines) under `src/main/resources/org/orienteer/architect/`:
  - `OArchitect*.js` and the en/ru/uk locale files
  - `config.tmpl.xml` (mxGraph config)
  - mxGraph itself loads from the webjar path `mxgraph/current/javascript/mxClient.min.js`
- **Key deps:** `org.webjars.bower:mxgraph:3.7.4`. **mxGraph is EOL**; the community successor is maxGraph.
- **Tests:** 7.
  - `TestSourceGeneratorMicroFramework` (5) and `OArchitectTest` (1) are plain JUnit.
  - `TestModuleSourceGenerator` (1) uses `OrienteerTestRunner`.

## Pitfalls

- JS talks to Java through 7 `AbstractDefaultAjaxBehavior` callbacks. Keep the JS and Java sides in sync.

## Upgrade risk: MEDIUM

- `ModalWindow` in `OArchitectEditorWidget` and 3 `event/*ModalWindowEvent` classes (removed in Wicket 10; use `ModalDialog`).
- mxGraph → maxGraph is a JS rewrite.
- Inline JS needs review for Wicket's CSP.
- See P5, P8.
