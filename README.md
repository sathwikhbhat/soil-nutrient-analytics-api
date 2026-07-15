# Soil Nutrient Analytics API

A backend REST API for recording, classifying, and analyzing soil nutrient test data, built for use cases like agricultural extension services, GIS-based fertility mapping, and district/crop-level fertility reporting.

The API stores individual soil sample test results (location, crop, and 12 nutrient parameters), automatically classifies each nutrient as **LOW / MEDIUM / HIGH** against configurable agronomic thresholds, and exposes dashboard, GIS, and CSV report endpoints built on top of that data.

## Features

- **Soil record management**: full CRUD for individual soil test samples, plus bulk import via CSV upload
- **Automatic nutrient classification**: each of the 12 tracked nutrients is classified as `LOW`, `MEDIUM`, or `HIGH` using thresholds defined in configuration (no code changes needed to tune agronomic limits)
- **Dashboard analytics**: sample/district/crop coverage, nutrient distribution, deficiency percentages, average nutrient levels, and time-based trends (monthly / seasonal / yearly)
- **GIS endpoints**: map markers and heat-map data for soil samples, filterable by state, district, taluk, village, and crop
- **CSV report generation**: downloadable reports: nutrient status, soil fertility, crop-wise summary, and district summary
- **Centralized error handling**: consistent JSON error responses across the API
- **Dockerized**: multi-stage Dockerfile plus a `docker-compose.yml` that wires up the API with MongoDB

## Tech Stack

| Layer            | Technology |
|-------------------|------------|
| Language           | Java 21 |
| Framework          | Spring Boot 4.1.0 (Spring Web MVC, Spring Validation) |
| Database           | MongoDB (Spring Data MongoDB) |
| CSV parsing/writing | Apache Commons CSV |
| Boilerplate reduction | Lombok |
| Build tool          | Maven (with Maven Wrapper) |
| Code style           | Spotless + Palantir Java Format |
| CI                   | GitHub Actions (`mvn clean verify` on push/PR to `main`) |
| Containerization      | Docker, Docker Compose |

## Project Structure

The codebase is organized by **feature/domain module** rather than by technical layer:

```
src/main/java/com/sathwikhbhat/soilanalytics/
├── SoilNutrientAnalyticsApiApplication.java   # Spring Boot entry point
│
├── soilrecord/          # Core soil sample CRUD + CSV import
│   ├── controller/
│   ├── dto/
│   ├── entity/          # SoilRecord, Location, NutrientData
│   ├── mapper/
│   ├── repository/
│   └── service/         # SoilRecordService, FileParserService
│
├── classification/      # Nutrient LOW/MEDIUM/HIGH classification logic
│   ├── config/          # NutrientClassificationProperties (bound from application.yml)
│   ├── dto/
│   └── model/
│
├── dashboard/           # Aggregated analytics (overview, distribution, trends, etc.)
│   ├── controller/
│   ├── dto/
│   ├── repository/      # MongoTemplate-based aggregations
│   └── service/
│
├── gis/                 # Map markers & heat-map data
│   ├── controller/
│   ├── dto/
│   └── service/
│
├── report/              # CSV report generation
│   ├── controller/
│   ├── dto/
│   ├── enums/
│   ├── service/
│   └── util/
│
├── health/               # Liveness / version / info endpoints
│   └── controller/
│
├── mongo/                # Shared Mongo query helpers
│
└── exception/            # Custom exceptions + @ControllerAdvice global handler
```

## Prerequisites

- **Java 21**
- **Maven** (or just use the bundled `./mvnw` wrapper, no local Maven install required)
- **MongoDB** (locally, via Docker, or a hosted instance), or use **Docker Compose** to run everything together

## Getting Started

### Option 1: Docker Compose (recommended)

