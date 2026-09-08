package com.stockpilot.sync;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stockpilot.api.ApiClient;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SyncWorker {

    private static final long INITIAL_DELAY_SECONDS = 5;
    private static final long SYNC_INTERVAL_SECONDS = 10;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {

                Thread thread =
                        new Thread(
                                r,
                                "StockPilot-SyncWorker"
                        );

                thread.setDaemon(true);

                return thread;
            });

    private final Gson gson =
            new Gson();

    private volatile boolean running =
            false;

    // =====================================================
    // START
    // =====================================================

    public synchronized void start() {

        if (running) {
            return;
        }

        running = true;

        try {

            SyncQueueManager.resetProcessingOperations();

        }
        catch (Exception e) {

            System.err.println(
                    "Unable to reset interrupted sync operations: "
                            + e.getMessage()
            );
        }

        scheduler.scheduleWithFixedDelay(
                this::safeSync,
                INITIAL_DELAY_SECONDS,
                SYNC_INTERVAL_SECONDS,
                TimeUnit.SECONDS
        );

        System.out.println(
                "Sync worker started."
        );
    }

    // =====================================================
    // STOP
    // =====================================================

    public synchronized void stop() {

        if (!running) {
            return;
        }

        running = false;

        scheduler.shutdownNow();

        System.out.println(
                "Sync worker stopped."
        );
    }

    // =====================================================
    // SAFE SYNC
    // =====================================================

    private void safeSync() {

        if (!running) {
            return;
        }

        try {

            /*
             * Check the backend first.
             *
             * If this fails, we simply remain offline.
             * No queue items are modified.
             */
            ApiClient.get(
                    "/ingredients"
            );

            /*
             * The backend is reachable again.
             *
             * Operations that previously failed because of
             * connectivity can now be retried.
             */
            SyncQueueManager.retryFailedOperations();

            syncPendingOperations();

        }
        catch (Exception e) {

            /*
             * Network failures are normal while offline.
             * Never terminate the worker because of them.
             */
            System.out.println(
                    "Sync attempt skipped: "
                            + e.getClass().getSimpleName()
            );
        }
    }

    // =====================================================
    // PROCESS QUEUE
    // =====================================================

    private void syncPendingOperations() {

        List<SyncQueueManager.SyncOperation> operations =
                SyncQueueManager.getPendingOperations(
                        20
                );

        if (operations.isEmpty()) {
            return;
        }

        System.out.println(
                "Sync worker found "
                        + operations.size()
                        + " pending operation(s)."
        );

        for (
                SyncQueueManager.SyncOperation operation
                : operations
        ) {

            if (!running) {
                return;
            }

            syncOperation(
                    operation
            );
        }
    }

    // =====================================================
    // PROCESS ONE OPERATION
    // =====================================================

    private void syncOperation(
            SyncQueueManager.SyncOperation operation
    ) {

        long queueId =
                operation.getId();

        try {

            SyncQueueManager.markProcessing(
                    queueId
            );

            System.out.println(
                    "Syncing "
                            + operation.getOperation()
                            + " "
                            + operation.getEntityType()
                            + " localId="
                            + operation.getLocalId()
            );

            JsonObject request =
                    new JsonObject();

            request.addProperty(
                    "operationId",
                    operation.getOperationId()
            );

            request.addProperty(
                    "entityType",
                    operation.getEntityType()
            );

            request.addProperty(
                    "operation",
                    operation.getOperation()
            );

            if (operation.getLocalId() != null) {

                request.addProperty(
                        "localId",
                        operation.getLocalId()
                );
            }

            /*
             * Existing server ID takes priority.
             *
             * Otherwise try the local -> server mapping.
             */
            Long serverId =
                    operation.getServerId();

            if (serverId == null
                    && operation.getLocalId() != null) {

                serverId =
                        SyncDatabase.getServerId(
                                operation.getEntityType(),
                                operation.getLocalId()
                        );
            }

            if (serverId != null) {

                request.addProperty(
                        "serverId",
                        serverId
                );
            }

            request.addProperty(
                    "payload",
                    operation.getPayload()
            );

            String response =
                    ApiClient.post(
                            "/sync",
                            gson.toJson(request)
                    );

            processSuccessfulResponse(
                    operation,
                    response
            );

        }
        catch (Exception e) {

            String message =
                    e.getMessage();

            if (message == null
                    || message.isBlank()) {

                message =
                        e.getClass()
                                .getSimpleName();
            }

            System.out.println(
                    "Sync failed for queue item "
                            + queueId
                            + ": "
                            + message
            );

            SyncQueueManager.markFailed(
                    queueId,
                    message
            );
        }
    }

    // =====================================================
    // PROCESS SERVER RESPONSE
    // =====================================================

    private void processSuccessfulResponse(
            SyncQueueManager.SyncOperation operation,
            String response
    ) {

        JsonObject json =
                JsonParser.parseString(
                        response
                ).getAsJsonObject();

        boolean success =
                json.has("success")
                        && json.get("success")
                        .getAsBoolean();

        if (!success) {

            throw new RuntimeException(
                    "Server rejected sync operation: "
                            + response
            );
        }

        Long serverId =
                null;

        if (json.has("serverId")
                && !json.get("serverId").isJsonNull()) {

            serverId =
                    json.get("serverId")
                            .getAsLong();
        }

        /*
         * Save local -> server identity mapping when
         * the server gives us a real ID.
         */
        if (serverId != null
                && operation.getLocalId() != null) {

            SyncDatabase.saveIdMapping(
                    operation.getEntityType(),
                    operation.getLocalId(),
                    serverId
            );
        }

        SyncQueueManager.markSynced(
                operation.getId(),
                serverId
        );

        System.out.println(
                "Sync successful: "
                        + operation.getOperation()
                        + " "
                        + operation.getEntityType()
                        + " localId="
                        + operation.getLocalId()
                        + " serverId="
                        + serverId
        );
    }
}
