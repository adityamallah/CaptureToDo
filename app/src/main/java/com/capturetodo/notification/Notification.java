package com.capturetodo.notification;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.capturetodo.R;
import com.capturetodo.SplashScreen;
import com.capturetodo.Timeline;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

@SuppressLint("Registered")
public class Notification extends FirebaseMessagingService {

    public static int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        generateNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
    }

    private void generateNotification(String body, String title) {
        Intent i = new Intent(this, SplashScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(NOTIFICATION_ID >  1073741824){
            NOTIFICATION_ID = 0;
        }

        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_ID++, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d("DEVICE_TOKEN", s);
    }
}
