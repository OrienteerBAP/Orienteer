# orienteer-devutils

Developer tools:
- Wicket DebugBar and the live-sessions inspector
- a web console built on `wicket-console`, with OrientDB SQL and OrientDB console script engines
- a monitoring widget
- `ToolsPage`

- **Module:** `org.orienteer.devutils.Module`, module name `devutils`, v1. It also implements `IComponentInitializationListener`.
  Registered by `org.orienteer.devutils.Initializer`.
- **Packages:**
  - `org.orienteer.devutils`: `ODBScriptEngine`, `ODBConsoleEngine` and their factories
  - `component(.widget)`
  - `web` (`ToolsPage`)
- **Key deps:**
  - `ru.ydn.wicket.wicket-console:wicket-console:1.4-SNAPSHOT`. **External and unresolvable**: it must be built from its own repo.
  - `wicket-devutils` (managed by the Wicket version)
  - `OConsoleDatabaseApp` from orientdb-tools
- **Tests:** 3 in `TestModule`: module loaded, SQL via `ScriptExecutor`, and `ToolsPage` render with `@Sudo`.

## Pitfalls

- logger-server, users, standalone (and the parked bpm) depend on this module. Keep its API stable.
- `wicket-console` finds engines through `javax.script`. Nashorn is gone from JDK 15+, so a JS console
  exists only if GraalJS (which OrientDB brings) or `nashorn-core` is on the classpath.
- Uses the deprecated `ODatabaseDocumentTx` and `ODatabaseRecordThreadLocal`.

## Upgrade risk: MEDIUM

The code is small. The blocker is `wicket-console`, which needs a release for Java 21 and later for Wicket 10/jakarta.
See `REFRESH_PLAN.md` → External dependencies, P8.
