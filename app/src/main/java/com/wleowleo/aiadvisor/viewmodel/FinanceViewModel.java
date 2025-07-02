package com.wleowleo.aiadvisor.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.wleowleo.aiadvisor.database.Goal;
import com.wleowleo.aiadvisor.database.Transaction;
import com.wleowleo.aiadvisor.repository.FinanceRepository;
import java.util.List;

public class FinanceViewModel extends AndroidViewModel {
    private FinanceRepository repository;
    private LiveData<List<Transaction>> allTransactions;
    private LiveData<List<Goal>> activeGoals;
    private LiveData<Double> totalIncome;
    private LiveData<Double> totalOutcome;
    
    public FinanceViewModel(@NonNull Application application) {
        super(application);
        repository = new FinanceRepository(application);
        allTransactions = repository.getAllTransactions();
        activeGoals = repository.getActiveGoals();
        totalIncome = repository.getTotalIncome();
        totalOutcome = repository.getTotalOutcome();
    }
    
    // Transaction methods
    public void insert(Transaction transaction) {
        repository.insert(transaction);
    }
    
    public void update(Transaction transaction) {
        repository.update(transaction);
    }
    
    public void delete(Transaction transaction) {
        repository.delete(transaction);
    }
    
    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }
    
    public LiveData<List<Transaction>> getTransactionsByType(String type) {
        return repository.getTransactionsByType(type);
    }
    
    public LiveData<Double> getTotalIncome() {
        return totalIncome;
    }
    
    public LiveData<Double> getTotalOutcome() {
        return totalOutcome;
    }
    
    public LiveData<Double> getIncomeInPeriod(long startTime, long endTime) {
        return repository.getIncomeInPeriod(startTime, endTime);
    }
    
    public LiveData<Double> getOutcomeInPeriod(long startTime, long endTime) {
        return repository.getOutcomeInPeriod(startTime, endTime);
    }
    
    // Goal methods
    public void insert(Goal goal) {
        repository.insert(goal);
    }
    
    public void update(Goal goal) {
        repository.update(goal);
    }
    
    public void delete(Goal goal) {
        repository.delete(goal);
    }
    
    public LiveData<List<Goal>> getActiveGoals() {
        return activeGoals;
    }
    
    public LiveData<List<Goal>> getAllGoals() {
        return repository.getAllGoals();
    }
    
    public LiveData<Goal> getGoalById(int goalId) {
        return repository.getGoalById(goalId);
    }
}
