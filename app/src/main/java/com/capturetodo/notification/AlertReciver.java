package com.capturetodo.notification;

        import android.app.Notification;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;

        import androidx.core.app.NotificationCompat;
        import androidx.core.app.NotificationManagerCompat;

        import com.capturetodo.R;
        import com.capturetodo.Timeline;

public class AlertReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        PendingIntent i = PendingIntent.getActivity(context, 0, new Intent(context, Timeline.class), 0);

        Notification notification = new NotificationCompat.Builder(context, "R_TODO_1")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Please Check Your TODO List")
                .setContentText("Your TODOS needs to be completed before times up......")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(i)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .build();
        assert false;
        notificationManagerCompat.notify(200, notification);
    }
}
