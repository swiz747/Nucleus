package fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.Gson;
import com.tritiumlabs.arthur.nucleus.adapters.ChatAdapter;
import com.tritiumlabs.arthur.nucleus.ChatMessage;
import com.tritiumlabs.arthur.nucleus.LocalDBHandler;
import com.tritiumlabs.arthur.nucleus.MyService;
import com.tritiumlabs.arthur.nucleus.R;

import java.util.ArrayList;

public class Chats extends Fragment {

    private EditText msg_edittext;
    private String user1 = "",user2 = "";
    private int chatID;
    private String threadID = "";
    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    ListView msgListView;
    static LocalDBHandler dbHandler;
    private UIEventListener uiEventListener;
    private Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_layout, container, false);
        dbHandler = dbHandler.getInstance(getContext());
        gson = new Gson();

        Bundle args = getArguments();
        setUser1(dbHandler.getUsername());
        setUser2(args.getString("friendName"));


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(user2);


        msg_edittext = (EditText) view.findViewById(R.id.messageEditText);
        msgListView = (ListView) view.findViewById(R.id.msgListView);
        final ImageButton sendButton = (ImageButton) view
                .findViewById(R.id.sendMessageButton);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //sendButton.setImageResource(R.drawable.send_selected);
                sendTextMessage(v,dbHandler.getChatIDByUser(user2));

            }
        });

        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        chatlist = new ArrayList<ChatMessage>();
        chatlist = dbHandler.getChatMessagesbyUser(user2);
        chatAdapter = new ChatAdapter(getActivity(), chatlist);
        msgListView.setAdapter(chatAdapter);
        //dbHandler.fuckeverything();

        uiEventListener = new UIEventListener();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(uiEventListener,
                new IntentFilter("updateChatList"));

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    public void sendTextMessage(View v, String chatID) {
        String message = msg_edittext.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            final ChatMessage chatMessage = new ChatMessage();

            chatMessage.setBody(message);
            //chatMessage.setSentTime(CommonMethods.getCurrentDateTime());
            chatMessage.setChatID(chatID);
            chatMessage.setSender(user1);
            chatMessage.setReceiver(user2);
            chatMessage.setIsMine(true);

            msg_edittext.setText("");
            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
            //TODO send Message here -AB
            MyService.xmpp.sendMessage(chatMessage, threadID);
        }
    }
    public void setUser1(String user1)
    {
        this.user1 = user1;
    }
    public void setUser2(String user2)
    {
        this.user2 = user2;
    }
    public void setChatID(int chatID)
    {
        this.chatID = chatID;
    }
    public void setThreadID(String threadID)
    {
        this.threadID = threadID;
    }

    private class UIEventListener extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String from = intent.getStringExtra("from");
            String message = intent.getStringExtra("message");
            Log.d("Chats", "recieved broadcast");
            if(from.equals(user2))
            {
                Log.d("Chats", "If statement was true");
                ChatMessage chatMessage = gson.fromJson(
                        message, ChatMessage.class);

                chatAdapter.add(chatMessage);
                chatAdapter.notifyDataSetChanged();
            }

        }
    }



}