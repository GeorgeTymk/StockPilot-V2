package com.stockpilot.backend.controller;

import com.stockpilot.backend.dto.SyncRequest;
import com.stockpilot.backend.service.SyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sync")
@CrossOrigin
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> sync(
            @RequestBody SyncRequest request
    ) {
        return ResponseEntity.ok(
                syncService.process(request)
        );
    }
}
