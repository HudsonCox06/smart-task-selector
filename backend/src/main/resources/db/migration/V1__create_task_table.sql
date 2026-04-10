CREATE TABLE task(
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    priority INTEGER NOT NULL,
    claimed_by VARCHAR(255),
    claimed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    version BIGINT NOT NULL DEFAULT 0
)