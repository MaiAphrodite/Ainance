package com.wleowleo.aiadvisor.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);
    
    @Update
    void update(Transaction transaction);
    
    @Delete
    void delete(Transaction transaction);
    
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    LiveData<List<Transaction>> getAllTransactions();
    
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY timestamp DESC")
    LiveData<List<Transaction>> getTransactionsByType(String type);
    
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'income'")
    LiveData<Double> getTotalIncome();
    
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'outcome'")
    LiveData<Double> getTotalOutcome();
    
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'income' AND timestamp >= :startTime AND timestamp <= :endTime")
    LiveData<Double> getIncomeInPeriod(long startTime, long endTime);
    
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'outcome' AND timestamp >= :startTime AND timestamp <= :endTime")
    LiveData<Double> getOutcomeInPeriod(long startTime, long endTime);
    
    @Query("SELECT * FROM transactions WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    LiveData<List<Transaction>> getTransactionsInPeriod(long startTime, long endTime);
    
    @Query("DELETE FROM transactions")
    void deleteAll();
}
