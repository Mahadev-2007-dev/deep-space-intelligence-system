package org.deepspace.dsis.controller;

import org.deepspace.dsis.model.EmergencyIncident;
import org.deepspace.dsis.repository.EmergencyIncidentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/emergencies")
public class EmergencyController {

    private final EmergencyIncidentRepository emergencyRepository;

    public EmergencyController(EmergencyIncidentRepository emergencyRepository) {
        this.emergencyRepository = emergencyRepository;
    }

    public static class ScenarioDefinition {
        public String code;
        public String title;
        public int severity;
        public String affectedSystems;
        public String description;
        public List<String> checklist;

        public ScenarioDefinition(String code, String title, int severity, String affectedSystems, String description, List<String> checklist) {
            this.code = code;
            this.title = title;
            this.severity = severity;
            this.affectedSystems = affectedSystems;
            this.description = description;
            this.checklist = checklist;
        }
    }

    private static final List<ScenarioDefinition> SCENARIOS = List.of(
            new ScenarioDefinition(
                    "EMG-O2-FAIL",
                    "Oxygen Sensor Calibration Failure & ECLSS Deprivation",
                    4,
                    "ECLSS Primary Loop, Atmospheric Monitor Bus, Cabin Gas Injector",
                    "Primary electrochemical O2 galvanic cell sensor failure inducing incorrect mixture regulation and rapid hypoxic atmospheric conditions.",
                    List.of(
                            "Don emergency high-pressure masks with dedicated 100% O2 bottles.",
                            "Manually override ECLSS automatic dosing solenoid valves.",
                            "Switch to secondary optical luminescence oxygen sensor array.",
                            "Conduct manual Dräger chemical gas tube verification test.",
                            "Confirm cabin partial pressure of O2 stabilizes between 20.0 - 21.5 kPa."
                    )
            ),
            new ScenarioDefinition(
                    "EMG-CABIN-DEPRESS",
                    "Cabin Pressure Abnormality (Micrometeorite Hull Puncture)",
                    5,
                    "Pressure Hull Structure, Module 2B Isolation Hatch, Barometric Sensors",
                    "Hypervelocity micro-meteoroid impact on Module 2B outer multi-layer insulation causing a 4mm pinhole breach and rapid cabin pressure dump.",
                    List.of(
                            "Immediate sounding of General Quarters Depressurization Klaxon.",
                            "Don Sokol/Orlan intravehicular pressure suits and latch visors.",
                            "Isolate Module 2B by closing quick-disconnect emergency bulkhead hatch.",
                            "Deploy ultrasonic acoustic leak detector to locate hull breach coordinates.",
                            "Apply composite elastomer vacuum-curing epoxy pressure patch.",
                            "Equalize pressure before re-opening transit hatch."
                    )
            ),
            new ScenarioDefinition(
                    "EMG-THERMAL-RUNAWAY",
                    "Thermal Control System (TCS) Overheat & Coolant Cascade",
                    4,
                    "Freon/Ammonia Heat Exchangers, External Radiator Deployer, Avionics Bay",
                    "External radiator loop pump cavitating during perihelion solar heating phase; avionics bay temperature rising by +2.8°C/minute.",
                    List.of(
                            "Power down non-critical scientific payloads and secondary transmitters.",
                            "Engage secondary auxiliary coolant pump loop B.",
                            "Orient spacecraft attitude to shade primary radiator panels from direct solar flux.",
                            "Purge gas bubbles from heat pipe ammonia loops.",
                            "Verify avionics cold-plate temperatures stabilize below +38°C."
                    )
            ),
            new ScenarioDefinition(
                    "EMG-BATTERY-COLLAPSE",
                    "EPS Main Bus Short-Circuit & Lithium-Ion Cell Cascade",
                    5,
                    "Main Electrical Power Bus 1A, Lithium-Ion Battery Array 3, DC-DC Converters",
                    "Thermal runaway inside Battery Module 3 cell bank causing main DC bus voltage to collapse from 28V down to 14.2V.",
                    List.of(
                            "Isolate faulted Battery Module 3 via pyrotechnic bus circuit breaker.",
                            "Shed non-essential loads (laboratory racks, observation lights, high-gain gimbal).",
                            "Cross-tie Essential Avionics Bus to RTG / Backup Fuel Cell reserve.",
                            "Monitor thermal sensors on adjacent battery banks to prevent thermal runaway propagation.",
                            "Rebalance electrical draw to within secondary battery bank discharge limits."
                    )
            ),
            new ScenarioDefinition(
                    "EMG-COMM-BLACKOUT",
                    "High-Gain Antenna Gimbal Stall & Ground Blackout",
                    3,
                    "2.4m High-Gain Parabolic Dish, Dual-Axis Az/El Gimbal, Deep Space Transponder",
                    "Azimuth drive motor mechanical freeze during scheduled Deep Space Network Goldstone acquisition pass, causing loss of telecommand link.",
                    List.of(
                            "Spacecraft automatically triggers autonomous Safe-Hold Conscan mode.",
                            "Switch communication routing to omni-directional low-gain S-band antenna.",
                            "Transmit emergency beacon packet containing inertial attitude telemetry.",
                            "Execute thermal pulse command to unstuck jammed drive motor bearings.",
                            "Re-acquire phased-lock loop on Madrid DSN ground station carrier."
                    )
            )
    );

