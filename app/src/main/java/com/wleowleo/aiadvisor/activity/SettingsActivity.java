package com.wleowleo.aiadvisor.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.wleowleo.aiadvisor.R;
import com.wleowleo.aiadvisor.database.AppDatabase;
import com.wleowleo.aiadvisor.service.AIService;
import com.wleowleo.aiadvisor.utils.PreferencesManager;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {
    public static final int RESULT_DATA_RESET = 1001;
    
    private EditText etApiUrl;
    private EditText etApiKey;
    private EditText etCurrencySymbol;
    private Button btnSaveSettings;
    private Button btnTestAI;
    private Button btnResetAllData;
    
    private PreferencesManager preferencesManager;
    private AIService aiService;
    private AppDatabase database;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        preferencesManager = new PreferencesManager(this);
        aiService = new AIService();
        database = AppDatabase.getInstance(this);
        
        setupToolbar();
        initViews();
        loadSettings();
        setupClickListeners();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Settings");
            }
        }
    }
    
    private void initViews() {
        etApiUrl = findViewById(R.id.et_api_url);
        etApiKey = findViewById(R.id.et_api_key);
        etCurrencySymbol = findViewById(R.id.et_currency_symbol);
        btnSaveSettings = findViewById(R.id.btn_save_settings);
        btnTestAI = findViewById(R.id.btn_test_ai);
        btnResetAllData = findViewById(R.id.btn_reset_all_data);
    }
    
    private void loadSettings() {
        etApiUrl.setText(preferencesManager.getApiUrl());
        etApiKey.setText(preferencesManager.getApiKey());
        etCurrencySymbol.setText(preferencesManager.getCurrencySymbol());
    }
    
    private void setupClickListeners() {
        btnSaveSettings.setOnClickListener(v -> saveSettings());
        btnTestAI.setOnClickListener(v -> testAIConnection());
        btnResetAllData.setOnClickListener(v -> showResetConfirmationDialog());
    }
    
    private void saveSettings() {
        String apiUrl = etApiUrl.getText().toString().trim();
        String apiKey = etApiKey.getText().toString().trim();
        String currencySymbol = etCurrencySymbol.getText().toString().trim();
        
        if (currencySymbol.isEmpty()) {
            currencySymbol = "Rp.";
        }
        
        preferencesManager.setApiUrl(apiUrl);
        preferencesManager.setApiKey(apiKey);
        preferencesManager.setCurrencySymbol(currencySymbol);
        
        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
    }
    
    private void testAIConnection() {
        String apiUrl = etApiUrl.getText().toString().trim();
        String apiKey = etApiKey.getText().toString().trim();
        
        // Check if this is a local server (doesn't need API key)
        boolean isLocalServer = isLocalServer(apiUrl);
        
        if (!isLocalServer && apiKey.isEmpty()) {
            Toast.makeText(this, "Please enter an API key for external AI services", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (apiUrl.isEmpty() && apiKey.isEmpty()) {
            Toast.makeText(this, "Please enter either a local server URL or OpenAI API key", Toast.LENGTH_SHORT).show();
            return;
        }
        
        btnTestAI.setEnabled(false);
        btnTestAI.setText("Testing...");
        
        aiService.getFinancialAdvice(
                apiUrl,
                apiKey,
                "Hello, can you provide a simple test response?",
                "This is a test message to verify the AI connection.",
                new AIService.AIResponseCallback() {
                    @Override
                    public void onSuccess(String advice) {
                        runOnUiThread(() -> {
                            btnTestAI.setEnabled(true);
                            btnTestAI.setText("Test AI");
                            Toast.makeText(SettingsActivity.this, 
                                    "AI connection successful!", Toast.LENGTH_SHORT).show();
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            btnTestAI.setEnabled(true);
                            btnTestAI.setText("Test AI");
                            Toast.makeText(SettingsActivity.this, 
                                    "AI connection failed: " + error, Toast.LENGTH_LONG).show();
                        });
                    }
                }
        );
    }
    
    private boolean isLocalServer(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        
        String lowerUrl = url.toLowerCase();
        
        // Check for common local server patterns
        return lowerUrl.contains("localhost") || 
               lowerUrl.contains("127.0.0.1") ||
               lowerUrl.contains("192.168.") ||
               lowerUrl.contains("10.0.") ||
               lowerUrl.contains("172.16.") ||
               lowerUrl.contains(":1234") ||  // Common LM Studio port
               lowerUrl.contains(":8080") ||  // Common local dev port
               lowerUrl.contains(":3000") ||  // Common local dev port
               lowerUrl.contains(":5000");    // Common local dev port
    }
    
    private void showResetConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset All Data")
                .setMessage("This will permanently delete all transactions, goals, and reset your settings. This action cannot be undone.\n\nAre you sure you want to continue?")
                .setPositiveButton("Yes, Reset All", (dialog, which) -> resetAllData())
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    
    private void resetAllData() {
        // Show progress
        btnResetAllData.setEnabled(false);
        btnResetAllData.setText("Resetting...");
        
        // Perform database operations in background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Clear all database tables
                database.transactionDao().deleteAll();
                database.goalDao().deleteAll();
                
                runOnUiThread(() -> {
                    // Reset button state
                    btnResetAllData.setEnabled(true);
                    btnResetAllData.setText("Reset All Data");
                    
                    // Set result to indicate data was reset
                    setResult(RESULT_DATA_RESET);
                    
                    // Show success message
                    Toast.makeText(SettingsActivity.this, 
                            "All data has been reset successfully", Toast.LENGTH_LONG).show();
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    // Reset button state
                    btnResetAllData.setEnabled(true);
                    btnResetAllData.setText("Reset All Data");
                    
                    // Show error message
                    Toast.makeText(SettingsActivity.this, 
                            "Error resetting data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
