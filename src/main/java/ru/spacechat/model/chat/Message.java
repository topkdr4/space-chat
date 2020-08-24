package ru.spacechat.model.chat;


import lombok.Data;

@Data
public class Message {
    private final long time = System.currentTimeMillis();
    private String initiator;
    private String chat;
    private String message;
    private MessageType type;
    private MessageAction action;
}
