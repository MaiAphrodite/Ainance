package com.wleowleo.aiadvisor.service;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import okhttp3.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AIService {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private OkHttpClient client;
    private Gson gson;
    
    public AIService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }
    
    public void getFinancialAdvice(String apiUrl, String apiKey, String userQuery, 
                                 String financialContext, AIResponseCallback callback) {
        
        // Determine the final URL to use
        String finalUrl = buildCompleteUrl(apiUrl);
        
        // Auto-detect LM Studio or similar local servers
        boolean isLocalServer = isLocalServer(finalUrl);
        boolean isOpenAI = finalUrl.contains("openai.com");
        boolean requiresAuth = !isLocalServer && !apiKey.isEmpty();
        
        Log.d("AIService", "Original URL: " + apiUrl);
        Log.d("AIService", "Final URL: " + finalUrl);
        Log.d("AIService", "Is local server: " + isLocalServer);
        Log.d("AIService", "Requires auth: " + requiresAuth);
        
        // Create the request body
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.model = "gpt-3.5-turbo";
        chatRequest.messages = new ArrayList<>();
        
        // System prompt
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.role = "system";
        systemMessage.content = "You are a friendly, slightly playful financial advisor AI who loves helping people master their money. " +
            "Analyze the user's financial data in depth and provide clear, actionable advice and planning. " +
            "Focus on budgeting, smart saving strategies, optimizing expenses, and achieving long-term financial goals. " +
            "Keep your tone approachable and engaging, with a dash of light humor, but make sure your analysis is thorough and well-reasoned. " +
            "Keep your entire response under 4096 tokens. Here's the user's financial context: " + financialContext;
        chatRequest.messages.add(systemMessage);

        // User query
        ChatMessage userMessage = new ChatMessage();
        userMessage.role = "user";
        userMessage.content = userQuery;
        chatRequest.messages.add(userMessage);
        
        chatRequest.maxTokens = 500;
        chatRequest.temperature = 0.7;
        
        String jsonBody = gson.toJson(chatRequest);
        
        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(finalUrl)
                .post(body)
                .addHeader("Content-Type", "application/json");
        
        // Add authorization header based on server type
        if (requiresAuth) {
            if (isOpenAI) {
                // OpenAI format
                requestBuilder.addHeader("Authorization", "Bearer " + apiKey);
            } else {
                // Generic API key format (for other paid APIs)
                requestBuilder.addHeader("Authorization", "Bearer " + apiKey);
                // Some APIs might use different formats, add alternatives if needed
                // requestBuilder.addHeader("X-API-Key", apiKey);
            }
            Log.d("AIService", "Added Authorization header");
        } else {
            Log.d("AIService", "No authorization required (local server or no API key)");
        }
        
        Request request = requestBuilder.build();
        
        Log.d("AIService", "Sending request to: " + request.url());
        Log.d("AIService", "Request body: " + jsonBody);
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }
              @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                
                if (!response.isSuccessful()) {
                    callback.onError("API error " + response.code() + ": " + responseBody);
                    return;
                }

                try {
                    ChatResponse chatResponse = gson.fromJson(responseBody, ChatResponse.class);
                    if (chatResponse.choices != null && !chatResponse.choices.isEmpty()) {
                        String advice = chatResponse.choices.get(0).message.content;
                        callback.onSuccess(advice);
                    } else {
                        callback.onError("No response from AI. Response: " + responseBody);
                    }
                } catch (Exception e) {
                    callback.onError("Failed to parse response: " + e.getMessage() + ". Response: " + responseBody);
                }
            }
        });
    }
    
    private String buildCompleteUrl(String apiUrl) {
        // If empty, use OpenAI default
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            return OPENAI_API_URL;
        }
        
        String trimmedUrl = apiUrl.trim();
        
        // If it already has the chat/completions endpoint, use as-is
        if (trimmedUrl.contains("/chat/completions")) {
            return trimmedUrl;
        }
        
        // For local servers, auto-append the endpoint
        if (isLocalServer(trimmedUrl)) {
            // Remove trailing slash if present
            if (trimmedUrl.endsWith("/")) {
                trimmedUrl = trimmedUrl.substring(0, trimmedUrl.length() - 1);
            }
            
            // Add the standard OpenAI-compatible endpoint
            return trimmedUrl + "/v1/chat/completions";
        }
        
        // For other URLs, assume they need the OpenAI endpoint structure
        if (trimmedUrl.endsWith("/")) {
            trimmedUrl = trimmedUrl.substring(0, trimmedUrl.length() - 1);
        }
        
        // Check if it ends with /v1, if so just add /chat/completions
        if (trimmedUrl.endsWith("/v1")) {
            return trimmedUrl + "/chat/completions";
        }
        
        // Otherwise add the full path
        return trimmedUrl + "/v1/chat/completions";
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
    
    public interface AIResponseCallback {
        void onSuccess(String advice);
        void onError(String error);
    }
    
    // Request/Response models
    private static class ChatRequest {
        String model;
        List<ChatMessage> messages;
        @SerializedName("max_tokens")
        int maxTokens;
        double temperature;
    }
    
    private static class ChatMessage {
        String role;
        String content;
    }
    
    private static class ChatResponse {
        List<Choice> choices;
    }
    
    private static class Choice {
        ChatMessage message;
    }
}
