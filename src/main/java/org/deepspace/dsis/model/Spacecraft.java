package org.deepspace.dsis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "spacecraft")
public class Spacecraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String code;
    private String target; // Mars, Europa, Titan, Deep Space / Interstellar
    private String status; // NOMINAL, DEGRADED, ANOMALOUS, CRITICAL
    private String launchDate;
    private int missionElapsedDays;

    // Telemetry Sensor Parameters (Simulated)
    private double temperature; // Celsius
    private double cabinPressure; // kPa
    private double oxygenLevel; // %
    private double batteryLevel; // %
    private double solarArrayOutput; // Watts
    private String communicationStatus; // OPTIMAL, DEGRADED, MARGINAL, LOST
    private double snrDb; // dB
    private long uplinkLatencyMs; // Milliseconds
    private double downlinkThroughputKbps; // kbps

    private boolean simulationActive;
    private boolean anomalyTriggered;
    private String activeAnomalyType; // NONE, CRYOGENIC_OVERHEAT, PRESSURE_DROP, BATTERY_DRAIN, COMM_LOSS

    private LocalDateTime lastTelemetryUpdate;

    public Spacecraft() {
        this.simulationActive = true;
        this.status = "NOMINAL";
        this.lastTelemetryUpdate = LocalDateTime.now();
    }

    public Spacecraft(String name, String code, String target, double temp, double pressure, double o2, double battery) {
        this.name = name;
        this.code = code;
        this.target = target;
        this.temperature = temp;
        this.cabinPressure = pressure;
        this.oxygenLevel = o2;
        this.batteryLevel = battery;
        this.solarArrayOutput = 1250.0;
        this.communicationStatus = "OPTIMAL";
        this.snrDb = 28.4;
        this.uplinkLatencyMs = 450;
        this.downlinkThroughputKbps = 1024.0;
        this.status = "NOMINAL";
        this.simulationActive = true;
        this.anomalyTriggered = false;
        this.activeAnomalyType = "NONE";
        this.lastTelemetryUpdate = LocalDateTime.now();
        this.launchDate = "2024-03-15";
        this.missionElapsedDays = 420;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLaunchDate() { return launchDate; }
    public void setLaunchDate(String launchDate) { this.launchDate = launchDate; }

    public int getMissionElapsedDays() { return missionElapsedDays; }
    public void setMissionElapsedDays(int missionElapsedDays) { this.missionElapsedDays = missionElapsedDays; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getCabinPressure() { return cabinPressure; }
    public void setCabinPressure(double cabinPressure) { this.cabinPressure = cabinPressure; }

    public double getOxygenLevel() { return oxygenLevel; }
    public void setOxygenLevel(double oxygenLevel) { this.oxygenLevel = oxygenLevel; }

    public double getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(double batteryLevel) { this.batteryLevel = batteryLevel; }

    public double getSolarArrayOutput() { return solarArrayOutput; }
    public void setSolarArrayOutput(double solarArrayOutput) { this.solarArrayOutput = solarArrayOutput; }

    public String getCommunicationStatus() { return communicationStatus; }
    public void setCommunicationStatus(String communicationStatus) { this.communicationStatus = communicationStatus; }

    public double getSnrDb() { return snrDb; }
    public void setSnrDb(double snrDb) { this.snrDb = snrDb; }

    public long getUplinkLatencyMs() { return uplinkLatencyMs; }
    public void setUplinkLatencyMs(long uplinkLatencyMs) { this.uplinkLatencyMs = uplinkLatencyMs; }

    public double getDownlinkThroughputKbps() { return downlinkThroughputKbps; }
    public void setDownlinkThroughputKbps(double downlinkThroughputKbps) { this.downlinkThroughputKbps = downlinkThroughputKbps; }

    public boolean isSimulationActive() { return simulationActive; }
    public void setSimulationActive(boolean simulationActive) { this.simulationActive = simulationActive; }

    public boolean isAnomalyTriggered() { return anomalyTriggered; }
    public void setAnomalyTriggered(boolean anomalyTriggered) { this.anomalyTriggered = anomalyTriggered; }

    public String getActiveAnomalyType() { return activeAnomalyType; }
    public void setActiveAnomalyType(String activeAnomalyType) { this.activeAnomalyType = activeAnomalyType; }

    public LocalDateTime getLastTelemetryUpdate() { return lastTelemetryUpdate; }
    public void setLastTelemetryUpdate(LocalDateTime lastTelemetryUpdate) { this.lastTelemetryUpdate = lastTelemetryUpdate; }
}
