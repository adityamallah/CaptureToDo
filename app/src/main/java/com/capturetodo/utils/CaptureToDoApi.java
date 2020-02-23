package com.capturetodo.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class CaptureToDoApi extends Application {

    private String fullName;
    private String emailId;
    private String userId;
    private String docPath;
    private static CaptureToDoApi captureToDoApi;

    public static CaptureToDoApi getCaptureToDoApi() {

        if(captureToDoApi == null){
            captureToDoApi = new CaptureToDoApi();
        }
        return captureToDoApi ;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotfication();
    }

    public CaptureToDoApi() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocPath() {
        return docPath;
    }

    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    private void createNotfication(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    "R_TODO_1",
                    "R_TODO_1",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Reminder TODOS");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }
    }
}
