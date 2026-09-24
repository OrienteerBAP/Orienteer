# orienteer-pages

A small CMS.
- `OPage` documents carry a path, markup content and an optional server-side script, and are mounted as Wicket pages,
  either full (`FullWebPage`) or embedded (`EmbeddedWebPage`).
- Also provides document-alias URL mappers.

- **Module:** `org.orienteer.pages.module.PagesModule`, module name `pages`, v2.
  `PagesHook` remounts pages when they change. Registered by `org.orienteer.pages.Initializer`.
- **Packages:** `module`, `repository`, `web` (`PageDelegate` does the rendering and scripting),
  `wicket.mapper` (`CompoundRequestMapper`/`MountedMapper` subclasses).
- **Key deps:** only orienteer-core.
- **Tests:** 1 (`OPagesTest.testPageRender`, `@Sudo`).
- `orienteer-standalone` depends on this module.

## Pitfalls

- `PageDelegate` uses OrientDB **internals** (`OrientDBInternal.extract(...).getScriptManager()`, `acquireDatabaseEngine`).
  These already broke between 3.1 and 3.2 (patched in `b39de728`); re-test on every OrientDB bump.
- Server-side JS runs on OrientDB's bundled GraalJS (Nashorn is gone). `javax.script` is still used.
- Uses the legacy `OSQLSynchQuery` (`repository/ODocumentAliasRepository`).

## Upgrade risk: MEDIUM

- OrientDB internal API churn.
- Markup from the DB may contain inline scripts, which clash with Wicket 9+/10's CSP.
- The mapper APIs used still exist in Wicket 10.
