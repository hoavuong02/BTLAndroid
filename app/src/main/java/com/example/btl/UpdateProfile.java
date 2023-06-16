package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import methods.FireStoreMethod;

public class UpdateProfile extends AppCompatActivity {
    private Uri selectedImageUri;
    private static final int REQUEST_IMAGE_PICK = 1;
    ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        FireStoreMethod fireStoreMethod = new FireStoreMethod();
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //logout section
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference usersRef = db.collection("users");

                Task<Void> deleteTokenTask = usersRef.document(currentUID).update("token", null);
                deleteTokenTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Token deletion successful
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(UpdateProfile.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finishAffinity();
                    }
                });
            }
        });
        //get current value
        avatar = findViewById(R.id.avartarshow);
        EditText editUsername = findViewById(R.id.editMyUserName);
        EditText editEmail = findViewById(R.id.editMyEmail);
        fireStoreMethod.getUserByUid(currentUID, new FireStoreMethod.DataCallback() {
            @Override
            public void onDataLoaded(User user) {
                Glide.with(UpdateProfile.this)
                        .load(user.getPhotoUrl())
                        .into(avatar);
                editUsername.setText(user.getUsername());
                editEmail.setText(user.getEmail());
            }

            @Override
            public void onError(Exception e) {

            }
        });

        //pick image
        ImageButton Imgavartar = findViewById(R.id.Imgavartar);
        Imgavartar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });
        //update
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editUsername.getText().toString().isEmpty()){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("users").document(currentUID);

                    // Update the username field
                    userRef.update("username", editUsername.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Username update successful
                                    Toast.makeText(getApplicationContext(), "Update user data successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(UpdateProfile.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // An error occurred while updating the username
                                    Toast.makeText(getApplicationContext(), "Update user data failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(UpdateProfile.this, "Please Enter UserName ", Toast.LENGTH_SHORT).show();
                }
                if(selectedImageUri != null) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                    // Tạo tên tệp tin duy nhất cho ảnh
                    String fileName = "image_" + currentUID + ".jpg";

                    // Tạo tham chiếu tới thư mục lưu trữ trong Firebase Storage
                    StorageReference imageRef = storageRef.child("users/" + fileName);

                    //xóa ảnh cũ trong Storage
                    imageRef.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    Log.d("FirebaseStorage", "File deleted successfully");
                                    // Tải ảnh mới lên Firebase Storage
                                    UploadTask uploadTask = imageRef.putFile(selectedImageUri);
                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                // Lấy URL của ảnh đã tải lên từ Firebase Storage
                                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri downloadUri) {
                                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                        // cập nhật dữ liệu vào Cloud Firestore
                                                        CollectionReference usersRef = db.collection("users");
                                                        usersRef.document(currentUID).update("photoUrl", downloadUri.toString())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        // Xử lý thành công
                                                                        Toast.makeText(getApplicationContext(), "Update user data successfully", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(UpdateProfile.this,MainActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        // Xử lý khi thất bại
                                                                        Toast.makeText(getApplicationContext(), "Save data failed", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                });
                                            } else {
                                                // Xử lý lỗi nếu tải lên không thành công
                                                Toast.makeText(UpdateProfile.this, "Upload failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // An error occurred while deleting the file
                                    Log.e("FirebaseStorage", "Error deleting file", e);
                                }
                            });


                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // Hiển thị ảnh lên ImageView
            avatar.setImageURI(selectedImageUri);
            avatar.setVisibility(View.VISIBLE);
        }
    }
}
