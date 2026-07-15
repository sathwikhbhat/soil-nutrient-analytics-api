# API Documentation: Soil Nutrient Analytics API

Base URL (local): `http://localhost:8080/api/v1`

All request/response bodies are JSON unless otherwise noted (CSV upload and CSV report download are the exceptions). All timestamps are generated in the `app.timezone` configured zone (default `Asia/Kolkata`).

## Table of Contents

- [Conventions](#conventions)
- [Error Format](#error-format)
- [Soil Records](#soil-records)
  - [Create Soil Record](#create-soil-record)
  - [List Soil Records](#list-soil-records)
  - [Get Soil Record by ID](#get-soil-record-by-id)
  - [Update Soil Record](#update-soil-record)
  - [Delete Soil Record](#delete-soil-record)
  - [Bulk Upload (CSV)](#bulk-upload-csv)
  - [Get Nutrient Classification](#get-nutrient-classification)
- [Dashboard](#dashboard)
  - [Overview](#overview)
  - [Nutrient Distribution](#nutrient-distribution)
  - [Deficiency Percentage](#deficiency-percentage)
  - [Average Nutrients](#average-nutrients)
  - [Trends](#trends)
- [GIS](#gis)
  - [Markers](#markers)
  - [Heat Map](#heat-map)
- [Reports](#reports)
  - [Generate Report (CSV)](#generate-report-csv)
- [Health](#health)
  - [Health Check](#health-check)
  - [Version](#version)
  - [Info](#info)
- [Data Model Reference](#data-model-reference)

## Conventions

- **`id`** fields are MongoDB ObjectId strings (24 hex characters), assigned on creation.
- **`testDate`** is an ISO-8601 date (`yyyy-MM-dd`), e.g. `2026-06-15`.
- **`createdAt`** / **`updatedAt`** are ISO-8601 local date-times without an offset, e.g. `2026-07-15T14:32:10.123`.
- All 12 nutrient fields (`ph`, `ec`, `organicCarbon`, `nitrogen`, `phosphorus`, `potassium`, `sulfur`, `zinc`, `boron`, `iron`, `copper`, `manganese`) are required `Double` values on write.
- Nutrient classification levels are one of `LOW`, `MEDIUM`, `HIGH`.

## Error Format

All errors (validation failures, not-found errors, bad input, and unhandled exceptions) are returned as a single consistent JSON shape:

```json
{
  "status": 404,
  "message": "Soil record not found with id: 64f1c2...",
  "timestamp": "2026-07-15T14:32:10.123",
  "errors": null
}
```

`errors` is populated only for request-body validation failures (HTTP 400), as a map of `field -> message`:

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-07-15T14:32:10.123",
  "errors": {
    "crop": "must not be blank",
    "location.latitude": "must be less than or equal to 90.0"
  }
}
```

| Status | Trigger |
|---|---|
| `400 Bad Request` | Request body fails `@Valid` validation |
| `400 Bad Request` | Unsupported file type on upload (not `.csv`) |
| `400 Bad Request` | Invalid `type` query param on `/dashboard/trends` or `/reports` |
| `404 Not Found` | Soil record does not exist for the given `id` |
| `500 Internal Server Error` | CSV file could not be processed, or any unhandled server error |

## Soil Records

Base path: `/api/v1/soil-records`

### Create Soil Record

`POST /api/v1/soil-records`

Creates a new soil test record. Body is validated; all fields shown below are required.

**Request body**

```json
{
  "sampleId": "KA-BLR-0001",
  "location": {
    "state": "Karnataka",
    "district": "Bengaluru Urban",
    "taluk": "Anekal",
    "village": "Jigani",
    "latitude": 12.7842,
    "longitude": 77.6408
  },
  "crop": "Ragi",
  "testDate": "2026-06-15",
  "nutrients": {
    "ph": 6.8,
    "ec": 0.5,
    "organicCarbon": 0.62,
    "nitrogen": 310,
    "phosphorus": 18,
    "potassium": 210,
    "sulfur": 12,
    "zinc": 0.9,
    "boron": 0.6,
    "iron": 6.2,
    "copper": 0.3,
    "manganese": 3.1
  }
}
```

**Validation rules**

- `sampleId`, `crop`: must not be blank
- `location`: required; nested object is itself validated
  - `state`, `district`, `taluk`, `village`: must not be blank
  - `latitude`: required, between `-90.0` and `90.0`
  - `longitude`: required, between `-180.0` and `180.0`
- `testDate`: required
- `nutrients`: required; all 12 fields are required `Double` values

**Response**: `201 Created`

```json
{
  "id": "64f1c2a9e4b0a1234567890a",
  "sampleId": "KA-BLR-0001",
  "location": { "...": "as submitted" },
  "crop": "Ragi",
  "testDate": "2026-06-15",
  "nutrients": { "...": "as submitted" },
  "createdAt": "2026-07-15T14:32:10.123",
  "updatedAt": "2026-07-15T14:32:10.123"
}
```

| Status | Meaning |
|---|---|
| `201 Created` | Record created successfully |
| `400 Bad Request` | Validation failed (see [Error Format](#error-format)) |

### List Soil Records

`GET /api/v1/soil-records`

Returns every soil record in the database. No pagination or filtering is currently applied; all records are returned.

**Response**: `200 OK`

```json
[
  {
    "id": "64f1c2a9e4b0a1234567890a",
    "sampleId": "KA-BLR-0001",
    "location": { "state": "Karnataka", "district": "Bengaluru Urban", "taluk": "Anekal", "village": "Jigani", "latitude": 12.7842, "longitude": 77.6408 },
    "crop": "Ragi",
    "testDate": "2026-06-15",
    "nutrients": { "ph": 6.8, "ec": 0.5, "organicCarbon": 0.62, "nitrogen": 310, "phosphorus": 18, "potassium": 210, "sulfur": 12, "zinc": 0.9, "boron": 0.6, "iron": 6.2, "copper": 0.3, "manganese": 3.1 },
    "createdAt": "2026-07-15T14:32:10.123",
    "updatedAt": "2026-07-15T14:32:10.123"
  }
]
```

### Get Soil Record by ID

`GET /api/v1/soil-records/{id}`

**Path parameters**

| Name | Type | Description |
|---|---|---|
| `id` | string | Soil record ID |

**Response**: `200 OK`, a single `SoilRecordResponse` object (same shape as above).

| Status | Meaning |
|---|---|
| `200 OK` | Record found |
| `404 Not Found` | No record exists with the given `id` |

### Update Soil Record

`PUT /api/v1/soil-records/{id}`

Fully replaces the record's fields (`sampleId`, `location`, `crop`, `testDate`, `nutrients`). `createdAt` is preserved from the original record; `updatedAt` is refreshed to the current time.

**Path parameters**

| Name | Type | Description |
|---|---|---|
| `id` | string | Soil record ID |

**Request body**: same shape as [Create Soil Record](#create-soil-record), subject to the same validation rules.

**Response**: `200 OK`, the updated `SoilRecordResponse`.

| Status | Meaning |
|---|---|
| `200 OK` | Record updated |
| `400 Bad Request` | Validation failed |
| `404 Not Found` | No record exists with the given `id` |

### Delete Soil Record

`DELETE /api/v1/soil-records/{id}`

**Path parameters**

| Name | Type | Description |
|---|---|---|
| `id` | string | Soil record ID |

**Response**: `204 No Content` (no body).

> Note: the delete operation does not currently check whether the record exists before deleting, so this returns `204` regardless of whether the `id` matched an existing record.

### Bulk Upload (CSV)

`POST /api/v1/soil-records/upload`

Uploads a CSV file and creates one soil record per row. Content type: `multipart/form-data`.

**Request**

| Field | Type | Description |
|---|---|---|
| `file` | file (`.csv`) | CSV file with a header row |

**Required CSV columns** (header names, case-sensitive):

```
sampleId,state,district,taluk,village,latitude,longitude,crop,testDate,ph,ec,organicCarbon,nitrogen,phosphorus,potassium,sulfur,zinc,boron,iron,copper,manganese
```

- `latitude`, `longitude`, and all nutrient columns are parsed as `double`; non-numeric values will cause the row (and the whole request) to fail.
- `testDate` must be an ISO-8601 date string (`yyyy-MM-dd`).
- Each row is inserted as a **new** record (there is no upsert/dedupe by `sampleId`); re-uploading the same file will create duplicates.
- The upload is **not transactional**: if a later row fails, earlier rows in the same file have already been saved.

**Example request (curl)**

```bash
curl -X POST http://localhost:8080/api/v1/soil-records/upload \
  -F "file=@soil-samples.csv"
```

**Response**: `200 OK`

```
"File uploaded and processed successfully."
```

| Status | Meaning |
|---|---|
| `200 OK` | File processed, all rows inserted |
| `400 Bad Request` | File is missing, empty, or not a `.csv` file |
| `500 Internal Server Error` | I/O error while reading the file, or a row failed to parse (e.g. missing column, bad number/date format) |

### Get Nutrient Classification

`GET /api/v1/soil-records/{id}/classification`

Classifies each of the 12 nutrients for the given soil record as `LOW`, `MEDIUM`, or `HIGH`, using the thresholds in `application.yml` (see [README: Configuration](README.md#nutrient-classification-thresholds)).

**Path parameters**

| Name | Type | Description |
|---|---|---|
| `id` | string | Soil record ID |

**Response**: `200 OK`

```json
{
  "ph": "MEDIUM",
  "ec": "LOW",
  "organicCarbon": "MEDIUM",
  "nitrogen": "MEDIUM",
  "phosphorus": "MEDIUM",
  "potassium": "MEDIUM",
  "sulfur": "MEDIUM",
  "zinc": "HIGH",
  "boron": "HIGH",
  "iron": "HIGH",
  "copper": "HIGH",
  "manganese": "HIGH"
}
```

| Status | Meaning |
|---|---|
| `200 OK` | Classification computed |
| `404 Not Found` | No record exists with the given `id` |

## Dashboard

Base path: `/api/v1/dashboard`. All endpoints aggregate over **every** soil record in the database; there is currently no filtering (by date range, district, crop, etc.) on dashboard endpoints.

### Overview

`GET /api/v1/dashboard/overview`

**Response**: `200 OK`

```json
{
  "totalSamples": 1240,
  "districtCoverage": 18,
  "cropCoverage": 9
}
```

| Field | Description |
|---|---|
| `totalSamples` | Total number of soil records |
| `districtCoverage` | Count of distinct `location.district` values |
| `cropCoverage` | Count of distinct `crop` values |

### Nutrient Distribution

`GET /api/v1/dashboard/nutrient-distribution`

Counts, per nutrient, how many records fall into each classification level.

**Response**: `200 OK`

```json
{
  "distribution": {
    "ph": { "LOW": 120, "MEDIUM": 900, "HIGH": 220 },
    "nitrogen": { "LOW": 430, "MEDIUM": 610, "HIGH": 200 }
  }
}
```

> Note: only nutrients present in at least one record's computed classification appear as keys; likewise, only the `NutrientLevel` values actually observed appear within each nutrient's map (there is no guarantee `LOW`/`MEDIUM`/`HIGH` are all present with a `0` default).

### Deficiency Percentage

`GET /api/v1/dashboard/deficiency-percentage`

Percentage of all records classified as `LOW` for each nutrient.

**Response**: `200 OK`

```json
{
  "deficiencyPercentage": {
    "ph": 9.68,
    "nitrogen": 34.68
  }
}
```

If there are zero records in the database, returns `{"deficiencyPercentage": {}}`.

### Average Nutrients

`GET /api/v1/dashboard/average-nutrients`

Mean value of each nutrient across all records.

**Response**: `200 OK`

```json
{
  "averageNutrients": {
    "ph": 6.9,
    "ec": 0.74,
    "organicCarbon": 0.61,
    "nitrogen": 298.4,
    "phosphorus": 19.2,
    "potassium": 205.7,
    "sulfur": 13.1,
    "zinc": 0.85,
    "boron": 0.58,
    "iron": 6.4,
    "copper": 0.31,
    "manganese": 3.0
  }
}
```

If there are zero records, returns `{"averageNutrients": {}}`.

### Trends

`GET /api/v1/dashboard/trends?type={type}`

Groups records by time period (based on `testDate`) and computes average nutrient values per period.

**Query parameters**

| Name | Type | Required | Values |
|---|---|---|---|
| `type` | string | yes | `MONTHLY`, `SEASONAL`, `YEARLY` (case-insensitive) |

Grouping keys:
- `MONTHLY` → `"2026-06"` (year-month)
- `YEARLY` → `"2026"`
- `SEASONAL` → `"<SEASON> <year>"`, where season is one of `WINTER` (Dec–Feb), `SUMMER` (Mar–May), `MONSOON` (Jun–Sep), `POST_MONSOON` (Oct–Nov)

**Response**: `200 OK`

```json
{
  "trends": {
    "2026-06": { "ph": 6.9, "nitrogen": 305.2 },
    "2026-05": { "ph": 6.7, "nitrogen": 288.1 }
  }
}
```

| Status | Meaning |
|---|---|
| `200 OK` | Trend data returned |
| `400 Bad Request` | `type` is missing or not one of `MONTHLY`/`SEASONAL`/`YEARLY` |

## GIS

Base path: `/api/v1/gis`. Both endpoints accept the same optional filter parameters (as query params), which are combined with `AND` when multiple are provided.

**Shared filter parameters**

| Name | Type | Required | Description |
|---|---|---|---|
| `state` | string | no | Exact match (case-insensitive collation) on `location.state` |
| `district` | string | no | Exact match on `location.district` |
| `taluk` | string | no | Exact match on `location.taluk` |
| `village` | string | no | Exact match on `location.village` |
| `crop` | string | no | Exact match on `crop` |

### Markers

`GET /api/v1/gis/markers`

Returns one marker per matching soil record, for plotting individual sample points on a map.

**Example**

```
GET /api/v1/gis/markers?district=Bengaluru%20Urban&crop=Ragi
```

**Response**: `200 OK`

```json
[
  { "sampleId": "KA-BLR-0001", "latitude": 12.7842, "longitude": 77.6408 }
]
```

### Heat Map

`GET /api/v1/gis/heatmap`

Groups matching records by exact `(latitude, longitude)` coordinate pair and returns an intensity value: the percentage of matching records located at that coordinate.

**Response**: `200 OK`

```json
[
  { "latitude": 12.7842, "longitude": 77.6408, "intensity": 4.35 }
]
```

> Note: if the filter matches zero records, this endpoint currently divides by a zero total when computing intensity; treat an all-filtered-out result as an edge case until this is addressed server-side.

## Reports

Base path: `/api/v1/reports`

### Generate Report (CSV)

`GET /api/v1/reports?type={type}`

Generates and streams a CSV report as a file download.

**Query parameters**

| Name | Type | Required | Values |
|---|---|---|---|
| `type` | string | yes | `NUTRIENT_STATUS`, `SOIL_FERTILITY`, `CROP_WISE`, `DISTRICT_SUMMARY` (case-insensitive) |

**Report types**

| Type | Contents |
|---|---|
| `NUTRIENT_STATUS` | Overview counts, average nutrients, full nutrient distribution, and deficiency %, across **all 12 nutrients** |
| `SOIL_FERTILITY` | Fertility summary (sample count, low-fertility sample count/%), plus averages/distribution/deficiency for the **5 fertility indicator nutrients** (`ph`, `organicCarbon`, `nitrogen`, `phosphorus`, `potassium`). A sample counts as "low fertility" if **3 or more** of these 5 indicators are classified `LOW`. |
| `CROP_WISE` | Per-crop breakdown: sample count, plus average and deficiency % for each fertility indicator nutrient |
| `DISTRICT_SUMMARY` | Same breakdown as `CROP_WISE`, grouped by district instead of crop |

**Response**: `200 OK`

- `Content-Type: text/csv`
- `Content-Disposition: attachment; filename="<type>-report.csv"` (e.g. `nutrient_status-report.csv`)
- `X-Report-Title`: human-readable report title
- `X-Report-Generated-At`: ISO-8601 timestamp of generation

Body is CSV text with one or more titled sections, each with its own header row, e.g.:

```
Overview,,
Metric,Value
Total Samples,1240
District Coverage,18
Crop Coverage,9

Average Nutrients,
Nutrient,Average
ph,6.90
ec,0.74
...
```

| Status | Meaning |
|---|---|
| `200 OK` | Report generated and returned |
| `400 Bad Request` | `type` is missing or invalid |

## Health

These endpoints are **not** grouped under a common `@RequestMapping` prefix beyond the global `/api/v1` servlet path.

### Health Check

`GET /api/v1/health`

**Response**: `200 OK`

```json
{
  "status": "UP",
  "service": "Soil Nutrient Analytics API",
  "timestamp": "2026-07-15T09:02:10.123Z"
}
```

### Version

`GET /api/v1/version`

**Response**: `200 OK`

```json
{
  "application": "Soil Nutrient Analytics API",
  "version": "1.0.0"
}
```

### Info

`GET /api/v1/info`

**Response**: `200 OK`

```json
{
  "application": "Soil Nutrient Analytics API",
  "version": "1.0.0",
  "status": "Running"
}
```

## Data Model Reference

### SoilRecord / SoilRecordResponse

| Field | Type | Notes |
|---|---|---|
| `id` | string | MongoDB ObjectId; absent on request bodies, present on responses |
| `sampleId` | string | Required, not blank |
| `location` | [Location](#location) | Required |
| `crop` | string | Required, not blank |
| `testDate` | date (`yyyy-MM-dd`) | Required |
| `nutrients` | [NutrientData](#nutrientdata) | Required |
| `createdAt` | date-time | Response only; set on creation |
| `updatedAt` | date-time | Response only; refreshed on update |

### Location

| Field | Type | Notes |
|---|---|---|
| `state` | string | Required, not blank |
| `district` | string | Required, not blank |
| `taluk` | string | Required, not blank |
| `village` | string | Required, not blank |
| `latitude` | double | Required, `-90.0` to `90.0` |
| `longitude` | double | Required, `-180.0` to `180.0` |

### NutrientData

All fields required, type `Double`, no explicit range validation at the API layer (range is only used for classification, not rejected on input):

`ph`, `ec`, `organicCarbon`, `nitrogen`, `phosphorus`, `potassium`, `sulfur`, `zinc`, `boron`, `iron`, `copper`, `manganese`

### NutrientClassificationResponse

Same 12 nutrient keys as `NutrientData`, each mapped to a `NutrientLevel`: `LOW` | `MEDIUM` | `HIGH`.

### ErrorResponse

| Field | Type | Notes |
|---|---|---|
| `status` | int | HTTP status code |
| `message` | string | Human-readable error message |
| `timestamp` | date-time | When the error occurred |
| `errors` | object \| null | Field-level validation errors (`400` validation failures only); `null` otherwise |
