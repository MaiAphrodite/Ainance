package com.wleowleo.aiadvisor.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import com.wleowleo.aiadvisor.R;
import com.wleowleo.aiadvisor.database.Goal;
import com.wleowleo.aiadvisor.utils.FormatUtils;

public class GoalPaymentDialog extends Dialog {
    
    private TextView tvGoalInfo;
    private TextView tvBalanceInfo;
    private TextInputEditText etPaymentAmount;
    private Button btnCancel;
    private Button btnPay;
    
    private Goal goal;
    private String currencySymbol;
    private double currentBalance;
    private OnPaymentMadeListener listener;
    
    public interface OnPaymentMadeListener {
        void onPaymentMade(Goal goal, double amount);
    }
    
    public GoalPaymentDialog(@NonNull Context context, Goal goal, String currencySymbol, 
                            double currentBalance, OnPaymentMadeListener listener) {
        super(context);
        this.goal = goal;
        this.currencySymbol = currencySymbol;
        this.currentBalance = currentBalance;
        this.listener = listener;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_goal_payment);
        
        // Set dialog width to be more appropriate
        if (getWindow() != null) {
            android.view.WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.85);
            getWindow().setAttributes(params);
        }
        
        initViews();
        setupGoalInfo();
        setupClickListeners();
    }
    
    private void initViews() {
        tvGoalInfo = findViewById(R.id.tv_goal_info);
        tvBalanceInfo = findViewById(R.id.tv_balance_info);
        etPaymentAmount = findViewById(R.id.et_payment_amount);
        btnCancel = findViewById(R.id.btn_cancel);
        btnPay = findViewById(R.id.btn_pay);
    }
    
    private void setupGoalInfo() {
        String goalInfo = goal.getTitle() + "\n" +
                "Progress: " + FormatUtils.formatCurrency(goal.getCurrentAmount(), currencySymbol) +
                " / " + FormatUtils.formatCurrency(goal.getTargetAmount(), currencySymbol) +
                " (" + String.format("%.1f%%", goal.getProgressPercentage()) + ")";
        tvGoalInfo.setText(goalInfo);
        
        // Show current balance info
        String balanceInfo = "Your current balance: " + FormatUtils.formatCurrency(currentBalance, currencySymbol);
        tvBalanceInfo.setText(balanceInfo);
        tvBalanceInfo.setVisibility(android.view.View.VISIBLE);
    }
    
    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> dismiss());
        
        btnPay.setOnClickListener(v -> {
            if (validateAndMakePayment()) {
                dismiss();
            }
        });
    }
    
    private boolean validateAndMakePayment() {
        String amountStr = etPaymentAmount.getText().toString().trim();
        
        if (amountStr.isEmpty()) {
            etPaymentAmount.setError("Payment amount is required");
            return false;
        }
        
        try {
            double paymentAmount = Double.parseDouble(amountStr);
            
            if (paymentAmount <= 0) {
                etPaymentAmount.setError("Payment amount must be greater than 0");
                return false;
            }
            
            // Check if user has sufficient balance
            if (paymentAmount > currentBalance) {
                etPaymentAmount.setError("Insufficient balance. Available: " + 
                    FormatUtils.formatCurrency(currentBalance, currencySymbol));
                return false;
            }
            
            // Goals can exceed target amount - users can save more than their goal
            
            // Make the payment
            if (listener != null) {
                listener.onPaymentMade(goal, paymentAmount);
            }
            
            Toast.makeText(getContext(), 
                "Payment of " + FormatUtils.formatCurrency(paymentAmount, currencySymbol) + 
                " added to " + goal.getTitle(), Toast.LENGTH_SHORT).show();
            
            return true;
            
        } catch (NumberFormatException e) {
            etPaymentAmount.setError("Invalid payment amount");
            return false;
        }
    }
}
