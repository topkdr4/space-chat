package ru.spacechat.rest;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.spacechat.model.User;
import ru.spacechat.repository.UserRepository;





@Slf4j
@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private UserRepository repository;
    @Autowired
    @Qualifier("attrName")
    private String attrName;


    @MessageMapping("/chat/{user}")
    public void simple(@DestinationVariable String user, ru.spacechat.model.chat.Message message, SimpMessageHeaderAccessor headerAccessor) {

        String login = (String) headerAccessor.getSessionAttributes().get(attrName);

        log.info("login: {}", login);

        User currentUser = repository.getUser(login);

        Message result = new Message();
        result.setMessage(message.getMessage());
        result.setFrom(currentUser.getLogin());
    }




    @Data
    protected static class Message {
        protected final long timestamp = System.currentTimeMillis();
        protected String from;
        protected String message;
    }

}
