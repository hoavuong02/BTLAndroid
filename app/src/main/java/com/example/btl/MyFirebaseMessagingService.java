package com.example.btl;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Handle the received notification message here
        if (remoteMessage.getNotification() != null) {
            // Retrieve the notification details
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            // Display the notification or handle it as needed
            showNotification(title, body);
        }
    }

    private void showNotification(String title, String body) {
        // Create a unique channel ID for the notification
        String channelId = UUID.randomUUID().toString();
        String channelName = "My Channel";

        // Create the notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an explicit intent to open the main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Create the PendingIntent to launch the main activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo) // Set your app's notification icon
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Display the notification


        int notificationId = generateNotificationId();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }

    private int generateNotificationId() {
        // Generate a random notification ID
        Random random = new Random();
        return random.nextInt(Integer.MAX_VALUE);
    }

    public static void sendNotification(List<String> tokens) throws JSONException {
        String FCM_API = "https://fcm.googleapis.com/fcm/send";
        String SERVER_KEY = "AAAAB5Za7iM:APA91bHIw_x4HuChHuVGc40wTL4CBgzcO_AMybC5zHu5zDu0RneCVR3GF5CciJeUe2JhPimFzXFcaevxoNBmAwWvspGDMbH0DyiTDP6vmXCZZnUbef00KcTfHhpxjY_D6ZLpC5Soo1WU";
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject notification = new JSONObject();

        JSONArray registrationTokens = new JSONArray(tokens);

        notification.put("registration_ids", registrationTokens);
        notification.put("priority", "high");

        JSONObject notificationBody = new JSONObject();
        notificationBody.put("title", "New message!");
        notificationBody.put("body", "You have a new message");

        notification.put("notification", notificationBody);

        RequestBody requestBody = RequestBody.create(mediaType, notification.toString());

        Request request = new Request.Builder()
                .url(FCM_API)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "key=" + SERVER_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle request failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle request success
                String responseData = response.body().string();
                // Process the response as needed
            }
        });
    }
}
