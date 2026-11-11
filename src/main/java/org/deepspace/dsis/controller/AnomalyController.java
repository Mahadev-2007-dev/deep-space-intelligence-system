package org.deepspace.dsis.controller;

import org.deepspace.dsis.model.AnomalyRecord;
import org.deepspace.dsis.repository.AnomalyRecordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/anomalies")
public class AnomalyController {

    private final AnomalyRecordRepository anomalyRepository;

    public AnomalyController(AnomalyRecordRepository anomalyRepository) {
        this.anomalyRepository = anomalyRepository;
    }

    @GetMapping
    public ResponseEntity<List<AnomalyRecord>> getAnomalies(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) Long spacecraftId) {

        List<AnomalyRecord> list = anomalyRepository.findTop100ByOrderByTimestampDesc();

        if (status != null && !status.isBlank()) {
            list = list.stream().filter(a -> status.equalsIgnoreCase(a.getStatus())).collect(Collectors.toList());
        }
        if (severity != null && !severity.isBlank()) {
            list = list.stream().filter(a -> severity.equalsIgnoreCase(a.getSeverity())).collect(Collectors.toList());
        }
        if (spacecraftId != null) {
            list = list.stream().filter(a -> spacecraftId.equals(a.getSpacecraftId())).collect(Collectors.toList());
        }

        return ResponseEntity.ok(list);
    }

    @PostMapping("/{id}/acknowledge")
    public ResponseEntity<?> acknowledgeAnomaly(@PathVariable Long id, Authentication auth) {
        AnomalyRecord record = anomalyRepository.findById(id).orElse(null);
        if (record == null) return ResponseEntity.notFound().build();

        String callsign = (auth != null && auth.getName() != null) ? auth.getName() : "OPERATOR_COMMAND";
        record.setStatus("ACKNOWLEDGED");
        record.setAcknowledgedBy(callsign);
        record.setAcknowledgedAt(LocalDateTime.now());
        anomalyRepository.save(record);

        return ResponseEntity.ok(Map.of("message", "Anomaly acknowledged by " + callsign, "anomaly", record));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolveAnomaly(@PathVariable Long id, Authentication auth) {
        AnomalyRecord record = anomalyRepository.findById(id).orElse(null);
        if (record == null) return ResponseEntity.notFound().build();

        String callsign = (auth != null && auth.getName() != null) ? auth.getName() : "OPERATOR_COMMAND";
        record.setStatus("RESOLVED");
        record.setAcknowledgedBy(callsign);
        record.setAcknowledgedAt(LocalDateTime.now());
        anomalyRepository.save(record);

        return ResponseEntity.ok(Map.of("message", "Anomaly resolved and archived.", "anomaly", record));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getAnomalyStats() {
        List<AnomalyRecord> all = anomalyRepository.findAll();
        Map<String, Long> severityCounts = all.stream()
                .collect(Collectors.groupingBy(AnomalyRecord::getSeverity, Collectors.counting()));
        Map<String, Long> parameterCounts = all.stream()
                .collect(Collectors.groupingBy(AnomalyRecord::getParameter, Collectors.counting()));
        Map<String, Long> statusCounts = all.stream()
                .collect(Collectors.groupingBy(AnomalyRecord::getStatus, Collectors.counting()));

        Map<String, Object> resp = new HashMap<>();
        resp.put("totalAnomalies", all.size());
        resp.put("severityCounts", severityCounts);
        resp.put("parameterCounts", parameterCounts);
        resp.put("statusCounts", statusCounts);
        resp.put("detectionAlgorithm", "Multi-parameter Rule-based & Dynamic Statistical Z-Score Thresholding");
        return ResponseEntity.ok(resp);
    }
}
