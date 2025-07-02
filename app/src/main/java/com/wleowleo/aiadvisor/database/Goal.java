package com.wleowleo.aiadvisor.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "goals")
public class Goal {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String title;
    private double targetAmount;
    private double currentAmount;
    private String type; // "monthly", "yearly", "custom"
    private long startDate;
    private long endDate;
    private boolean isActive;
    
    public Goal() {}
    
    @Ignore
    public Goal(String title, double targetAmount, String type, long startDate, long endDate) {
        this.title = title;
        this.targetAmount = targetAmount;
        this.currentAmount = 0.0;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    
    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public long getStartDate() { return startDate; }
    public void setStartDate(long startDate) { this.startDate = startDate; }
    
    public long getEndDate() { return endDate; }
    public void setEndDate(long endDate) { this.endDate = endDate; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public double getProgressPercentage() {
        if (targetAmount == 0) return 0;
        return Math.min((currentAmount / targetAmount) * 100, 100);
    }
}
