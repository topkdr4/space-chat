package ru.spacechat.model;

import lombok.Data;





@Data
public class AvailableFriend extends Friend {
    private FriendStatus status;
    private String message;
}
