# Wyz Travel Agent

AI-assisted travel booking dashboard for travel agents. Create itineraries from plain English, manage multi-step workflows, and get automatic travel-advisory compliance checks.

**Stack:** MongoDB · Spring Boot 3 · React 19 · Vite · Tailwind CSS · Google Gemini

---

## Quick start (Docker Compose)

**Prerequisites:** [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/)

```bash
# 1. Clone and enter the project
cd travelapp

# 2. (Optional) Copy env file and add your Gemini API key
cp .env.example .env
# Edit .env — set AI_API_KEY for AI features (itinerary parsing, advisory checks)

# 3. Start all services
docker compose up --build
```

| Service   | URL                        |
|-----------|----------------------------|
| Frontend  | http://localhost:5173      |
| Backend   | http://localhost:8080      |
| MongoDB   | localhost:27017            |

Stop the stack:

```bash
docker compose down
```

Remove persisted database data:

```bash
docker compose down -v
```

---

## Environment variables

Set these in a root `.env` file (loaded automatically by Docker Compose) or export them in your shell.

| Variable        | Required | Description                                      |
|-----------------|----------|--------------------------------------------------|
| `AI_API_KEY`    | No       | Google Gemini API key for AI features            |
| `AI_API_URL`    | No       | Gemini model endpoint (has a sensible default)   |
| `MAIL_USERNAME` | No       | Gmail address for customer notification emails   |
| `MAIL_PASSWORD` | No       | Gmail app password                               |

Without `AI_API_KEY`, the app still runs — itinerary parsing falls back to simple heuristics and advisory LLM checks are skipped.

Without mail credentials, notification emails are printed to the backend container logs.

---

## Local development (without Docker)

### Prerequisites

- Java 17+
- Node.js 20+ (see `frontend/.nvmrc`)
- MongoDB running locally on port 27017
- Maven 3.9+

### Backend

```bash
cd backend
export MONGODB_URI=mongodb://localhost:27017/travelapp
export AI_API_KEY=your_key_here   # optional
mvn spring-boot:run
```

API available at http://localhost:8080

### Frontend

```bash
cd frontend
npm install
npm run dev
```

App available at http://localhost:5173 — the Vite dev server proxies `/api` requests to the backend.

---

## API overview

| Method | Endpoint                    | Description                        |
|--------|-----------------------------|------------------------------------|
| GET    | `/api/workflows`            | List all bookings                  |
| GET    | `/api/workflows/{id}`       | Get booking (runs advisory check)  |
| POST   | `/api/workflows`            | Create booking                     |
| POST   | `/api/workflows/parse`    | Create booking from plain text     |
| PUT    | `/api/workflows/{id}`       | Update booking                     |
| GET    | `/api/advisories`           | List travel advisories             |
| POST   | `/api/advisories`           | Create advisory (triggers impact)  |
| DELETE | `/api/advisories/{id}`      | Delete advisory                    |

---

## Project structure

```
travelapp/
├── docker-compose.yml      # MongoDB + backend + frontend
├── .env.example            # Environment variable template
├── backend/                # Spring Boot API
│   ├── Dockerfile
│   └── src/main/java/com/travelapp/
└── frontend/               # React dashboard
    ├── Dockerfile
    └── nginx.conf          # Proxies /api → backend in Docker
```

---

## Tests

```bash
cd backend
mvn test
```
