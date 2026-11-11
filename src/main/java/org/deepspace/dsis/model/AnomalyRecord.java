package org.deepspace.dsis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anomaly_records")
public class AnomalyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long spacecraftId;
    private String spacecraftName;
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private String parameter; // TEMPERATURE, CABIN_PRESSURE, OXYGEN_LEVEL, BATTERY, COMMUNICATION
    private double measuredValue;
    private String expectedRange;
    private String explanation;
    private String status; // OPEN, ACKNOWLEDGED, RESOLVED
    private LocalDateTime timestamp;
    private String acknowledgedBy;
    private LocalDateTime acknowledgedAt;

    public AnomalyRecord() {
        this.status = "OPEN";
        this.timestamp = LocalDateTime.now();
    }

    public AnomalyRecord(Long spacecraftId, String spacecraftName, String severity, String parameter, double measuredValue, String expectedRange, String explanation) {
        this.spacecraftId = spacecraftId;
        this.spacecraftName = spacecraftName;
        this.severity = severity;
        this.parameter = parameter;
        this.measuredValue = measuredValue;
        this.expectedRange = expectedRange;
        this.explanation = explanation;
        this.status = "OPEN";
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSpacecraftId() { return spacecraftId; }
    public void setSpacecraftId(Long spacecraftId) { this.spacecraftId = spacecraftId; }

    public String getSpacecraftName() { return spacecraftName; }
    public void setSpacecraftName(String spacecraftName) { this.spacecraftName = spacecraftName; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getParameter() { return parameter; }
    public void setParameter(String parameter) { this.parameter = parameter; }

    public double getMeasuredValue() { return measuredValue; }
    public void setMeasuredValue(double measuredValue) { this.measuredValue = measuredValue; }

    public String getExpectedRange() { return expectedRange; }
    public void setExpectedRange(String expectedRange) { this.expectedRange = expectedRange; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getAcknowledgedBy() { return acknowledgedBy; }
    public void setAcknowledgedBy(String acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }

    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }
}
