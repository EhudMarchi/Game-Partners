package com.example.gamepartners.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.gamepartners.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final String TAG = "MyFirebaseMessaging";
    NotificationManager manager;
    Notification.Builder builder;
    public static void subscribeUserToMessaging(String i_UID)
    {
        FirebaseMessaging messaging = FirebaseMessaging.getInstance();
        messaging.subscribeToTopic(i_UID);
    }
    public static void unsubscribeUserToMessaging(String i_UID)
    {
        FirebaseMessaging messaging = FirebaseMessaging.getInstance();
        messaging.unsubscribeFromTopic(i_UID);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Intent intent = new Intent("message_received");
            intent.putExtra("message", remoteMessage.getData().get("message"));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            //if the application is not in foreground post notification

            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            builder = new Notification.Builder(this);

            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel("id_" + remoteMessage.getData().get("type"), remoteMessage.getData().get("type"), NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
                builder.setChannelId("id_" + remoteMessage.getData().get("type"));
            }

            switch ( remoteMessage.getData().get("type")) {
                case "FRIEND":
                    sendFriendNotification(remoteMessage);
                    break;
                case "JOIN_GROUP":
                    sendGroupNotification(remoteMessage);
                    break;
                case "MESSAGE":
                    sendMessageNotification(remoteMessage);
                    break;
                case "RESPONSE":
                    sendResponseNotification(remoteMessage);
                    break;
                case "COMMENT":
                    sendCommentNotification(remoteMessage);
                    break;
                case "LIKE":
                    sendLikeNotification(remoteMessage);
                    break;
            }

            }
        }

    private void sendLikeNotification(RemoteMessage remoteMessage) {
        builder.setContentTitle("Like Is In The Air! ❤️").setContentText(remoteMessage.getData().get("message")).setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1, builder.build());
    }

    private void sendCommentNotification(RemoteMessage remoteMessage) {
        builder.setContentTitle("Check Out New Comments \uD83D\uDE0A").setContentText(remoteMessage.getData().get("message")).setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1, builder.build());
    }

    private void sendResponseNotification(RemoteMessage remoteMessage) {
        builder.setContentTitle("Game Partners").setContentText(remoteMessage.getData().get("message")).setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1, builder.build());
    }

    private void sendMessageNotification(RemoteMessage remoteMessage) {
        builder.setContentTitle("Check Out New Messages \uD83D\uDE09").setContentText(remoteMessage.getData().get("message")).setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1, builder.build());
    }

    private void sendGroupNotification(RemoteMessage remoteMessage) {
        builder.setContentTitle("Your Game Partners Are Waiting \uD83D\uDE00").setContentText(remoteMessage.getData().get("message")).setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1, builder.build());
    }

    private void sendFriendNotification(RemoteMessage remoteMessage) {
        builder.setContentTitle("Make New Game Partners \uD83E\uDD1D").setContentText(remoteMessage.getData().get("message")).setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1, builder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
    }

}
