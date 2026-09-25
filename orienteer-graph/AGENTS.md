# orienteer-graph

OrientDB graph support: widgets for vertices, edges and neighbours, plus commands to create, delete and unlink
vertices and edges.

- **Module:** `org.orienteer.graph.module.GraphModule`, module name `graph`, v1.
  - Guice: `org.orienteer.graph.service.GraphGuiceModule`, which provides a `@RequestScoped` Blueprints `OrientGraph`
    via guice-servlet. Registered in `META-INF/services/com.google.inject.Module`.
  - Wicket: `org.orienteer.graph.Initializer`.
- **Packages:** `component.command`, `component.widget`, `model` (`OVertexWrapper`, `OEdgeWrapper`), `module`, `service`.
- **Key deps:** `orientdb-graphdb:${orientdb.version}`. It brings:
  - TinkerPop **2** Blueprints 2.6.0 (legacy API)
  - Groovy 2.5 (EOL)
  - jettison 1.3.3 (CVEs)
  - commons-configuration 1.6
- **Tests:** **none**.

## Pitfalls

- The pom sets `<finalName>orienteer</finalName>` (copy-paste from core).
- No tests: add at least a smoke test and a vertex/edge create test before refactoring (P8).

## Upgrade risk: MEDIUM

- Rewrite from Blueprints (`OrientGraph`/`OrientVertex`/`OrientEdge`) to OrientDB 3's native `OVertex`/`OEdge`.
  That drops orientdb-graphdb, Groovy and jettison.
- `CreateVertexCommand`/`CreateEdgeCommand` use `ModalWindow` via core (removed in Wicket 10).
- guice-servlet's `@RequestScoped` moves to jakarta in Guice 7.
- **Parked** (profile `parked`, plan D3): `./mvnw -Pparked -pl orienteer-graph -am verify`. It was removed from standalone; re-add it there if P8 keeps it.
