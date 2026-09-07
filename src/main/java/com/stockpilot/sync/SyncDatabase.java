package com.stockpilot.sync;

import com.stockpilot.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class SyncDatabase {

    public static void initialize() {

        try (Connection conn = Database.connect()) {

            try (PreparedStatement stmt = conn.prepareStatement("""
                CREATE TABLE IF NOT EXISTS sync_queue (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    operation_id TEXT NOT NULL UNIQUE,
                    entity_type TEXT NOT NULL,
                    operation TEXT NOT NULL,
                    local_id INTEGER,
                    server_id INTEGER,
                    payload TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING',
                    retry_count INTEGER NOT NULL DEFAULT 0,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    last_attempt TEXT,
                    error_message TEXT
                )
            """)) {
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("""
                CREATE INDEX IF NOT EXISTS idx_sync_queue_status
                ON sync_queue(status)
            """)) {
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("""
                CREATE INDEX IF NOT EXISTS idx_sync_queue_created
                ON sync_queue(created_at)
            """)) {
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("""
                CREATE TABLE IF NOT EXISTS sync_id_map (
                    entity_type TEXT NOT NULL,
                    local_id INTEGER NOT NULL,
                    server_id INTEGER NOT NULL,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (entity_type, local_id),
                    UNIQUE (entity_type, server_id)
                )
            """)) {
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("""
                CREATE TABLE IF NOT EXISTS sync_metadata (
                    key TEXT PRIMARY KEY,
                    value TEXT
                )
            """)) {
                stmt.executeUpdate();
            }

            System.out.println("Sync database initialized successfully.");

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to initialize sync database: " + e.getMessage(),
                    e
            );
        }
    }

    public static String createOperationId() {
        return UUID.randomUUID().toString();
    }

    public static void addToQueue(
            String entityType,
            String operation,
            Integer localId,
            Integer serverId,
            String payload
    ) {

        String operationId = createOperationId();

        String sql = """
            INSERT INTO sync_queue
            (
                operation_id,
                entity_type,
                operation,
                local_id,
                server_id,
                payload
            )
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, operationId);
            stmt.setString(2, entityType);
            stmt.setString(3, operation);

            if (localId == null) {
                stmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(4, localId);
            }

            if (serverId == null) {
                stmt.setNull(5, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(5, serverId);
            }

            stmt.setString(6, payload);

            stmt.executeUpdate();

            System.out.println(
                    "Sync queued: " +
                    operation +
                    " " +
                    entityType +
                    " #" +
                    localId
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to add sync operation: " + e.getMessage(),
                    e
            );
        }
    }

    public static int getPendingCount() {

        String sql = """
            SELECT COUNT(*)
            FROM sync_queue
            WHERE status = 'PENDING'
        """;

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to read pending sync count: " + e.getMessage(),
                    e
            );
        }
    }
}
