# Smart Task Selector

A Spring Boot backend that selects and assigns tasks using a configurable scoring system, built around a single hard problem: **when two users request the "best" task at the same time, only one of them can get it.**

This project is a focused study in domain modeling, REST API design, and concurrency control. The frontend is intentionally minimal — the interesting work is on the server.

---

## The Problem

Picking the highest-scoring open task is easy. Picking it *and* assigning it atomically, under concurrent load, is not. A naive implementation has a race:

1. User A reads the list of open tasks, picks the best one.
2. User B reads the same list, picks the same one.
3. Both write back `CLAIMED`. One write silently overwrites the other.

Smart Task Selector solves this with optimistic locking via JPA's `@Version` annotation. Concurrent claims on the same task produce an `ObjectOptimisticLockingFailureException`; the losing request retries on a fresh candidate set.

---

## Features

- Configurable scoring: `score = priority × weight + incomplete_bonus`
- Deterministic selection — same inputs always produce the same output
- Task lifecycle enforcement: `OPEN → CLAIMED → COMPLETED`
- Two selection strategies:
  - `POST /tasks/select-task` — preview the best task without claiming it
  - `POST /tasks/claim-best` — atomically select and claim in one transaction
- Optimistic locking with automatic retry on conflict
- DTO layer — entities are never exposed over the wire
- Flyway-managed schema migrations
- Unit tests for scoring logic, service behavior, and concurrent claims

---

## Tech Stack

- Java 17
- Spring Boot 4.0.5 (Spring Framework 7, Jakarta EE 11)
- Spring Data JPA / Hibernate
- PostgreSQL 16
- Flyway for schema migrations
- JUnit 5 + Mockito

---

## Running Locally

You need Java 17+, Maven, and Docker.

**1. Start PostgreSQL:**

```bash
docker run --name taskselector-db \
  -e POSTGRES_DB=taskdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:16
```

**2. Run the backend:**

```bash
cd backend
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8081`. Flyway will automatically create the `task` table on first start.

**3. (Optional) Open the frontend:**

Open `frontend/index.html` directly in a browser. It's a minimal HTML/CSS/vanilla-JS client for manual testing.

---

## API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET`  | `/tasks` | List all tasks |
| `POST` | `/tasks` | Create a new task |
| `POST` | `/tasks/select-task` | Preview the best matching task (no state change) |
| `POST` | `/tasks/claim-best` | Atomically select and claim the best matching task |
| `PUT`  | `/tasks/{id}` | Update a task's fields |
| `PUT`  | `/tasks/{id}/claim` | Claim a specific task by ID |
| `PUT`  | `/tasks/{id}/complete` | Mark a claimed task as completed |

### Example flow

**Create a task:**

```bash
curl -X POST http://localhost:8081/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Write report","priority":5,"category":"school"}'
```

Response:

```json
{
  "id": 1,
  "title": "Write report",
  "priority": 5,
  "category": "school",
  "status": "OPEN"
}
```

**Claim the best task in a category:**

```bash
curl -X POST http://localhost:8081/tasks/claim-best \
  -H "Content-Type: application/json" \
  -d '{
    "category":"school",
    "minPriority":1,
    "userId":"hudson",
    "priorityWeight":5,
    "incompleteBonus":30
  }'
```

Response:

```json
{
  "task": {
    "id": 1,
    "title": "Write report",
    "priority": 5,
    "category": "school",
    "status": "CLAIMED"
  },
  "score": 55,
  "reason": "Priority 5 × weight 5, plus incomplete bonus 30",
  "priorityWeightUsed": 5,
  "incompleteBonusUsed": 30,
  "claimedBy": "hudson"
}
```

**Complete the task** (use the `id` from the response above):

```bash
curl -X PUT http://localhost:8081/tasks/1/complete
```

---

## How Concurrency Is Handled

Each `Task` entity carries a `@Version` column. Any update is checked against the version read at load time; if another transaction won the race, Hibernate throws `ObjectOptimisticLockingFailureException` instead of silently overwriting.

`claimBestTask` wraps the select-then-claim logic in a single `@Transactional` boundary and retries once on conflict. This is enough to make the vast majority of collisions recover transparently while keeping the code honest about the race.

**Known limitation:** the retry is a fixed single attempt with no backoff. Under heavy sustained contention this is not ideal — exponential backoff with jitter would be the next step.

---

## Project Structure

```
backend/
  src/main/java/com/hudson/taskselector/
    controller/        REST endpoints
    service/           Business logic, scoring, claim orchestration
    repository/        Spring Data JPA interfaces
    model/             JPA entities + enums
    dto/               Request/response types
    mapper/            Entity ↔ DTO conversion
    exception/         Custom exceptions + global handler
  src/main/resources/
    db/migration/      Flyway SQL migrations
    application.properties
  src/test/java/       Unit + concurrency tests
frontend/              Minimal vanilla-JS client
docs/                  Design notes
```

---

## Roadmap

- [ ] Authentication (Spring Security, session-based)
- [ ] Per-user task ownership and authorization rules
- [ ] Containerize with a multi-stage Dockerfile
- [ ] Deploy to a public URL (Render or Fly.io)
- [ ] Exponential backoff on claim retry

---

## License

[MIT](./LICENSE)
