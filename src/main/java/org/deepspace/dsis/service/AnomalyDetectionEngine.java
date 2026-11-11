package org.deepspace.dsis.service;

import org.deepspace.dsis.model.AnomalyRecord;
import org.deepspace.dsis.model.Spacecraft;
import org.deepspace.dsis.repository.AnomalyRecordRepository;
import org.deepspace.dsis.repository.SpacecraftRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnomalyDetectionEngine {

    private static final Logger log = LoggerFactory.getLogger(AnomalyDetectionEngine.class);

    private final AnomalyRecordRepository anomalyRepository;
    private final SpacecraftRepository spacecraftRepository;

    public AnomalyDetectionEngine(AnomalyRecordRepository anomalyRepository, SpacecraftRepository spacecraftRepository) {
        this.anomalyRepository = anomalyRepository;
        this.spacecraftRepository = spacecraftRepository;
    }

    public List<AnomalyRecord> evaluateSpacecraft(Spacecraft sc) {
        List<AnomalyRecord> detected = new ArrayList<>();

        // 1. Temperature Check
        double temp = sc.getTemperature();
        if (temp > 65.0) {
            detected.add(createAnomaly(sc, "CRITICAL", "TEMPERATURE", temp, "-25.0°C to +45.0°C",
                    String.format("Critical thermal excursion (+%.1f°C). Potential active cooling loop rupture or direct solar flare exposure.", temp)));
        } else if (temp > 45.0) {
            detected.add(createAnomaly(sc, "HIGH", "TEMPERATURE", temp, "-25.0°C to +45.0°C",
                    String.format("High temperature detected (+%.1f°C). Heat rejection radiators exceeding nominal dissipation limits.", temp)));
        } else if (temp < -35.0) {
            detected.add(createAnomaly(sc, "HIGH", "TEMPERATURE", temp, "-25.0°C to +45.0°C",
                    String.format("Cryogenic freeze threat (%.1f°C). Electrical bus heater failure or prolonged shadow transit.", temp)));
        }

        // 2. Cabin Pressure Check
        double pressure = sc.getCabinPressure();
        if (pressure < 85.0) {
            detected.add(createAnomaly(sc, "CRITICAL", "CABIN_PRESSURE", pressure, "95.0 kPa to 105.0 kPa",
                    String.format("Rapid cabin depressurization (%.1f kPa). Potential micrometeorite puncture or pressure seal failure.", pressure)));
        } else if (pressure < 95.0) {
            detected.add(createAnomaly(sc, "MEDIUM", "CABIN_PRESSURE", pressure, "95.0 kPa to 105.0 kPa",
                    String.format("Sub-nominal pressure (%.1f kPa). Pressure equalization valve leak detected.", pressure)));
        } else if (pressure > 115.0) {
            detected.add(createAnomaly(sc, "HIGH", "CABIN_PRESSURE", pressure, "95.0 kPa to 105.0 kPa",
                    String.format("Over-pressurization event (%.1f kPa). Regulator valve stuck open on N2 supply tank.", pressure)));
        }

        // 3. Oxygen Level Check
        double o2 = sc.getOxygenLevel();
        if (o2 < 17.5) {
            detected.add(createAnomaly(sc, "CRITICAL", "OXYGEN_LEVEL", o2, "19.5% to 23.5%",
                    String.format("Hypoxic atmospheric hazard (%.1f%% O2). Secondary oxygen catalytic candle generator offline.", o2)));
        } else if (o2 < 19.5) {
            detected.add(createAnomaly(sc, "HIGH", "OXYGEN_LEVEL", o2, "19.5% to 23.5%",
                    String.format("Degraded oxygen concentration (%.1f%% O2). Primary electrolysis cell efficiency dropping.", o2)));
        } else if (o2 > 24.5) {
            detected.add(createAnomaly(sc, "MEDIUM", "OXYGEN_LEVEL", o2, "19.5% to 23.5%",
                    String.format("Hyperoxic mixture advisory (%.1f%% O2). Increased flammability risk inside habitable module.", o2)));
        }

        // 4. Battery Level Check
        double bat = sc.getBatteryLevel();
        if (bat < 25.0) {
            detected.add(createAnomaly(sc, "CRITICAL", "BATTERY", bat, "60.0% to 100.0%",
                    String.format("Catastrophic battery depletion (%.1f%%). Electrical power system entering emergency survival brownout mode.", bat)));
        } else if (bat < 50.0) {
            detected.add(createAnomaly(sc, "MEDIUM", "BATTERY", bat, "60.0% to 100.0%",
                    String.format("Low main bus power reserve (%.1f%%). Non-essential science payloads should be throttled.", bat)));
        }

        // 5. Communication SNR Check
        double snr = sc.getSnrDb();
        if (snr < 12.0) {
            detected.add(createAnomaly(sc, "HIGH", "COMMUNICATION", snr, "20.0 dB to 35.0 dB",
                    String.format("Severe telecommand signal degradation (%.1f dB SNR). High bit-error rate on X-band downlink.", snr)));
        } else if (snr < 18.0) {
            detected.add(createAnomaly(sc, "LOW", "COMMUNICATION", snr, "20.0 dB to 35.0 dB",
                    String.format("Deep Space Network signal fading (%.1f dB SNR). High-gain dish re-alignment recommended.", snr)));
        }

        // Update spacecraft status based on highest severity
        if (!detected.isEmpty()) {
            boolean hasCritical = detected.stream().anyMatch(a -> "CRITICAL".equals(a.getSeverity()));
            boolean hasHigh = detected.stream().anyMatch(a -> "HIGH".equals(a.getSeverity()));
            if (hasCritical) {
                sc.setStatus("CRITICAL");
            } else if (hasHigh) {
                sc.setStatus("ANOMALOUS");
            } else {
                sc.setStatus("DEGRADED");
            }
        } else {
            sc.setStatus("NOMINAL");
        }
        spacecraftRepository.save(sc);

        // Deduplicate and save only if not already open recently
        List<AnomalyRecord> newlySaved = new ArrayList<>();
        for (AnomalyRecord ar : detected) {
            List<AnomalyRecord> openList = anomalyRepository.findByStatus("OPEN");
            boolean alreadyOpen = openList.stream().anyMatch(existing ->
                    existing.getSpacecraftId().equals(ar.getSpacecraftId()) &&
                    existing.getParameter().equals(ar.getParameter()) &&
                    existing.getTimestamp().isAfter(LocalDateTime.now().minusMinutes(2)));

            if (!alreadyOpen) {
                AnomalyRecord saved = anomalyRepository.save(ar);
                newlySaved.add(saved);
                log.warn(">>> AI Anomaly Triggered on [{}]: [{}] - {}", sc.getName(), ar.getSeverity(), ar.getExplanation());
            }
        }

        return newlySaved;
    }

    private AnomalyRecord createAnomaly(Spacecraft sc, String severity, String parameter, double value, String expected, String explanation) {
        return new AnomalyRecord(sc.getId(), sc.getName(), severity, parameter, value, expected, explanation);
    }
}
