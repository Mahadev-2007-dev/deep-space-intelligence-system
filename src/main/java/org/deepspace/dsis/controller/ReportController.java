package org.deepspace.dsis.controller;

import org.deepspace.dsis.model.*;
import org.deepspace.dsis.repository.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final MissionRepository missionRepository;
    private final SpacecraftRepository spacecraftRepository;
    private final AnomalyRecordRepository anomalyRepository;
    private final EmergencyIncidentRepository emergencyRepository;
    private final ScanRecordRepository scanRepository;
    private final TelemetryRecordRepository telemetryRepository;

    public ReportController(MissionRepository missionRepository,
                            SpacecraftRepository spacecraftRepository,
                            AnomalyRecordRepository anomalyRepository,
                            EmergencyIncidentRepository emergencyRepository,
                            ScanRecordRepository scanRepository,
                            TelemetryRecordRepository telemetryRepository) {
        this.missionRepository = missionRepository;
        this.spacecraftRepository = spacecraftRepository;
        this.anomalyRepository = anomalyRepository;
        this.emergencyRepository = emergencyRepository;
        this.scanRepository = scanRepository;
        this.telemetryRepository = telemetryRepository;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getReportsSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("missionsTotal", missionRepository.count());
        summary.put("spacecraftTotal", spacecraftRepository.count());
        summary.put("anomaliesTotal", anomalyRepository.count());
        summary.put("emergenciesTotal", emergencyRepository.count());
        summary.put("scansTotal", scanRepository.count());
        summary.put("telemetryFramesTotal", telemetryRepository.count());

        List<AnomalyRecord> anomalies = anomalyRepository.findAll();
        Map<String, Integer> anomalyBySeverity = new HashMap<>();
        for (AnomalyRecord a : anomalies) {
            anomalyBySeverity.put(a.getSeverity(), anomalyBySeverity.getOrDefault(a.getSeverity(), 0) + 1);
        }
        summary.put("anomalyBySeverity", anomalyBySeverity);

        List<Mission> missions = missionRepository.findAll();
        Map<String, Integer> missionByStatus = new HashMap<>();
        for (Mission m : missions) {
            missionByStatus.put(m.getStatus(), missionByStatus.getOrDefault(m.getStatus(), 0) + 1);
        }
        summary.put("missionByStatus", missionByStatus);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(@RequestParam(defaultValue = "all") String type) {
        StringBuilder csv = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if ("anomalies".equalsIgnoreCase(type) || "all".equalsIgnoreCase(type)) {
            csv.append("# --- DSIS AI ANOMALY DETECTION LOG ---\n");
            csv.append("ID,Timestamp,Spacecraft,Severity,Parameter,MeasuredValue,ExpectedRange,Status,Explanation\n");
            for (AnomalyRecord a : anomalyRepository.findAll()) {
                csv.append(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",%.2f,\"%s\",\"%s\",\"%s\"\n",
                        a.getId(),
                        a.getTimestamp() != null ? a.getTimestamp().format(dtf) : "",
                        escape(a.getSpacecraftName()),
                        a.getSeverity(),
                        a.getParameter(),
                        a.getMeasuredValue(),
                        escape(a.getExpectedRange()),
                        a.getStatus(),
                        escape(a.getExplanation())
                ));
            }
            csv.append("\n");
        }

        if ("missions".equalsIgnoreCase(type) || "all".equalsIgnoreCase(type)) {
            csv.append("# --- DSIS MISSION DIRECTORY ---\n");
            csv.append("ID,Name,CodeName,TargetBody,Spacecraft,Status,Progress%,FlightDirector,Priority\n");
            for (Mission m : missionRepository.findAll()) {
                csv.append(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,\"%s\",\"%s\"\n",
                        m.getId(),
                        escape(m.getName()),
                        escape(m.getCodeName()),
                        escape(m.getTargetBody()),
                        escape(m.getSpacecraftName()),
                        m.getStatus(),
                        m.getProgressPercentage(),
                        escape(m.getFlightDirector()),
                        m.getPriority()
                ));
            }
            csv.append("\n");
        }

        if ("telemetry".equalsIgnoreCase(type) || "all".equalsIgnoreCase(type)) {
            csv.append("# --- DSIS TELEMETRY ARCHIVE (RECENT FRAMES) ---\n");
            csv.append("ID,Timestamp,Spacecraft,Temperature_C,Pressure_kPa,Oxygen_pct,Battery_pct,SNR_dB,Condition\n");
            for (TelemetryRecord t : telemetryRepository.findTop200ByOrderByTimestampDesc()) {
                csv.append(String.format("%d,\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2f,%.2f,\"%s\"\n",
                        t.getId(),
                        t.getTimestamp() != null ? t.getTimestamp().format(dtf) : "",
                        escape(t.getSpacecraftName()),
                        t.getTemperature(),
                        t.getCabinPressure(),
                        t.getOxygenLevel(),
                        t.getBatteryLevel(),
                        t.getSnrDb(),
                        t.getCondition()
                ));
            }
        }

        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"dsis_telemetry_report_" + System.currentTimeMillis() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\"\"");
    }
}
