package org.deepspace.dsis.controller;

import org.deepspace.dsis.model.Spacecraft;
import org.deepspace.dsis.model.TelemetryRecord;
import org.deepspace.dsis.repository.SpacecraftRepository;
import org.deepspace.dsis.repository.TelemetryRecordRepository;
import org.deepspace.dsis.service.TelemetrySimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/spacecraft")
public class SpacecraftController {

    private final SpacecraftRepository spacecraftRepository;
    private final TelemetryRecordRepository telemetryRepository;
    private final TelemetrySimulationService simulationService;

    public SpacecraftController(SpacecraftRepository spacecraftRepository,
                                TelemetryRecordRepository telemetryRepository,
                                TelemetrySimulationService simulationService) {
        this.spacecraftRepository = spacecraftRepository;
        this.telemetryRepository = telemetryRepository;
        this.simulationService = simulationService;
    }

    @GetMapping
    public ResponseEntity<List<Spacecraft>> getAllSpacecraft() {
        return ResponseEntity.ok(spacecraftRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSpacecraftById(@PathVariable Long id) {
        return spacecraftRepository.findById(id)
                .map(sc -> {
                    Map<String, Object> details = new HashMap<>();
                    details.put("spacecraft", sc);

                    // Detailed Subsystem Status
                    Map<String, Object> subsystems = new LinkedHashMap<>();
                    subsystems.put("Thermal Control Subsystem (TCS)", Map.of("status", sc.getTemperature() > 45 ? "DEGRADED" : "NOMINAL", "activeRadiators", 4, "coolantFlowLitersMin", 3.2));
                    subsystems.put("Environmental Control & Life Support (ECLSS)", Map.of("status", (sc.getCabinPressure() < 90 || sc.getOxygenLevel() < 18) ? "WARNING" : "NOMINAL", "co2Scrubber", "ACTIVE", "n2ReservesKpa", 320.0));
                    subsystems.put("Electrical Power System (EPS)", Map.of("status", sc.getBatteryLevel() < 35 ? "CRITICAL" : "NOMINAL", "busVoltageV", 28.2, "solarOutputW", sc.getSolarArrayOutput()));
                    subsystems.put("Telemetry, Tracking & Command (TT&C)", Map.of("status", sc.getSnrDb() < 15 ? "MARGINAL" : "OPTIMAL", "carrierLock", true, "dishGimbalDeg", 14.8));
                    subsystems.put("Attitude Determination & Control (ADCS)", Map.of("status", "NOMINAL", "starTrackersLocked", 3, "reactionWheelsRpm", 4200));
                    subsystems.put("Propulsion Monopropellant RCS", Map.of("status", "NOMINAL", "hydrazineRemainingKg", 84.6, "tankPressureBar", 22.4));

                    details.put("subsystems", subsystems);
                    details.put("simulatedNotice", "TELEMETRY IS SIMULATED FOR ACADEMIC DEMONSTRATION");
                    return ResponseEntity.ok(details);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startSimulation(@PathVariable Long id) {
        Spacecraft sc = simulationService.startSimulation(id);
        return ResponseEntity.ok(Map.of("message", "Telemetry simulation started.", "spacecraft", sc));
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<?> stopSimulation(@PathVariable Long id) {
        Spacecraft sc = simulationService.stopSimulation(id);
        return ResponseEntity.ok(Map.of("message", "Telemetry simulation paused.", "spacecraft", sc));
    }

    @PostMapping("/{id}/reset")
    public ResponseEntity<?> resetSimulation(@PathVariable Long id) {
        Spacecraft sc = simulationService.resetSimulation(id);
        return ResponseEntity.ok(Map.of("message", "Telemetry baseline restored to nominal.", "spacecraft", sc));
    }

    public static class AnomalyTriggerRequest {
        public String anomalyType;
    }

    @PostMapping("/{id}/trigger-anomaly")
    public ResponseEntity<?> triggerAnomaly(@PathVariable Long id, @RequestBody(required = false) AnomalyTriggerRequest req) {
        String type = (req != null && req.anomalyType != null) ? req.anomalyType : "CRYOGENIC_OVERHEAT";
        Spacecraft sc = simulationService.triggerAnomaly(id, type);
        return ResponseEntity.ok(Map.of("message", "Abnormal sensor value injected: " + type, "spacecraft", sc));
    }

    @GetMapping("/{id}/telemetry-history")
    public ResponseEntity<?> getTelemetryHistory(@PathVariable Long id) {
        List<TelemetryRecord> history = telemetryRepository.findTop50BySpacecraftIdOrderByTimestampDesc(id);
        Collections.reverse(history); // Oldest to newest for clean chronological chart plotting
        return ResponseEntity.ok(history);
    }
}
