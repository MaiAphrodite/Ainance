package com.wleowleo.aiadvisor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.wleowleo.aiadvisor.activity.SettingsActivity;
import com.wleowleo.aiadvisor.adapter.GoalAdapter;
import com.wleowleo.aiadvisor.adapter.TransactionAdapter;
import com.wleowleo.aiadvisor.database.Goal;
import com.wleowleo.aiadvisor.database.Transaction;
import com.wleowleo.aiadvisor.dialog.AddGoalDialog;
import com.wleowleo.aiadvisor.dialog.GoalPaymentDialog;
import com.wleowleo.aiadvisor.dialog.AddGoalDialog;
import com.wleowleo.aiadvisor.dialog.AddTransactionDialog;
import com.wleowleo.aiadvisor.service.AIService;
import com.wleowleo.aiadvisor.utils.FormatUtils;
import com.wleowleo.aiadvisor.utils.MarkdownUtils;
import com.wleowleo.aiadvisor.utils.PreferencesManager;
import com.wleowleo.aiadvisor.viewmodel.FinanceViewModel;
import java.util.Calendar;
import java.util.List;

public class FirstFragment extends Fragment {
    
    private TextView tvBalance;
    private TextView tvIncome;
    private TextView tvOutcome;
    private TextView tvAiAdvice;
    private Button btnAddGoal;
    private Button btnAskAi;
    private RecyclerView rvGoals;
    private RecyclerView rvTransactions;
    
    private FinanceViewModel financeViewModel;
    private TransactionAdapter transactionAdapter;
    private GoalAdapter goalAdapter;
    private PreferencesManager preferencesManager;
    private AIService aiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        initViewModel();
        initAdapters();
        setupRecyclerViews();
        setupClickListeners();
        observeData();
        
