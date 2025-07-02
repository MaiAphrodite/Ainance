package com.wleowleo.aiadvisor;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wleowleo.aiadvisor.activity.SettingsActivity;
import com.wleowleo.aiadvisor.database.Transaction;
import com.wleowleo.aiadvisor.dialog.AddTransactionDialog;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private FloatingActionButton fabMain;
    private FloatingActionButton fabAddTransaction;
    private FloatingActionButton fabSettings;
    private LinearLayout fabAddTransactionLayout;
    private LinearLayout fabSettingsLayout;
    private View fabBackgroundOverlay;
    private boolean isFabMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupNavigation();
        setupFabMenu();
    }
    
    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Initialize FAB components
        fabMain = findViewById(R.id.fab_main);
        fabAddTransaction = findViewById(R.id.fab_add_transaction);
        fabSettings = findViewById(R.id.fab_settings);
        fabAddTransactionLayout = findViewById(R.id.fab_add_transaction_layout);
        fabSettingsLayout = findViewById(R.id.fab_settings_layout);
        fabBackgroundOverlay = findViewById(R.id.fab_background_overlay);
    }
    
    private void setupNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }
    
    private void setupFabMenu() {
        fabMain.setOnClickListener(v -> toggleFabMenu());
        fabAddTransaction.setOnClickListener(v -> {
            closeFabMenu();
            showAddTransactionDialog();
        });
        fabSettings.setOnClickListener(v -> {
            closeFabMenu();
            openSettings();
        });
        fabBackgroundOverlay.setOnClickListener(v -> closeFabMenu());
    }
    
    private void toggleFabMenu() {
        if (isFabMenuOpen) {
            closeFabMenu();
        } else {
            openFabMenu();
        }
    }
    
    private void openFabMenu() {
        isFabMenuOpen = true;
        
        // Show background overlay with fade in
        fabBackgroundOverlay.setVisibility(View.VISIBLE);
        fabBackgroundOverlay.setAlpha(0f);
        ObjectAnimator.ofFloat(fabBackgroundOverlay, "alpha", 0f, 1f).setDuration(200).start();
        
        // Show the FAB layouts with slide up animation
        fabAddTransactionLayout.setVisibility(View.VISIBLE);
        fabSettingsLayout.setVisibility(View.VISIBLE);
        
        // Animate from bottom to position
        fabAddTransactionLayout.setTranslationY(100f);
        fabSettingsLayout.setTranslationY(150f);
        fabAddTransactionLayout.setAlpha(0f);
        fabSettingsLayout.setAlpha(0f);
        
        ObjectAnimator.ofFloat(fabAddTransactionLayout, "translationY", 100f, 0f).setDuration(200).start();
        ObjectAnimator.ofFloat(fabSettingsLayout, "translationY", 150f, 0f).setDuration(250).start();
        ObjectAnimator.ofFloat(fabAddTransactionLayout, "alpha", 0f, 1f).setDuration(200).start();
        ObjectAnimator.ofFloat(fabSettingsLayout, "alpha", 0f, 1f).setDuration(250).start();
    }
    
    private void closeFabMenu() {
        isFabMenuOpen = false;
        
        // Fade out background overlay
        ObjectAnimator bgFadeOut = ObjectAnimator.ofFloat(fabBackgroundOverlay, "alpha", 1f, 0f);
        bgFadeOut.setDuration(200);
        bgFadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                fabBackgroundOverlay.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        bgFadeOut.start();
        
        // Animate FABs sliding down and fading out
        ObjectAnimator.ofFloat(fabAddTransactionLayout, "translationY", 0f, 100f).setDuration(150).start();
        ObjectAnimator.ofFloat(fabSettingsLayout, "translationY", 0f, 150f).setDuration(150).start();
        
        ObjectAnimator alphaOut1 = ObjectAnimator.ofFloat(fabAddTransactionLayout, "alpha", 1f, 0f);
        alphaOut1.setDuration(150);
        alphaOut1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                fabAddTransactionLayout.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        alphaOut1.start();
        
        ObjectAnimator alphaOut2 = ObjectAnimator.ofFloat(fabSettingsLayout, "alpha", 1f, 0f);
        alphaOut2.setDuration(150);
        alphaOut2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                fabSettingsLayout.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        alphaOut2.start();
    }
    
    private void showAddTransactionDialog() {
        AddTransactionDialog dialog = new AddTransactionDialog(this);
        dialog.setOnTransactionSavedListener(this::onTransactionSaved);
        dialog.show();
    }
    
    private void onTransactionSaved(Transaction transaction) {
        // Find the FirstFragment and call its method
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        FirstFragment firstFragment = (FirstFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main)
                .getChildFragmentManager()
                .getFragments()
                .get(0);
        
        if (firstFragment != null) {
            firstFragment.onTransactionAdded(transaction);
        }
    }
    
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (isFabMenuOpen) {
            closeFabMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Remove three-dot menu
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            openSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}