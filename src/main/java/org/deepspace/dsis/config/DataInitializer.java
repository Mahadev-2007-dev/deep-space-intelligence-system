package org.deepspace.dsis.config;

import org.deepspace.dsis.model.*;
import org.deepspace.dsis.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final SpacecraftRepository spacecraftRepository;
    private final MissionRepository missionRepository;
    private final AnomalyRecordRepository anomalyRepository;
    private final EmergencyIncidentRepository emergencyRepository;
    private final TelemetryRecordRepository telemetryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           SpacecraftRepository spacecraftRepository,
                           MissionRepository missionRepository,
                           AnomalyRecordRepository anomalyRepository,
                           EmergencyIncidentRepository emergencyRepository,
                           TelemetryRecordRepository telemetryRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.spacecraftRepository = spacecraftRepository;
        this.missionRepository = missionRepository;
        this.anomalyRepository = anomalyRepository;
        this.emergencyRepository = emergencyRepository;
        this.telemetryRepository = telemetryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedSpacecraft();
        seedMissions();
        seedInitialAnomalies();
        seedInitialEmergencies();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            log.info(">>> Seeding initial users...");
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "flight.director@dsis.space",
                    "Commander Alan Vance",
                    "ROLE_ADMIN",
                    "FLIGHT_DIRECTOR_1",
                    "Deep Space Operations Command"
            );
            userRepository.save(admin);

            User operator = new User(
                    "operator",
                    passwordEncoder.encode("operator123"),
                    "operator@dsis.space",
                    "Lt. Elena Rostova",
                    "ROLE_OPERATOR",
                    "AERO_SPEC_4",
                    "Telemetry & Telecommand Division"
            );
            userRepository.save(operator);
            log.info(">>> Seeded default users: admin / admin123 and operator / operator123");
        }
    }

    private void seedSpacecraft() {
        if (spacecraftRepository.count() == 0) {
            log.info(">>> Seeding 4 fictional spacecraft...");
            Spacecraft s1 = new Spacecraft("Mars Explorer", "MEX-01", "Mars", 22.4, 101.3, 21.0, 94.5);
            s1.setLaunchDate("2024-03-15");
            s1.setMissionElapsedDays(425);
            s1.setSnrDb(31.2);
            s1.setSolarArrayOutput(1380.0);
            s1.setDownlinkThroughputKbps(2048.0);
            spacecraftRepository.save(s1);

            Spacecraft s2 = new Spacecraft("Europa Explorer", "EEX-02", "Europa", -14.2, 98.7, 20.8, 88.2);
            s2.setLaunchDate("2023-11-20");
            s2.setMissionElapsedDays(540);
            s2.setSnrDb(26.8);
            s2.setSolarArrayOutput(950.0);
            s2.setDownlinkThroughputKbps(512.0);
            spacecraftRepository.save(s2);

            Spacecraft s3 = new Spacecraft("Titan Explorer", "TEX-03", "Titan", 18.5, 102.1, 21.2, 91.0);
            s3.setLaunchDate("2024-01-10");
            s3.setMissionElapsedDays(488);
            s3.setSnrDb(28.4);
            s3.setSolarArrayOutput(1100.0);
            s3.setDownlinkThroughputKbps(1024.0);
            spacecraftRepository.save(s3);

            Spacecraft s4 = new Spacecraft("Deep Space Voyager", "DSV-04", "Deep Space", -28.6, 96.5, 20.5, 79.4);
            s4.setLaunchDate("2022-08-05");
            s4.setMissionElapsedDays(1012);
            s4.setSnrDb(19.5);
            s4.setSolarArrayOutput(420.0); // RTG powered primary
            s4.setDownlinkThroughputKbps(256.0);
            spacecraftRepository.save(s4);

            // Initial telemetry frames
            for (Spacecraft sc : spacecraftRepository.findAll()) {
                for (int i = 10; i >= 0; i--) {
                    TelemetryRecord record = new TelemetryRecord(
                            sc.getId(),
                            sc.getName(),
                            sc.getTemperature() + (Math.sin(i) * 1.5),
                            sc.getCabinPressure() + (Math.cos(i) * 0.4),
                            sc.getOxygenLevel() + (Math.sin(i * 0.5) * 0.1),
                            sc.getBatteryLevel() - (i * 0.05),
                            sc.getSnrDb() + (Math.cos(i) * 0.8),
                            sc.getStatus()
                    );
                    record.setTimestamp(LocalDateTime.now().minusMinutes(i * 2L));
                    telemetryRepository.save(record);
                }
            }
            log.info(">>> Seeded 4 fictional spacecraft and baseline telemetry history.");
        }
    }

    private void seedMissions() {
        if (missionRepository.count() == 0) {
            log.info(">>> Seeding missions...");
            missionRepository.save(new Mission(
                    "Ares Borealis Survey",
                    "MSN-ARS-01",
                    "Mars",
                    1L,
                    "Mars Explorer",
                    "ACTIVE",
                    "2024-03-15",
                    "2027-09-30",
                    68,
                    "High-resolution polar regolith spectrometry and subsurface ice mapping across Planum Boreum.",
                    "Gene Kranz II",
                    "CLASS-A FLAGSHIP",
                    "HIGH"
            ));

            missionRepository.save(new Mission(
                    "Oceanus Cryo-Drill Recon",
                    "MSN-OCN-02",
                    "Europa",
                    2L,
                    "Europa Explorer",
                    "MONITORING",
                    "2023-11-20",
                    "2028-12-15",
                    42,
                    "Gravitational sounding and thermal plume detection over chaotic terrain Conamara Chaos.",
                    "Dr. Sarah Lin",
                    "CLASS-A FLAGSHIP",
                    "CRITICAL"
            ));

            missionRepository.save(new Mission(
                    "Kraken Mare Hydrocarbon Survey",
                    "MSN-KRK-03",
                    "Titan",
                    3L,
                    "Titan Explorer",
                    "PLANNED",
                    "2025-05-10",
                    "2031-02-18",
                    18,
                    "Atmospheric entry, aero-braking and low-frequency radar profiling of methane-ethane liquid lakes.",
                    "Alex Mercer",
                    "CLASS-B DISCOVERY",
                    "MEDIUM"
            ));

            missionRepository.save(new Mission(
                    "Interstellar Boundary Explorer",
                    "MSN-IBX-04",
                    "Deep Space",
                    4L,
                    "Deep Space Voyager",
                    "ACTIVE",
                    "2022-08-05",
                    "2035-01-01",
                    83,
                    "Solar wind heliopause transition monitoring, magnetic field vectoring and galactic cosmic ray flux profiling.",
                    "Elena Rostova",
                    "CLASS-A FLAGSHIP",
                    "HIGH"
            ));
            log.info(">>> Seeded 4 active/planned space missions.");
        }
    }

    private void seedInitialAnomalies() {
        if (anomalyRepository.count() == 0) {
            log.info(">>> Seeding baseline anomaly records...");
            AnomalyRecord a1 = new AnomalyRecord(
                    2L,
                    "Europa Explorer",
                    "MEDIUM",
                    "TEMPERATURE",
                    -19.8,
                    "-15.0°C to +35.0°C",
                    "Cryogenic cold-soak excursion detected during Jupiter shadow transit; secondary heater circuits engaged."
            );
            a1.setStatus("ACKNOWLEDGED");
            a1.setAcknowledgedBy("FLIGHT_DIRECTOR_1");
            a1.setAcknowledgedAt(LocalDateTime.now().minusHours(2));
            anomalyRepository.save(a1);

            AnomalyRecord a2 = new AnomalyRecord(
                    4L,
                    "Deep Space Voyager",
                    "LOW",
                    "COMMUNICATION",
                    18.2,
                    "20.0 dB to 35.0 dB",
                    "Deep Space Network (DSN) Goldstone station SNR dip caused by high solar storm proton flux."
            );
            a2.setStatus("RESOLVED");
            a2.setAcknowledgedBy("AERO_SPEC_4");
            a2.setAcknowledgedAt(LocalDateTime.now().minusHours(5));
            anomalyRepository.save(a2);
        }
    }

    private void seedInitialEmergencies() {
        if (emergencyRepository.count() == 0) {
            log.info(">>> Seeding sample emergency incident log...");
            EmergencyIncident inc = new EmergencyIncident(
                    "EMG-O2-FAIL",
                    "Oxygen Sensor Calibration Drift & ECLSS Cascade",
                    3,
                    "ECLSS Life Support, Primary O2 Loop",
                    "AERO_SPEC_4",
                    "Educational simulation exercise executed during shift change; backup manifolds successfully routed."
            );
            inc.setStatus("RESOLVED");
            inc.setChecklistProgress(100);
            inc.setResolvedAt(LocalDateTime.now().minusDays(1));
            emergencyRepository.save(inc);
        }
    }
}
