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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPassActivity extends AppCompatActivity {

    EditText editEmailForgot;
    Button btnSubmitForgot;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);


        editEmailForgot = findViewById(R.id.editEmailForgot);
        btnSubmitForgot = findViewById(R.id.btnSubmitForgot);
        mAuth = FirebaseAuth.getInstance();
        btnSubmitForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });
    }

    private void forgotPassword() {
        String email;
        email = editEmailForgot.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please Enter Email!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isValidEmail(email) == false){
            Toast.makeText(this,"Invalid email", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
//                    Toast.makeText(ForgotPassActivity.this,email, Toast.LENGTH_SHORT).show();
                    Toast.makeText(ForgotPassActivity.this,"Check your email :))", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ForgotPassActivity.this,LoginActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(ForgotPassActivity.this,"Email does not exist !!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public boolean isValidEmail(String email) {
        //phương thức EMAIL_ADDRESS của lớp Patterns để kiểm tra xem email có đúng định dạng email hay không.
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}