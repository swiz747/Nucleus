package com.tritiumlabs.arthur.nucleus;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fragments.Chats;

public class MyXMPP {

    private static boolean connected = false;
    private boolean loggedin = false;
    private static boolean isconnecting = false;
    private static String serverAddress;
    public static XMPPTCPConnection connection;
    private static String loginUser;
    private static String host;
    private static int port;
    private static String passwordUser;

    private static MyXMPP instance = null;
    private static boolean instanceCreated = false;
    public static LocalDBHandler dbHandler;
    private XMPPConnectionListener connectionListener;
    private XMPPStanzaListener presenceListener;
    private XMPPChatManagerListener chatManagerListener;
    private XMPPMessageListener messageListener;
    private XMPPRosterListener rosterListener;
    private static LocalBroadcastManager lbm;
    private Gson gson;

    //NEVER USE THE CONSTRUCTOR FOR THIS CLASS USE THE GET INSTANCE METHOD -AB

    private MyXMPP(Context context, String domain, String host, int port) {
        MyXMPP.serverAddress = domain;
        MyXMPP.host = host;
        MyXMPP.port = port;
        MyXMPP.lbm = LocalBroadcastManager.getInstance(context);
        gson = new Gson();

        //TODO add packet listeners here -AB
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
                .builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setServiceName(serverAddress);
        config.setHost(host);
        config.setPort(port);
        config.setDebuggerEnabled(true);//TODO remember to turn off - AB
        config.setSendPresence(false);
        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        connection = new XMPPTCPConnection(config.build());
        connectionListener = new XMPPConnectionListener();

        connection.addConnectionListener(connectionListener);



    }


    public static MyXMPP getInstance(Context context) {

        if (instance == null) {
            //stapled the DB onto the xmpp service im not sure if this was a good idea or not -AB
            dbHandler = LocalDBHandler.getInstance(context);
            instance = new MyXMPP(context, dbHandler.getDomain(), dbHandler.getHost(), dbHandler.getPort());

            instanceCreated = true;
        }
        Log.d("xmpp", "inside get instance!");
        return instance;

    }

    public void setLoginCreds(String logiUser, String passwordser) {
        this.loginUser = logiUser;
        this.passwordUser = passwordser;
    }


