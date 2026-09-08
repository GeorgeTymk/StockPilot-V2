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

            System.out.println(
                    "Sync database initialized successfully."
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to initialize sync database: "
                            + e.getMessage(),
                    e
            );
        }
    }

    // =====================================================
    // OPERATION ID
    // =====================================================

    public static String createOperationId() {
        return UUID.randomUUID().toString();
    }

    // =====================================================
    // ADD OPERATION TO QUEUE
    // =====================================================

    public static String addToQueue(
            String entityType,
            String operation,
            Long localId,
            Long serverId,
            String payload
    ) {

        String operationId =
                createOperationId();

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

        try (
                Connection conn =
                        Database.connect();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setString(
                    1,
                    operationId
            );

            stmt.setString(
                    2,
                    entityType
            );

            stmt.setString(
                    3,
                    operation
            );

            if (localId == null) {

                stmt.setNull(
                        4,
                        java.sql.Types.INTEGER
                );

            } else {

                stmt.setLong(
                        4,
                        localId
                );
            }

            if (serverId == null) {

                stmt.setNull(
                        5,
                        java.sql.Types.INTEGER
                );

            } else {

                stmt.setLong(
                        5,
                        serverId
                );
            }

            stmt.setString(
                    6,
                    payload
            );

            stmt.executeUpdate();

            System.out.println(
                    "Sync queued: "
                            + operation
                            + " "
                            + entityType
                            + " #"
                            + localId
            );

            return operationId;

        }
        catch (Exception e) {

            throw new RuntimeException(
                    "Failed to add sync operation: "
                            + e.getMessage(),
                    e
            );
        }
    }

    // =====================================================
    // PENDING COUNT
    // =====================================================

    public static int getPendingCount() {

        String sql = """
            SELECT COUNT(*)
            FROM sync_queue
            WHERE status = 'PENDING'
        """;

        try (
                Connection conn =
                        Database.connect();

                PreparedStatement stmt =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        stmt.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        }
        catch (Exception e) {

            throw new RuntimeException(
                    "Failed to read pending sync count: "
                            + e.getMessage(),
                    e
            );
        }
    }

    // =====================================================
    // SAVE LOCAL -> SERVER ID MAPPING
    // =====================================================

    public static void saveIdMapping(
            String entityType,
            Long localId,
            Long serverId
    ) {

        String updateSql = """
            UPDATE sync_id_map
            SET
                server_id = ?,
                created_at = CURRENT_TIMESTAMP
            WHERE entity_type = ?
              AND local_id = ?
        """;

        String insertSql = """
            INSERT INTO sync_id_map
            (
                entity_type,
                local_id,
                server_id
            )
            VALUES (?, ?, ?)
        """;

        try (
                Connection conn =
                        Database.connect()
        ) {

            try (
                    PreparedStatement update =
                            conn.prepareStatement(updateSql)
            ) {

                update.setLong(
                        1,
                        serverId
                );

                update.setString(
                        2,
                        entityType
                );

                update.setLong(
                        3,
                        localId
                );

                int updated =
                        update.executeUpdate();

                if (updated > 0) {
                    return;
                }
            }

            try (
                    PreparedStatement insert =
                            conn.prepareStatement(insertSql)
            ) {

                insert.setString(
                        1,
                        entityType
                );

                insert.setLong(
                        2,
                        localId
                );

                insert.setLong(
                        3,
                        serverId
                );

                insert.executeUpdate();
            }

        }
        catch (Exception e) {

            throw new RuntimeException(
                    "Failed to save sync ID mapping: "
                            + e.getMessage(),
                    e
            );
        }
    }

    // =====================================================
    // GET SERVER ID
    // =====================================================

    public static Long getServerId(
            String entityType,
            Long localId
    ) {

        String sql = """
            SELECT server_id
            FROM sync_id_map
            WHERE entity_type = ?
              AND local_id = ?
        """;

        try (
                Connection conn =
                        Database.connect();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setString(
                    1,
                    entityType
            );

            stmt.setLong(
                    2,
                    localId
            );

            try (
                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getLong(
                            "server_id"
                    );
                }
            }

        }
        catch (Exception e) {

            throw new RuntimeException(
                    "Failed to read server ID mapping: "
                            + e.getMessage(),
                    e
            );
        }

        return null;
    }

    // =====================================================
    // GET LOCAL ID
    // =====================================================

    public static Long getLocalId(
            String entityType,
            Long serverId
    ) {

        String sql = """
            SELECT local_id
            FROM sync_id_map
            WHERE entity_type = ?
              AND server_id = ?
        """;

        try (
                Connection conn =
                        Database.connect();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setString(
                    1,
                    entityType
            );

            stmt.setLong(
                    2,
                    serverId
            );

            try (
                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getLong(
                            "local_id"
                    );
                }
            }

        }
        catch (Exception e) {

            throw new RuntimeException(
                    "Failed to read local ID mapping: "
                            + e.getMessage(),
                    e
            );
        }

        return null;
    }

    // =====================================================
    // SAVE SYNC METADATA
    // =====================================================

    public static void setMetadata(
            String key,
            String value
    ) {

        String updateSql = """
            UPDATE sync_metadata
            SET value = ?
            WHERE key = ?
        """;

        String insertSql = """
            INSERT INTO sync_metadata
            (key, value)
            VALUES (?, ?)
        """;

        try (
                Connection conn =
                        Database.connect()
        ) {

            try (
                    PreparedStatement update =
                            conn.prepareStatement(updateSql)
            ) {

                update.setString(
                        1,
                        value
                );

                update.setString(
                        2,
                        key
                );

                int rows =
                        update.executeUpdate();

                if (rows > 0) {
                    return;
                }
            }

            try (
                    PreparedStatement insert =
                            conn.prepareStatement(insertSql)
            ) {

                insert.setString(
                        1,
                        key
                );

                insert.setString(
                        2,
                        value
                );

                insert.executeUpdate();
            }

        }
        catch (Exception e) {

            throw new RuntimeException(
                    "Failed to save sync metadata: "
                            + e.getMessage(),
                    e
            );
        }
    }

    // =====================================================
    // GET SYNC METADATA
    // =====================================================

    public static String getMetadata(
            String key
    ) {

        String sql = """
            SELECT value
            FROM sync_metadata
            WHERE key = ?
        """;

        try (
                Connection conn =
                        Database.connect();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setString(
                    1,
                    key
            );

            try (
                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getString(
                            "value"
                    );
                }
            }

        }
        catch (Exception e) {

            throw new RuntimeException(
                    "Failed to read sync metadata: "
                            + e.getMessage(),
                    e
            );
        }

        return null;
    }
}
