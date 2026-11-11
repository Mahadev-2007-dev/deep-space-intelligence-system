package org.deepspace.dsis.controller;

import org.deepspace.dsis.model.Mission;
import org.deepspace.dsis.repository.MissionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/missions")
public class MissionController {

    private final MissionRepository missionRepository;

    public MissionController(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Mission>> getAllMissions() {
        return ResponseEntity.ok(missionRepository.findAllByOrderByCreatedAtDesc());
    }

    @PostMapping
    public ResponseEntity<?> createMission(@RequestBody Mission mission) {
        if (mission.getName() == null || mission.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mission name is required."));
        }
        mission.setCreatedAt(LocalDateTime.now());
        mission.setUpdatedAt(LocalDateTime.now());
        if (mission.getStatus() == null) mission.setStatus("PLANNED");
        Mission saved = missionRepository.save(mission);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMission(@PathVariable Long id, @RequestBody Mission updated) {
        Mission existing = missionRepository.findById(id).orElse(null);
        if (existing == null) return ResponseEntity.notFound().build();

        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getCodeName() != null) existing.setCodeName(updated.getCodeName());
        if (updated.getTargetBody() != null) existing.setTargetBody(updated.getTargetBody());
        if (updated.getSpacecraftId() != null) existing.setSpacecraftId(updated.getSpacecraftId());
        if (updated.getSpacecraftName() != null) existing.setSpacecraftName(updated.getSpacecraftName());
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
        if (updated.getLaunchDate() != null) existing.setLaunchDate(updated.getLaunchDate());
        if (updated.getArrivalDate() != null) existing.setArrivalDate(updated.getArrivalDate());
        existing.setProgressPercentage(updated.getProgressPercentage());
        if (updated.getObjective() != null) existing.setObjective(updated.getObjective());
        if (updated.getFlightDirector() != null) existing.setFlightDirector(updated.getFlightDirector());
        if (updated.getBudgetClassification() != null) existing.setBudgetClassification(updated.getBudgetClassification());
        if (updated.getPriority() != null) existing.setPriority(updated.getPriority());
        existing.setUpdatedAt(LocalDateTime.now());

        Mission saved = missionRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    public static class StatusUpdateRequest {
        public String status;
        public Integer progress;
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest req) {
        Mission existing = missionRepository.findById(id).orElse(null);
        if (existing == null) return ResponseEntity.notFound().build();

        if (req.status != null) existing.setStatus(req.status.toUpperCase());
        if (req.progress != null) existing.setProgressPercentage(Math.max(0, Math.min(100, req.progress)));
        existing.setUpdatedAt(LocalDateTime.now());

        Mission saved = missionRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMission(@PathVariable Long id) {
        if (!missionRepository.existsById(id)) return ResponseEntity.notFound().build();
        missionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Mission decommissioned successfully."));
    }
}
