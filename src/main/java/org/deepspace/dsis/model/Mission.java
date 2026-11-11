package org.deepspace.dsis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "missions")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String codeName;
    private String targetBody; // Mars, Europa, Titan, Luna, Deep Space
    private Long spacecraftId;
    private String spacecraftName;

    private String status; // PLANNED, ACTIVE, MONITORING, EMERGENCY, COMPLETED
    private String launchDate;
    private String arrivalDate;
    private int progressPercentage;

    @Column(length = 1000)
    private String objective;

    private String flightDirector;
    private String budgetClassification; // CLASS-A FLAGSHIP, CLASS-B DISCOVERY, CLASS-C PIONEER
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Mission() {
        this.status = "PLANNED";
        this.progressPercentage = 0;
        this.priority = "MEDIUM";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Mission(String name, String codeName, String targetBody, Long spacecraftId, String spacecraftName, String status, String launchDate, String arrivalDate, int progress, String objective, String flightDirector, String budgetClassification, String priority) {
        this.name = name;
        this.codeName = codeName;
        this.targetBody = targetBody;
        this.spacecraftId = spacecraftId;
        this.spacecraftName = spacecraftName;
        this.status = status;
        this.launchDate = launchDate;
        this.arrivalDate = arrivalDate;
        this.progressPercentage = progress;
        this.objective = objective;
        this.flightDirector = flightDirector;
        this.budgetClassification = budgetClassification;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCodeName() { return codeName; }
    public void setCodeName(String codeName) { this.codeName = codeName; }

    public String getTargetBody() { return targetBody; }
    public void setTargetBody(String targetBody) { this.targetBody = targetBody; }

    public Long getSpacecraftId() { return spacecraftId; }
    public void setSpacecraftId(Long spacecraftId) { this.spacecraftId = spacecraftId; }

    public String getSpacecraftName() { return spacecraftName; }
    public void setSpacecraftName(String spacecraftName) { this.spacecraftName = spacecraftName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLaunchDate() { return launchDate; }
    public void setLaunchDate(String launchDate) { this.launchDate = launchDate; }

    public String getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(String arrivalDate) { this.arrivalDate = arrivalDate; }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }

    public String getFlightDirector() { return flightDirector; }
    public void setFlightDirector(String flightDirector) { this.flightDirector = flightDirector; }

    public String getBudgetClassification() { return budgetClassification; }
    public void setBudgetClassification(String budgetClassification) { this.budgetClassification = budgetClassification; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
