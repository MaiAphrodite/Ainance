package com.wleowleo.aiadvisor.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.wleowleo.aiadvisor.database.AppDatabase;
import com.wleowleo.aiadvisor.database.Goal;
import com.wleowleo.aiadvisor.database.GoalDao;
import com.wleowleo.aiadvisor.database.Transaction;
import com.wleowleo.aiadvisor.database.TransactionDao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinanceRepository {
    private TransactionDao transactionDao;
    private GoalDao goalDao;
    private LiveData<List<Transaction>> allTransactions;
    private LiveData<List<Goal>> activeGoals;
    private ExecutorService executor;
    
    public FinanceRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        transactionDao = database.transactionDao();
        goalDao = database.goalDao();
        allTransactions = transactionDao.getAllTransactions();
        activeGoals = goalDao.getActiveGoals();
        executor = Executors.newFixedThreadPool(2);
    }
    
    // Transaction methods
    public void insert(Transaction transaction) {
        executor.execute(() -> transactionDao.insert(transaction));
    }
    
    public void update(Transaction transaction) {
        executor.execute(() -> transactionDao.update(transaction));
    }
    
    public void delete(Transaction transaction) {
        executor.execute(() -> transactionDao.delete(transaction));
    }
    
    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }
    
    public LiveData<List<Transaction>> getTransactionsByType(String type) {
        return transactionDao.getTransactionsByType(type);
    }
    
    public LiveData<Double> getTotalIncome() {
        return transactionDao.getTotalIncome();
    }
    
    public LiveData<Double> getTotalOutcome() {
        return transactionDao.getTotalOutcome();
    }
    
    public LiveData<Double> getIncomeInPeriod(long startTime, long endTime) {
        return transactionDao.getIncomeInPeriod(startTime, endTime);
    }
    
    public LiveData<Double> getOutcomeInPeriod(long startTime, long endTime) {
        return transactionDao.getOutcomeInPeriod(startTime, endTime);
    }
    
    // Goal methods
    public void insert(Goal goal) {
        executor.execute(() -> goalDao.insert(goal));
    }
    
    public void update(Goal goal) {
        executor.execute(() -> goalDao.update(goal));
    }
    
    public void delete(Goal goal) {
        executor.execute(() -> goalDao.delete(goal));
    }
    
    public LiveData<List<Goal>> getActiveGoals() {
        return activeGoals;
    }
    
    public LiveData<List<Goal>> getAllGoals() {
        return goalDao.getAllGoals();
    }
    
    public LiveData<Goal> getGoalById(int goalId) {
        return goalDao.getGoalById(goalId);
    }
}
