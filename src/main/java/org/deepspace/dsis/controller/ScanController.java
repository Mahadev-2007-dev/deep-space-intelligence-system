package org.deepspace.dsis.controller;

import org.deepspace.dsis.cv.OpenCVService;
import org.deepspace.dsis.model.ScanRecord;
import org.deepspace.dsis.repository.ScanRecordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scans")
public class ScanController {

    private final OpenCVService openCVService;
    private final ScanRecordRepository scanRecordRepository;

    public ScanController(OpenCVService openCVService, ScanRecordRepository scanRecordRepository) {
        this.openCVService = openCVService;
        this.scanRecordRepository = scanRecordRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndScan(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "notes", required = false) String notes) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please select an astronomical image file to scan."));
        }

        try {
            ScanRecord record = openCVService.processAstronomicalImage(file, title, notes);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "message", "Computer Vision processing failed: " + e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<List<ScanRecord>> getAllScans() {
        return ResponseEntity.ok(scanRecordRepository.findAllByOrderByTimestampDesc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getScanById(@PathVariable Long id) {
        return scanRecordRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
