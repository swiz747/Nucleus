package com.tritiumlabs.arthur.nucleus;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class LocalDBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database instance
    private static LocalDBHandler instance = null;
    // Database Name
    //TODO: Change Name to be relevant -AB
    private static final String DATABASE_NAME = "RoundAbout";
    // table names
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_SETTINGS = "settings";
    private static final String TABLE_NOTIFICATIONS = "notifications";

    // messages table columns
    private static final String KEY_MSG_ID = "messages_id";
    private static final String KEY_MSG_CHAT_ID = "messages_chat_id";
    private static final String KEY_MSG_SENDER = "messages_sender";
    private static final String KEY_MSG_RECIEVER = "messages_reciever";
    private static final String KEY_MSG_BODY = "messages_body";
    private static final String KEY_MSG_SENTTIME = "messages_sent";
    private static final String KEY_MSG_RECVTIME = "messages_recv";
    private static final String KEY_MSG_CREATETIME = "messages_created";
    // settings table columns
    private static final String KEY_SET_ID = "settings_id";
    private static final String KEY_SET_LAYOUT = "settings_layout";
    private static final String KEY_SET_SERVER = "settings_server";
    private static final String KEY_SET_PORT = "settings_port";
    private static final String KEY_SET_DOMAIN = "settings_domain";
    private static final String KEY_SET_USERNAME = "settings_username";
    private static final String KEY_SET_PASSWORD = "settings_password";
    //TODO implement this -AB
    private static final String KEY_SET_REMEMBER = "settings_remember";
    private static final String KEY_SET_STAYLOGGED = "settings_staylogged";

    //notification columns
    private static final String KEY_NOT_ID = "notifications_id";
    private static final String KEY_NOT_TYPE = "notifications_type";
    private static final String KEY_NOT_FROM = "notifications_from";
    private static final String KEY_NOT_BODY = "notifications_body";
    private static final String KEY_NOT_EXTRA = "notifications_extra";
    private static final String KEY_NOT_TIME = "notifications_time";




    public static synchronized LocalDBHandler getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new LocalDBHandler(context.getApplicationContext());
        }
        return instance;
    }

    public LocalDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //create message table
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_MSG_ID + " INTEGER PRIMARY KEY," + KEY_MSG_CHAT_ID + " VARCHAR,"
                + KEY_MSG_SENDER + " VARCHAR," + KEY_MSG_RECIEVER + " VARCHAR," + KEY_MSG_BODY + " TEXT,"
                + KEY_MSG_SENTTIME + " DATETIME,"+ KEY_MSG_RECVTIME + " TIMESTAMP"+ KEY_MSG_CREATETIME + " DATETIME"+")";

        //TODO: ONLY UPDATES ON THIS TABLE YOU FUCKS -AB
        //create setting table
        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
                + KEY_SET_ID + " INTEGER PRIMARY KEY," + KEY_SET_DOMAIN + " VARCHAR,"
                + KEY_SET_PORT + " INTEGER," + KEY_SET_SERVER + " VARCHAR,"
                + KEY_SET_USERNAME + " VARCHAR," + KEY_SET_PASSWORD + " VARCHAR,"
                + KEY_SET_REMEMBER + " VARCHAR,"+ KEY_SET_STAYLOGGED + " VARCHAR" + ")";



        //create notifications table
        String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + KEY_NOT_ID + " INTEGER PRIMARY KEY," + KEY_NOT_TYPE + " VARCHAR,"+ KEY_NOT_FROM + " VARCHAR,"
                + KEY_NOT_BODY + " TEXT," + KEY_NOT_EXTRA + " VARCHAR," + KEY_NOT_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";



        db.execSQL(CREATE_MESSAGES_TABLE);
        db.execSQL(CREATE_SETTINGS_TABLE);
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);


        ContentValues values = new ContentValues();
        values.put(KEY_SET_DOMAIN,"tritium");
        values.put(KEY_SET_PORT, 5222);
        values.put(KEY_SET_SERVER, "45.35.4.171");
        values.put(KEY_SET_USERNAME, "");
        values.put(KEY_SET_PASSWORD, "");
        values.put(KEY_SET_REMEMBER, "N");
        values.put(KEY_SET_STAYLOGGED, "N");

        // Inserting default settings
        db.insert(TABLE_SETTINGS, null, values);


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //TODO: i dont know if i like this, if we ever upgrade we will wipe all user data -AB
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        // Creating tables again
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //TODO: i dont know if i like this, if we ever upgrade we will wipe all user data -AB
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_MESSAGES + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_SETTINGS + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_NOTIFICATIONS + "'");
        // Creating tables again
        onCreate(db);
    }
    //add new Message -AB
    public void addMessage(ChatMessage msg)
    {
        Log.e("LocalDBHandler", "adding message to DB");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MSG_CHAT_ID, msg.getChatID());
        values.put(KEY_MSG_SENDER, msg.getSender());
        values.put(KEY_MSG_RECIEVER, msg.getReceiver());
        values.put(KEY_MSG_BODY, msg.getBody());
        values.put(KEY_MSG_SENTTIME, msg.getSentTime());
        values.put(KEY_MSG_RECVTIME, msg.getRecvTime());

        // Inserting Row
        db.insert(TABLE_MESSAGES, null, values);
        //db.close(); // Closing database connection
    }
    // TODO: should we have deletable messages? -AB
    public void deleteMessage(ChatMessage msg)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MESSAGES + " WHERE " + KEY_MSG_ID + " = " + msg.getMsgID());
    }

    public  ArrayList<ChatMessage> getChatMessagesbyID(String chat_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();

        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE "
                + KEY_MSG_RECIEVER + " = '" + chat_id + "'";

        Log.e("LocalDBHandler", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    ChatMessage msg = new ChatMessage();
                    msg.setMsgID(c.getInt((c.getColumnIndex(KEY_MSG_ID))));
                    msg.setChatID(c.getString((c.getColumnIndex(KEY_MSG_CHAT_ID))));
                    msg.setBody((c.getString(c.getColumnIndex(KEY_MSG_BODY))));
                    msg.setSender((c.getString(c.getColumnIndex(KEY_MSG_SENDER))));
                    msg.setReceiver((c.getString(c.getColumnIndex(KEY_MSG_RECIEVER))));
                    msg.setSentTime(c.getString(c.getColumnIndex(KEY_MSG_SENTTIME)));
                    msg.setRecvTime(c.getString(c.getColumnIndex(KEY_MSG_RECVTIME)));
                    msg.setCreateTime(c.getString(c.getColumnIndex(KEY_MSG_CREATETIME)));

                    // adding to ChatMessage ArrayList -AB
                    messages.add(msg);
                } while (c.moveToNext());
            }
        }



        return messages;
    }


    public ArrayList<ChatMessage> getChatMessagesbyUser(String friend) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();

        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE "
                + KEY_MSG_SENDER + " = '" + friend + "' OR " + KEY_MSG_RECIEVER + " = '" + friend + "'";

        Log.e("LocalDBHandler", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    ChatMessage msg = new ChatMessage();
                    msg.setMsgID(c.getInt((c.getColumnIndex(KEY_MSG_ID))));
                    msg.setChatID(c.getString((c.getColumnIndex(KEY_MSG_CHAT_ID))));
                    msg.setSender((c.getString(c.getColumnIndex(KEY_MSG_SENDER))));
                    if (msg.getSender().equals(getUsername()))
                    {
                        msg.setIsMine(true);
                    }
                    else
                    {
                        msg.setIsMine(false);
                    }


                    msg.setReceiver((c.getString(c.getColumnIndex(KEY_MSG_RECIEVER))));
                    msg.setBody((c.getString(c.getColumnIndex(KEY_MSG_BODY))));
                   // msg.setSentTime(c.getString(c.getColumnIndex(KEY_MSG_SENTTIME)));
                    //msg.setRecvTime(c.getString(c.getColumnIndex(KEY_MSG_RECVTIME)));
                   // msg.setCreateTime(c.getString(c.getColumnIndex(KEY_MSG_CREATETIME)));





                    // adding to ChatMessage ArrayList -AB
                    messages.add(msg);
                } while (c.moveToNext());
            }
        }



        return messages;
    }

    public String getChatIDByUser(String friend) {
        SQLiteDatabase db = this.getReadableDatabase();
        String chatID = "";

        String selectQuery = "SELECT DISTINCT " + KEY_MSG_CHAT_ID + " FROM " + TABLE_MESSAGES + " WHERE "
                + KEY_MSG_SENDER + " = '" + friend + "' OR " + KEY_MSG_RECIEVER + " = '" + friend + "'";

        Log.e("LocalDBHandler", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {

                   chatID = c.getString((c.getColumnIndex(KEY_MSG_CHAT_ID)));

                } while (c.moveToNext());
            }
        }



        return chatID;
    }

    //TODO make these function, nigger -AB
    //setters
    public int setUserName(String username)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SET_USERNAME, username);
