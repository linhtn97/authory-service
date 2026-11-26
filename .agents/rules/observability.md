# Observability, Logging & Monitoring Standards

## 1. Observability Pillars

Production microservices must be observable across three pillars: **Structured Logging**, **Distributed Tracing**, and **Metrics & Health Probes**.

---

## 2. Structured Logging Standards

### 2.1 Log Pattern & Mapped Diagnostic Context (MDC)
All log statements must include distributed tracing context to correlate log events across microservice boundaries.
Standard pattern in `application.yaml`:
```yaml
logging:
  pattern:
    level: "%5p [${spring.application.name:service},%X{traceId:-},%X{spanId:-}]"
```

### 2.2 Standard Log Levels & Discipline
- **`ERROR`:** An unhandled exception, critical database failure, or severe technical defect that requires immediate operational intervention. Always include the exception instance:
  ```java
  log.error("Failed to commit transactional outbox event for aggregate ID: {}", id, ex);
  ```
- **`WARN`:** An unexpected situation or degraded state that was handled gracefully (e.g. lock acquisition timeout, cache fallback activation, client rate-limited):
  ```java
  log.warn("Distributed lock acquisition timed out for resource: {} within {}", lockKey, waitTime);
  ```
- **`INFO`:** High-level lifecycle milestones (service startup, batch job completion, critical state transition):
  ```java
  log.info("Order status transitioned to CONFIRMED for ID: {}", orderId);
  ```
- **`DEBUG`:** Diagnostic details useful for local development and root cause investigation in non-production environments.

### 2.3 Strict Privacy & Security Logging Prohibitions
**NEVER log the following sensitive data:**
- Passwords, client secrets, API keys, or private keys.
- Authorization bearer tokens (`Bearer eyJ...`).
- Full Payment Card Industry (PCI) data (credit card numbers, CVVs).
- Personally Identifiable Information (PII) without masking.
- Large binary payloads, complete file streams, or request bodies exceeding 10 KB.

---

## 3. Distributed Tracing (OpenTelemetry & Micrometer)

Distributed tracing tracks incoming requests as they traverse across HTTP clients, relational databases, distributed caches, and message brokers.

### 3.1 Trace Context Propagation (W3C Standard)
- Incoming HTTP requests must accept and propagate standard W3C `traceparent` headers.
- When publishing messages to Kafka or RabbitMQ, inject `traceparent` into message headers.
- OpenTelemetry instrumentation automatically bridges tracing to Spring Boot via Micrometer Tracing:
  ```yaml
  management:
    tracing:
      sampling:
        probability: 1.0 # Sample 100% in dev/staging, configure 0.1-0.2 in high-throughput prod
    otlp:
      tracing:
        endpoint: ${OTLP_TRACING_ENDPOINT:http://localhost:4318/v1/traces}
  ```

---

## 4. Spring Boot Actuator & Health Probes

Microservices running in containerized environments (Kubernetes, Docker Swarm) must expose standard health and readiness probes.

### 4.1 Actuator Configuration
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus
  endpoint:
    health:
      show-details: when_authorized
      probes:
        enabled: true
```

### 4.2 Standard Probe Endpoints
- **Liveness Probe:** `/actuator/health/liveness`
  - Verifies that the internal application context is alive and responsive. If down, container orchestrator restarts the pod.
- **Readiness Probe:** `/actuator/health/readiness`
  - Verifies that the service can accept user traffic (database connectivity, cache cluster ready). If down, traffic routing stops until recovery.
- **Prometheus Metrics:** `/actuator/prometheus`
  - Scraped periodically by Prometheus for alerting and Grafana dashboards.

---

## 5. Metrics & Custom Instrumentation (Micrometer)

### 5.1 Standard System & HTTP Metrics
- Monitor HTTP server requests latency and percentile distribution:
  ```yaml
  management:
    metrics:
      distribution:
        percentiles-histogram:
          http.server.requests: true
  ```
- Monitor HikariCP connection pool usage (`hikaricp.connections.active`, `hikaricp.connections.pending`).

### 5.2 Custom Domain Metrics
Inject `MeterRegistry` into application services for critical business metrics:
```java
@Service
public class OrderCreateService implements OrderCreateUseCase {
    private final Counter orderCreatedCounter;

    public OrderCreateService(MeterRegistry meterRegistry, ...) {
        this.orderCreatedCounter = Counter.builder("business.orders.created")
            .description("Total number of successfully placed orders")
            .tag("tier", "standard")
            .register(meterRegistry);
    }

    public OrderCreateResult execute(...) {
        // ... business logic
        orderCreatedCounter.increment();
        return result;
    }
}
```