This spins up both the API and a MongoDB instance:

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080/api/v1`, and MongoDB at `localhost:27017`.

### Option 2: Run locally with Maven

1. Start a local MongoDB instance (or point the app at an existing one; see [Configuration](#configuration)):
   ```bash
   docker run -d --name soil-mongodb -p 27017:27017 mongo:7
   ```
2. Build and run the application:
   ```bash
   ./mvnw clean spring-boot:run
   ```
   or build a jar and run it directly:
   ```bash
   ./mvnw clean package -DskipTests
   java -jar target/soil-nutrient-analytics-api-0.0.1-SNAPSHOT.jar
   ```
3. The API will be available at `http://localhost:8080/api/v1`.

## Configuration

Configuration lives in `src/main/resources/application.yml`.

| Property | Default | Description |
|---|---|---|
| `spring.mvc.servlet.path` | `/api/v1` | Base path prefixed to every endpoint |
| `spring.data.mongodb.host` | `mongo` | MongoDB host (overridable via `SPRING_DATA_MONGODB_HOST`) |
| `spring.data.mongodb.port` | `27017` | MongoDB port (overridable via `SPRING_DATA_MONGODB_PORT`) |
| `spring.data.mongodb.database` | `soil_nutrient_analytics` | MongoDB database name (overridable via `SPRING_DATA_MONGODB_DATABASE`) |
| `app.timezone` | `Asia/Kolkata` | Timezone used for all generated timestamps (`createdAt`, `updatedAt`, report generation time, error timestamps) |
| `nutrient-classification.*` | see below | Per-nutrient LOW/MEDIUM thresholds used for classification |

All standard Spring Boot environment variable overrides apply (e.g. running via `docker-compose.yml` overrides the Mongo connection via `SPRING_DATA_MONGODB_HOST`/`PORT`/`DATABASE` environment variables).

### Nutrient classification thresholds

Each nutrient is classified as `LOW` if its value is `<= low`, `MEDIUM` if `<= medium`, otherwise `HIGH`. Defaults (from `application.yml`):

| Nutrient | LOW ≤ | MEDIUM ≤ |
|---|---|---|
| pH | 6.5 | 7.5 |
| EC (dS/m) | 0.8 | 2.0 |
| Organic Carbon (%) | 0.50 | 0.75 |
| Nitrogen (kg/ha) | 280 | 560 |
| Phosphorus (kg/ha) | 10 | 25 |
| Potassium (kg/ha) | 120 | 280 |
| Sulfur (ppm) | 10 | 20 |
| Zinc (ppm) | 0.6 | 1.2 |
| Boron (ppm) | 0.5 | 1.0 |
| Iron (ppm) | 4.5 | 9.0 |
| Copper (ppm) | 0.2 | 0.4 |
| Manganese (ppm) | 2.0 | 4.0 |

These are configurable; no code changes are needed to adjust the agronomic thresholds, only `application.yml` (or an environment-specific override).

## API Documentation

Full endpoint-by-endpoint reference, including request/response bodies, status codes, and error formats, is in **[API_DOCS.md](API_DOCS.md)**.

Quick summary of resource groups (all prefixed with `/api/v1`):

| Group | Base path | Purpose |
|---|---|---|
| Soil Records | `/soil-records` | CRUD + CSV upload + per-record nutrient classification |
| Dashboard | `/dashboard` | Aggregated analytics across all soil records |
| GIS | `/gis` | Map markers and heat-map data |
| Reports | `/reports` | Downloadable CSV reports |
| Health | `/health`, `/version`, `/info` | Service liveness and metadata |

## Building & Testing

```bash
# Run tests
./mvnw test

# Full build (compile, test, package)
./mvnw clean verify

# Apply code formatting (Spotless / Palantir Java Format)
./mvnw spotless:apply
```

Code style is enforced via the **Spotless** Maven plugin using **Palantir Java Format**; CI (`.github/workflows/maven-build.yml`) runs `./mvnw clean verify` on every push and pull request to `main`.

## Deployment

The included `Dockerfile` is a multi-stage build:
1. Builds the jar using `maven:3.9.11-eclipse-temurin-21`
2. Packages the final image on the slim `eclipse-temurin:21-jre` runtime, exposing port `8080`

```bash
docker build -t soil-nutrient-analytics-api .
docker run -p 8080:8080 --env SPRING_DATA_MONGODB_HOST=<mongo-host> soil-nutrient-analytics-api
```
