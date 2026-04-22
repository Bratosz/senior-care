# AI Context – System Architecture

## Overview
This project is a fullstack application with a clear separation between frontend and backend, using a contract-driven approach.

---

## Backend

**Stack:**
- Kotlin
- Ktor
- Arrow
- Exposed

**API Approach:**
- Code-first OpenAPI
- OpenAPI specification is generated from backend code
- Backend is the single source of truth for API contracts

**Principles:**
- Strong typing via Kotlin
- Functional patterns where beneficial (Arrow)
- Explicit error handling (no hidden exceptions)
- Database access via Exposed (no leaking SQL outside repository layer)

---

## Frontend

**Stack:**
- TypeScript
- TanStack Query
- TanStack Router (file-based routing)

**Architecture:**
- Thin route pattern
- Feature-based folder structure

**Principles:**
- Routes are responsible only for wiring (no business logic)
- All domain logic lives inside feature modules
- Data fetching is handled via TanStack Query
- API layer is generated from OpenAPI (no manual typing)

---

## Contract Between Frontend and Backend

- Backend generates OpenAPI specification
- Frontend generates TypeScript types and API client from OpenAPI
- No manual duplication of DTOs on frontend
- Breaking changes must be reflected in OpenAPI

---

## Folder Structure Strategy (Frontend)

- `routes/` → routing layer (thin, declarative)
- `features/` → domain modules (business logic, components, queries)
- `shared/` → reusable UI and utilities

---

## Key Architectural Rules

1. **Colocation by feature**
    - Related logic must live together

2. **Separation of concerns**
    - Routing ≠ business logic
    - UI ≠ data fetching ≠ domain logic

3. **Single source of truth**
    - Backend defines API
    - Frontend consumes generated artifacts

4. **Scalability over simplicity**
    - Structure should support growth, not just MVP

5. **Minimal coupling**
    - Features should be as independent as possible

---

## Suggested Improvements (Important)

- Introduce **API client generation step in CI**
- Add **runtime validation (e.g. Zod) for critical endpoints**
- Define **error model contract (shared understanding of failures)**
- Consider **feature-level testing strategy**
- Add **naming conventions section** (very important for scaling)

---