package com.stockpilot.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockpilot.backend.dto.SyncRequest;
import com.stockpilot.backend.entity.Ingredient;
import com.stockpilot.backend.repository.IngredientRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class SyncService {

    private final JdbcTemplate jdbcTemplate;
    private final IngredientRepository ingredientRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public SyncService(
            JdbcTemplate jdbcTemplate,
            IngredientRepository ingredientRepository    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional
    public Map<String, Object> process(SyncRequest request) {

        validateRequest(request);

        String existingStatus =
                findExistingOperation(request.getOperationId());

        if (existingStatus != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("alreadyProcessed", true);
            response.put("operationId", request.getOperationId());
            response.put("status", existingStatus);

            if (request.getServerId() != null) {
                response.put("serverId", request.getServerId());
            }

            return response;
        }

        Long serverId;

        if ("INGREDIENT".equalsIgnoreCase(request.getEntityType())) {

            serverId = processIngredient(request);

        } else {

            throw new IllegalArgumentException(
                    "Unsupported sync entity type: "
                            + request.getEntityType()
            );
        }

        saveOperation(
                request,
                serverId,
                "PROCESSED",
                null
        );

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("alreadyProcessed", false);
        response.put("operationId", request.getOperationId());
        response.put("serverId", serverId);
        response.put("status", "PROCESSED");

        return response;
    }

    private Long processIngredient(SyncRequest request) {

        if (request.getPayload() == null
                || request.getPayload().isBlank()) {

            throw new IllegalArgumentException(
                    "Ingredient payload is required"
            );
        }

        String operation =
                request.getOperation().toUpperCase();

        switch (operation) {

            case "CREATE":
                return createIngredient(request);

            case "UPDATE":
                return updateIngredient(request);

            case "DELETE":
                deleteIngredient(request);
                return request.getServerId();

            default:
                throw new IllegalArgumentException(
                        "Unsupported ingredient operation: "
                                + operation
                );
        }
    }

    private Long createIngredient(SyncRequest request) {

        try {

            Ingredient incoming =
                    objectMapper.readValue(
                            request.getPayload(),
                            Ingredient.class
                    );

            if (incoming == null) {
                throw new IllegalArgumentException(
                        "Invalid ingredient payload"
                );
            }

            /*
             * The local SQLite ID must never become
             * the PostgreSQL ID.
             */
            incoming.setId(null);

            Ingredient saved =
                    ingredientRepository.save(incoming);

            return saved.getId().longValue();

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "Failed to create ingredient from sync payload",
                    e
            );
        }
    }

    private Long updateIngredient(SyncRequest request) {

        if (request.getServerId() == null) {
            throw new IllegalArgumentException(
                    "serverId is required for ingredient UPDATE"
            );
        }

        Ingredient existing =
                ingredientRepository.findById(
                        request.getServerId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Ingredient not found: "
                                        + request.getServerId()
                        )
                );

        try {

            Ingredient incoming =
                    objectMapper.readValue(
                            request.getPayload(),
                            Ingredient.class
                    );

            if (incoming == null) {
                throw new IllegalArgumentException(
                        "Invalid ingredient payload"
                );
            }

            existing.setName(incoming.getName());
            existing.setQuantity(incoming.getQuantity());
            existing.setUnit(incoming.getUnit());
            existing.setMinimumStock(
                    incoming.getMinimumStock()
            );

            Ingredient saved =
                    ingredientRepository.save(existing);

            return saved.getId().longValue();

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "Failed to update ingredient from sync payload",
                    e
            );
        }
    }

    private void deleteIngredient(SyncRequest request) {

        if (request.getServerId() == null) {
            throw new IllegalArgumentException(
                    "serverId is required for ingredient DELETE"
            );
        }

        if (!ingredientRepository.existsById(
                request.getServerId()
        )) {

            /*
             * If it is already gone from the server,
             * the desired final state has already been achieved.
             */
            return;
        }

        ingredientRepository.deleteById(
                request.getServerId()
        );
    }

    private String findExistingOperation(String operationId) {

        String sql = """
                SELECT status
                FROM sync_operations
                WHERE operation_id = ?
                LIMIT 1
                """;

        try {

            return jdbcTemplate.query(
                    sql,
                    ps -> ps.setString(1, operationId),
                    rs -> rs.next()
                            ? rs.getString("status")
                            : null
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to check sync operation",
                    e
            );
        }
    }

    private void saveOperation(
            SyncRequest request,
            Long serverId,
            String status,
            String errorMessage
    ) {

        String sql = """
                INSERT INTO sync_operations
                (
                    operation_id,
                    entity_type,
                    operation,
                    local_id,
                    server_id,
                    payload,
                    status,
                    processed_at,
                    error_message
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)
                """;

        try {

            jdbcTemplate.update(
                    sql,
                    request.getOperationId(),
                    request.getEntityType(),
                    request.getOperation(),
                    request.getLocalId(),
                    serverId,
                    request.getPayload(),
                    status,
                    errorMessage
            );

        } catch (DataIntegrityViolationException e) {

            /*
             * Another request may have submitted the same
             * operation at almost exactly the same time.
             */
            if (findExistingOperation(
                    request.getOperationId()
            ) == null) {
                throw e;
            }
        }
    }

    private void validateRequest(SyncRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Sync request cannot be null"
            );
        }

        if (request.getOperationId() == null
                || request.getOperationId().isBlank()) {

            throw new IllegalArgumentException(
                    "operationId is required"
            );
        }

        if (request.getEntityType() == null
                || request.getEntityType().isBlank()) {

            throw new IllegalArgumentException(
                    "entityType is required"
            );
        }

        if (request.getOperation() == null
                || request.getOperation().isBlank()) {

            throw new IllegalArgumentException(
                    "operation is required"
            );
        }
    }
}

