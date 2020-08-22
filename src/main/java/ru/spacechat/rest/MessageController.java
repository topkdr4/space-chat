package ru.spacechat.rest;


import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.spacechat.model.User;
import ru.spacechat.repository.UserRepository;
import ru.spacechat.wsmodel.InputMessage;





@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private UserRepository repository;
    @Autowired
    @Qualifier("attrName")
    private String attrName;


    @MessageMapping("/topic/chat/{user}")
    @SendTo("/topic/chat/{user}")
    public Message simple(@DestinationVariable String user, InputMessage message, SimpMessageHeaderAccessor headerAccessor) {

        String login = (String) headerAccessor.getSessionAttributes().get(attrName);

        User currentUser = repository.getUser(login);

        Message result = new Message();
        result.setMessage(message.getMessage());
        result.setFrom(currentUser.getLogin());

        return result;
    }




    @Data
    protected class Message {
        protected final long timestamp = System.currentTimeMillis();
        protected String from;
        protected String message;
    }

}
