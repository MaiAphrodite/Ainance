package com.wleowleo.aiadvisor.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private double amount;
    private String description;
    private String category;
    private String type; // "income" or "outcome"
    private long timestamp;
    
    public Transaction() {}
    
    @Ignore
    public Transaction(double amount, String description, String category, String type, long timestamp) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.type = type;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
