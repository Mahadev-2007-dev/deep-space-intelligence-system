package org.deepspace.dsis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scan_records")
public class ScanRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String notes;

    private String fileName;
    private int objectsDetectedCount;
    private double maxIntensity;
    private double meanIntensity;
    private String status; // PROCESSED, REVIEW_PENDING, ARCHIVED
    private LocalDateTime timestamp;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String originalImageBase64;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String processedImageBase64;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String detectionsJson; // JSON array of detected bounding boxes and properties

    public ScanRecord() {
        this.status = "PROCESSED";
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public int getObjectsDetectedCount() { return objectsDetectedCount; }
    public void setObjectsDetectedCount(int objectsDetectedCount) { this.objectsDetectedCount = objectsDetectedCount; }

    public double getMaxIntensity() { return maxIntensity; }
    public void setMaxIntensity(double maxIntensity) { this.maxIntensity = maxIntensity; }

    public double getMeanIntensity() { return meanIntensity; }
    public void setMeanIntensity(double meanIntensity) { this.meanIntensity = meanIntensity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getOriginalImageBase64() { return originalImageBase64; }
    public void setOriginalImageBase64(String originalImageBase64) { this.originalImageBase64 = originalImageBase64; }

    public String getProcessedImageBase64() { return processedImageBase64; }
    public void setProcessedImageBase64(String processedImageBase64) { this.processedImageBase64 = processedImageBase64; }

    public String getDetectionsJson() { return detectionsJson; }
    public void setDetectionsJson(String detectionsJson) { this.detectionsJson = detectionsJson; }
}
