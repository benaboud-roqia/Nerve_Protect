package com.dianerverotect;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.dianerverotect.chatbot.ChatAdapter;
import com.dianerverotect.chatbot.ChatMessage;
import com.dianerverotect.chatbot.DialogflowService;
import com.dianerverotect.chatbot.MedicalAIModel;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatbotFragment extends Fragment {

    private static final String TAG = "ChatbotFragment";
    
    // UI Components
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    
    // Chat messages
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    
    // Medical AI model for handling predictions
    private MedicalAIModel medicalAIModel;
    
    // Dialogflow service for natural language processing
    private DialogflowService dialogflowService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);
        
        // Initialize UI components
        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        messageInput = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.send_button_fab);
        
        // Initialize chat messages and adapter
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), chatMessages);
        
        // Set up RecyclerView with improved scrolling
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setHasFixedSize(false);
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        
        // Initialize Medical AI Model
        medicalAIModel = new MedicalAIModel(getContext());
        boolean modelsLoaded = medicalAIModel.initialize();
        
        if (!modelsLoaded) {
            Log.e(TAG, "Error loading AI models");
            Toast.makeText(getContext(), "Error loading AI models", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "AI models loaded successfully");
        }
        
        // Initialize Dialogflow Service
        dialogflowService = new DialogflowService(getContext());
        dialogflowService.initialize();
        
        // Add welcome message
        addBotMessage("Hello! I'm NerveBot, your medical assistant for neuropathy and ALS. How can I help you today?");
        
        // Set up send button click listener
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageInput.setText("");
            }
        });
        
        return view;
    }
    
    private void sendMessage(String message) {
        // Add user message to chat
        addUserMessage(message);
        
        // Identify language
        LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(message)
                .addOnSuccessListener(languageCode -> {
                    if (languageCode.equals("und")) {
                        Log.i(TAG, "Can't identify language");
                    } else {
                        Log.i(TAG, "Language: " + languageCode);
                    }
                    
                    // First try to get a response using Dialogflow
                    dialogflowService.sendQuery(message, new DialogflowService.DialogflowCallback() {
                        @Override
                        public void onResult(String response) {
                            // Add bot response to chat
                            addBotMessage(response);
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Dialogflow error: " + error);
                            
                            // Fall back to AI model if Dialogflow fails
                            processWithAIModel(message);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Language identification failed: " + e.getMessage());
                    
                    // Process with AI model if language identification fails
                    processWithAIModel(message);
                });
    }
    
    /**
     * Process a message using the AI model
     * @param message Message to process
     */
    private void processWithAIModel(String message) {
        // Determine if the message is about ALS or neuropathy
        String lowerMessage = message.toLowerCase(Locale.getDefault());
        int modelType;
        
        if (isAboutALS(lowerMessage)) {
            modelType = MedicalAIModel.MODEL_ALS;
        } else if (isAboutNeuropathy(lowerMessage)) {
            modelType = MedicalAIModel.MODEL_NEUROPATHY;
        } else {
            // If we can't determine, default to neuropathy
            modelType = MedicalAIModel.MODEL_NEUROPATHY;
        }
        
        // Get response from the AI model
        String response = medicalAIModel.getResponse(lowerMessage, modelType);
        
        // Add bot response to chat
        addBotMessage(response);
    }
    
    private boolean isGreeting(String message) {
        String[] greetings = {"hello", "hi", "hey", "greetings", "good morning", "good afternoon", "good evening", "bonjour", "salut"};
        for (String greeting : greetings) {
            if (message.contains(greeting)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isAboutALS(String message) {
        String[] alsKeywords = {
            "als", "amyotrophic lateral sclerosis", "motor neuron", "lou gehrig", 
            "muscle weakness", "muscle atrophy", "fasciculations", "bulbar", 
            "upper motor neuron", "lower motor neuron", "familial als", "sporadic als"
        };
        
        for (String keyword : alsKeywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isAboutNeuropathy(String message) {
        String[] neuropathyKeywords = {
            "neuropathy", "diabetic neuropathy", "peripheral neuropathy", "nerve damage", 
            "nerve pain", "tingling", "numbness", "burning sensation", "pins and needles", 
            "diabetic nerve", "foot pain", "hand pain", "glucose", "diabetes", "diabetic"
        };
        
        for (String keyword : neuropathyKeywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
    
    private void addUserMessage(String message) {
        chatMessages.add(new ChatMessage(message, true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatAdapter.notifyDataSetChanged(); // Assure que l'adaptateur est bien mis à jour
        scrollToBottom();
    }
    
    private void addBotMessage(String message) {
        chatMessages.add(new ChatMessage(message, false));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatAdapter.notifyDataSetChanged(); // Assure que l'adaptateur est bien mis à jour
        scrollToBottom();
    }
    
    private void scrollToBottom() {
        // Utilisation de postDelayed pour s'assurer que le défilement se produit après le rendu complet
        chatRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (chatMessages.size() > 0) {
                    chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                }
            }
        }, 100); // Délai court pour s'assurer que la vue est complètement mise à jour
    }
    
    private void addBotMessageWithImage(String message, int imageResourceId) {
        chatMessages.add(new ChatMessage(message, imageResourceId));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatAdapter.notifyDataSetChanged(); // Assure que l'adaptateur est bien mis à jour
        scrollToBottom();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Clean up resources
        if (medicalAIModel != null) {
            medicalAIModel.close();
        }
        
        if (dialogflowService != null) {
            dialogflowService.close();
        }
    }
}
