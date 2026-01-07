package com.learnmax.kpi.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single KPI data point for performance tracking
 */
public class KpiDataPoint {
    
    public enum EntityType {
        STUDENT, TEACHER, SCHOOL, PLATFORM
    }
    
    public enum KpiCategory {
        ASSESSMENT, LAB_ACTIVITY, ATTENDANCE, ENGAGEMENT, IMPROVEMENT
    }
    
    private String kpiId;
    private String entityId;
    private EntityType entityType;
    private KpiCategory category;
    private double value;
    private LocalDateTime timestamp;
    private Map<String, String> metadata;
    
    public KpiDataPoint() {
        this.metadata = new HashMap<>();
        this.timestamp = LocalDateTime.now();
    }
    
    public KpiDataPoint(String kpiId, String entityId, EntityType entityType, 
                        KpiCategory category, double value) {
        this();
        this.kpiId = kpiId;
        this.entityId = entityId;
        this.entityType = entityType;
        this.category = category;
        this.value = value;
    }
    
    // Getters and Setters
    
    public String getKpiId() {
        return kpiId;
    }
    
    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }
    
    public String getEntityId() {
        return entityId;
    }
    
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
    
    public EntityType getEntityType() {
        return entityType;
    }
    
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
    
    public KpiCategory getCategory() {
        return category;
    }
    
    public void setCategory(KpiCategory category) {
        this.category = category;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = value;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
    
    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }
    
    public String getMetadata(String key) {
        return this.metadata.get(key);
    }
    
    @Override
    public String toString() {
        return String.format("KPI[%s] %s=%s %.2f at %s", 
            category, entityType, entityId, value, timestamp);
    }
}