    public static void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }).start();
    }

    public static void connect(final String caller) {
        Log.d("xmpp", "in connect chumk!");




        Log.d("Connect() Function", caller + "=>connecting....");

        try {
            connection.connect();
            connected = true;

        } catch (IOException e) {


            Log.e("(" + caller + ")", "IOException: " + e.getMessage());
        } catch (SmackException e) {

            Log.e("(" + caller + ")",
                    "SMACKException: " + e.getMessage());
        } catch (XMPPException e) {

            Log.e("connect(" + caller + ")",
                    "XMPPException: " + e.getMessage());

        }


    }





    //Start of helper methods/functions -AB
    public Message createMessage(ChatMessage chatMessage)
    {
        String body = gson.toJson(chatMessage);
        ChatManager.getInstanceFor(connection).getThreadChat(chatMessage.getChatID());
        return null;
    }

    public Presence createPresence(String to, String id, String type,String mode, String body, String Extras)
    {


        Presence sendPresence = new Presence(Presence.Type.valueOf(type),body, 1, Presence.Mode.valueOf(mode));
        sendPresence.setTo(to);

        return sendPresence;
    }


    public void sendMessage(Message message)
    {
    }
    public void sendPresence(Presence presence)
    {


        try {
            connection.sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            login();
        }
    }

    //end helper methods/functions


    // changed from void to boolean for login functionality -AB
    public boolean login() {
        boolean isSuccessful = true;
        String derp = "";
        try {
            connection.login(loginUser, passwordUser);
            Log.i("LOGIN", "Yey! We're connected to the Xmpp server!");

        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
            Log.e("(" + "login()" + ")",
                    "SMACKException: " + e.getMessage());

            isSuccessful = false;
            derp = "became false in main catch";
        } catch (Exception e) {
            isSuccessful = false;
            derp = "became false in second catch";
        }

        Log.e("(" + "login()" + "):", "success =" + derp);
        loggedin = isSuccessful;
        return isSuccessful;

    }

    public boolean getLoggedIn() {
        return loggedin;
    }

    public static XMPPTCPConnection getConnection() {
        return connection;
    }
    //TODO only add if not on roster -AB
    public void addFriend(String friend) {

        Roster roster = Roster.getInstanceFor(connection);
        try {
            if (connection.isAuthenticated()) {
                roster.createEntry(friend + "@tritium", null, null);
            } else {
                login();
            }

        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }


    }


    //TODO exclude people on roster -AB
    //TODO Try/catch should break if failed
    public ArrayList<Friend> searchUsers(String userName)
    {
        ArrayList<Friend> searchList = new ArrayList<Friend>();

        UserSearchManager userSearchManager = new UserSearchManager(connection);
        Form searchForm = null;
        try {
            searchForm = userSearchManager.getSearchForm("search." + connection.getServiceName());
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        Form answerForm = searchForm.createAnswerForm();
        answerForm.setAnswer("Username", true);
        answerForm.setAnswer("search", userName); // here i'm passing the Text value to search

        ReportedData resultData = null;
        try {
            resultData = userSearchManager.getSearchResults(answerForm, "search."+ connection.getServiceName());
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        if (resultData != null) {
            List<ReportedData.Row> rows = resultData.getRows();
            Iterator<ReportedData.Row> it = rows.iterator();
            while (it.hasNext()) {

                ReportedData.Row row = it.next();
                List<String> values = row.getValues("Username");
                Iterator<String> iterator = values.iterator();
                if (iterator.hasNext()) {
                    Friend friend = new Friend();
                    String value = iterator.next();
                    friend.setName(value);
                    searchList.add(friend);
                    //Do what you want
                }
            }
        }



        return searchList;


    }
    public ArrayList<Friend> getRoster()
    {
        Log.d("GET ROSTER: ","starting to get roster");
        ArrayList<Friend> friendsList = new ArrayList<Friend>();

        Roster roster = Roster.getInstanceFor(connection);
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        Collection<RosterEntry> entries = roster.getEntries();

        for (RosterEntry entry : entries) {
            String debugString = "";
            Friend tempFriend = new Friend();
            Presence tempPresence = roster.getPresence(entry.getUser());
            tempFriend.setUserName(entry.getUser());
            debugString += "username: "+entry.getUser() + ",";
            tempFriend.setName(entry.getName());
            debugString += "name: "+entry.getName() + ",";
            if(tempPresence == null)
            {
                tempFriend.setOnlineStatus("offline");
                debugString += "mode: "+ "offline" + ",";
                tempFriend.setEmoStatus("");
                debugString += "Status: "+ "" + "";
            }
            else
            {
                tempFriend.setOnlineStatus(String.valueOf(tempPresence.getType()));
                debugString += "type: "+ String.valueOf(tempPresence.getType()) + ",";
                tempFriend.setEmoStatus(tempPresence.getStatus());
                debugString += "Status: "+tempPresence.getStatus() + "";
            }

            friendsList.add(tempFriend);

            Log.d("GET ROSTER: ", debugString);

            debugString = "";

            debugString += "checker: "+ String.valueOf(tempPresence.isAvailable()) + ",";
            debugString += "type: "+ String.valueOf(tempPresence.getType()) + ",";

            Log.d("Lets double check: ", debugString);

        }
        return friendsList;
    }

    //Reconnection manager, its weird code i dont understand but it makes magic happen i think -AB
    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex) {
            // problem loading reconnection manager
            Log.d("xmpp", "no reconnection manager HALP!!!");
        }
    }

    private class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {

            Log.d("xmpp", "Connected!");
            connected = true;
            if (!connection.isAuthenticated()) {
                login();
            }
        }

        @Override
        public void connectionClosed() {
            Log.d("xmpp", "ConnectionCLosed!");
            connected = false;
            loggedin = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            Log.d("xmpp", "ConnectionClosedOn Error!");
            connected = false;
            loggedin = false;
        }

        @Override
        public void reconnectingIn(int arg0) {

            Log.d("xmpp", "Reconnectingin " + arg0);

            loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            Log.d("xmpp", "ReconnectionFailed!");
            connected = false;

            loggedin = false;
        }

        @Override
        public void reconnectionSuccessful() {
            Log.d("xmpp", "ReconnectionSuccessful");
            connected = true;
            loggedin = true;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp", "Authenticated!");

            //initialize listeners -AB
            chatManagerListener = new XMPPChatManagerListener();
            messageListener = new XMPPMessageListener();
            rosterListener = new XMPPRosterListener();



            Roster.getInstanceFor(connection).setSubscriptionMode(Roster.SubscriptionMode.manual);
            Roster.getInstanceFor(connection).addRosterListener(rosterListener);
            ChatManager.getInstanceFor(connection).addChatListener(chatManagerListener);


            //TODO get rid of the derp -AB
            //let friends know youre online
            Stanza onlineNow = new Presence(Presence.Type.available,"derp", 1, Presence.Mode.available);
            try {
                connection.sendStanza(onlineNow);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }


            loggedin = true;
        }
    }
    //TODO: update this to only look for typing notification -AB
    //Listens for all received presence Packets -AB
    private class XMPPStanzaListener implements StanzaListener {

        @Override
        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
            Presence temp = (Presence) packet;

            Log.d("xmpp", "im processing a packet i guess: " + temp.getStanzaId() + ": status from "+ temp.getFrom() + " :" + temp.getStatus());
        }

    }

    private class XMPPChatManagerListener implements ChatManagerListener
    {

        @Override
        public void chatCreated(Chat chat, boolean createdLocally)
        {
            if (!createdLocally)
            {
                Log.d("xmpp", "i dont know why but a chat was created!");
                chat.addMessageListener(messageListener);
            }
        }
    }

    private class XMPPMessageListener implements ChatMessageListener {
        public XMPPMessageListener()
        {
            Log.d("xmpp", "im a message listener!");
        }


        @Override
        public void processMessage(Chat chat, Message message) {
            Log.i("MyXMPP_MESSAGE_LISTENER", "Xmpp message received: '"
                    + message);

            if (message.getType() == Message.Type.chat && message.getBody() != null)
            {
                //TODO if this actually works then Gson library is straight up fucking magic -AB
                final ChatMessage chatMessage = gson.fromJson(
                        message.getBody(), ChatMessage.class);
                chatMessage.setChatID(message.getThread());

                processSingleMessage(chatMessage);
            }
            else if (message.getType() == Message.Type.groupchat && message.getBody() != null)
            {
                //TODO Implement groupchat functionality -AB
                processGroupMessage();
            }
            //add elseifs for different types of chats
        }
        private void processSingleMessage(final ChatMessage chatMessage)
        {
            chatMessage.setIsMine(false);

            //holy shit i hope this works -AB
            dbHandler.addMessage(chatMessage);
            Chats.chatlist.add(chatMessage);
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Chats.chatAdapter.notifyDataSetChanged();

                }
            });
        }
        private void processGroupMessage()
        {

        }
    }

    private class XMPPRosterListener implements RosterListener
    {

        @Override
        public void entriesAdded(Collection<String> addresses) {
            Log.d("xmpp", "entriesAdded");
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
            Log.d("xmpp", "entriesUpdated");
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            Log.d("xmpp", "entriesDeleted");

        }

        @Override
        public void presenceChanged(Presence presence) {
            Presence temp = presence;
            String statusText = "";
            String sender = "";
            String statusType = "";
            Log.d("xmpp", "presence listener in roster?");
            Log.d("xmpp", "im processing a packet i guess: " + temp.getStanzaId() + ": status from "+ temp.getFrom() + " :" + temp.getStatus());
            if(temp.getFrom().equals("phoneapp@tritium/Smack"))
            {
                Log.d("xmpp", "I GOT A STATUS FROM PHONEAPP!");



            }
            else if(temp.getFrom().equals("mxpptester@tritium/Smack"))
            {
                Log.d("xmpp", "I GOT A STATUS FROM XMPPTESTER!");
            }

            statusType = temp.getMode().name();
            sender = temp.getFrom();
            statusText = temp.getStatus();

            //TODO change event names -AB
            Intent i = new Intent("custom-event-name");
            i.putExtra("from", sender);
            i.putExtra("message", statusText);
            i.putExtra("onlineStat", statusType);

            Log.d("xmpp", "Status Type (mode)"+statusType );

            lbm.sendBroadcast(i);




        }
    }


}


