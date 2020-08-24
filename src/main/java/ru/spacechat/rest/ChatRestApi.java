package ru.spacechat.rest;


import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ru.spacechat.commons.OperationException;
import ru.spacechat.commons.SimpleResp;
import ru.spacechat.commons.Util;
import ru.spacechat.model.ChatInfo;
import ru.spacechat.model.ChatMember;
import ru.spacechat.model.User;
import ru.spacechat.model.chat.Message;
import ru.spacechat.model.chat.MessageAction;
import ru.spacechat.model.chat.MessageType;
import ru.spacechat.repository.ChatRepository;
import ru.spacechat.repository.UserService;

import java.util.List;
import java.util.Set;





@RestController
@RequestMapping("/api/chat")
public class ChatRestApi {


    @Autowired
    private UserService userService;


    @Autowired
    private ChatRepository chatRepository;


    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @ResponseBody
    @PostMapping("/create")
    public SimpleResp<ChatInfo> createChat(@RequestBody CreateChatReqt reqt) {
        User user = userService.getCurrentUser();

        if (reqt.getMembers() == null)
            throw new OperationException("Участники чата не определены");

        Set<String> members = reqt.getMembers();

        ChatInfo info;

        // Групповой чат
        if (members.size() > 1) {
            if (Util.isEmpty(reqt.getName()))
                throw new OperationException("Имя группового чата не определено");


            info = chatRepository.createGroupChat(user.getLogin(), reqt.getName(), reqt.getMembers());
        } else {
            String friend = null;

            for (String member : members) {
                if (true) {
                    friend = member;
                    break;
                }
            }

            if (Util.isEmpty(friend))
                throw new OperationException("Собеседник не определен");

            info = chatRepository.createFriendChat(user.getLogin(), friend);
        }

        return new SimpleResp<>(info);
    }


    @ResponseBody
    @PostMapping("/leave")
    public SimpleResp<Boolean> leave(@RequestBody LeaveChatReqt reqt) {
        if (Util.isEmpty(reqt.getId()))
            throw new OperationException("Идентификатор чата не определен");

        return new SimpleResp<>(true);
    }


    @ResponseBody
    @PostMapping("/list")
    public SimpleResp<List<ChatInfo>> chatList() {
        User user = userService.getCurrentUser();
        return new SimpleResp<>(chatRepository.getChatList(user.getLogin()));
    }


    @ResponseBody
    @PostMapping("/send")
    public SimpleResp send(@RequestBody SendMessageReqt reqt) {
        if (Util.isEmpty(reqt.getChat())) {
            throw new OperationException("Чат не определен");
        }

        if (Util.isEmpty(reqt.getMessage())) {
            throw new OperationException("Сообщение не определено");
        }

        User currentUser = userService.getCurrentUser();

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

        return SimpleResp.EMPTY;
    }




    @Data
    protected static class CreateChatReqt {
        protected String name;
        protected Set<String> members;
    }


    @Data
    protected static class LeaveChatReqt {
        protected String id;
    }


    @Data
    protected static class SendMessageReqt {
        protected String chat;
        protected String message;
    }
}
