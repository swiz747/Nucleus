package com.tritiumlabs.arthur.nucleus;

/**
 * Created by Arthur on 10/8/2016.
 */

public class Notification {
    private int notificationID;
    private String type;
    private String from;
    private String body;
    private String extra;
    private String timestamp;


    public Notification(int notificationID, String type, String from, String body, String extra, String timestamp) {
        this.notificationID = notificationID;
        this.type = type;
        this.from = from;
        this.body = body;
        this.extra = extra;
        this.timestamp = timestamp;
    }

    public Notification() {
        this.notificationID = -1;
        this.type = "";
        this.from = "";
        this.body = "";
        this.extra = "";
        this.timestamp = "";
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getClippedName()
    {
        String returnString = "";

        if(from != null)
        {
            returnString = from.substring(0,from.indexOf("@"));
        }

        return returnString;
    }
}
