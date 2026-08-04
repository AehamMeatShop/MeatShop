# Monitoring

## Overview
MeatShop implements a comprehensive monitoring stack using Prometheus, Grafana, Loki, and Grafana Alloy. This setup provides complete observability with metrics collection, log aggregation, and visualization capabilities.

## Monitoring Architecture

### Components
```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Backend   │────▶│    Alloy    │────▶│  Prometheus │
│  Instances  │     │  (Collector)│     │  (Metrics)  │
└─────────────┘     └─────────────┘     └─────────────┘
                                              │
                                              ▼
                                     ┌─────────────┐
                                     │   Grafana   │
                                     │(Visualization)│
                                     └─────────────┘
                                              ▲
                                              │
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Backend   │────▶│    Alloy    │────▶│    Loki     │
│  Instances  │     │  (Collector)│     │   (Logs)    │
└─────────────┘     └─────────────┘     └─────────────┘
```

### Data Flow
1. **Backend Instances**: Expose metrics via Spring Boot Actuator
2. **Alloy**: Collects metrics and logs from containers
3. **Prometheus**: Stores and queries metrics
4. **Loki**: Stores and queries logs
5. **Grafana**: Visualizes metrics and logs
6. **Node Exporter**: Provides system-level metrics

## Components

### 1. Prometheus

**Purpose**: Time-series database for metrics storage and querying

**Version**: `latest`

**Configuration File**: `Monitoring/prometheus/prometheus.yml`

**Port**: `9090`

**Key Features**:
- **Metrics Storage**: Efficient time-series data storage
- **Query Language**: Powerful PromQL for querying
- **Alerting**: Built-in alerting capabilities
- **Service Discovery**: Automatic service discovery
- **Scraping**: Periodic metric scraping

