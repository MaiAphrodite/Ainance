package com.wleowleo.aiadvisor.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
    private static final DecimalFormat currencyFormat = new DecimalFormat("#,###.##");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    
    public static String formatCurrency(double amount, String currencySymbol) {
        return currencySymbol + " " + currencyFormat.format(amount);
    }
    
    public static String formatDate(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }
    
    public static String formatTime(long timestamp) {
        return timeFormat.format(new Date(timestamp));
    }
    
    public static String formatDateTime(long timestamp) {
        return formatDate(timestamp) + " " + formatTime(timestamp);
    }
    
    public static String[] getIncomeCategories() {
        return new String[]{
            "Salary", "Freelance", "Business", "Investment", "Gift", "Bonus", "Other Income"
        };
    }
    
    public static String[] getOutcomeCategories() {
        return new String[]{
            "Food & Dining", "Transportation", "Shopping", "Entertainment", "Bills & Utilities", 
            "Healthcare", "Education", "Travel", "Insurance", "Investment", "Other Expense"
        };
    }
}
