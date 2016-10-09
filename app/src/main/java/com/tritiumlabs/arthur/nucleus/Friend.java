package com.tritiumlabs.arthur.nucleus;

/**
 * Created by Arthur on 7/15/2016.
 *
 * not currently in use but will be soon -AB
 *
 * we might need to refactor this to be the "User"
 * Class rather than the friend class, right now im
 * coding it to pull double duty -AB
 */


public class Friend {

    private String userName;
    private String name;
    private String email;
    private String JID;
    private String emoStatus;
    private String onlineStatus;
    private String image; //this is a place holder for user picture -AB
    private double latitude;
    private double longitude;


    public Friend(String userName, String name, String emoStatus, String onlineStatus) {
        this.userName = userName;
        this.name = name;
        this.emoStatus = emoStatus;
        this.onlineStatus = onlineStatus;
    }

    public Friend(String userName, String name)
    {
        this.userName = userName;
        this.name = name;
    }
    // be careful about these constructors, seriously like dont forget to manually set shit -AB
    public Friend()
    {
        this.userName = "default User Name";
        this.name = "default Name";
        this.emoStatus = "derp status";
        this.onlineStatus = "offline";
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getEmoStatus() {
        return emoStatus;
    }

    public void setEmoStatus(String emoStatus) {
        this.emoStatus = emoStatus;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getJID() {
        return JID;
    }

    public void setJID(String JID) {
        this.JID = JID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}