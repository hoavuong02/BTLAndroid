package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class SignUpActivity extends AppCompatActivity {
    TextView textDirectLogin;
    ImageButton Imgavartar;

    ImageView avartarshow;
    EditText editUserName,editEmailSigUp,editPasswordSignUp,editConfirmPassSignUp;

    Button btnSignUpPrimary;

    private FirebaseAuth mAuth;
    private Uri selectedImageUri;

    FirebaseFirestore db;


//    định nghĩa một biến để lưu đường dẫn của ảnh đã chọn
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private String selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textDirectLogin = findViewById(R.id.textDirectLogin);
        editUserName = findViewById(R.id.editMyUserName);
        Imgavartar = findViewById(R.id.Imgavartar);
        avartarshow = findViewById(R.id.avartarshow);
        editEmailSigUp = findViewById(R.id.editMyEmail);
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

      if(isValidEmail(email) == false){
          Toast.makeText(this,"Invalid email", Toast.LENGTH_SHORT).show();
          return;
      }

      if(isValidLength(username,1,30) == false){
          Toast.makeText(this,"Length must be from 1 and less than 30 characters", Toast.LENGTH_SHORT).show();
          return;
      }

        if(isValidLength(pass,6,30) == false){
            Toast.makeText(this,"Length must be from 6 and less than 30 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isValidLength(confirmpass,6,30) == false){
            Toast.makeText(this,"Length must be from 6 and less than 30 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if(selectedImageUri != null){
            if(pass.equals(confirmpass)){
                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if(task.isSuccessful()){

                            //Gửi email xác minh
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(emailTask -> {
                                            if (emailTask.isSuccessful()) {
                                                // Xử lý khi gửi email xác minh thành công
                                                Toast.makeText(getApplicationContext(), "Gửi email xác minh thành công", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Xử lý khi gửi email xác minh thất bại
                                                Toast.makeText(getApplicationContext(), "Gửi email xác minh thất bại", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (!task.isSuccessful()) {
                                                Log.w("MainActivity", "Failed to retrieve FCM registration token", task.getException());
                                                return;
                                            }

                                            String token = task.getResult();

                                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            // Lấy tham chiếu tới Firebase Storage
                                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                                            // Tạo tên tệp tin duy nhất cho ảnh
                                            String fileName = "image_" + userId + ".jpg";

                                            // Tạo tham chiếu tới thư mục lưu trữ trong Firebase Storage
                                            StorageReference imageRef = storageRef.child("users/" + fileName);

                                            // Tải ảnh lên Firebase Storage
                                            UploadTask uploadTask = imageRef.putFile(selectedImageUri);


                                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        // Lấy URL của ảnh đã tải lên từ Firebase Storage
                                                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri downloadUri) {
                                                                // Tạo đối tượng User với uid và token

                                                                User user = new User(userId,username,email,downloadUri.toString(),token);
                                                                // Lưu dữ liệu vào Cloud Firestore
                                                                CollectionReference usersRef = db.collection("users");
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
                                                                                //Toast.makeText(getApplicationContext(), "Save data failed", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        });
                                                    } else {
                                                        // Xử lý lỗi nếu tải lên không thành công
                                                        Toast.makeText(SignUpActivity.this, "Upload failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });


                                            //...
                                        }
                                    });

                            //thông báo tạo tài khoản thành công và chuyển hướng
                            Toast.makeText(getApplicationContext(),"Create Account success, Please verify to login :))", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Account already exists!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(), "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(SignUpActivity.this, "Please choose your avatar !! " , Toast.LENGTH_SHORT).show();
        }

    }

    //Override phương thức onActivityResult() để nhận kết quả trả về từ Gallery:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();


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


    //VALIDATE
    public boolean isValidEmail(String email) {
        //phương thức EMAIL_ADDRESS của lớp Patterns để kiểm tra xem email có đúng định dạng email hay không.
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidLength(String text, int minLength, int maxLength) {

        int length = text.length();
        return length >= minLength && length < maxLength;
    }


}