**Configuration**:
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'meat-shop'
    static_configs:
      - targets: ['backend:8080']
      - targets: ['backend-2:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 10s
    scrape_timeout: 5s

  - job_name: 'node'
    static_configs:
      - targets: ['node-exporter:9100']
    scrape_interval: 15s

  - job_name: 'prometheus'
    static_configs:
      - targets: ['prometheus:9090']
```

**Scrape Jobs**:
- **meat-shop**: Scrapes backend application metrics (10s interval)
- **node**: Scrapes system metrics from node-exporter (15s interval)
- **prometheus**: Scrapes Prometheus self-metrics

**Why Prometheus**:
- **Industry Standard**: De facto standard for metrics monitoring
- **Powerful Querying**: PromQL provides powerful querying capabilities
- **Ecosystem**: Large ecosystem of integrations
- **Scalability**: Horizontal scaling capabilities
- **Open Source**: Free and open source

### 2. Grafana

**Purpose**: Visualization and dashboard platform

**Version**: `10.4.2`

**Port**: `3000` (configurable via `GF_SERVER_HTTP_PORT`)

**Key Features**:
- **Dashboards**: Pre-built and custom dashboards
- **Visualization**: Multiple visualization types
- **Alerting**: Visual alerting and notifications
- **Data Sources**: Multiple data source support
- **User Management**: Built-in user management

**Configuration**:
- **Dashboards**: `Monitoring/grafana/dashboards/`
- **Provisioning**: `Monitoring/grafana/provisioning/`
- **Data Sources**: Auto-provisioned Prometheus and Loki data sources
- **Authentication**: Admin credentials from environment variables

**Environment Variables**:
- `GF_SECURITY_ADMIN_USER`: Admin username
- `GF_SECURITY_ADMIN_PASSWORD`: Admin password
- `GF_USERS_ALLOW_SIGN_UP`: User sign-up setting

**Why Grafana 10.4.2**:
- **Stability**: Stable and mature version
- **Features**: Rich feature set
- **Integration**: Seamless integration with Prometheus and Loki
- **User Experience**: Intuitive user interface
- **Community**: Large community and plugin ecosystem

### 3. Loki

**Purpose**: Log aggregation and storage system

**Version**: `3.0.0`

**Port**: `3100` (configurable via `LOKI_SERVER_HTTP_PORT`)

**Key Features**:
- **Log Aggregation**: Centralized log collection
- **LogQL**: Powerful log query language
- **Labels**: Label-based log organization
- **Efficient Storage**: Efficient log storage
- **Grafana Integration**: Seamless Grafana integration

**Configuration File**: `Monitoring/loki/loki.yml`

**Configuration**:
```yaml
auth_enabled: false

server:
  http_listen_port: 3100

common:
  path_prefix: /loki
  storage:
    filesystem:
      chunks_directory: /loki/chunks
      rules_directory: /loki/rules
  replication_factor: 1

schema_config:
  configs:
    - from: 2024-01-01
      store: tsdb
      object_store: filesystem
      schema: v13
      index:
        prefix: index_
        period: 24h

storage_config:
  filesystem:
    directory: /loki/chunks

limits_config:
  retention_period: 72h

compactor:
  working_directory: /loki/compactor
  retention_enabled: true
  delete_request_store: filesystem

table_manager:
  retention_deletes_enabled: true
  retention_period: 72h
```

**Key Settings**:
- **Retention**: 72-hour log retention
- **Storage**: Filesystem-based storage
- **Schema**: v13 schema for optimal performance
- **Replication**: Single replica (replication_factor: 1)

**Why Loki 3.0.0**:
- **Lightweight**: Lower resource requirements than ELK stack
- **Grafana Native**: Native Grafana integration
- **Cost Effective**: Lower operational costs
- **Modern**: Modern log aggregation approach
- **Performance**: High-performance log querying

### 4. Grafana Alloy

**Purpose**: Unified agent for metrics and logs collection

**Version**: `latest`

**Key Features**:
- **Unified Agent**: Single agent for metrics and logs
- **Service Discovery**: Automatic service discovery
- **Processing**: Built-in log processing
- **Prometheus Compatible**: Drop-in Prometheus replacement
- **Flexible Configuration**: Flexible configuration language

**Configuration File**: `Monitoring/alloy/alloy.config`

**Configuration**:
```alloy
// ===== LOKI OUTPUT =====
loki.write "default" {
  endpoint {
    url = "http://loki:3100/loki/api/v1/push"
  }
}

// ===== DOCKER DISCOVERY =====
discovery.docker "containers" {
  host = "unix:///var/run/docker.sock"
  refresh_interval = "10s"
}

// ===== FILTER ONLY NEEDED CONTAINERS =====
discovery.relabel "filtered_containers" {
  targets = discovery.docker.containers.targets
  rule {
    source_labels = ["__meta_docker_container_name"]
    regex = "/(meat_shop_backend|meat_shop_postgres|meat_shop_redis|meat_shop_backend_2)"
    action = "keep"
  }
  rule {
    source_labels = ["__meta_docker_container_name"]
    regex = "/meat_shop_(.*)"
    target_label = "service"
  }
}

// ===== LOGS SOURCE =====
loki.source.docker "docker_logs" {
  host    = "unix:///var/run/docker.sock"
  targets = discovery.relabel.filtered_containers.output
  forward_to = [loki.process.docker_processor.receiver]
}

// ===== PROCESSING & LABELS =====
loki.process "docker_processor" {
  forward_to = [loki.write.default.receiver]
  stage.docker {}
  stage.regex {
    expression = "\\s+(?P<level>DEBUG|INFO|WARN|ERROR)\\s+"
  }
  stage.labels {
    values = {
      level     = "level",
      container = "name",
      service = "service",
    }
  }
  stage.static_labels {
    values = {
      environment = "development",
      application = "MeatShop",
    }
  }
}

// ===== PROMETHEUS =====
prometheus.scrape "meatshop" {
  targets = [
    {
      __address__ = "backend:8080",
      job = "meat-shop",
      instance_name = "Main",
    },
    {
      __address__ = "backend-2:8080",
      job = "meat-shop",
      instance_name = "Backup",
    },
  ]
  metrics_path = "/actuator/prometheus"
  forward_to = [prometheus.remote_write.default.receiver]
}

prometheus.remote_write "default" {
  endpoint {
    url = "http://prometheus:9090/api/v1/write"
  }
}
```

**Key Features**:
- **Docker Discovery**: Automatic container discovery
- **Log Processing**: Log level extraction and labeling
- **Metrics Scraping**: Prometheus-compatible metrics scraping
- **Remote Write**: Remote write to Prometheus
- **Label Enrichment**: Automatic label enrichment

**Why Grafana Alloy**:
- **Unified**: Single agent for metrics and logs
- **Modern**: Modern observability agent
- **Flexible**: Flexible configuration
- **Prometheus Compatible**: Drop-in Prometheus replacement
- **Grafana Native**: Native Grafana integration

### 5. Node Exporter

**Purpose**: System-level metrics exporter

**Version**: `latest`

**Port**: `9100`

**Key Features**:
- **System Metrics**: CPU, memory, disk, network metrics
- **Lightweight**: Minimal resource usage
- **Prometheus Compatible**: Native Prometheus support
- **Comprehensive**: Comprehensive system metrics
- **Standard**: Industry standard for system metrics

**Configuration**:
```yaml
command:
  - '--path.procfs=/host/proc'
  - '--path.sysfs=/host/sys'
  - '--collector.filesystem.mount-points-exclude=^/(sys|proc|dev|host|etc)($$|/)'
  - '--collector.netdev.device-exclude=^(veth.*)$$'
```

**Metrics Collected**:
- **CPU**: CPU usage, load average
- **Memory**: Memory usage, swap
- **Disk**: Disk usage, I/O
- **Network**: Network traffic, errors
- **System**: System information, uptime

**Why Node Exporter**:
- **Standard**: Industry standard for system metrics
- **Comprehensive**: Comprehensive metrics coverage
- **Lightweight**: Minimal resource usage
- **Reliable**: Reliable and stable
- **Prometheus Native**: Native Prometheus support

## Metrics

### Application Metrics

#### Spring Boot Actuator Metrics
The backend application exposes metrics via Spring Boot Actuator at `/actuator/prometheus`:

**JVM Metrics**:
- `jvm_memory_used_bytes`: JVM memory usage
- `jvm_memory_max_bytes`: JVM maximum memory
- `jvm_threads_live_threads`: Live thread count
- `jvm_gc_pause_seconds`: GC pause time

**HTTP Metrics**:
- `http_server_requests_seconds`: HTTP request duration
- `http_server_requests_seconds_count`: Request count
- `http_server_requests_seconds_sum`: Request duration sum

**Database Metrics**:
- `hikaricp_connections_active`: Active database connections
- `hikaricp_connections_idle`: Idle database connections
- `hikaricp_connections_max`: Maximum connections

**Cache Metrics**:
- `cache_gets`: Cache get operations
- `cache_puts`: Cache put operations
- `cache_hits`: Cache hits
- `cache_misses`: Cache misses

**Custom Metrics**:
- `application_instance_id`: Application instance identifier
- `application_instance_name`: Application instance name

### System Metrics

#### Node Exporter Metrics
- `node_cpu_seconds_total`: CPU time
- `node_memory_MemAvailable_bytes`: Available memory
- `node_memory_MemTotal_bytes`: Total memory
- `node_filesystem_avail_bytes`: Available disk space
- `node_filesystem_size_bytes`: Total disk space
- `node_network_receive_bytes_total`: Network receive bytes
- `node_network_transmit_bytes_total`: Network transmit bytes

## Logging

### Log Sources

#### Application Logs
- **Backend Instances**: Spring Boot application logs
- **PostgreSQL**: Database logs
- **Redis**: Cache logs

#### Log Levels
- **DEBUG**: Detailed debugging information
- **INFO**: General informational messages
- **WARN**: Warning messages
- **ERROR**: Error messages

### Log Processing

#### Alloy Processing
- **Docker Logs**: Collected from Docker containers
- **Log Level Extraction**: Automatic log level extraction
- **Label Enrichment**: Automatic label enrichment
- **Service Labeling**: Service-based labeling

#### Log Labels
- **service**: Service name (backend, postgres, redis)
- **level**: Log level (DEBUG, INFO, WARN, ERROR)
- **container**: Container name
- **environment**: Environment (development)
- **application**: Application name (MeatShop)

### Log Retention
- **Retention Period**: 72 hours
- **Compaction**: Automatic log compaction
- **Cleanup**: Automatic log cleanup

## Dashboards

### Pre-configured Dashboards

#### Application Dashboard
- **Request Rate**: HTTP request rate
- **Response Time**: HTTP response time
- **Error Rate**: HTTP error rate
- **JVM Memory**: JVM memory usage
- **Database Connections**: Database connection pool
- **Cache Performance**: Cache hit/miss ratio

#### System Dashboard
- **CPU Usage**: CPU usage percentage
- **Memory Usage**: Memory usage percentage
- **Disk Usage**: Disk usage percentage
- **Network Traffic**: Network traffic rate
- **System Load**: System load average

#### Logs Dashboard
- **Log Volume**: Log volume over time
- **Error Logs**: Error log count
- **Log Levels**: Log level distribution
- **Service Logs**: Logs by service
- **Recent Logs**: Recent log entries

### Custom Dashboards
Create custom dashboards in Grafana using:
- **Prometheus Queries**: PromQL for metrics
- **LogQL Queries**: LogQL for logs
- **Variables**: Dashboard variables
- **Panels**: Various panel types

## Alerting

### Prometheus Alerting
Configure alerting rules in Prometheus:

```yaml
groups:
  - name: meatshop_alerts
    rules:
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.1
        for: 5m
        annotations:
          summary: "High error rate detected"
      
      - alert: HighMemoryUsage
        expr: jvm_memory_used_bytes / jvm_memory_max_bytes > 0.9
        for: 5m
        annotations:
          summary: "High memory usage detected"
```

### Grafana Alerting
Configure alerts in Grafana dashboards:
- **Threshold Alerts**: Alert on threshold breaches
- **Query Alerts**: Alert on query results
- **Notifications**: Various notification channels

## Access

### Prometheus UI
- **URL**: `http://localhost:9090`
- **Features**: Query interface, graph visualization, status

### Grafana UI
- **URL**: `http://localhost:3000`
- **Credentials**: From environment variables
- **Features**: Dashboards, explore, alerting

### Loki UI
- **URL**: Via Grafana Explore
- **Features**: Log querying, visualization

## Configuration

### Monitoring Configuration Files
- **Prometheus**: `Monitoring/prometheus/prometheus.yml`
- **Loki**: `Monitoring/loki/loki.yml`
- **Alloy**: `Monitoring/alloy/alloy.config`
- **Grafana Dashboards**: `Monitoring/grafana/dashboards/`
- **Grafana Provisioning**: `Monitoring/grafana/provisioning/`

### Environment Variables
```bash
# Grafana
GF_SECURITY_ADMIN_USER=admin
GF_SECURITY_ADMIN_PASSWORD=admin
GF_USERS_ALLOW_SIGN_UP=false
GF_SERVER_HTTP_PORT=3000

# Loki
LOKI_SERVER_HTTP_PORT=3100
```

## Best Practices

### Metrics
- **Relevant Metrics**: Monitor relevant metrics only
- **Cardinality**: Avoid high cardinality metrics
- **Labeling**: Use meaningful labels
- **Retention**: Configure appropriate retention
- **Querying**: Optimize queries for performance

### Logging
- **Log Levels**: Use appropriate log levels
- **Structured Logging**: Use structured logging
- **Sensitive Data**: Avoid logging sensitive data
- **Log Rotation**: Configure log rotation
- **Log Aggregation**: Centralize log aggregation

### Alerting
- **Thresholds**: Set appropriate thresholds
- **Notifications**: Configure appropriate notifications
- **Testing**: Test alert rules
- **Documentation**: Document alert rules
- **Maintenance**: Regularly review and update alerts

## Troubleshooting

### Common Issues

#### Prometheus Not Scraping
- Check Prometheus targets: `http://localhost:9090/targets`
- Check backend metrics endpoint: `curl http://localhost:8080/actuator/prometheus`
- Verify network connectivity
- Check Prometheus configuration

#### Logs Not Appearing in Grafana
- Check Loki status: Verify Loki is running
- Check Alloy configuration: Verify Alloy is configured correctly
- Check log sources: Verify logs are being generated
- Verify Grafana data source: Check Loki data source in Grafana

#### High Memory Usage
- Check metric cardinality: Reduce high cardinality metrics
- Check retention period: Reduce retention if needed
- Check scrape interval: Increase scrape interval
- Check query performance: Optimize queries

## Future Enhancements

### Planned Improvements
- **Alertmanager**: Add Alertmanager for alert management
- **Custom Metrics**: Add more custom business metrics
- **Distributed Tracing**: Add distributed tracing with Jaeger
- **Synthetic Monitoring**: Add synthetic monitoring
- **Anomaly Detection**: Add anomaly detection

### Integration Opportunities
- **PagerDuty**: PagerDuty integration for alerts
- **Slack**: Slack integration for notifications
- **Email**: Email notifications for alerts
- **Webhooks**: Webhook notifications
- **SMS**: SMS notifications for critical alerts
