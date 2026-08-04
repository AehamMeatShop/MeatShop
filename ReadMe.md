# MeatShop

A comprehensive meat shop management system built with Spring Boot 3.4.5 and Spring Modulith, implementing a modular monolith architecture for managing employees, sales, payments, inventory, and customer relationships.

## Overview

MeatShop is a full-featured business management system designed specifically for meat shop operations. It provides end-to-end functionality for product management, inventory tracking, financial operations, employee management, and customer relationship management.

## Technology Stack

- **Framework**: Spring Boot 3.4.5
- **Java**: 21
- **Architecture**: Modular Monolith (Spring Modulith 1.3.4)
- **Database**: PostgreSQL 17
- **Cache**: Redis 7.2
- **Security**: Spring Security + JWT (JJWT 0.12.6)
- **Monitoring**: Prometheus, Grafana, Loki, Grafana Alloy
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose
- **CI/CD**: GitHub Actions

## Documentation

Comprehensive documentation is available in the `docs/` folder:

- **[Architecture](docs/architecture.md)** - System architecture, technology stack, and design decisions
- **[Features](docs/features.md)** - Complete list of features and capabilities
- **[Security](docs/security.md)** - Security module features and authentication/authorization
- **[Modular Monolith](docs/modular-monolith.md)** - Modular monolith design details and implementation
- **[Database Design](docs/database-design.md)** - Database schema, tables, and relationships
- **[API Documentation](docs/api.md)** - REST API endpoints and usage
- **[Docker Compose](docs/docker-compose.md)** - Docker services configuration and rationale
- **[Monitoring](docs/monitoring.md)** - Monitoring stack configuration and usage
- **[CI/CD](docs/ci-cd.md)** - Continuous integration and deployment pipeline

## Quick Start

### Prerequisites

- Java 21
- Maven 3.x
- Docker & Docker Compose
- Git

### Local Development

1. **Clone the repository**
```bash
git clone <repository-url>
cd MeatShop
```

2. **Configure environment variables**
```bash
cd Docker
cp .env.example .env
# Edit .env with your configuration
```

3. **Start services with Docker Compose**
```bash
docker compose up -d
```

4. **Access the application**
- Backend API: http://localhost:8080
- Grafana: http://localhost:3000
- Prometheus: http://localhost:9090

### Building from Source

1. **Build the backend**
```bash
cd BackEnd
mvn clean install
```

2. **Run the application**
```bash
mvn spring-boot:run
```

## Project Structure

```
MeatShop/
├── BackEnd/                 # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Java source code
│   │   │   └── resources/  # Configuration files
│   │   └── test/           # Test files
│   ├── Dockerfile          # Docker image definition
│   └── pom.xml             # Maven configuration
├── Docker/                 # Docker Compose configuration
│   ├── docker-compose.yaml # Service orchestration
│   └── .env                # Environment variables
├── Monitoring/             # Monitoring configuration
│   ├── grafana/            # Grafana dashboards and provisioning
│   ├── prometheus/         # Prometheus configuration
│   ├── loki/               # Loki configuration
│   └── alloy/              # Alloy configuration
├── docs/                   # Documentation
│   ├── architecture.md
│   ├── features.md
│   ├── security.md
│   ├── modular-monolith.md
│   ├── database-design.md
│   ├── api.md
│   ├── docker-compose.md
│   ├── monitoring.md
│   └── ci-cd.md
└── .github/                # GitHub Actions CI/CD
    └── workflows/
        └── ci-cd.yaml
```

## Modules

The application is organized into the following modules:

- **Security** - Authentication, authorization, and session management
- **Employees** - Employee management and operations
- **Products** - Product catalog and inventory management
- **Parties** - Customer and supplier management
- **Finances** - Financial operations and transactions
- **Shared** - Common utilities and components

## API Access

The API is accessible at `http://localhost:8080`. All endpoints require JWT authentication except for the login endpoint.

### Authentication

1. **Login**
```bash
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password"
}
```

2. **Use the token**
```bash
Authorization: Bearer <access_token>
```

See [API Documentation](docs/api.md) for complete endpoint details.

## Monitoring

### Grafana Dashboards
- **URL**: http://localhost:3000
- **Default Credentials**: Check `.env` file
- **Dashboards**: Pre-configured dashboards for application and system metrics

### Prometheus
- **URL**: http://localhost:9090
- **Metrics**: http://localhost:9090/metrics

### Logs
Logs are aggregated by Loki and can be viewed in Grafana Explore.

See [Monitoring Documentation](docs/monitoring.md) for detailed monitoring setup.

## Development

### Code Quality
```bash
cd BackEnd
mvn checkstyle:check
```

### Database Migrations
Database migrations are managed by Flyway and located in `BackEnd/src/main/resources/db/migration/`.

## Deployment

### Docker Deployment
```bash
cd Docker
docker compose up -d
```

### CI/CD Pipeline
The project uses GitHub Actions for CI/CD. See [CI/CD Documentation](docs/ci-cd.md) for pipeline details.

### Environment Variables
Required environment variables are listed in the [Docker Compose Documentation](docs/docker-compose.md).

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

[Specify your license here]

## Support

For issues, questions, or contributions:
- Create an issue in the repository
- Contact the development team
- Refer to the documentation in the `docs/` folder

## Acknowledgments

- Spring Boot team for the excellent framework
- Spring Modulith team for the modular monolith support
- The open-source community for the tools and libraries used
