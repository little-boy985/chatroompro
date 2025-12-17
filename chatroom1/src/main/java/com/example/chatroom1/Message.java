package com.example.chatroom1;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String SYSTEM_USER = "系统通知";

    private final long id;
    private final String sender;
    private final String receiver; // null表示群聊
    private final String content;
    private final long time;

    public Message(long id, String sender, String receiver, String content, long time) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.time = time;
    }

    public long getId() { return id; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getContent() { return content; }
    public long getTime() { return time; }

    public String getTimeString() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date(time));
    }

    // 判断是否是系统消息
    public boolean isSystem() {
        return SYSTEM_USER.equals(sender);
    }
}