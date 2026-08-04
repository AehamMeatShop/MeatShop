# CI/CD Pipeline

## Overview

MeatShop uses **GitHub Actions** to automate the build and deployment process. Every change pushed to the **main**
branch triggers a deployment pipeline that builds the application, prepares the runtime environment, creates the Docker
image, and deploys the latest version using Docker Compose.

The pipeline is intentionally designed to keep deployments **repeatable, versioned, and fully automated**, while
requiring no manual intervention.

Although the current workflow targets a development environment, its structure is prepared to evolve into a complete
production-grade CI/CD pipeline.

---

# Pipeline Architecture

The deployment workflow follows the sequence below:

```text
Developer Push

        │

        ▼

Checkout Source Code

        │

        ▼

Read Application Version

        │

        ▼

Generate Environment Configuration

        │

        ▼

Build Spring Boot Application

        │

        ▼

Build Docker Image

        │

        ▼

Deploy using Docker Compose

        │

        ▼

Verify Running Containers
```

Each stage has a single responsibility, making the workflow easier to maintain and extend.

---

# Deployment Trigger

The pipeline automatically executes whenever changes are pushed to the **main** branch.

```yaml
on:
  push:
    branches:
      - main
```

Using the main branch as the deployment trigger guarantees that every deployed version corresponds to a tracked state in
the repository.

---

# Versioning Strategy

Application versioning is managed through a dedicated **VERSION** file located at the project root.

Before building the Docker image, the pipeline reads the current version and uses it as the image tag.

Example:

```text
VERSION

1.0.0
```

Docker image:

```text
aehamalhaeak/meat_shop:1.0.0
```

This approach provides:

- Traceable deployments
- Predictable image versions
- Easier rollback
- Clear release history

---

# Environment Configuration

Runtime configuration is generated dynamically during deployment.

Instead of storing sensitive configuration inside the repository, the workflow creates the application's `.env` file
using **GitHub Secrets**.

Configuration includes:

- Database settings
- Redis configuration
- Logging configuration
- JWT secrets
- Monitoring configuration
- Grafana credentials

This ensures that sensitive information never becomes part of the source code.

---

# Build Process

The backend application is built using Maven.

The workflow performs a clean build before creating the Docker image to guarantee that every deployment is produced from
a fresh build.

Current build command:

```bash
mvn clean install -DskipTests
```

The resulting executable JAR is packaged inside the backend Docker image.

---

# Containerization

The backend is distributed as a Docker image.

Each deployment builds a versioned image using the current application version.

Example:

```text
aehamalhaeak/meat_shop:1.0.0
```

Using Docker guarantees that the application behaves consistently across different environments regardless of the
underlying operating system.

---

# Deployment

Application deployment is performed through Docker Compose.

The deployment process automatically:

- Stops previous containers
- Starts updated containers
- Recreates services when necessary
- Preserves persistent data using Docker Volumes

The deployment includes the complete application stack rather than only the backend service.

Current services include:

- PostgreSQL
- Redis
- Spring Boot Backend
- Loki
- Alloy
- Prometheus
- Grafana

This allows every deployment to provision a complete development environment with a single workflow execution.

---

# Deployment Environment

Deployments are executed using a **Self-Hosted GitHub Actions Runner**.

Unlike cloud-hosted runners, the self-hosted runner has direct access to the target server and Docker Engine, allowing
the workflow to deploy containers immediately after a successful build.

Benefits include:

- Direct Docker access
- Faster deployments
- Complete control over the execution environment
- Reduced infrastructure limitations
- Easier integration with local services

---

# Health Verification

After deployment, the workflow verifies that the containers have started successfully.

Container status is validated before the pipeline completes, providing immediate feedback if deployment issues occur.

This verification step helps detect problems early and reduces deployment failures.

---

# Security

Sensitive configuration is managed entirely through GitHub Secrets.

Examples include:

- Database credentials
- JWT secret key
- Redis configuration
- Grafana credentials

No confidential information is stored inside the repository.

This approach follows modern DevOps security practices by separating application code from runtime configuration.

---

# Design Principles

The CI/CD pipeline was designed around the following principles:

- Fully automated deployments
- Reproducible builds
- Version-controlled releases
- Secure configuration management
- Container-based deployment
- Minimal manual intervention
- Easy maintenance
- Future scalability

---

# Future Evolution

The current pipeline represents the first stage of the project's deployment strategy.

As the application evolves toward a **Microservices Architecture**, the deployment process will evolve accordingly.

Future improvements include:

- Unit Test execution
- Integration Test execution
- Automated Security Scanning
- Docker Image Scanning
- Staging Environment
- Production Environment
- Blue-Green Deployment
- Rolling Updates
- Independent pipelines for each microservice
- Kubernetes deployment
- Automated rollback
- Deployment notifications

---

# Long-Term Vision

The current pipeline is intentionally simple while establishing the foundation for future DevOps practices.

As MeatShop grows, the deployment workflow will transition from deploying a single modular application into
orchestrating multiple independently deployable services, each with its own build, testing, versioning, and deployment
lifecycle.

This evolution will allow every service to be released independently while maintaining a fully automated deployment
process.

---

# Summary

The CI/CD pipeline provides an automated and repeatable deployment process for MeatShop.

By combining GitHub Actions, Docker, Docker Compose, versioned releases, and secure configuration management, the
project achieves reliable deployments while remaining ready for future expansion toward Kubernetes, Microservices, and
production-grade DevOps workflows.