package com.wleowleo.aiadvisor.utils;

import com.wleowleo.aiadvisor.database.Goal;
import com.wleowleo.aiadvisor.database.Transaction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class SampleDataGenerator {
    private static final Random random = new Random();
    
    public static List<Transaction> generateSampleTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        
        String[] incomeCategories = FormatUtils.getIncomeCategories();
        String[] outcomeCategories = FormatUtils.getOutcomeCategories();
        
        Calendar cal = Calendar.getInstance();
        
        // Generate 20 sample transactions
        for (int i = 0; i < 20; i++) {
            cal.add(Calendar.DAY_OF_YEAR, -random.nextInt(30)); // Random day in last 30 days
            
            boolean isIncome = random.nextBoolean();
            String type = isIncome ? "income" : "outcome";
            String[] categories = isIncome ? incomeCategories : outcomeCategories;
            String category = categories[random.nextInt(categories.length)];
            
            double amount;
            String description;
            
            if (isIncome) {
                amount = 500000 + random.nextDouble() * 4500000; // 500k to 5M IDR
                description = generateIncomeDescription(category);
            } else {
                amount = 10000 + random.nextDouble() * 490000; // 10k to 500k IDR
                description = generateOutcomeDescription(category);
            }
            
            Transaction transaction = new Transaction(
                    amount,
                    description,
                    category,
                    type,
                    cal.getTimeInMillis()
            );
            
            transactions.add(transaction);
        }
        
        return transactions;
    }
    
    public static List<Goal> generateSampleGoals() {
        List<Goal> goals = new ArrayList<>();
        
        Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        
        // Monthly savings goal
        Goal savingsGoal = new Goal(
                "Monthly Savings",
                2000000.0, // 2M IDR
                "monthly",
                now,
                now + (30L * 24 * 60 * 60 * 1000) // 30 days
        );
        savingsGoal.setCurrentAmount(1300000.0); // 65% progress
        
        // Emergency fund goal
        Goal emergencyGoal = new Goal(
                "Emergency Fund",
                10000000.0, // 10M IDR
                "yearly",
                now,
                now + (365L * 24 * 60 * 60 * 1000) // 1 year
        );
        emergencyGoal.setCurrentAmount(2500000.0); // 25% progress
        
        // Vacation goal
        Goal vacationGoal = new Goal(
                "Vacation Fund",
                5000000.0, // 5M IDR
                "custom",
                now,
                now + (180L * 24 * 60 * 60 * 1000) // 6 months
        );
        vacationGoal.setCurrentAmount(1200000.0); // 24% progress
        
        goals.add(savingsGoal);
        goals.add(emergencyGoal);
        goals.add(vacationGoal);
        
        return goals;
    }
    
    private static String generateIncomeDescription(String category) {
        switch (category) {
            case "Salary":
                return "Monthly salary";
            case "Freelance":
                return "Freelance project payment";
            case "Business":
                return "Business revenue";
            case "Investment":
                return "Investment return";
            case "Gift":
                return "Gift money";
            case "Bonus":
                return "Performance bonus";
            default:
                return "Other income";
        }
    }
    
    private static String generateOutcomeDescription(String category) {
        switch (category) {
            case "Food & Dining":
                return random.nextBoolean() ? "Restaurant dinner" : "Grocery shopping";
            case "Transportation":
                return random.nextBoolean() ? "Uber ride" : "Gas refill";
            case "Shopping":
                return random.nextBoolean() ? "Clothes shopping" : "Online purchase";
            case "Entertainment":
                return random.nextBoolean() ? "Movie tickets" : "Concert";
            case "Bills & Utilities":
                return random.nextBoolean() ? "Electricity bill" : "Internet bill";
            case "Healthcare":
                return random.nextBoolean() ? "Doctor visit" : "Medicine";
            case "Education":
                return random.nextBoolean() ? "Course fee" : "Books";
            case "Travel":
                return random.nextBoolean() ? "Flight ticket" : "Hotel";
            case "Insurance":
                return "Insurance premium";
            case "Investment":
                return "Stock purchase";
            default:
                return "Other expense";
        }
    }
}
