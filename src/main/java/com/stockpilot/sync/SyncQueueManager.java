package com.stockpilot.sync;

import com.stockpilot.database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SyncQueueManager {

    public static final String PENDING = "PENDING";
    public static final String PROCESSING = "PROCESSING";
    public static final String SYNCED = "SYNCED";
    public static final String FAILED = "FAILED";

    public static class SyncOperation {

        private long id;
        private String operationId;
        private String entityType;
        private String operation;
        private Long localId;
        private Long serverId;
        private String payload;
        private String status;
        private int retryCount;
        private String createdAt;
        private String lastAttempt;
        private String errorMessage;

        public long getId() {
            return id;
        }

        public String getOperationId() {
            return operationId;
        }

        public String getEntityType() {
            return entityType;
        }

        public String getOperation() {
            return operation;
        }

        public Long getLocalId() {
            return localId;
        }

        public Long getServerId() {
            return serverId;
        }

        public String getPayload() {
            return payload;
        }

        public String getStatus() {
            return status;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getLastAttempt() {
            return lastAttempt;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        private static SyncOperation fromResultSet(ResultSet rs) throws SQLException {
            SyncOperation op = new SyncOperation();

            op.id = rs.getLong("id");
            op.operationId = rs.getString("operation_id");
            op.entityType = rs.getString("entity_type");
            op.operation = rs.getString("operation");

            long localId = rs.getLong("local_id");
            op.localId = rs.wasNull() ? null : localId;

            long serverId = rs.getLong("server_id");
            op.serverId = rs.wasNull() ? null : serverId;

            op.payload = rs.getString("payload");
            op.status = rs.getString("status");
            op.retryCount = rs.getInt("retry_count");
            op.createdAt = rs.getString("created_at");
            op.lastAttempt = rs.getString("last_attempt");
            op.errorMessage = rs.getString("error_message");

            return op;
        }
    }

    public static List<SyncOperation> getPendingOperations(int limit) {
        List<SyncOperation> operations = new ArrayList<>();

        String sql = """
                SELECT *
                FROM sync_queue
                WHERE status = ?
                ORDER BY id ASC
                LIMIT ?
                """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, PENDING);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    operations.add(SyncOperation.fromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to load pending sync operations: " + e.getMessage());
        }

        return operations;
    }

    public static SyncOperation getOperation(long id) {
        String sql = "SELECT * FROM sync_queue WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return SyncOperation.fromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to load sync operation: " + e.getMessage());
        }

        return null;
    }

    /**
     * If an offline CREATE already exists for this local entity,
     * update its payload instead of creating a separate UPDATE operation.
     */
    public static boolean updatePendingCreate(
            String entityType,
            long localId,
            String payload
    ) {
        String sql = """
                UPDATE sync_queue
                SET payload = ?,
                    last_attempt = NULL,
                    error_message = NULL,
                    status = ?
                WHERE entity_type = ?
                  AND local_id = ?
                  AND operation = 'CREATE'
                  AND status IN (?, ?)
                """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, payload);
            ps.setString(2, PENDING);
            ps.setString(3, entityType);
            ps.setLong(4, localId);
            ps.setString(5, PENDING);
            ps.setString(6, FAILED);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Failed to update pending CREATE: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cancels an offline CREATE when the entity is deleted
     * before it has ever reached the server.
     */
    public static boolean cancelPendingCreate(
            String entityType,
            long localId
    ) {
        String sql = """
                DELETE FROM sync_queue
                WHERE entity_type = ?
                  AND local_id = ?
                  AND operation = 'CREATE'
                  AND status IN (?, ?)
                """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entityType);
            ps.setLong(2, localId);
            ps.setString(3, PENDING);
            ps.setString(4, FAILED);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Failed to cancel pending CREATE: " + e.getMessage());
            return false;
        }
    }

    public static boolean hasPendingCreate(
            String entityType,
            long localId
    ) {
        String sql = """
                SELECT 1
                FROM sync_queue
                WHERE entity_type = ?
                  AND local_id = ?
                  AND operation = 'CREATE'
                  AND status IN (?, ?)
                LIMIT 1
                """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entityType);
            ps.setLong(2, localId);
            ps.setString(3, PENDING);
            ps.setString(4, FAILED);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Failed to check pending CREATE: " + e.getMessage());
            return false;
        }
    }

    public static void markProcessing(long id) {
        updateStatus(id, PROCESSING, null);
    }

    public static void markSynced(long id, Long serverId) {
        String sql = """
                UPDATE sync_queue
                SET status = ?,
                    server_id = ?,
                    last_attempt = CURRENT_TIMESTAMP,
                    error_message = NULL
                WHERE id = ?
                """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, SYNCED);

            if (serverId == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setLong(2, serverId);
            }

            ps.setLong(3, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to mark sync operation as synced: " + e.getMessage());
        }
    }

    public static void markFailed(long id, String errorMessage) {
        String sql = """
                UPDATE sync_queue
                SET status = ?,
                    retry_count = retry_count + 1,
                    last_attempt = CURRENT_TIMESTAMP,
                    error_message = ?
                WHERE id = ?
                """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, FAILED);
            ps.setString(2, errorMessage);
            ps.setLong(3, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to mark sync operation as failed: " + e.getMessage());
        }
    }

    public static void retryOperation(long id) {
        updateStatus(id, PENDING, null);
    }

    public static void retryFailedOperations() {
        String sql = """
                UPDATE sync_queue
                SET status = ?,
                    error_message = NULL
                WHERE status = ?
                """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, PENDING);
            ps.setString(2, FAILED);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to retry failed sync operations: " + e.getMessage());
        }
    }

    public static void resetProcessingOperations() {
        String sql = """
                UPDATE sync_queue
                SET status = ?
                WHERE status = ?
                """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, PENDING);
            ps.setString(2, PROCESSING);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to reset processing operations: " + e.getMessage());
        }
    }

    public static int getPendingCount() {
        return getCountByStatus(PENDING);
    }

    public static int getFailedCount() {
        return getCountByStatus(FAILED);
    }

    public static int getSyncedCount() {
        return getCountByStatus(SYNCED);
    }

    private static int getCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM sync_queue WHERE status = ?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to count sync operations: " + e.getMessage());
        }

        return 0;
    }

    public static int deleteSyncedOperations(int keepLatest) {
        String sql = """
                DELETE FROM sync_queue
                WHERE status = ?
                  AND id NOT IN (
                      SELECT id
                      FROM sync_queue
                      WHERE status = ?
                      ORDER BY id DESC
                      LIMIT ?
                  )
                """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, SYNCED);
            ps.setString(2, SYNCED);
            ps.setInt(3, keepLatest);

            return ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to delete synced operations: " + e.getMessage());
            return 0;
        }
    }

    private static void updateStatus(
            long id,
            String status,
            String errorMessage
    ) {
        String sql = """
                UPDATE sync_queue
                SET status = ?,
                    error_message = ?,
                    last_attempt = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, errorMessage);
            ps.setLong(3, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to update sync operation: " + e.getMessage());
        }
    }
}
