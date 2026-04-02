# API Design

## Tasks

### POST /tasks
Create a new task

Request body:
- title (string)
- priority (int)
- category (string)
- dueDate (optional date)

---

### GET /tasks
Return all tasks

---

### POST /select-task
Return one task based on filters

Request body (all optional):
- category (string)
- minPriority (int)
- random (boolean)
- includeCompleted (boolean, default false)

Response:
- single Task object
