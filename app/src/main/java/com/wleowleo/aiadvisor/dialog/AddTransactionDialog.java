package com.wleowleo.aiadvisor.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.wleowleo.aiadvisor.R;
import com.wleowleo.aiadvisor.database.Transaction;
import com.wleowleo.aiadvisor.utils.FormatUtils;

public class AddTransactionDialog extends Dialog {
    private EditText etAmount;
    private EditText etDescription;
    private AutoCompleteTextView dropdownCategory;
    private TabLayout tabTransactionType;
    private TextInputLayout tilAmount;
    private Button btnSave;
    private Button btnCancel;
    
    private OnTransactionSavedListener listener;
    private String currentType = "income";
    
    public interface OnTransactionSavedListener {
        void onTransactionSaved(Transaction transaction);
    }
    
    public AddTransactionDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    
    public void setOnTransactionSavedListener(OnTransactionSavedListener listener) {
        this.listener = listener;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_transaction);
        
        // Make dialog appropriately sized
        if (getWindow() != null) {
            int widthInDp = (int) (360 * getContext().getResources().getDisplayMetrics().density);
            getWindow().setLayout(
                widthInDp,
                (int)(getContext().getResources().getDisplayMetrics().heightPixels * 0.7)
            );
        }
        
        initViews();
        setupTabs();
        setupCategoryDropdown(); // Setup category dropdown
        setupClickListeners();
        updateUIForTransactionType(); // Set initial colors
    }
    
    private void initViews() {
        etAmount = findViewById(R.id.et_amount);
        etDescription = findViewById(R.id.et_description);
        dropdownCategory = findViewById(R.id.dropdown_category);
        tabTransactionType = findViewById(R.id.tab_transaction_type);
        tilAmount = findViewById(R.id.til_amount);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }
    
    private void setupTabs() {
        tabTransactionType.addTab(tabTransactionType.newTab().setText("Income"));
        tabTransactionType.addTab(tabTransactionType.newTab().setText("Outcome"));
        
        tabTransactionType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentType = tab.getPosition() == 0 ? "income" : "outcome";
                setupCategoryDropdown();
                updateUIForTransactionType();
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    
    private void updateUIForTransactionType() {
        int color = currentType.equals("income") ? 
                ContextCompat.getColor(getContext(), R.color.income_green) :
                ContextCompat.getColor(getContext(), R.color.outcome_red);
        
        // Update tab indicator color
        tabTransactionType.setSelectedTabIndicatorColor(color);
        
        // Update input field colors
        if (tilAmount != null) {
            tilAmount.setBoxStrokeColor(color);
            tilAmount.setHintTextColor(ColorStateList.valueOf(color));
        }
        
        // Update save button color
        btnSave.setBackgroundTintList(ColorStateList.valueOf(color));
        
        // Update save button text based on type
        btnSave.setText(currentType.equals("income") ? "Add Income" : "Add Expense");
    }
    
    private void setupCategoryDropdown() {
        String[] categories = currentType.equals("income") ? 
                FormatUtils.getIncomeCategories() : FormatUtils.getOutcomeCategories();
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_dropdown_item_1line, categories);
        dropdownCategory.setAdapter(adapter);
        dropdownCategory.setText("", false);
    }
    
    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> dismiss());
        
        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveTransaction();
            }
        });
    }
    
    private boolean validateInput() {
        String amountText = etAmount.getText().toString().trim();
        String category = dropdownCategory.getText().toString().trim();
        
        if (amountText.isEmpty()) {
            etAmount.setError("Amount is required");
            return false;
        }
        
        if (category.isEmpty()) {
            dropdownCategory.setError("Please select a category");
            return false;
        }
        
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                etAmount.setError("Amount must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid amount format");
            return false;
        }
        
        return true;
    }
    
    private void saveTransaction() {
        double amount = Double.parseDouble(etAmount.getText().toString().trim());
        String description = etDescription.getText().toString().trim();
        String category = dropdownCategory.getText().toString().trim();
        
        Transaction transaction = new Transaction(
                amount,
                description,
                category,
                currentType,
                System.currentTimeMillis()
        );
        
        if (listener != null) {
            listener.onTransactionSaved(transaction);
        }
        
        dismiss();
    }
}