        preferencesManager = new PreferencesManager(requireContext());
        aiService = new AIService();
    }
    
    private void initViews(View view) {
        tvBalance = view.findViewById(R.id.tv_balance);
        tvIncome = view.findViewById(R.id.tv_income);
        tvOutcome = view.findViewById(R.id.tv_outcome);
        tvAiAdvice = view.findViewById(R.id.tv_ai_advice);
        btnAddGoal = view.findViewById(R.id.btn_add_goal);
        btnAskAi = view.findViewById(R.id.btn_ask_ai);
        rvGoals = view.findViewById(R.id.rv_goals);
        rvTransactions = view.findViewById(R.id.rv_transactions);
        
        // Configure AI advice TextView for better handling of dynamic content
        tvAiAdvice.setMovementMethod(android.text.method.ScrollingMovementMethod.getInstance());
        tvAiAdvice.setVerticalScrollBarEnabled(true);
    }
    
    private void initViewModel() {
        financeViewModel = new ViewModelProvider(this).get(FinanceViewModel.class);
    }
    
    private void initAdapters() {
        transactionAdapter = new TransactionAdapter(new PreferencesManager(requireContext()));
        goalAdapter = new GoalAdapter(new PreferencesManager(requireContext()));
        
        // Set up goal payment listener
        goalAdapter.setOnGoalPaymentListener(goal -> showGoalPaymentDialog(goal));
    }
    
    private void setupRecyclerViews() {
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTransactions.setAdapter(transactionAdapter);
        
        rvGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGoals.setAdapter(goalAdapter);
    }
    
    private void setupClickListeners() {
        btnAddGoal.setOnClickListener(v -> showAddGoalDialog());
        btnAskAi.setOnClickListener(v -> askAIForAdvice());
    }
    
    private void observeData() {
        // Observe transactions
        financeViewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null) {
                // Show recent 5 transactions
                List<Transaction> recentTransactions = transactions.size() > 5 ? 
                        transactions.subList(0, 5) : transactions;
                transactionAdapter.setTransactions(recentTransactions);
                
                // Update goals progress based on transactions
                updateGoalsProgress(transactions);
            }
        });
        
        // Observe total income
        financeViewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            if (income != null) {
                String currencySymbol = preferencesManager.getCurrencySymbol();
                tvIncome.setText(FormatUtils.formatCurrency(income, currencySymbol));
                updateBalance();
            }
        });
        
        // Observe total outcome
        financeViewModel.getTotalOutcome().observe(getViewLifecycleOwner(), outcome -> {
            if (outcome != null) {
                String currencySymbol = preferencesManager.getCurrencySymbol();
                tvOutcome.setText(FormatUtils.formatCurrency(outcome, currencySymbol));
                updateBalance();
            }
        });
        
        // Observe goals
        financeViewModel.getActiveGoals().observe(getViewLifecycleOwner(), goals -> {
            if (goals != null) {
                goalAdapter.setGoals(goals);
            }
        });
    }
    
    private void updateBalance() {
        financeViewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            financeViewModel.getTotalOutcome().observe(getViewLifecycleOwner(), outcome -> {
                if (income != null && outcome != null) {
                    double balance = income - outcome;
                    String currencySymbol = preferencesManager.getCurrencySymbol();
                    tvBalance.setText(FormatUtils.formatCurrency(balance, currencySymbol));
                }
            });
        });
    }
    
    private void updateGoalsProgress(List<Transaction> transactions) {
        // Goals progress is now only updated through explicit payments via GoalPaymentDialog
        // This method is kept for potential future goal-related calculations
        // but no longer automatically assigns income to goal progress
    }
    
    private void showAddGoalDialog() {
        AddGoalDialog dialog = new AddGoalDialog(requireContext(), goal -> {
            financeViewModel.insert(goal);
        });
        dialog.show();
    }
    
    private void askAIForAdvice() {
        String apiUrl = preferencesManager.getApiUrl();
        String apiKey = preferencesManager.getApiKey();
        
        // Only require API key if using OpenAI API (not for tunnels)
        if ((apiUrl.isEmpty() || apiUrl.contains("openai.com")) && apiKey.isEmpty()) {
            Toast.makeText(getContext(), "Please configure AI settings first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), SettingsActivity.class));
            return;
        }
        
        btnAskAi.setEnabled(false);
        btnAskAi.setText("Asking...");
        tvAiAdvice.setText(MarkdownUtils.markdownToSpanned("*Getting financial advice from AI...*"));
        
        // Prepare financial context
        financeViewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            financeViewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
                financeViewModel.getTotalOutcome().observe(getViewLifecycleOwner(), outcome -> {
                    financeViewModel.getActiveGoals().observe(getViewLifecycleOwner(), goals -> {
                        String financialContext = prepareFinancialContext(transactions, income, outcome, goals);
                        
                        aiService.getFinancialAdvice(
                                apiUrl,
                                apiKey,
                                "Based on my financial data, what advice can you give me to improve my financial health?",
                                financialContext,
                                new AIService.AIResponseCallback() {
                                @Override
                                public void onSuccess(String advice) {
                                    requireActivity().runOnUiThread(() -> {
                                        btnAskAi.setEnabled(true);
                                        btnAskAi.setText("Ask AI");
                                        tvAiAdvice.setText(MarkdownUtils.markdownToSpanned(advice));
                                        // Request layout update to accommodate new content
                                        tvAiAdvice.requestLayout();
                                    });
                                }
                                
                                @Override
                                public void onError(String error) {
                                    requireActivity().runOnUiThread(() -> {
                                        btnAskAi.setEnabled(true);
                                        btnAskAi.setText("Ask AI");
                                        tvAiAdvice.setText(MarkdownUtils.markdownToSpanned("**Error getting AI advice:** " + error));
                                        Toast.makeText(getContext(), 
                                                "AI request failed: " + error, Toast.LENGTH_LONG).show();
                                    });
                                }
                            }
                    );
                    });
                });
            });
        });
    }
    
    private String prepareFinancialContext(List<Transaction> transactions, Double income, Double outcome, List<Goal> goals) {
        StringBuilder context = new StringBuilder();
        String currencySymbol = preferencesManager.getCurrencySymbol();
        
        context.append("Financial Summary:\n");
        context.append("Total Income: ").append(FormatUtils.formatCurrency(income != null ? income : 0, currencySymbol)).append("\n");
        context.append("Total Expenses: ").append(FormatUtils.formatCurrency(outcome != null ? outcome : 0, currencySymbol)).append("\n");
        context.append("Current Balance: ").append(FormatUtils.formatCurrency((income != null ? income : 0) - (outcome != null ? outcome : 0), currencySymbol)).append("\n\n");
        
        if (transactions != null && !transactions.isEmpty()) {
            context.append("Recent Transactions:\n");
            int count = Math.min(transactions.size(), 10);
            for (int i = 0; i < count; i++) {
                Transaction t = transactions.get(i);
                context.append("- ").append(t.getType()).append(": ")
                        .append(FormatUtils.formatCurrency(t.getAmount(), currencySymbol))
                        .append(" (").append(t.getCategory()).append(")\n");
            }
            context.append("\n");
        }
        
        if (goals != null && !goals.isEmpty()) {
            context.append("Financial Goals:\n");
            for (Goal goal : goals) {
                context.append("- ").append(goal.getTitle()).append(": ")
                        .append(FormatUtils.formatCurrency(goal.getCurrentAmount(), currencySymbol))
                        .append(" / ").append(FormatUtils.formatCurrency(goal.getTargetAmount(), currencySymbol))
                        .append(" (").append(String.format("%.1f", goal.getProgressPercentage()))
                        .append("% complete, target date: ")
                        .append(new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                                .format(new java.util.Date(goal.getEndDate())))
                        .append(")\n");
            }
        }
        
        return context.toString();
    }
    
    public void onTransactionAdded(Transaction transaction) {
        financeViewModel.insert(transaction);
    }
    
    private void showGoalPaymentDialog(Goal goal) {
        String currencySymbol = preferencesManager.getCurrencySymbol();
        
        // Get current balance values - we'll use the already observed values
        Double incomeValue = financeViewModel.getTotalIncome().getValue();
        Double outcomeValue = financeViewModel.getTotalOutcome().getValue();
        double currentBalance = (incomeValue != null ? incomeValue : 0.0) - (outcomeValue != null ? outcomeValue : 0.0);
        
        GoalPaymentDialog dialog = new GoalPaymentDialog(
            requireContext(),
            goal,
            currencySymbol,
            currentBalance,
            (paidGoal, amount) -> {                        // Create an outcome transaction to deduct from main balance
                        Transaction goalPayment = new Transaction(
                            amount,
                            "Payment to " + paidGoal.getTitle(),
                            "goal payment",
                            "outcome",
                            System.currentTimeMillis()
                        );
                financeViewModel.insert(goalPayment);
                
                // Update goal current amount (add to progress)
                paidGoal.setCurrentAmount(paidGoal.getCurrentAmount() + amount);
                financeViewModel.update(paidGoal);
            }
        );
        dialog.show();
    }
}