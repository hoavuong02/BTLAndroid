package com.example.btl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BeginActivity extends AppCompatActivity {

    Button btnloginoutside,btnSigupoutside;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);



        // Check if the user is already authenticated
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, navigate to MainActivity
            Intent intent = new Intent(BeginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnloginoutside = findViewById(R.id.btnloginoutside);
        btnSigupoutside = findViewById(R.id.btnSigupoutside);

        btnloginoutside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeginActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSigupoutside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

    }
}