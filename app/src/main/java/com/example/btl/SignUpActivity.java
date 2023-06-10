package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {
    TextView textDirectLogin;
    ImageButton Imgavartar;

    ImageView avartarshow;
    EditText editUserName,editEmailSigUp,editPasswordSignUp,editConfirmPassSignUp;

    Button btnSignUpPrimary;

    private FirebaseAuth mAuth;

    FirebaseFirestore db;


//    định nghĩa một biến để lưu đường dẫn của ảnh đã chọn
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private String selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textDirectLogin = findViewById(R.id.textDirectLogin);
        editUserName = findViewById(R.id.editUserName);
        Imgavartar = findViewById(R.id.Imgavartar);
        avartarshow = findViewById(R.id.avartarshow);
        editEmailSigUp = findViewById(R.id.editEmailSigUp);
        editPasswordSignUp = findViewById(R.id.editPasswordSignUp);
        editConfirmPassSignUp = findViewById(R.id.editConfirmPassSignUp);
        btnSignUpPrimary = findViewById(R.id.btnSignUpPrimary);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        textDirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


        Imgavartar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnSignUpPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sigup();
            }
        });

    }

    private void sigup() {
        String email,pass,confirmpass,username;
        email = editEmailSigUp.getText().toString();
        pass = editPasswordSignUp.getText().toString();
        confirmpass = editConfirmPassSignUp.getText().toString();
        username = editUserName.getText().toString().trim();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this,"Please Enter username!!!", Toast.LENGTH_SHORT).show();
            return;
        }


        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please Enter Email!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Please Enter Password!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(confirmpass)){
            Toast.makeText(this,"Please Enter Confirm Password!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(pass.equals(confirmpass)){
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            firebaseUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        // Lấy token thành công
                                        String token = task.getResult().getToken();

                                        // Lưu dữ liệu vào Cloud Firestore
                                        CollectionReference usersRef = db.collection("users");
                                        User user = new User(userId,username,email,"",token); // Tạo đối tượng User với uid và token
                                        usersRef.document(userId).set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Xử lý thành công
                                                        Toast.makeText(getApplicationContext(), "Save data success. UserID: " + userId, Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Xử lý khi thất bại
                                                        Toast.makeText(getApplicationContext(), "Save data failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        // Xử lý khi không lấy được token
                                        Toast.makeText(getApplicationContext(), "Failed to get token", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }



                        //thông báo tạo tài khoản thành công và chuyển hướng
                        Toast.makeText(getApplicationContext(),"Create Account success :))", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Create Account failed!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(), "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
        }

    }

    //Định nghĩa phương thức getRealPathFromURI() để lấy đường dẫn thực sự của ảnh từ Uri:
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }
    }

    //Override phương thức onActivityResult() để nhận kết quả trả về từ Gallery:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
//            selectedImagePath = getRealPathFromURI(selectedImageUri);
//            Imgavartar.setImageURI(selectedImageUri);

            // Hiển thị ảnh lên ImageView
            avartarshow.setImageURI(selectedImageUri);


            avartarshow.setVisibility(View.VISIBLE);
        }
    }


    //Định nghĩa phương thức openGallery() để mở Gallery và chọn ảnh:
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }


}