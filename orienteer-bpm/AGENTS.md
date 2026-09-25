# orienteer-bpm — PARKED

**Not in the default build.** It was commented out of the root `<modules>` in `b39de728` (2024-02-08,
"Disable BPM and tours temporary"); no reason was recorded. Now it's in the opt-in profile `parked` (plan D3):
`./mvnw -Pparked -pl orienteer-bpm -am verify`.

Business Process Management on **Camunda 7.5**.
- A custom persistence layer stores all Camunda entities in OrientDB instead of SQL/MyBatis.
- Also provides BPMN modeler widgets and task/start-form widgets.

- **Module:** `org.orienteer.bpm.BPMModule`, module name `bpm`, v2. Depends on devutils. Registered by `org.orienteer.bpm.Initializer`.
- **Packages:**
  - `camunda`: `OProcessEngineConfiguration extends StandaloneProcessEngineConfiguration`, `OPersistenceSession`
  - `camunda.handler(.history)`: about 50 entity handlers mirroring Camunda-internal classes
  - `camunda.scripting`, `component(.command|.widget)`, `method`, `web`
- **Key deps:**
  - `camunda-bom` 7.5.0 (2016). Camunda 7 Community Edition reached EOL with 7.24 in Oct 2025.
    Transitively: MyBatis 3.2.8, Spring 3.1.2, commons-email 1.2.
  - `wicket-bpmn-io:1.0`: external, built against **Wicket 7.4**.
  - `orientqb` 0.2.0 (declared in core, used only here).
- **Tests:** 14 in `TestBPMModule`, using Camunda's `ProcessEngineRule`, with 10 `.bpmn` fixtures.
  Some fixtures contain JavaScript script tasks, which relied on Nashorn.
- **Resources:** `META-INF/processes.xml` wires `OProcessEngineConfiguration`.

## Pitfalls

- Deeply coupled to Camunda **internal** entity and session classes, including reflection into a private `JobEntity` field.
- Heavy use of the legacy `OCommandSQL`/`OSQLSynchQuery`.

## Upgrade risk: HIGH

Effectively a rewrite. P8 options:
1. drop it
2. rewrite on another engine (Flowable, Camunda 8/Zeebe via API)
3. build native Orienteer workflows

Until then, it stays parked.
