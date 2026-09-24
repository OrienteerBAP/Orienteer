# orienteer-etl

UI for OrientDB ETL. A JSON ETL config is stored as an `OETLConfig` document and run as an Orienteer task,
using a custom loader and a link transformer.

- **Module:** `org.orienteer.etl.Module`, module name `orienteer-etl`, v2. `IOETLConfig` is a Transponder DAO.
  Registered by `org.orienteer.etl.Initializer`.
- **Packages:** `org.orienteer.etl`, `org.orienteer.etl.component`. Main classes:
  - `OrienteerETLProcessorConfigurator extends OETLProcessorConfigurator`
  - `OETLOrienteerLoader extends OETLAbstractLoader`: about 485 lines, largely copied from OrientDB, and uses the internal `OClassImpl`
  - `OETLLinkFixedTransformer`
  - `OSLF4JMessageHandler`
- **Key deps:** `com.orientechnologies:orientdb-etl:${orientdb.version}`. It is still published in lockstep with OrientDB 3.2.x.
- **Tests:** 1 smoke test. `src/test/resources/config.json` and `source.csv` are unused.

## Pitfalls

- It subclasses ETL internals that change between OrientDB minor releases.
  `b39de728` had to adapt `configureComponent` generics and `OETLContext`. Re-test on every OrientDB bump.
- The loader connects as the DB admin (TODO in code).
- The README still links to the OrientDB 2.2 docs.
- A dead duplicate `IInitializer` sits under `src/main/resources/org/orienteer/orienteerEtl/META-INF/services/`.

## Upgrade risk: MEDIUM

The library is alive, but the subclassed internals are fragile. A real ETL round-trip test using the unused fixtures would help.
