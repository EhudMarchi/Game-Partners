package com.example.gamepartners.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Request;
import com.example.gamepartners.data.model.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final String TAG = "MyFirebaseMessaging";
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
    public static void sendMessage(Request request , Context context)
    {
        final JSONObject rootObject  = new JSONObject();
        try {
            rootObject.put("to", "/topics/"+request.getTargetID());
            rootObject.put("data",new JSONObject().put("message",request.getRequestText()).put("type",request.getType().toString()));
            String url = "https://fcm.googleapis.com/fcm/send";

            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest queueRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> headers = new HashMap<>();
                    headers.put("Content-Type","application/json");
                    headers.put("Authorization","key="+ R.string.fcm_api_key);
                    return headers;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return rootObject.toString().getBytes();
                }
            };
            queue.add(queueRequest);
            queue.start();

        }catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        //if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

//            Intent intent = new Intent("message_received");
//            intent.putExtra("message",remoteMessage.getData().get("message"));
//            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            //if the application is not in forground post notification
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(this);

            if(Build.VERSION.SDK_INT>=26) {
                NotificationChannel channel = new NotificationChannel("id_1", "name_1", NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
                builder.setChannelId("id_1");
            }
            builder.setContentTitle("new message from topic").setContentText(remoteMessage.getData().get("message")).setSmallIcon(android.R.drawable.star_on);

            manager.notify(1,builder.build());
        //}

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
    }

}
