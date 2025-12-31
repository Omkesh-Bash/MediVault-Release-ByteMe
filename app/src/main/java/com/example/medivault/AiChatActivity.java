package com.example.medivault;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AiChatActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ChatHistory";
    private static final String CHAT_HISTORY_KEY = "chat_history";

    private RecyclerView rvChatMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private Button btnDeleteHistory;
    private ProgressBar progressBar;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private GenerativeModel gm;
    private Executor executor = Executors.newSingleThreadExecutor();
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    private String userName, userAge, userGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        rvChatMessages = findViewById(R.id.rv_chat_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        btnDeleteHistory = findViewById(R.id.btn_delete_history);
        progressBar = findViewById(R.id.progress_bar);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadChatHistory();

        chatAdapter = new ChatAdapter(chatMessages);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        rvChatMessages.setAdapter(chatAdapter);

        fetchUserDetails();

        btnSend.setOnClickListener(v -> {
            String userQuestion = etMessage.getText().toString().trim();
            if (!userQuestion.isEmpty()) {
                addMessage(new ChatMessage(userQuestion, true));
                etMessage.setText("");
                callGeminiApi();
            }
        });

        btnDeleteHistory.setOnClickListener(v -> {
            chatMessages.clear();
            chatAdapter.notifyDataSetChanged();
            saveChatHistory();
            Toast.makeText(this, "Chat history cleared", Toast.LENGTH_SHORT).show();
        });

        gm = new GenerativeModel("gemini-flash-latest", "AIzaSyChY5NkCe1Pa6V21JI1yToH1z-Nb2hq-eA");

    }

    private void fetchUserDetails() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userName = document.getString("name");
                            userAge = document.getString("age");
                            userGender = document.getString("gender");
                        } else {
                            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void callGeminiApi() {
        progressBar.setVisibility(View.VISIBLE);

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content.Builder contentBuilder = new Content.Builder()
                .addText("You are a medical expert. Only answer medical-related questions. If the question is not medical, say you cannot answer.");

        if (userName != null && userAge != null && userGender != null) {
            contentBuilder.addText("My name is " + userName + ", I am a " + userAge + " year old " + userGender + ".");
        }

        // Add previous messages to the history
        for (ChatMessage chatMessage : chatMessages) {
            contentBuilder.addText((chatMessage.isUser() ? "User: " : "Model: ") + chatMessage.getMessage());
        }

        ListenableFuture<GenerateContentResponse> response = model.generateContent(contentBuilder.build());
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                String resultText = result.getText();
                addMessage(new ChatMessage(resultText, false));
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AiChatActivity.this, "Error generating response", Toast.LENGTH_SHORT).show();
                });
                t.printStackTrace();
            }
        }, executor);
    }

    private void addMessage(ChatMessage message) {
        runOnUiThread(() -> {
            chatMessages.add(message);
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            rvChatMessages.scrollToPosition(chatMessages.size() - 1);
            saveChatHistory();
        });
    }

    private void saveChatHistory() {
        String json = gson.toJson(chatMessages);
        sharedPreferences.edit().putString(CHAT_HISTORY_KEY, json).apply();
    }

    private void loadChatHistory() {
        String json = sharedPreferences.getString(CHAT_HISTORY_KEY, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<ChatMessage>>() {}.getType();
            chatMessages = gson.fromJson(json, type);
        } else {
            chatMessages = new ArrayList<>();
        }
    }
}
