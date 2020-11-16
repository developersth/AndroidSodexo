package com.developerth.sodexobooking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.developerth.sodexobooking.ui.login.LoginActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by MacBookPro on 4/7/2017 AD.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private NotificationManager mNotificationManager;
    String isLastTime="False";
    private android.os.Vibrator vibrator;
    static Ringtone ringtone;
    private String customer_group_id;
    private String driver_id;
    private String shipment_no;
    private String tu_id;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
      //  isLastTime = remoteMessage.getData().get("isLastTime");

        Log.d(TAG, notification.getTitle());
        Log.d(TAG, notification.getBody());
        sendNotification(notification, data);

     //   String box = remoteMessage.getData().get("box");
     //   Log.d("notification",txt+","+box);
    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

        Log.d(TAG, "Implement this method to send token to your app server.");
    }


    /**
     * Create and show a custom notification containing the received FCM message.
     *
     * @param notification FCM notification payload received.
     * @param data FCM data payload received.
     */
    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_sodexo_icon);
        Intent intent;
        String newjob = data.get("newjob");
        if(newjob!=null) {
            try {
                customer_group_id = data.get("customer_group_id");
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("isNotification",true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("customer_group_id", customer_group_id);
                startActivity(intent);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "TOP")
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getBody())
                        .setAutoCancel(false)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                        .setContentIntent(pendingIntent)
                        .setContentInfo(notification.getTitle())
                        .setLargeIcon(icon)
                        .setColor(Color.RED)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_DEFAULT);

//        notificationBuilder.setOngoing(isLastTime.equals("true")?false:true);
                if (Build.VERSION.SDK_INT >= 21) notificationBuilder.setVibrate(new long[0]);


                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                notificationBuilder.setLights(Color.YELLOW, 1000, 300);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else{
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ringtone = RingtoneManager.getRingtone(this, uri);

            ringtone.play();
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("isNotification",true);
            intent.putExtra("body_message",notification.getBody());
            intent.putExtra("time_message",notification.getTitle());
            try {
                customer_group_id = data.get("customer_group_id");
                driver_id = data.get("driver_id");
                shipment_no = data.get("shipment_no");
                tu_id = data.get("tu_id");
                shipment_no = data.get("shipment_no");
                intent.putExtra("shipment_no",shipment_no);
                intent.putExtra("driver_id",driver_id);
                intent.putExtra("customer_group_id",customer_group_id);
                intent.putExtra("tu_id",tu_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(intent);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);


            //Uri.parse("android.resource://my.package.name/raw/notification");

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(notification.getTitle())
                    .setContentText(notification.getBody())
                    .setAutoCancel(false)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent)
                    .setContentInfo(notification.getTitle())
                    .setLargeIcon(icon)
                    .setColor(Color.RED)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_MAX);

//        notificationBuilder.setOngoing(isLastTime.equals("true")?false:true);
            if (Build.VERSION.SDK_INT >= 21) notificationBuilder.setVibrate(new long[0]);



            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            notificationBuilder.setLights(Color.YELLOW, 1000, 300);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        }


    }

}

