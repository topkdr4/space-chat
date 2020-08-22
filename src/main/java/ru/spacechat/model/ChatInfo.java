package ru.spacechat.model;


import lombok.Data;

import java.util.List;





@Data
public class ChatInfo {

    private String id;
    private String name;
    private List<ChatMember> members;

}
