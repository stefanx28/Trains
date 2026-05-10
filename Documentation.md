# 🚆 Train Ticketing System

A Spring Boot REST API for managing train schedules, routes, bookings, and passenger notifications. Built with Java 17, Spring Boot 3, and PostgreSQL.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Data Model](#data-model)
- [API Reference](#api-reference)
    - [Stations](#stations)
    - [Routes](#routes)
    - [Trains](#trains)
    - [Schedules](#schedules)
    - [Journey Search](#journey-search)
    - [Bookings](#bookings)
- [Email Notifications](#email-notifications)
- [Examples](#examples)
    - [a) Booking Tickets](#a-booking-tickets)
    - [b) Finding Journeys](#b-finding-journeys)
    - [c) Admin Operations](#c-admin-operations)
- [Error Handling](#error-handling)
- [Configuration](#configuration)

---

## Overview

The Train Ticketing System allows passengers to search for journeys between any two stations, book one or multiple seats, and receive email confirmations. Administrators can manage routes, trains, and schedules, view all bookings per train, and record delays — automatically notifying affected passengers by email.

---

## Tech Stack

| Layer        | Technology                        |
|--------------|-----------------------------------|
| Language     | Java 17                           |
| Framework    | Spring Boot 3.x                   |
| Database     | PostgreSQL                        |
| ORM          | Spring Data JPA / Hibernate       |
| Email        | Spring Mail (SMTP)                |
| Build Tool   | Maven                             |
| API Style    | REST (JSON)                       |
| Frontend     | Angular 17 (standalone components)|

---

## Architecture

```
┌─────────────────────────────────────────┐
│              Angular Frontend           │
│  Search │ Book │ My Bookings │ Admin    │
└───────────────────┬─────────────────────┘
                    │ HTTP / REST
┌───────────────────▼─────────────────────┐
│           Spring Boot REST API          │
│                                         │
│  Controllers → Services → Repositories  │
│                                         │
│  ┌─────────────┐   ┌─────────────────┐  │
│  │ Mail Service│   │ Journey Planner │  │
│  │  (SMTP)     │   │ (BFS/Graph)     │  │
│  └─────────────┘   └─────────────────┘  │
└───────────────────┬─────────────────────┘
                    │
┌───────────────────▼─────────────────────┐
│              PostgreSQL                 │
└─────────────────────────────────────────┘
```

**Journey search** uses a graph traversal (BFS) over station nodes to find both direct connections and changeover routes between any two stations.

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- SMTP credentials (e.g. Gmail App Password)

### 1. Clone and configure

```bash
git clone https://github.com/your-org/train-ticketing.git
cd train-ticketing
```

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/traindb
spring.datasource.username=postgres
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 2. Build and run

```bash
mvn clean install
mvn spring-boot:run
```

The API is available at `http://localhost:8080/api`.

### 3. Seed data (optional)

On first run with `ddl-auto=update`, the schema is created automatically. You can seed initial stations, routes, and trains via the admin endpoints described below.

---

## Data Model

```
Station
  id, name, code

Route
  id, routeNumber
  stations: [Station]   ← ordered list

Train
  id, trainNumber, name, totalSeats

Schedule
  id
  train       → Train
  route       → Route
  departureTime, arrivalTime
  delayMinutes
  availableSeats

Booking
  id
  schedule    → Schedule
  person      → Person (name, email)
  tickets     → [Ticket]
  status      (CONFIRMED | CANCELLED)
  totalPrice
  bookedAt

Ticket
  id
  booking     → Booking
  seatNumber
  price

Person
  id, name, email
```

---

## API Reference

All endpoints are prefixed with `/api`. Admin endpoints require the `X-Admin-Key` header (configurable). Passenger endpoints that are user-scoped accept `X-Person-Id`.

---

### Stations

#### Get all stations
```
GET /api/stations
```
**Response `200`**
```json
[
  { "id": "uuid-1", "name": "Cluj-Napoca", "code": "CJ" },
  { "id": "uuid-2", "name": "București Nord", "code": "BN" },
  { "id": "uuid-3", "name": "Brașov", "code": "BV" }
]
```

#### Create station *(Admin)*
```
POST /api/stations
```
**Request**
```json
{ "name": "Sinaia", "code": "SN" }
```
**Response `201`**
```json
{ "id": "uuid-4", "name": "Sinaia", "code": "SN" }
```

#### Update station *(Admin)*
```
PUT /api/stations/{id}
```
**Request**
```json
{ "name": "Sinaia", "code": "SNA" }
```

#### Delete station *(Admin)*
```
DELETE /api/stations/{id}
```
**Response `204 No Content`**

---

### Routes

A route is an ordered sequence of stations that a train travels through.

#### Get all routes
```
GET /api/routes
```
**Response `200`**
```json
[
  {
    "id": "uuid-r1",
    "routeNumber": "R100",
    "stations": [
      { "id": "uuid-1", "name": "Cluj-Napoca", "code": "CJ" },
      { "id": "uuid-3", "name": "Brașov",      "code": "BV" },
      { "id": "uuid-2", "name": "București Nord", "code": "BN" }
    ]
  }
]
```

#### Create route *(Admin)*
```
POST /api/routes
```
**Request**
```json
{
  "routeNumber": "R200",
  "stationIds": ["uuid-1", "uuid-3", "uuid-2"]
}
```
**Response `201`** — returns full route object with resolved stations.

#### Update route *(Admin)*
```
PUT /api/routes/{id}
```
**Request**
```json
{
  "routeNumber": "R200",
  "stationIds": ["uuid-1", "uuid-4", "uuid-3", "uuid-2"]
}
```

#### Delete route *(Admin)*
```
DELETE /api/routes/{id}
```

---

### Trains

#### Get all trains
```
GET /api/trains
```
**Response `200`**
```json
[
  {
    "id": "uuid-t1",
    "trainNumber": "IC 521",
    "name": "Transilvania Express",
    "totalSeats": 120
  }
]
```

#### Create train *(Admin)*
```
POST /api/trains
```
**Request**
```json
{
  "trainNumber": "IR 1832",
  "name": "Muntenia",
  "totalSeats": 200
}
```

#### Update train *(Admin)*
```
PUT /api/trains/{id}
```

#### Delete train *(Admin)*
```
DELETE /api/trains/{id}
```

---

### Schedules

#### Get all schedules
```
GET /api/schedules
```
**Response `200`**
```json
[
  {
    "id": "uuid-s1",
    "train": { "id": "uuid-t1", "trainNumber": "IC 521", "name": "Transilvania Express"
    },
    "route": { "id": "uuid-r1", "routeNumber": "R100", "stations": [ "..." ] },
    "departureTime": "2025-06-01T08:00:00",
    "arrivalTime":   "2025-06-01T12:30:00",
    "delayMinutes": 0,
    "availableSeats": 98
  }
]
```

#### Create schedule *(Admin)*
```
POST /api/schedules
```
**Request**
```json
{
  "trainId":       "uuid-t1",
  "routeId":       "uuid-r1",
  "departureTime": "2025-06-01T08:00:00",
  "arrivalTime":   "2025-06-01T12:30:00"
}
```

#### Update schedule *(Admin)*
```
PUT /api/schedules/{id}
```

#### Delete schedule *(Admin)*
```
DELETE /api/schedules/{id}
```

#### Set delay *(Admin)*
```
PATCH /api/schedules/{id}/delay
```
**Request**
```json
{ "delayMinutes": 25 }
```
**Response `200`**
```json
{
  "id": "uuid-s1",
  "delayMinutes": 25,
  "message": "Delay updated. 47 passengers notified by email."
}
```
> All passengers with confirmed bookings on this schedule immediately receive a delay notification email.

#### Get bookings for a schedule *(Admin)*
```
GET /api/schedules/{id}/bookings
```
**Response `200`**
```json
[
  {
    "id": "uuid-b1",
    "person": { "name": "Ion Popescu", "email": "ion@example.com" },
    "tickets": [
      { "seatNumber": "12A", "price": 85.00 },
      { "seatNumber": "12B", "price": 85.00 }
    ],
    "status": "CONFIRMED",
    "totalPrice": 170.00,
    "bookedAt": "2025-05-20T14:32:00"
  }
]
```

---

### Journey Search

The search endpoint accepts an origin station, destination station, and a minimum departure time. It returns both direct trains and multi-leg changeover options.

```
POST /api/journeys/search
```
**Request**
```json
{
  "fromStationId": "uuid-1",
  "toStationId":   "uuid-2",
  "after":         "2025-06-01T07:00:00"
}
```
**Response `200`**
```json
{
  "directResponses": [
    {
      "id": "uuid-s1",
      "train": { "trainNumber": "IC 521", "name": "Transilvania Express" },
      "route": { "routeNumber": "R100", "stations": [ "..." ] },
      "departureTime": "2025-06-01T08:00:00",
      "arrivalTime":   "2025-06-01T12:30:00",
      "delayMinutes": 0,
      "availableSeats": 98
    }
  ],
  "changeoverResponses": [
    [
      {
        "id": "uuid-s3",
        "train": { "trainNumber": "R 2041", "name": "Someșul" },
        "departureTime": "2025-06-01T07:15:00",
        "arrivalTime":   "2025-06-01T09:45:00",
        "availableSeats": 54
      },
      {
        "id": "uuid-s4",
        "train": { "trainNumber": "IR 1832", "name": "Muntenia" },
        "departureTime": "2025-06-01T10:10:00",
        "arrivalTime":   "2025-06-01T13:00:00",
        "availableSeats": 120
      }
    ]
  ]
}
```

**No connection found — Response `404`**
```json
{
  "message": "No connections found between the selected stations for the given date and time."
}
```

---

### Bookings

#### Create a booking
```
POST /api/bookings
```
**Request**
```json
{
  "scheduleId": "uuid-s1",
  "person": {
    "name":  "Maria Ionescu",
    "email": "maria@example.com"
  },
  "numberOfTickets": 2
}
```
**Response `201`**
```json
{
  "id": "uuid-b2",
  "schedule": { "id": "uuid-s1", "train": { "trainNumber": "IC 521" }},
  "person": { "name": "Maria Ionescu", "email": "maria@example.com" },
  "tickets": [
    { "id": "uuid-tk1", "seatNumber": "14A", "price": 85.00 },
    { "id": "uuid-tk2", "seatNumber": "14B", "price": 85.00 }
  ],
  "status": "CONFIRMED",
  "totalPrice": 170.00,
  "bookedAt": "2025-05-20T15:00:00"
}
```
> A confirmation email is sent automatically to `maria@example.com`.

**Overbooking attempt — Response `409`**
```json
{
  "message": "Not enough seats available. Requested: 2, Available: 1."
}
```

#### Get my bookings
```
GET /api/bookings/my
Headers: X-Person-Id: uuid-person-1
```
**Response `200`** — array of booking objects as above.

#### Cancel a booking
```
PATCH /api/bookings/{id}/cancel
Headers: X-Person-Id: uuid-person-1
```
**Response `200`**
```json
{ "id": "uuid-b2", "status": "CANCELLED" }
```

---

## Email Notifications

The system sends two types of automated emails via SMTP:

### Booking Confirmation

Sent immediately after a successful booking.

```
Subject: Booking Confirmed — IC 521 on 01 Jun 2025

Dear Maria Ionescu,

Your booking is confirmed! Here are your details:

  Train:      IC 521 — Transilvania Express
  Route:      Cluj-Napoca → Brașov → București Nord
  Departure:  01 Jun 2025 at 08:00
  Arrival:    01 Jun 2025 at 12:30
  Seats:      14A, 14B
  Total paid: 170.00 RON

Thank you for travelling with us!
```

### Delay Notification

Sent to all confirmed passengers when an admin records a delay.

```
Subject: ⚠ Delay Notice — IC 521 on 01 Jun 2025

Dear Ion Popescu,

We regret to inform you that train IC 521 (Transilvania Express)
departing on 01 Jun 2025 at 08:00 is delayed by 25 minutes.

New expected departure: 08:25
New expected arrival:   12:55

We apologise for any inconvenience caused.
```

---

## Examples

### a) Booking Tickets

**Scenario:** A passenger wants to book 2 tickets on train IC 521 from Cluj-Napoca to București Nord on 1 June 2025.

**Step 1 — Search for available journeys**
```bash
curl -X POST http://localhost:8080/api/journeys/search \
  -H "Content-Type: application/json" \
  -d '{
    "fromStationId": "uuid-1",
    "toStationId": "uuid-2",
    "after": "2025-06-01T07:00:00"
  }'
```

**Step 2 — Book the chosen schedule**
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "scheduleId": "uuid-s1",
    "person": {
      "name": "Maria Ionescu",
      "email": "maria@example.com"
    },
    "numberOfTickets": 2
  }'
```

**Output**
```json
{
  "id": "uuid-b2",
  "status": "CONFIRMED",
  "tickets": [
    { "seatNumber": "14A", "price": 85.00 },
    { "seatNumber": "14B", "price": 85.00 }
  ],
  "totalPrice": 170.00
}
```
> ✉ Confirmation email sent to `maria@example.com`.

---

**Overbooking scenario:** Only 1 seat remains, passenger requests 2.

```json
{
  "message": "Not enough seats available. Requested: 2, Available: 1."
}
```

---

### b) Finding Journeys

**Scenario 1 — Direct connection exists**

```bash
curl -X POST http://localhost:8080/api/journeys/search \
  -H "Content-Type: application/json" \
  -d '{
    "fromStationId": "uuid-1",
    "toStationId": "uuid-2",
    "after": "2025-06-01T07:00:00"
  }'
```

**Output**
```json
{
  "directResponses": [
    {
      "train": { "trainNumber": "IC 521" },
      "departureTime": "2025-06-01T08:00:00",
      "arrivalTime":   "2025-06-01T12:30:00",
      "availableSeats": 98
    }
  ],
  "changeoverResponses": []
}
```

---

**Scenario 2 — No direct train, changeover required**

```bash
curl -X POST http://localhost:8080/api/journeys/search \
  -H "Content-Type: application/json" \
  -d '{
    "fromStationId": "uuid-5",
    "toStationId": "uuid-6",
    "after": "2025-06-01T09:00:00"
  }'
```

**Output**
```json
{
  "directResponses": [],
  "changeoverResponses": [
    [
      {
        "train": { "trainNumber": "R 2041" },
        "departureTime": "2025-06-01T09:30:00",
        "arrivalTime":   "2025-06-01T11:00:00"
      },
      {
        "train": { "trainNumber": "IR 1832" },
        "departureTime": "2025-06-01T11:30:00",
        "arrivalTime":   "2025-06-01T14:00:00"
      }
    ]
  ]
}
```

---

**Scenario 3 — No connection at all**

**Output `404`**
```json
{
  "message": "No connections found between the selected stations for the given date and time."
}
```

---

### c) Admin Operations

#### Add a new route

```bash
curl -X POST http://localhost:8080/api/routes \
  -H "Content-Type: application/json" \
  -d '{
    "routeNumber": "R300",
    "stationIds": ["uuid-1", "uuid-4", "uuid-6"]
  }'
```

**Output `201`**
```json
{
  "id": "uuid-r3",
  "routeNumber": "R300",
  "stations": [
    { "name": "Cluj-Napoca", "code": "CJ" },
    { "name": "Sinaia",      "code": "SN" },
    { "name": "Ploiești",    "code": "PL" }
  ]
}
```

---

#### Modify a train

```bash
curl -X PUT http://localhost:8080/api/trains/uuid-t1 \
  -H "Content-Type: application/json" \
  -d '{
    "trainNumber": "IC 521",
    "name": "Transilvania Express Plus",
    "totalSeats": 150
  }'
```

---

#### Remove a schedule

```bash
curl -X DELETE http://localhost:8080/api/schedules/uuid-s1
```
**Output `204 No Content`**

---

#### View bookings for a train schedule

```bash
curl http://localhost:8080/api/schedules/uuid-s1/bookings
```

**Output**
```json
[
  {
    "person": { "name": "Ion Popescu", "email": "ion@example.com" },
    "tickets": [{ "seatNumber": "12A" }, { "seatNumber": "12B" }],
    "status": "CONFIRMED",
    "totalPrice": 170.00,
    "bookedAt": "2025-05-20T14:32:00"
  },
  {
    "person": { "name": "Maria Ionescu", "email": "maria@example.com" },
    "tickets": [{ "seatNumber": "14A" }, { "seatNumber": "14B" }],
    "status": "CONFIRMED",
    "totalPrice": 170.00,
    "bookedAt": "2025-05-20T15:00:00"
  }
]
```

---

#### Record a delay and notify passengers

```bash
curl -X PATCH http://localhost:8080/api/schedules/uuid-s1/delay \
  -H "Content-Type: application/json" \
  -d '{ "delayMinutes": 25 }'
```

**Output**
```json
{
  "id": "uuid-s1",
  "delayMinutes": 25,
  "message": "Delay updated. 2 passengers notified by email."
}
```
> ✉ Delay emails sent automatically to `ion@example.com` and `maria@example.com`.

---

#### Remove a station

```bash
curl -X DELETE http://localhost:8080/api/stations/uuid-4
```
> ⚠ Returns `409 Conflict` if the station is currently part of an active route.

---

## Error Handling

All errors follow a consistent response shape:

```json
{ "message": "Human-readable description of the error." }
```

| Status | Meaning                                           |
|--------|---------------------------------------------------|
| `400`  | Validation failed (missing fields, bad format)    |
| `404`  | Resource not found / no journey connection        |
| `409`  | Conflict (overbooking, station in use)            |
| `500`  | Unexpected server error                           |

---

## Configuration

| Property                        | Description                          | Default              |
|---------------------------------|--------------------------------------|----------------------|
| `spring.datasource.url`         | PostgreSQL JDBC URL                  | —                    |
| `spring.datasource.username`    | DB username                          | —                    |
| `spring.datasource.password`    | DB password                          | —                    |
| `spring.mail.host`              | SMTP host                            | `smtp.gmail.com`     |
| `spring.mail.port`              | SMTP port                            | `587`                |
| `spring.mail.username`          | SMTP sender address                  | —                    |
| `spring.mail.password`          | SMTP password / app password         | —                    |
| `app.ticket.base-price`         | Base price per ticket (RON)          | `85.00`              |
| `app.admin.key`                 | Secret key for admin endpoints       | —                    |