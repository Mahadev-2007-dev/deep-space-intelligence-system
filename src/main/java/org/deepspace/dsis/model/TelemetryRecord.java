package org.deepspace.dsis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry_records")
public class TelemetryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long spacecraftId;
    private String spacecraftName;
    private LocalDateTime timestamp;

    private double temperature;
    private double cabinPressure;
    private double oxygenLevel;
    private double batteryLevel;
    private double snrDb;
    private String condition;

    public TelemetryRecord() {
        this.timestamp = LocalDateTime.now();
    }

    public TelemetryRecord(Long spacecraftId, String spacecraftName, double temperature, double cabinPressure, double oxygenLevel, double batteryLevel, double snrDb, String condition) {
        this.spacecraftId = spacecraftId;
        this.spacecraftName = spacecraftName;
        this.timestamp = LocalDateTime.now();
        this.temperature = temperature;
        this.cabinPressure = cabinPressure;
        this.oxygenLevel = oxygenLevel;
        this.batteryLevel = batteryLevel;
        this.snrDb = snrDb;
        this.condition = condition;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSpacecraftId() { return spacecraftId; }
    public void setSpacecraftId(Long spacecraftId) { this.spacecraftId = spacecraftId; }

    public String getSpacecraftName() { return spacecraftName; }
    public void setSpacecraftName(String spacecraftName) { this.spacecraftName = spacecraftName; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getCabinPressure() { return cabinPressure; }
    public void setCabinPressure(double cabinPressure) { this.cabinPressure = cabinPressure; }

    public double getOxygenLevel() { return oxygenLevel; }
    public void setOxygenLevel(double oxygenLevel) { this.oxygenLevel = oxygenLevel; }

    public double getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(double batteryLevel) { this.batteryLevel = batteryLevel; }

    public double getSnrDb() { return snrDb; }
    public void setSnrDb(double snrDb) { this.snrDb = snrDb; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}
