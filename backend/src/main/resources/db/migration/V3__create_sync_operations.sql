CREATE TABLE IF NOT EXISTS sync_operations (
    id BIGSERIAL PRIMARY KEY,
    operation_id VARCHAR(100) NOT NULL UNIQUE,
    entity_type VARCHAR(50) NOT NULL,
    operation VARCHAR(30) NOT NULL,
    local_id BIGINT,
    server_id BIGINT,
    payload TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PROCESSED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    error_message TEXT
);

CREATE INDEX IF NOT EXISTS idx_sync_operations_operation_id
    ON sync_operations(operation_id);

CREATE INDEX IF NOT EXISTS idx_sync_operations_entity
    ON sync_operations(entity_type);

CREATE INDEX IF NOT EXISTS idx_sync_operations_created
    ON sync_operations(created_at);