// updating row
        return db.update(TABLE_SETTINGS, values, null, null);

    }
    public void setUserPassword(String username)
    {

    }

    //getters
    public String getDomain()
    {
        return "tritium";
    }
    public String getHost()
    {
        return "45.35.4.171";
    }
    public int getPort()
    {
        return 5222;
    }
    public String getUsername()
    {
        String returnValue = "fucking nothing";
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_SETTINGS + " WHERE "
            + KEY_SET_ID + " = " + 1;

        Log.e("LocalDBHandler", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                returnValue = c.getString(c.getColumnIndex(KEY_SET_USERNAME));
            }
        }

        return returnValue;
    }
    public String getLocalSettings()
    {
        String returnValue = "fucking nothing";
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_SETTINGS;

        Log.e("LocalDBHandler", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                returnValue = c.getString(c.getColumnIndex(KEY_SET_USERNAME));
            }
        }


        Log.d("look at me motherfucker", returnValue);
        return returnValue;
    }
    //TODO holy shit this almost slipped by, fix this shit -AB
    public String getPassword()
    {
        return "password";
    }

    public void addNotification(Notification note)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String name = note.getFrom();
        String type = note.getType();
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATIONS +
                " WHERE " + KEY_NOT_TYPE + " = '" + type + "'" +
                " AND " + KEY_NOT_FROM + " = '" + name + "'";

        Cursor c = db.rawQuery(selectQuery, null);
        Log.e("LocalDBHandler", "checking Notification in DB");

        if(c != null)
        {
            if(c.getCount() == 0)
            {
                Log.e("LocalDBHandler", "adding Notification to DB");

                ContentValues values = new ContentValues();

                values.put(KEY_NOT_TYPE, note.getType());
                values.put(KEY_NOT_FROM, note.getFrom());
                values.put(KEY_NOT_BODY, note.getBody());
                values.put(KEY_NOT_EXTRA, note.getExtra());



                // Inserting Row
                db.insert(TABLE_NOTIFICATIONS, null, values);
            }
            else
            {
                Log.e("LocalDBHandler", "notification exists");
            }

        }
        else
        {
            Log.e("LocalDBHandler", "c was null!");
        }
        c.close();
    }
    public void clearNotificationByType(String type)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NOTIFICATIONS + " WHERE " + KEY_NOT_TYPE + " = '" + type + "'";

        db.execSQL(query);
    }

    public void clearNotificationByID(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NOTIFICATIONS + " WHERE " + KEY_NOT_ID + " = " + id;
        Log.e("LocalDBHandler", query);
        db.execSQL(query);
    }
    public void clearNotificationByNameAndType(String name, String type)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NOTIFICATIONS + " WHERE " + KEY_NOT_TYPE + " = '" + type + "'" + " AND " + KEY_NOT_FROM + " = '" + name + "'";
        db.execSQL(query);
    }
    public void clearAllNotifications()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NOTIFICATIONS;
        db.execSQL(query);
    }
    public ArrayList<Notification> getAllNotifications()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Notification> notifications = new ArrayList<Notification>();

        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATIONS;

        Log.e("LocalDBHandler", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    Notification notification = new Notification();

                    notification.setNotificationID(c.getInt(c.getColumnIndex(KEY_NOT_ID)));
                    notification.setType(c.getString(c.getColumnIndex(KEY_NOT_TYPE)));
                    notification.setFrom(c.getString(c.getColumnIndex(KEY_NOT_FROM)));
                    notification.setBody(c.getString(c.getColumnIndex(KEY_NOT_BODY)));
                    notification.setExtra(c.getString(c.getColumnIndex(KEY_NOT_EXTRA)));
                    notification.setTimestamp(c.getString(c.getColumnIndex(KEY_NOT_TIME)));


                    // adding to ChatMessage ArrayList -AB
                    notifications.add(notification);
                } while (c.moveToNext());
            }
        }



        return notifications;
    }

    public ArrayList<Notification> getNotificationsByType(String type)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Notification> notifications = new ArrayList<Notification>();

        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE "
                + KEY_NOT_TYPE + " = '" + type + "'";

        Log.e("LocalDBHandler", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    Notification notification = new Notification();

                    notification.setNotificationID(c.getInt(c.getColumnIndex(KEY_NOT_ID)));
                    notification.setType(c.getString(c.getColumnIndex(KEY_NOT_TYPE)));
                    notification.setFrom(c.getString(c.getColumnIndex(KEY_NOT_FROM)));
                    notification.setBody(c.getString(c.getColumnIndex(KEY_NOT_BODY)));
                    notification.setExtra(c.getString(c.getColumnIndex(KEY_NOT_EXTRA)));
                    notification.setTimestamp(c.getString(c.getColumnIndex(KEY_NOT_TIME)));


                    // adding to ChatMessage ArrayList -AB
                    notifications.add(notification);
                } while (c.moveToNext());
            }
        }



        return notifications;
    }



    public void fuckeverything()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteAll = "DELETE FROM " + TABLE_MESSAGES;
        db.execSQL(deleteAll);
        String deleteAll2 = "DELETE FROM " + TABLE_SETTINGS;
        db.execSQL(deleteAll2);
        String deleteAll3 = "DELETE FROM " + TABLE_NOTIFICATIONS;
        db.execSQL(deleteAll3);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.close();

    }







}