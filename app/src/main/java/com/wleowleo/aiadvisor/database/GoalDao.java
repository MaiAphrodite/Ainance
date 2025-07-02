package com.wleowleo.aiadvisor.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface GoalDao {
    @Insert
    void insert(Goal goal);
    
    @Update
    void update(Goal goal);
    
    @Delete
    void delete(Goal goal);
    
    @Query("SELECT * FROM goals WHERE isActive = 1 ORDER BY endDate ASC")
    LiveData<List<Goal>> getActiveGoals();
    
    @Query("SELECT * FROM goals ORDER BY endDate ASC")
    LiveData<List<Goal>> getAllGoals();
    
    @Query("SELECT * FROM goals WHERE id = :goalId")
    LiveData<Goal> getGoalById(int goalId);
    
    @Query("SELECT * FROM goals WHERE type = :type AND isActive = 1")
    LiveData<List<Goal>> getActiveGoalsByType(String type);
    
    @Query("DELETE FROM goals")
    void deleteAll();
}
