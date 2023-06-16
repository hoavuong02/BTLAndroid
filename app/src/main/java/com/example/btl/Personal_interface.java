package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class Personal_interface extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_interface);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String photoUrl = intent.getStringExtra("photoUrl");

        // Sử dụng dữ liệu trong giao diện
        TextView textViewUserName = findViewById(R.id.textViewUserName);
        CircleImageView circleImageViewUser = findViewById(R.id.circleImageViewUser);

        textViewUserName.setText(userName);

        // Sử dụng thư viện Glide để tải và hiển thị hình ảnh
        Glide.with(this)
                .load(photoUrl)
                .into(circleImageViewUser);
    }
}
