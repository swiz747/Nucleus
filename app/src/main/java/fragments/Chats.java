package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

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
    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    ListView msgListView;
    static LocalDBHandler dbHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_layout, container, false);
        dbHandler = dbHandler.getInstance(getContext());


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
            MyService.xmpp.sendMessage(MyService.xmpp.createMessage(chatMessage));
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



}