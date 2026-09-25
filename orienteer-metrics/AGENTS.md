# orienteer-metrics

Prometheus endpoint at `/metrics` (`OMetricsResource`, `@MountPath`). It exposes:
- Wicket request, page and exception counters
- session gauges
- OrientDB metrics
- hotspot JVM metrics

- **Module:** `org.orienteer.metrics.OMetricsModule`, module name `metrics`, v1. Registered by `org.orienteer.metrics.Initializer`.
  Everything is in package `org.orienteer.metrics` (7 classes).
- **Key deps:** `io.prometheus:simpleclient`, `simpleclient_common` and `simpleclient_hotspot`, all 0.8.1.
  The simpleclient line is deprecated in favour of `prometheus-metrics-*` 1.x.
- **Tests:** 1 smoke test (`TestModule`).

## Pitfalls

- The pom has a large block of commented-out dependencies copied from tours. Delete it (P10).
- There's a TODO waiting on OrientDB issue #9169.

## Upgrade risk: LOW

- `org.apache.wicket.util.time.Time` is removed in Wicket 10; use `java.time.Instant`.
- Optionally migrate to Prometheus client 1.x.
