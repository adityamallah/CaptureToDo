package com.capturetodo.model;
import com.google.firebase.Timestamp;

public class ToDo_Model {
    private String title;
    private String description;
    private String imgUrl;
    private String userId;
    private Timestamp timestamp;
    private String fullName;
    private String docPath;
    private String emailId;
    private String timerDays;
    private String timerHours;
    private String timerMinutes;

    public ToDo_Model() {
    }

    public ToDo_Model(String title, String description, String imgUrl, String userId,
                      Timestamp timestamp, String fullName, String docPath, String emailId,
                      String timerDays, String timerHours, String timerMinutes) {
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.userId = userId;
        this.timestamp = timestamp;
        this.fullName = fullName;
        this.docPath = docPath;
        this.emailId = emailId;
        this.timerDays = timerDays;
        this.timerHours = timerHours;
        this.timerMinutes = timerMinutes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDocPath() {
        return docPath;
    }

    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getTimerDays() {
        return timerDays;
    }

    public void setTimerDays(String timerDays) {
        this.timerDays = timerDays;
    }

    public String getTimerHours() {
        return timerHours;
    }

    public void setTimerHours(String timerHours) {
        this.timerHours = timerHours;
    }

    public String getTimerMinutes() {
        return timerMinutes;
    }

    public void setTimerMinutes(String timerMinutes) {
        this.timerMinutes = timerMinutes;
    }
}
