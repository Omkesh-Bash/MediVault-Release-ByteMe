package com.example.medivault;

import android.os.Bundle;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AiChatActivity extends AppCompatActivity {

    private RecyclerView rvChatMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private ProgressBar progressBar;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private GenerativeModel gm;
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        rvChatMessages = findViewById(R.id.rv_chat_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        progressBar = findViewById(R.id.progress_bar);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        rvChatMessages.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> {
            String userQuestion = etMessage.getText().toString().trim();
            if (!userQuestion.isEmpty()) {
                addMessage(new ChatMessage(userQuestion, true));
                etMessage.setText("");
                callGeminiApi(userQuestion);
            }
        });

        gm = new GenerativeModel("gemini-flash-latest", "AIzaSyChY5NkCe1Pa6V21JI1yToH1z-Nb2hq-eA");

    }

    private void callGeminiApi(String userQuestion) {
        progressBar.setVisibility(View.VISIBLE);

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText("You are a medical expert. Only answer medical-related questions. If the question is not medical, say you cannot answer. The user's question is: " + userQuestion)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
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
        });
    }
}
