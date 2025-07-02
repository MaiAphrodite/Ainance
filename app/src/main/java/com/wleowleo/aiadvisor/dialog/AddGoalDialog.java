package com.wleowleo.aiadvisor.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import com.wleowleo.aiadvisor.R;
import com.wleowleo.aiadvisor.database.Goal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddGoalDialog extends Dialog {
    
    private TextInputEditText etGoalTitle;
    private TextInputEditText etGoalAmount;
    private Spinner spinnerGoalType;
    private Button btnSelectDate;
    private Button btnCancel;
    private Button btnSave;
    
    private OnGoalAddedListener listener;
    private Calendar selectedDate;
    
    public interface OnGoalAddedListener {
        void onGoalAdded(Goal goal);
    }
    
    public AddGoalDialog(@NonNull Context context, OnGoalAddedListener listener) {
        super(context);
        this.listener = listener;
        // Initialize with a default date (3 months from now)
        this.selectedDate = Calendar.getInstance();
        this.selectedDate.add(Calendar.MONTH, 3);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_goal);
        
        // Set dialog width to be more appropriate
        if (getWindow() != null) {
            android.view.WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.85);
            getWindow().setAttributes(params);
        }
        
        initViews();
        setupSpinner();
        setupClickListeners();
        updateDateButton();
    }
    
    private void initViews() {
        etGoalTitle = findViewById(R.id.et_goal_title);
        etGoalAmount = findViewById(R.id.et_goal_amount);
        spinnerGoalType = findViewById(R.id.spinner_goal_type);
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
    }
    
    private void setupSpinner() {
        String[] goalTypes = {"Monthly", "Yearly", "Custom"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_spinner_item, goalTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoalType.setAdapter(adapter);
    }
    
    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> dismiss());
        
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        
        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                saveGoal();
            }
        });
    }
    
    private void showDatePicker() {
        Calendar calendar = selectedDate;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateDateButton();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    
    private void updateDateButton() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        btnSelectDate.setText(dateFormat.format(selectedDate.getTime()));
    }
    
    private boolean validateInputs() {
        String title = etGoalTitle.getText().toString().trim();
        String amountStr = etGoalAmount.getText().toString().trim();
        
        if (title.isEmpty()) {
            etGoalTitle.setError("Title is required");
            return false;
        }
        
        if (amountStr.isEmpty()) {
            etGoalAmount.setError("Amount is required");
            return false;
        }
        
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                etGoalAmount.setError("Amount must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            etGoalAmount.setError("Invalid amount");
            return false;
        }
        
        return true;
    }
    
    private void saveGoal() {
        String title = etGoalTitle.getText().toString().trim();
        double targetAmount = Double.parseDouble(etGoalAmount.getText().toString().trim());
        String type = spinnerGoalType.getSelectedItem().toString().toLowerCase();
        
        Calendar calendar = Calendar.getInstance();
        long startDate = calendar.getTimeInMillis();
        long endDate = selectedDate.getTimeInMillis();
        
        Goal newGoal = new Goal(title, targetAmount, type, startDate, endDate);
        
        if (listener != null) {
            listener.onGoalAdded(newGoal);
        }
        
        Toast.makeText(getContext(), "Goal added successfully!", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
