package org.deepspace.dsis.controller;

import org.deepspace.dsis.service.CelesTrakSatelliteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/satellite")
public class SatelliteController {

    private final CelesTrakSatelliteService satelliteService;

    public SatelliteController(CelesTrakSatelliteService satelliteService) {
        this.satelliteService = satelliteService;
    }

    @GetMapping("/tle")
    public ResponseEntity<List<CelesTrakSatelliteService.SatelliteTleData>> getAllSatellites() {
        return ResponseEntity.ok(satelliteService.getAllSatellites());
    }

    @GetMapping("/tle/{noradId}")
    public ResponseEntity<?> getSatellite(@PathVariable String noradId) {
        CelesTrakSatelliteService.SatelliteTleData sat = satelliteService.getSatelliteByNoradId(noradId);
        if (sat == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(sat);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CelesTrakSatelliteService.SatelliteTleData>> search(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(satelliteService.searchSatellites(q));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh() {
        satelliteService.triggerRefresh();
        return ResponseEntity.ok(Map.of("message", "Orbital TLE cache refresh completed.", "status", satelliteService.getStatus()));
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        return ResponseEntity.ok(satelliteService.getStatus());
    }
}
