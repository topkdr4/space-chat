package ru.spacechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import ru.spacechat.commons.OperationException;
import ru.spacechat.commons.Util;
import ru.spacechat.model.User;
import ru.spacechat.repository.UserRepository;
import ru.spacechat.repository.UserService;

import javax.servlet.http.HttpSession;
import java.util.Map;





@Service
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("attrName")
    private String attrName;


    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) serverHttpRequest;
            HttpSession session = servletRequest.getServletRequest().getSession();

            try {
                if (session == null)
                    throw new OperationException("Вы не авторизованы в системе");

                String login = String.valueOf(session.getAttribute(attrName));

                if (Util.isEmpty(login))
                    throw new OperationException("Вы не авторизованы в системе");

                User user = userRepository.getUser(login);

                if (user == null)
                    throw new OperationException("Вы не авторизованы в системе");

                userService.setCurrentUser(user);

                map.put(attrName, login);

            } catch (Exception e) {

            }

        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
