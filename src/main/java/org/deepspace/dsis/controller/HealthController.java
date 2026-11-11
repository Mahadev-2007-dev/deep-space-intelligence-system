package org.deepspace.dsis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<?> checkHealth() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "system", "Deep Space Intelligence System (DSIS)",
                "tagline", "INTELLIGENCE BEYOND EARTH.",
                "database", "SQLite 3",
                "telemetryEngine", "ACTIVE",
                "anomalyEngine", "ACTIVE",
                "cvModule", "OPENCV_LOADED",
                "satelliteTracker", "SGP4_CELESTRAK_READY",
                "timestamp", LocalDateTime.now()
        ));
    }
}
