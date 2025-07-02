package com.wleowleo.aiadvisor.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "ai_advisor_prefs";
    private static final String KEY_API_URL = "api_url";
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_CURRENCY_SYMBOL = "currency_symbol";
    
    private SharedPreferences prefs;
    
    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void setApiUrl(String url) {
        prefs.edit().putString(KEY_API_URL, url).apply();
    }
    
    public String getApiUrl() {
        return prefs.getString(KEY_API_URL, "");
    }
    
    public void setApiKey(String key) {
        prefs.edit().putString(KEY_API_KEY, key).apply();
    }
    
    public String getApiKey() {
        return prefs.getString(KEY_API_KEY, "");
    }
    
    public void setCurrencySymbol(String symbol) {
        prefs.edit().putString(KEY_CURRENCY_SYMBOL, symbol).apply();
    }
    
    public String getCurrencySymbol() {
        return prefs.getString(KEY_CURRENCY_SYMBOL, "Rp.");
    }
}