    @GetMapping("/scenarios")
    public ResponseEntity<List<ScenarioDefinition>> getScenarios() {
        return ResponseEntity.ok(SCENARIOS);
    }

    public static class TriggerRequest {
        public String scenarioCode;
        public String notes;
    }

    @PostMapping("/trigger")
    public ResponseEntity<?> triggerEmergency(@RequestBody TriggerRequest req, Authentication auth) {
        ScenarioDefinition scenario = SCENARIOS.stream()
                .filter(s -> s.code.equalsIgnoreCase(req.scenarioCode))
                .findFirst()
                .orElse(SCENARIOS.get(0));

        String callsign = (auth != null && auth.getName() != null) ? auth.getName() : "AERO_SPEC_4";

        EmergencyIncident incident = new EmergencyIncident(
                scenario.code,
                scenario.title,
                scenario.severity,
                scenario.affectedSystems,
                callsign,
                (req.notes != null && !req.notes.isBlank()) ? req.notes : "Educational simulation initiated from Emergency Simulator console."
        );

        emergencyRepository.save(incident);

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Educational Emergency Scenario Triggered: " + scenario.title);
        resp.put("incident", incident);
        resp.put("checklist", scenario.checklist);
        resp.put("educationalDisclaimer", "EDUCATIONAL SPACECRAFT SIMULATION ONLY - NOT CERTIFIED FLIGHT PROCEDURES");

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/incidents")
    public ResponseEntity<List<EmergencyIncident>> getAllIncidents() {
        return ResponseEntity.ok(emergencyRepository.findAllByOrderByTriggeredAtDesc());
    }

    public static class ChecklistUpdateRequest {
        public int progress;
    }

    @PostMapping("/incidents/{id}/checklist")
    public ResponseEntity<?> updateChecklist(@PathVariable Long id, @RequestBody ChecklistUpdateRequest req) {
        EmergencyIncident incident = emergencyRepository.findById(id).orElse(null);
        if (incident == null) return ResponseEntity.notFound().build();

        incident.setChecklistProgress(Math.min(100, Math.max(0, req.progress)));
        if (incident.getChecklistProgress() >= 100) {
            incident.setStatus("RESOLVED");
            incident.setResolvedAt(LocalDateTime.now());
        } else if (incident.getChecklistProgress() > 0) {
            incident.setStatus("MITIGATED");
        }
        emergencyRepository.save(incident);

        return ResponseEntity.ok(incident);
    }

    @PostMapping("/incidents/{id}/resolve")
    public ResponseEntity<?> resolveIncident(@PathVariable Long id) {
        EmergencyIncident incident = emergencyRepository.findById(id).orElse(null);
        if (incident == null) return ResponseEntity.notFound().build();

        incident.setStatus("RESOLVED");
        incident.setChecklistProgress(100);
        incident.setResolvedAt(LocalDateTime.now());
        emergencyRepository.save(incident);

        return ResponseEntity.ok(Map.of("message", "Emergency incident marked as resolved.", "incident", incident));
    }
}
