package com.tritiumlabs.arthur.servertest;

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

    private String userName, name;
    private String image; //this is a place holder for user picture -AB

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
}