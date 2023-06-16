package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;


public class LoginActivity extends AppCompatActivity {

    TextView txtForgotPass,textDirectSignup;

    EditText editEmailLogin,editPasswordLogin;

    Button btnLoginPrimary;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtForgotPass = findViewById(R.id.txtForgotPass);
        textDirectSignup = findViewById(R.id.textDirectSignup);
        editEmailLogin = findViewById(R.id.editEmailLogin);
        editPasswordLogin = findViewById(R.id.editPasswordLogin);
        btnLoginPrimary = findViewById(R.id.btnLoginPrimary);
        mAuth = FirebaseAuth.getInstance();

        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotPassActivity.class);
                startActivity(intent);
            }
        });

        textDirectSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnLoginPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


    }

    private void login() {
        String email, pass;

        email = editEmailLogin.getText().toString();
        pass = editPasswordLogin.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please Enter Email!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Please Enter Password!!!", Toast.LENGTH_SHORT).show();
            return;
        }


        if(isValidEmail(email) == false){
            Toast.makeText(this,"Invalid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isValidLength(pass,6,30) == false){
            Toast.makeText(this,"Length must be from 6 and less than 30 characters", Toast.LENGTH_SHORT).show();
            return;
        }



        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                    if (task.isSuccessful()) {
                        if (user != null && user.isEmailVerified()) { //phải xác minh account thì mới đăng nhập được //user != null : điều này đảm bảo rằng đăng nhập thành công và có một đối tượng FirebaseUser hiện tại.
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (task.isSuccessful()) {
                                            String token = task.getResult();
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            DocumentReference userRef = db.collection("users").document(uid);

                                            userRef.update("token", token)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Token update successful
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Token update failed, handle the error
                                                        }
                                                    });
                                        } else {
                                            // Token retrieval failed, handle the error
                                        }
                                    }
                                });

                        //getApplicationContext(): Là một phương thức của Activity để lấy ra Context (ngữ cảnh) của ứng dụng.
                        Toast.makeText(getApplicationContext(), "Login successful :))", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                        else{
                            Toast.makeText(getApplicationContext(), "Please verify your account", Toast.LENGTH_SHORT).show();
                        }
                }
                else{
                        Toast.makeText(getApplicationContext(), "Email or password incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public boolean isValidEmail(String email) {
        //phương thức EMAIL_ADDRESS của lớp Patterns để kiểm tra xem email có đúng định dạng email hay không.
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidLength(String text, int minLength, int maxLength) {

        int length = text.length();
        return length >= minLength && length < maxLength;
    }
}