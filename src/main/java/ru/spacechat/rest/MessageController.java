package ru.spacechat.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.spacechat.commons.OperationException;
import ru.spacechat.commons.Util;
import ru.spacechat.model.ChatInfo;
import ru.spacechat.model.ChatMember;
import ru.spacechat.model.User;
import ru.spacechat.model.chat.Message;
import ru.spacechat.model.chat.MessageAction;
import ru.spacechat.model.chat.MessageType;
import ru.spacechat.repository.ChatRepository;
import ru.spacechat.repository.UserRepository;
import ru.spacechat.repository.UserService;

import java.util.List;


@Slf4j
@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    @Qualifier("attrName")
    private String attrName;


    @MessageMapping("/chat.send")
    public void simple(Message reqt, SimpMessageHeaderAccessor headerAccessor) {
        String login = (String) headerAccessor.getSessionAttributes().get(attrName);
        if (Util.isEmpty(login)) {
            throw new OperationException("Вы не авторизованы в системе");
        }

        User currentUser = userRepository.getUser(login);
        if (currentUser == null) {
            throw new OperationException("Вы не авторизованы в системе");
        }

        if (reqt == null) {
            throw new OperationException("Сообщение не определено");
        }

        if (Util.isEmpty(reqt.getMessage())) {
            throw new OperationException("Сообщение не определено");
        }


        List<ChatInfo> allChats = chatRepository.getChatList(currentUser.getLogin());
        ChatInfo chat = null;

        for (ChatInfo chatInfo : allChats) {
            if (chatInfo.getId().equals(reqt.getChat())) {
                chat = chatInfo;
                break;
            }
        }

        if (chat == null) {
            throw new OperationException("Чат недоступен");
        }

        Message message = new Message();
        message.setChat(reqt.getChat());
        message.setInitiator(currentUser.getLogin());
        message.setMessage(reqt.getMessage());
        message.setAction(MessageAction.NEW_MESSAGE);
        message.setType(MessageType.TEXT);

        for (ChatMember member : chat.getMembers()) {
            simpMessagingTemplate.convertAndSend("/topic/chat/" + member.getLogin(), message);
        }
    }


}
