package com.tritiumlabs.arthur.nucleus;


import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
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
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PresenceTypeFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fragments.Chats;
import fragments.FriendRequest;
import fragments.FriendsList;

import static android.content.Context.NOTIFICATION_SERVICE;

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
    private XMPPSubscriptionListener subscriptionListener;
    private XMPPChatManagerListener chatManagerListener;
    private XMPPMessageListener messageListener;
    private XMPPRosterListener rosterListener;
    private static LocalBroadcastManager lbm;
    private static NotificationHelper notificationHelper;
    private Gson gson;

    //NEVER USE THE CONSTRUCTOR FOR THIS CLASS USE THE GET INSTANCE METHOD -AB

    private MyXMPP(Context context, String domain, String host, int port) {
        MyXMPP.serverAddress = domain;
        MyXMPP.host = host;
        MyXMPP.port = port;
        MyXMPP.lbm = LocalBroadcastManager.getInstance(context);
        MyXMPP.notificationHelper = new NotificationHelper(context);
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

        ChatManager.getInstanceFor(connection).getThreadChat(chatMessage.getChatID());
        return null;
    }

    public Presence createPresence(String to, String id, String type,String mode, String body, String Extras)
    {


        Presence sendPresence = new Presence(Presence.Type.valueOf(type),body, 1, Presence.Mode.valueOf(mode));
        sendPresence.setTo(to);

        return sendPresence;
    }


    public void sendMessage(ChatMessage chatMessage, String threadID)
    {
        String body = gson.toJson(chatMessage);
        Chat currentChat = ChatManager.getInstanceFor(connection).getThreadChat(threadID);

        if (currentChat == null)
        {
            currentChat = ChatManager.getInstanceFor(connection).createChat(chatMessage.getReceiver() +"@tritium");
        }
        final Message message = new Message();
        message.setBody(body);
        message.setType(Message.Type.chat);

        try {
            if (connection.isAuthenticated()) {
                // maybe preprocess outgoing messages here
                currentChat.sendMessage(message);
                Log.d("sendMEssage", body);
                dbHandler.addMessage(chatMessage);

            } else {

                login();
            }
        } catch (SmackException.NotConnectedException e) {
            Log.e("xmpp.SendMessage()", "msg Not sent!-Not Connected!");

        } catch (Exception e) {
            Log.e("SendMessage()-Exception","msg Notsent!" + e.getMessage());
        }
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

    public void confirmFriend(String friend)
    {
        Presence confirmFriendSubscribed = new Presence(Presence.Type.subscribed);
        Presence confirmFriendSubscribe = new Presence(Presence.Type.subscribe);
        confirmFriendSubscribed.setTo(friend);
        confirmFriendSubscribed.setStatus("confirmFriend");
        confirmFriendSubscribe.setTo(friend);
        confirmFriendSubscribe.setStatus("confirmFriend");

        sendPresence(confirmFriendSubscribe);
        sendPresence(confirmFriendSubscribed);
    }
    //TODO only add if not on roster -AB
    public void addFriend(String friend) {

        try {
            if (connection.isAuthenticated()) {
                Presence requestFriend = new Presence(Presence.Type.subscribe);
                requestFriend.setTo(friend);
                requestFriend.setStatus("friendReq");


                Notification tempNote = new Notification();
                tempNote.setType("pendReq");
                tempNote.setBody("Friend Request Pending: " + friend);
                tempNote.setExtra("picture resource");
                tempNote.setFrom(friend);
                dbHandler.addNotification(tempNote);
                connection.sendStanza(requestFriend);

            }
            else
            {
                login();
            }
        }
        catch (SmackException.NotConnectedException e)
        {
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
                List<String> usernames = row.getValues("Username");
                List<String> jids = row.getValues("jid");
                List<String> emails = row.getValues("Email");
                List<String> names = row.getValues("Name");
                Iterator<String> iterator = usernames.iterator();
                for(int i = 0; i < usernames.size(); i++)
                {
                    Friend friend = new Friend();
                    String username = usernames.get(i);
                    String jid = jids.get(i);
                    String email = emails.get(i);
                    String name = names.get(i);
                    String errorString = "";

                    //errorString += "JID: " + jid + " " + "Username: " + username;
                    //Log.e("fucking look at me",errorString );

                    if(!username.equals(MyXMPP.dbHandler.getUsername()))
                    {
                        friend.setName(name);
                        friend.setJID(jid);
                        friend.setEmail(email);
                        searchList.add(friend);
                    }

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
        Collection<RosterEntry> entries = roster.getEntries();

        for (RosterEntry entry : entries) {
            String debugString = "";
            if (entry.getType() == RosterPacket.ItemType.both)
            {
                Friend tempFriend = new Friend();
                Presence tempPresence = roster.getPresence(entry.getUser());
                tempFriend.setJID(entry.getUser());
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

        }
        return friendsList;
    }

    public ArrayList<Friend> getRequests()
    {
        ArrayList<Friend> requestList = new ArrayList<Friend>();
        Roster.getInstanceFor(connection);



        return requestList;
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
            subscriptionListener = new XMPPSubscriptionListener();




            Roster.getInstanceFor(connection).setSubscriptionMode(Roster.SubscriptionMode.manual);
            Roster.getInstanceFor(connection).addRosterListener(rosterListener);
            ChatManager.getInstanceFor(connection).addChatListener(chatManagerListener);
            connection.addAsyncStanzaListener(subscriptionListener, new OrFilter(PresenceTypeFilter.SUBSCRIBE, PresenceTypeFilter.SUBSCRIBED,
                    PresenceTypeFilter.UNSUBSCRIBE, PresenceTypeFilter.UNSUBSCRIBED));

            /**
             *
             * new OrFilter(PresenceTypeFilter.SUBSCRIBE, PresenceTypeFilter.SUBSCRIBED,
             PresenceTypeFilter.UNSUBSCRIBE, PresenceTypeFilter.UNSUBSCRIBED)
             */


            //let friends know youre online
            Stanza onlineNow = new Presence(Presence.Type.available,"online", 1, Presence.Mode.available);
            try {
                connection.sendStanza(onlineNow);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }


            loggedin = true;
        }
    }

    //listens for subscriptions
    private class XMPPSubscriptionListener implements StanzaListener {

        @Override
        public void processPacket(Stanza stanza) throws SmackException.NotConnectedException {
            Presence temp = (Presence) stanza;

            Roster roster = Roster.getInstanceFor(connection);

            Log.d("xmpp", "im processing a packet i guess: " + temp.getStanzaId() + ": status from "+ temp.getFrom() + " :" + temp.getStatus());

            if(temp.getType() == Presence.Type.subscribe)
            {
                if (temp.getStatus().equals("friendReq"))
                {
                    Log.d("xmpp", "some nigga subscribed to me");
                    //TODO make a notification some nigga friend requested me
                    Notification newRequest = new Notification();
                    newRequest.setFrom(temp.getFrom());
                    newRequest.setType("friendReq");
                    newRequest.setBody("New Request From " + newRequest.getClippedName());
                    newRequest.setExtra("derp");
                    dbHandler.addNotification(newRequest);
                }
                else if(temp.getStatus().equals("confirmFriend"))
                {

                }

            }
            else if(temp.getType() == Presence.Type.subscribed)
            {


                    if (temp.getStatus().equals("confirmFriend"))
                    {
                        try {
                            roster.createEntry(temp.getFrom(),"",null);
                            dbHandler.clearNotificationByNameAndType(temp.getFrom(), "pendReq");
                            Presence ackFriend = new Presence(Presence.Type.subscribed);
                            ackFriend.setTo(temp.getFrom());
                            ackFriend.setStatus("acknowledged");
                            connection.sendStanza(ackFriend);
                        } catch (SmackException.NotLoggedInException e) {
                            login();
                        } catch (SmackException.NoResponseException e) {
                            e.printStackTrace();
                        } catch (XMPPException.XMPPErrorException e) {
                            e.printStackTrace();
                        }

                    }
                    else
                    {

                    }



                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            //TODO check fragment manager and see if fragment is created before modifying
                            //FriendsList.friendslistAdapter.notifyDataSetChanged();
                            //FriendRequest.friendRequestAdapter.notifyDataSetChanged();

                        }
                    });

                Log.d("xmpp", "some nigga accepted my subscription");

            }
            else if(temp.getType() == Presence.Type.unsubscribe)
            {
                Log.d("xmpp", "some nigga unsubscribed to me");
            }
            else
            {
                Log.d("xmpp", "i have no idea when this fires");
            }
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
            ArrayList<ExtensionElement> derp = (ArrayList<ExtensionElement>) message.getExtensions();
            String extensions = "";
            for(int i = 0; i<derp.size();i++)
            {
                extensions += derp.get(i).getNamespace();
                extensions += ": ";
            }

            Log.e("MyXMPP", extensions);

            if (message.getType() == Message.Type.chat && message.getBody() != null && message.getExtension("delay","urn:xmpp:delay") == null)
            {
                //TODO if this actually works then Gson library is straight up fucking magic -AB
                final ChatMessage chatMessage = gson.fromJson(
                        message.getBody(), ChatMessage.class);
                chatMessage.setChatID(message.getThread());

                processSingleMessage(chatMessage);
            }
            else if (message.getType() == Message.Type.groupchat && message.getBody() != null && message.getExtension("delay","urn:xmpp:delay") == null)
            {
                //TODO Implement groupchat functionality -AB
                processGroupMessage();
            }
            else if (message.getExtension("delay","urn:xmpp:delay") != null && message.getBody() != null)
            {
                Log.e("MyXMPP", "Delayed fucking message");
            }
            //add elseifs for different types of chats
        }
        private void processSingleMessage(final ChatMessage chatMessage)
        {
            chatMessage.setIsMine(false);
            String jsonMessage = gson.toJson(chatMessage);
            //holy shit i hope this works -AB
            dbHandler.addMessage(chatMessage);



            Intent i = new Intent("updateChatList");
            //i.putExtra("chatID", chatMessage.getChatID());
            i.putExtra("from", chatMessage.getSender());
            i.putExtra("message", jsonMessage);
            lbm.sendBroadcast(i);

            //if activity is not active
            // prepare intent which is triggered if the
            // notification is selected
            notificationHelper.messageNotificcation(chatMessage);



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

    public void doTheThing()
    {
        Notification derp = new Notification();
        derp.setFrom("dildo@tritium");
        derp.setType("pendReq");
        derp.setBody("Wants to add you as a friend");
        derp.setExtra("extra info");

        dbHandler.addNotification(derp);
    }

}


