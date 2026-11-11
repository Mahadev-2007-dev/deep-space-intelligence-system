package org.deepspace.dsis.controller;

import org.deepspace.dsis.model.*;
import org.deepspace.dsis.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final SpacecraftRepository spacecraftRepository;
    private final MissionRepository missionRepository;
    private final AnomalyRecordRepository anomalyRepository;
    private final EmergencyIncidentRepository emergencyRepository;
    private final TelemetryRecordRepository telemetryRepository;

    public DashboardController(SpacecraftRepository spacecraftRepository,
                               MissionRepository missionRepository,
                               AnomalyRecordRepository anomalyRepository,
                               EmergencyIncidentRepository emergencyRepository,
                               TelemetryRecordRepository telemetryRepository) {
        this.spacecraftRepository = spacecraftRepository;
        this.missionRepository = missionRepository;
        this.anomalyRepository = anomalyRepository;
        this.emergencyRepository = emergencyRepository;
        this.telemetryRepository = telemetryRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        List<Spacecraft> spacecraftList = spacecraftRepository.findAll();
        List<Mission> missions = missionRepository.findAll();
        List<AnomalyRecord> openAnomalies = anomalyRepository.findByStatus("OPEN");
        List<EmergencyIncident> activeEmergencies = emergencyRepository.findByStatus("ACTIVE");

        long activeMissionsCount = missions.stream().filter(m -> "ACTIVE".equalsIgnoreCase(m.getStatus()) || "MONITORING".equalsIgnoreCase(m.getStatus())).count();
        long nominalCraft = spacecraftList.stream().filter(s -> "NOMINAL".equalsIgnoreCase(s.getStatus())).count();

        // Calculate overall system health
        double healthScore = 98.4;
        if (!openAnomalies.isEmpty()) {
            healthScore -= (openAnomalies.size() * 4.5);
        }
        if (!activeEmergencies.isEmpty()) {
            healthScore -= (activeEmergencies.size() * 12.0);
        }
        healthScore = Math.max(10.0, Math.min(100.0, healthScore));

        // Ground stations status
        List<Map<String, Object>> groundStations = List.of(
                Map.of("name", "Goldstone Deep Space Station (DSS-14)", "location", "Mojave Desert, USA", "dishSize", "70m Aperture", "status", "TRACKING_ACTIVE", "target", "Mars Explorer", "frequency", "8.4 GHz X-Band", "snr", 31.4),
                Map.of("name", "Madrid Deep Space Station (DSS-63)", "location", "Robledo de Chavela, Spain", "dishSize", "70m Aperture", "status", "STANDBY_ACQUISITION", "target", "Europa Explorer", "frequency", "32 GHz Ka-Band", "snr", 27.2),
                Map.of("name", "Canberra Deep Space Station (DSS-43)", "location", "Tidbinbilla, Australia", "dishSize", "70m Aperture", "status", "TRACKING_ACTIVE", "target", "Deep Space Voyager", "frequency", "2.2 GHz S-Band", "snr", 19.8)
        );

        // Recent mission activities log
        List<Map<String, Object>> activities = new ArrayList<>();
        activities.add(Map.of("time", LocalDateTime.now().minusMinutes(3).format(DateTimeFormatter.ofPattern("HH:mm:ss")), "event", "MEX-01 Mars orbital insertion trim burn successful (Δv = 4.2 m/s).", "type", "TRAJECTORY", "severity", "NOMINAL"));
        activities.add(Map.of("time", LocalDateTime.now().minusMinutes(12).format(DateTimeFormatter.ofPattern("HH:mm:ss")), "event", "EEX-02 Europa subsurface ice radar sounding sweep data frame received.", "type", "TELEMETRY", "severity", "NOMINAL"));
        activities.add(Map.of("time", LocalDateTime.now().minusMinutes(28).format(DateTimeFormatter.ofPattern("HH:mm:ss")), "event", "Deep Space Voyager entered Jovian magnetosphere boundary transition zone.", "type", "NAVIGATION", "severity", "ADVISORY"));
        if (!openAnomalies.isEmpty()) {
            AnomalyRecord latest = openAnomalies.get(0);
            activities.add(0, Map.of("time", latest.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")), "event", "AI Anomaly Triggered on " + latest.getSpacecraftName() + ": " + latest.getExplanation(), "type", "ANOMALY", "severity", latest.getSeverity()));
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("totalSpacecraft", spacecraftList.size());
        resp.put("activeMissions", activeMissionsCount);
        resp.put("totalMissions", missions.size());
        resp.put("nominalSpacecraft", nominalCraft);
        resp.put("activeAnomalies", openAnomalies.size());
        resp.put("activeEmergencies", activeEmergencies.size());
        resp.put("systemHealthScore", Math.round(healthScore * 10.0) / 10.0);
        resp.put("totalTelemetryFrames", telemetryRepository.count());
        resp.put("downlinkAggregateKbps", spacecraftList.stream().mapToDouble(Spacecraft::getDownlinkThroughputKbps).sum());
        resp.put("groundStations", groundStations);
        resp.put("recentActivities", activities);
        resp.put("spacecraftSummary", spacecraftList);
        resp.put("simulatedNotice", "SIMULATED TELEMETRY FEED - ACADEMIC REPLICATION");

        return ResponseEntity.ok(resp);
    }
}
