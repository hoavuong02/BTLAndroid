package com.example.btl;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NofiticaitonActivity extends Application{
    public static final String ID ="push_nofitication_id";

    @Override
    public void onCreate() {
        super.onCreate();
        
        createChannelNotification();
    }

    private void createChannelNotification() {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ID,"PushNofitiocatin",
                    NotificationManager.IMPORTANCE_DEFAULT
                    );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        }
    }


