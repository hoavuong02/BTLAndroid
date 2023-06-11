package com.example.btl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapters.MessageAdapter;
import methods.FileHelper;
import methods.FireStoreMethod;
import methods.StorageMethod;
import models.Message;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private String checker = "";
    private Uri fileUri;
    private Button buttonSendFile;
    private FileHelper fileHelper;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    private static final int PICK_FILE_REQUEST = 438;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB

    //send image
    private static final int REQUEST_IMAGE_PICK = 1;
    private Uri imageUri;


    private BroadcastReceiver downloadCompleteReceiver;
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
        messageAdapter = new MessageAdapter(messageList,this);
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
        //send File
        buttonSendFile = findViewById(R.id.btnFile);
        fileHelper = new FileHelper(this, buttonSendFile);

        //send image section
        Button btnSendImage = findViewById(R.id.btnImage);
        btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở hộp thoại chọn ảnh từ thiết bị
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });

        //send message section
        FireStoreMethod fireStoreMethod = new FireStoreMethod();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Button btnSend = findViewById(R.id.btnSend);
        EditText text = findViewById(R.id.editTextText);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().toString().isEmpty() && imageUri ==null) {
                    Toast.makeText(MainActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                } else if(!text.getText().toString().isEmpty() && imageUri ==null) {
                    fireStoreMethod.addMessage(text.getText().toString(), uid,"","","" ,new Date());
                    text.getText().clear();
                    text.clearFocus();
                }
            }
        });

        //logout section
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fileHelper.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            //set value for selectedImageUri
            imageUri = data.getData();
            StorageMethod storageMethod = new StorageMethod(imageUri,MainActivity.this);
            storageMethod.uploadImage();
        }

    }
}