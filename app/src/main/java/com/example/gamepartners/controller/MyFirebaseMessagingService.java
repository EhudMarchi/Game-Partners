package com.example.gamepartners.controller;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.fragment.NavHostFragment;

import com.example.gamepartners.R;
import com.example.gamepartners.ui.Activities_Fragments.MainActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

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
            if(!isAppOnForeground(getBaseContext(),"com.example.gamepartners")) {
                //if the application is not in foreground post notification
                generateNotification(remoteMessage.getData().get("message"), remoteMessage.getData().get("type"));
            }
            else
            {
                Intent intent = new Intent("message_received");
                intent.putExtra("message", remoteMessage.getData().get("message"));
                intent.putExtra("type", remoteMessage.getData().get("type"));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
    }
    public void generateNotification(String message, String type) {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new Notification.Builder(this);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("id_" + type, type, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId("id_" + type);
            //open app by clicking the intent
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            builder.setAutoCancel(true);
        }
        //checking Message type
        switch ( type) {
            case "FRIEND":
                sendFriendNotification(message);
                break;
            case "JOIN_GROUP":
                sendGroupNotification(message);
                break;
            case "MESSAGE":
                sendMessageNotification(message);
                break;
            case "RESPONSE":
                sendResponseNotification(message);
                break;
            case "COMMENT":
                sendCommentNotification(message);
                break;
            case "LIKE":
                sendLikeNotification(message);
                break;
        }
    }

    private void sendLikeNotification(String message) {
        builder.setContentTitle("Like Is In The Air! ❤️").setContentText(message).setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1, builder.build());
    }

    private void sendCommentNotification(String message) {
        builder.setContentTitle("Check Out New Comments \uD83D\uDE0A").setContentText(message).setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1, builder.build());
    }

    private void sendResponseNotification(String message) {
        builder.setContentTitle("Game Partners").setContentText(message).setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1, builder.build());
    }

    private void sendMessageNotification(String message) {
        builder.setContentTitle("Check Out New Messages \uD83D\uDE09").setContentText(message).setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1, builder.build());
    }

    private void sendGroupNotification(String message) {
        builder.setContentTitle("Your Game Partners Are Waiting \uD83D\uDE00").setContentText(message).setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1, builder.build());
    }

    private void sendFriendNotification(String message) {
        builder.setContentTitle("Make New Game Partners \uD83E\uDD1D").setContentText(message).setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1, builder.build());
    }
    //checks if app is on foreground
    private boolean isAppOnForeground(Context context, String appPackageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = appPackageName;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
    }

}
