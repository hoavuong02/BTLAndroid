package com.example.btl;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessagemessage) {
        super.onMessageReceived(remoteMessagemessage);
        RemoteMessage.Notification notification =
                remoteMessagemessage.getNotification();
        if (notification==null){
            return;
        }
        String strTitle = notification.getTitle();
        String strMessage = notification.getBody();
        sendNofiticaion(strTitle,strMessage);
    }

    private void sendNofiticaion(String strTitle, String strMessage) {

        Intent intent = new Intent(this,NofiticaitonActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this,NofiticaitonActivity.ID)
                        .setContentTitle(strTitle)
                        .setContentText(strMessage)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager !=null){
            notificationManager.notify(1,notification);
        }
    }
}
