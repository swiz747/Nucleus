package com.tritiumlabs.arthur.servertest;

/**
 * Created by Arthur on 6/7/2016.
 */


public class ChatMessage {

    private String body, sender, receiver;
    private String sentTime;
    private String recvTime;
    private String createTime;
    private int msgID;
    private int chatID;
    private boolean isMine;

    public ChatMessage(String Sender, String Receiver, String messageString, String sentTime, String recvTime, String createTime) {
        this.body = messageString;
        this.sender = Sender;
        this.receiver = Receiver;
        this.sentTime = sentTime;
        this.recvTime = recvTime;
        this.createTime = createTime;
    }
    // be careful about these constructors, seriously like dont forget to manually set shit -AB
    public ChatMessage() {
        this.body = "";
        this.sender = "";
        this.receiver = "";
        this.sentTime = "";
        this.recvTime = "";
        this.createTime = "";
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getRecvTime() {
        return recvTime;
    }

    public void setRecvTime(String recvTime) {
        this.recvTime = recvTime;
    }

    public int getMsgID() {
        return msgID;
    }

    public void setMsgID(int msgID) {
        this.msgID = msgID;
    }

    public int getChatID() {
        return chatID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }

    public boolean getIsMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}