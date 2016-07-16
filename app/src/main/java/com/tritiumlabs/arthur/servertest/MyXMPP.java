package com.tritiumlabs.arthur.servertest;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import fragments.Chats;

public class MyXMPP {

    public static boolean connected = false;
    public boolean loggedin = false;
    public static boolean isconnecting = false;
    public static boolean isToasted = true;
    private boolean chat_created = false;
    private String serverAddress;
    public static XMPPTCPConnection connection;
    private static String loginUser;
    public static String host;
    public static int port;
    private static String passwordUser;
    Gson gson;
    Context context;
    public static MyXMPP instance = null;
    public static boolean instanceCreated = false;
    public static LocalDBHandler dbHandler;

    //TODO: NEVER USE THE CONSTRUCTOR FOR THIS CLASS USE THE GET INSTANCE METHOD -AB

    public MyXMPP(Context context, String domain, String host, int port) {
        this.serverAddress = domain;
        this.host = host;
        this.port = port;

        this.context = context;
        init();

    }



    public static MyXMPP getInstance(Context context)
    {

        if (instance == null) {
            //stapled the DB onto the xmpp service im not sure if this was a good idea or not -AB
            dbHandler = dbHandler.getInstance(context);
            instance = new MyXMPP(context, dbHandler.getDomain(), dbHandler.getHost(), dbHandler.getPort());

            instanceCreated = true;
        }
        Log.d("xmpp", "inside get instance!");
        return instance;

    }

    public void setLoginCreds(String logiUser, String passwordser)
    {
        this.loginUser = logiUser;
        this.passwordUser = passwordser;
    }

    public org.jivesoftware.smack.chat.Chat Mychat;

    ChatManagerListenerImpl mChatManagerListener;
    MMessageListener mMessageListener;

    String text = "";
    String mMessage = "", mReceiver = "";
    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex) {
            // problem loading reconnection manager
        }
    }

    public void init() {
        gson = new Gson();
        mMessageListener = new MMessageListener(context);
        mChatManagerListener = new ChatManagerListenerImpl();
        initialiseConnection();

    }

    private void initialiseConnection() {

        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
                .builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setServiceName(serverAddress);
        config.setHost(host);
        config.setPort(port);
        config.setDebuggerEnabled(true);
        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        connection = new XMPPTCPConnection(config.build());
        XMPPConnectionListener connectionListener = new XMPPConnectionListener();
        connection.addConnectionListener(connectionListener);
    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }).start();
    }

    public void connect(final String caller) {
        Log.d("xmpp", "in connect chumk!");
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                if (connection.isConnected())
                    return false;
                isconnecting = true;
                if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {

                            Toast.makeText(context,
                                    caller + "=>connecting....",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                Log.d("Connect() Function", caller + "=>connecting....");

                try {
                    connection.connect();
                    DeliveryReceiptManager dm = DeliveryReceiptManager
                            .getInstanceFor(connection);
                    dm.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
                    dm.addReceiptReceivedListener(new ReceiptReceivedListener() {

                        @Override
                        public void onReceiptReceived(final String fromid,
                                                      final String toid, final String msgid,
                                                      final Stanza packet) {

                        }
                    });
                    connected = true;

                } catch (IOException e) {
                    if (isToasted)
                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {

                                    @Override
                                    public void run() {

                                        Toast.makeText(
                                                context,
                                                "(" + caller + ")"
                                                        + "IOException: ",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                    Log.e("(" + caller + ")", "IOException: " + e.getMessage());
                } catch (SmackException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(context,
                                    "(" + caller + ")" + "SMACKException: ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("(" + caller + ")",
                            "SMACKException: " + e.getMessage());
                } catch (XMPPException e) {
                    if (isToasted)

                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {

                                    @Override
                                    public void run() {

                                        Toast.makeText(
                                                context,
                                                "(" + caller + ")"
                                                        + "XMPPException: ",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    Log.e("connect(" + caller + ")",
                            "XMPPException: " + e.getMessage());

                }
                return isconnecting = false;
            }
        };
        connectionThread.execute();
    }
    // changed from void to boolean for login functionality -AB
    public boolean login()
    {
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

        Log.e("(" + "login()" + "):" ,"success =" + derp);
        return isSuccessful;

    }

    private class ChatManagerListenerImpl implements ChatManagerListener {
        @Override
        public void chatCreated(final org.jivesoftware.smack.chat.Chat chat,
                                final boolean createdLocally)
        {
            if (!createdLocally)
                chat.addMessageListener(mMessageListener);

        }

    }
    //TODO: fix server string to not be retarded - AB
    public void sendMessage(ChatMessage chatMessage) {
        String body = gson.toJson(chatMessage);


        if (!chat_created) {
            Mychat = ChatManager.getInstanceFor(connection).createChat(
                    chatMessage.getReceiver() + "@"
                            + context.getString(R.string.server),
                    mMessageListener);
            chat_created = true;
        }
        final Message message = new Message();
       // message.setThread(chatMessage.getChatID());
        message.setBody(body);
        message.setType(Message.Type.chat);

        try {
            if (connection.isAuthenticated()) {

                Mychat.sendMessage(message);
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
    //TODO: add documentation explaining why this is convoluted and retarded, sorry :( -AB
    //TODO: this is where we can add status updates super easy -AB
    public String getRoster()
    {
        String strFriends= "";
        Log.d("GET ROSTER: ", strFriends);

        Roster roster = Roster.getInstanceFor(connection);
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            String i = entry.getUser();

            strFriends = strFriends + i + ",";
        }
        strFriends = strFriends.substring(0,strFriends.length());
        return strFriends;
    }
    public void addFriend(String friend)
    {

        Roster roster = Roster.getInstanceFor(connection);
        try {
            if (connection.isAuthenticated()) {
                roster.createEntry(friend,null,null);
            }
            else
            {
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
        answerForm.setAnswer("Name", true);
        answerForm.setAnswer("search", "test"); // here i'm passsing the Text value to search

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



        return searchList;


    }
    //TODO: add packet listener for friend requests -AB
    public class XMPPConnectionListener implements ConnectionListener {
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
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, "ConnectionCLosed!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ConnectionCLosed!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context, "ConnectionClosedOn Error!!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ConnectionClosedOn Error!");
            connected = false;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectingIn(int arg0) {

            Log.d("xmpp", "Reconnectingin " + arg0);

            loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(context, "ReconnectionFailed!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ReconnectionFailed!");
            connected = false;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectionSuccessful() {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, "REConnected!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ReconnectionSuccessful");
            connected = true;

            chat_created = false;
            loggedin = true;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp", "Authenticated!");


            ChatManager.getInstanceFor(connection).addChatListener(
                    mChatManagerListener);

            chat_created = false;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, "Connected!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            loggedin = true;
        }
    }

    private class MMessageListener implements ChatMessageListener {
        public MMessageListener(Context contxt) {
        }

        @Override
        public void processMessage(final org.jivesoftware.smack.chat.Chat chat,
                                   final Message message) {
            Log.i("MyXMPP_MESSAGE_LISTENER", "Xmpp message received: '"
                    + message);

            if (message.getType() == Message.Type.chat
                    && message.getBody() != null)
            {
                //TODO if this actually works then Gson library is straight up fucking magic -AB
                final ChatMessage chatMessage = gson.fromJson(
                        message.getBody(), ChatMessage.class);

                processMessage(chatMessage);
            }
            //add elseifs for different types of chats
        }

        private void processMessage(final ChatMessage chatMessage) {

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

    }


}