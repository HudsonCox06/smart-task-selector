# API Design

## Tasks

POST /tasks
- Create a new task
- Fields: name, priority, category

GET /tasks
- Get all tasks

POST /select-task
- Returns a task based on filters

Optional filters:
- category
- priority
- random selection
- due date (related to priority possibly)
