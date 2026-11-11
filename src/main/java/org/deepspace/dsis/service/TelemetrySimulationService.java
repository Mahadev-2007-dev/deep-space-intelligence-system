package org.deepspace.dsis.service;

import org.deepspace.dsis.model.Spacecraft;
import org.deepspace.dsis.model.TelemetryRecord;
import org.deepspace.dsis.repository.SpacecraftRepository;
import org.deepspace.dsis.repository.TelemetryRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class TelemetrySimulationService {

    private static final Logger log = LoggerFactory.getLogger(TelemetrySimulationService.class);

    private final SpacecraftRepository spacecraftRepository;
    private final TelemetryRecordRepository telemetryRepository;
    private final AnomalyDetectionEngine anomalyEngine;
    private final Random random = new Random();

    public TelemetrySimulationService(SpacecraftRepository spacecraftRepository,
                                     TelemetryRecordRepository telemetryRepository,
                                     AnomalyDetectionEngine anomalyEngine) {
        this.spacecraftRepository = spacecraftRepository;
        this.telemetryRepository = telemetryRepository;
        this.anomalyEngine = anomalyEngine;
    }

    @Scheduled(fixedRate = 3000)
    public void runSimulationCycle() {
        List<Spacecraft> allSpacecraft = spacecraftRepository.findAll();
        for (Spacecraft sc : allSpacecraft) {
            if (!sc.isSimulationActive()) {
                continue;
            }

            simulateTelemetryFluctuation(sc);
            anomalyEngine.evaluateSpacecraft(sc);

            // Record snapshot in telemetry database
            TelemetryRecord record = new TelemetryRecord(
                    sc.getId(),
                    sc.getName(),
                    sc.getTemperature(),
                    sc.getCabinPressure(),
                    sc.getOxygenLevel(),
                    sc.getBatteryLevel(),
                    sc.getSnrDb(),
                    sc.getStatus()
            );
            telemetryRepository.save(record);
        }
    }

    private void simulateTelemetryFluctuation(Spacecraft sc) {
        if (sc.isAnomalyTriggered()) {
            applyAnomalyDistortion(sc);
        } else {
            applyNominalNoise(sc);
        }
        sc.setLastTelemetryUpdate(LocalDateTime.now());
        spacecraftRepository.save(sc);
    }

    private void applyNominalNoise(Spacecraft sc) {
        // Small Gaussian noise around baseline values
        double tempNoise = (random.nextDouble() - 0.5) * 0.4;
        double pressureNoise = (random.nextDouble() - 0.5) * 0.15;
        double o2Noise = (random.nextDouble() - 0.5) * 0.05;
        double batteryNoise = (random.nextDouble() - 0.48) * 0.1; // Slow discharge/charge cycle
        double snrNoise = (random.nextDouble() - 0.5) * 0.3;

        sc.setTemperature(round(sc.getTemperature() + tempNoise, 1));
        sc.setCabinPressure(round(Math.max(98.0, Math.min(103.0, sc.getCabinPressure() + pressureNoise)), 1));
        sc.setOxygenLevel(round(Math.max(20.4, Math.min(21.8, sc.getOxygenLevel() + o2Noise)), 1));
        sc.setBatteryLevel(round(Math.max(75.0, Math.min(99.5, sc.getBatteryLevel() + batteryNoise)), 1));
        sc.setSnrDb(round(Math.max(24.0, Math.min(34.0, sc.getSnrDb() + snrNoise)), 1));
    }

    private void applyAnomalyDistortion(Spacecraft sc) {
        String type = sc.getActiveAnomalyType();
        if (type == null) type = "CRYOGENIC_OVERHEAT";

        switch (type) {
            case "CRYOGENIC_OVERHEAT":
                sc.setTemperature(round(sc.getTemperature() + 3.5 + (random.nextDouble() * 1.5), 1));
                if (sc.getTemperature() > 88.0) sc.setTemperature(88.0);
                break;
            case "PRESSURE_DROP":
                sc.setCabinPressure(round(sc.getCabinPressure() - 2.8 - (random.nextDouble() * 1.2), 1));
                if (sc.getCabinPressure() < 72.0) sc.setCabinPressure(72.0);
                break;
            case "O2_FAILURE":
                sc.setOxygenLevel(round(sc.getOxygenLevel() - 0.8 - (random.nextDouble() * 0.3), 1));
                if (sc.getOxygenLevel() < 15.5) sc.setOxygenLevel(15.5);
                break;
            case "BATTERY_DRAIN":
                sc.setBatteryLevel(round(sc.getBatteryLevel() - 4.5 - (random.nextDouble() * 1.8), 1));
                if (sc.getBatteryLevel() < 16.0) sc.setBatteryLevel(16.0);
                break;
            case "COMM_LOSS":
                sc.setSnrDb(round(sc.getSnrDb() - 3.2 - (random.nextDouble() * 1.0), 1));
                if (sc.getSnrDb() < 6.5) sc.setSnrDb(6.5);
                sc.setCommunicationStatus("DEGRADED");
                break;
            default:
                sc.setTemperature(round(sc.getTemperature() + 2.0, 1));
                break;
        }
    }

    public Spacecraft startSimulation(Long id) {
        Spacecraft sc = spacecraftRepository.findById(id).orElseThrow();
        sc.setSimulationActive(true);
        return spacecraftRepository.save(sc);
    }

    public Spacecraft stopSimulation(Long id) {
        Spacecraft sc = spacecraftRepository.findById(id).orElseThrow();
        sc.setSimulationActive(false);
        return spacecraftRepository.save(sc);
    }

    public Spacecraft resetSimulation(Long id) {
        Spacecraft sc = spacecraftRepository.findById(id).orElseThrow();
        sc.setAnomalyTriggered(false);
        sc.setActiveAnomalyType("NONE");
        sc.setStatus("NOMINAL");
        sc.setCommunicationStatus("OPTIMAL");

        // Reset to normal baselines based on spacecraft
        if (sc.getName().contains("Mars")) {
            sc.setTemperature(22.4);
            sc.setCabinPressure(101.3);
            sc.setOxygenLevel(21.0);
            sc.setBatteryLevel(95.0);
            sc.setSnrDb(31.2);
        } else if (sc.getName().contains("Europa")) {
            sc.setTemperature(-14.2);
            sc.setCabinPressure(99.0);
            sc.setOxygenLevel(20.8);
            sc.setBatteryLevel(89.0);
            sc.setSnrDb(27.0);
        } else if (sc.getName().contains("Titan")) {
            sc.setTemperature(18.5);
            sc.setCabinPressure(101.8);
            sc.setOxygenLevel(21.1);
            sc.setBatteryLevel(92.0);
            sc.setSnrDb(28.5);
        } else {
            sc.setTemperature(-28.6);
            sc.setCabinPressure(96.5);
            sc.setOxygenLevel(20.5);
            sc.setBatteryLevel(80.0);
            sc.setSnrDb(21.0);
        }
        sc.setLastTelemetryUpdate(LocalDateTime.now());
        return spacecraftRepository.save(sc);
    }

    public Spacecraft triggerAnomaly(Long id, String anomalyType) {
        Spacecraft sc = spacecraftRepository.findById(id).orElseThrow();
        sc.setAnomalyTriggered(true);
        sc.setActiveAnomalyType(anomalyType);
        return spacecraftRepository.save(sc);
    }

    public Spacecraft clearAnomaly(Long id) {
        return resetSimulation(id);
    }

    private double round(double val, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(val * factor) / factor;
    }
}
