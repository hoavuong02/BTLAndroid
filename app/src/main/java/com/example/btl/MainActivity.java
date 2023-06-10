package com.example.btl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapters.MessageAdapter;
import methods.FireStoreMethod;
import models.Message;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerViewMessages;
        MessageAdapter messageAdapter;
        List<Message> messageList;
        CollectionReference messagesRef;

        // Initialize RecyclerView and adapter
        recyclerViewMessages = findViewById(R.id.recycleViewMessage);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);

        // Initialize FirebaseFirestore and collection reference
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        messagesRef = db.collection("messages");

        messagesRef.orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("MainActivity", "Failed to retrieve messages: " + e.getMessage());
                    return;
                }

                messageList.clear();
                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Message message = document.toObject(Message.class);
                        if (message != null) {
                            messageList.add(message);
                        }
                    }
                    messageAdapter.notifyDataSetChanged();
                }
            }
        });

        //send message section
        FireStoreMethod fireStoreMethod = new FireStoreMethod();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ImageButton btnSend = findViewById(R.id.btnSend);
        EditText text = findViewById(R.id.editTextText);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                } else {
                    fireStoreMethod.addMessage(text.getText().toString(), uid, "text", new Date());
                    text.getText().clear();
                    text.clearFocus();
                }
            }
        });
    }
}