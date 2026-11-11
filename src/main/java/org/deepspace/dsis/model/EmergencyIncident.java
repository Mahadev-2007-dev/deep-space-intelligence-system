package org.deepspace.dsis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_incidents")
public class EmergencyIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scenarioCode;
    private String scenarioName;
    private int severityLevel; // 1 to 5
    private String affectedSystems;
    private String status; // ACTIVE, MITIGATED, RESOLVED
    private int checklistProgress; // 0 to 100%
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;
    private String operatorCallsign;

    @Column(length = 1000)
    private String notes;

    public EmergencyIncident() {
        this.status = "ACTIVE";
        this.checklistProgress = 0;
        this.triggeredAt = LocalDateTime.now();
    }

    public EmergencyIncident(String scenarioCode, String scenarioName, int severityLevel, String affectedSystems, String operatorCallsign, String notes) {
        this.scenarioCode = scenarioCode;
        this.scenarioName = scenarioName;
        this.severityLevel = severityLevel;
        this.affectedSystems = affectedSystems;
        this.operatorCallsign = operatorCallsign;
        this.notes = notes;
        this.status = "ACTIVE";
        this.checklistProgress = 0;
        this.triggeredAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getScenarioCode() { return scenarioCode; }
    public void setScenarioCode(String scenarioCode) { this.scenarioCode = scenarioCode; }

    public String getScenarioName() { return scenarioName; }
    public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }

    public int getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(int severityLevel) { this.severityLevel = severityLevel; }

    public String getAffectedSystems() { return affectedSystems; }
    public void setAffectedSystems(String affectedSystems) { this.affectedSystems = affectedSystems; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getChecklistProgress() { return checklistProgress; }
    public void setChecklistProgress(int checklistProgress) { this.checklistProgress = checklistProgress; }

    public LocalDateTime getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(LocalDateTime triggeredAt) { this.triggeredAt = triggeredAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getOperatorCallsign() { return operatorCallsign; }
    public void setOperatorCallsign(String operatorCallsign) { this.operatorCallsign = operatorCallsign; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
