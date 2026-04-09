
---

## Title

```md
# Smart Task Selector Backend
```

---

## 1. Overview

```md
A Spring Boot backend that selects and assigns tasks using a configurable scoring system and enforces a safe task lifecycle under concurrent access.

This project focuses on backend correctness, domain modeling, and concurrency handling rather than frontend features.
```

---

## 2. Core Problem

```md
Selecting the "best" task is not enough in a multi-user system.

If two users request the best task at the same time, both can receive the same result unless selection and assignment are handled atomically.

This project addresses that problem by combining scoring, state transitions, and optimistic locking to ensure tasks are only claimed once.
```

---

## 3. Key Features

```md
- Configurable task scoring system (priority × weight + bonus)
- Deterministic task selection (no randomness)
- Task lifecycle: OPEN → CLAIMED → COMPLETED
- Claim-by-id endpoint with validation of state transitions
- Claim-best endpoint that selects and assigns a task in one operation
- Optimistic locking using JPA @Version to prevent duplicate claims
- Retry mechanism for handling concurrent updates
- DTO-based API design (no entity exposure)
- Unit and concurrency-focused tests
```

---

## 4. Task Lifecycle

```md
OPEN → CLAIMED → COMPLETED

Rules:
- Only OPEN tasks can be claimed
- Only CLAIMED tasks can be completed
- Completed tasks are never considered for selection
```


---

## 5. Example: Claim Best Task

```md
POST /tasks/claim-best
```

Example request:

```json
{
  "category": "school",
  "minPriority": 1,
  "userId": "hudson",
  "priorityWeight": 5,
  "incompleteBonus": 30
}
```

Example response:

```json
{
  "task": {
    "id": 2,
    "title": "High Priority Task",
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

---

## 6. Concurrency Handling

```md
To prevent multiple users from claiming the same task:

- Each Task entity uses optimistic locking via @Version
- Concurrent updates trigger ObjectOptimisticLockingFailureException
- The service retries once before failing

This ensures that a task can only be claimed once, even under concurrent requests.
```


---

## 7. Tech Stack

```md
- Java
- Spring Boot
- Spring Data JPA (Hibernate)
- H2 (development)
- JUnit + Mockito
```

(Optional later: PostgreSQL + Flyway)

---

## 8. Testing

```md
The project includes:
- Unit tests for scoring logic and service behavior
- Controller tests using mocks
- Concurrency test simulating simultaneous claims to verify only one succeeds
```

---

## 9. Future Improvements

```md
- Replace H2 with PostgreSQL for production realism
- Add Flyway for schema migrations
- Introduce transaction boundaries for stronger consistency guarantees
- Add authentication for multi-user environments
```
