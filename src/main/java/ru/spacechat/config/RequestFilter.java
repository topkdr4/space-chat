package ru.spacechat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import ru.spacechat.commons.OperationException;
import ru.spacechat.commons.Util;
import ru.spacechat.model.User;
import ru.spacechat.repository.UserRepository;
import ru.spacechat.repository.UserService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;





public class RequestFilter implements Filter {

    private final UserService userService;

    private final UserRepository userRepository;

    private final String attributeName;

    private final ObjectMapper mapper;


    public RequestFilter(UserRepository userRepository,
                         UserService userService,
                         ObjectMapper mapper,
                         String attributeName) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mapper = mapper;
        this.attributeName = attributeName;
    }


    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpSession session = request.getSession(false);

            if (session == null)
                throw new OperationException("Вы не авторизованы в системе");

            String login = String.valueOf(session.getAttribute(attributeName));

            if (Util.isEmpty(login))
                throw new OperationException("Вы не авторизованы в системе");

            User user = userRepository.getUser(login);

            if (user == null)
                throw new OperationException("Вы не авторизованы в системе");

            userService.setCurrentUser(user);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Content-Type", "application/json; utf-8");
            response.getWriter().write(mapper.writeValueAsString(new RestApiErrorHandler.ErrorResp(1, "Вы не авторизованы в системе")));
        }
    }



    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilterInternal((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }
}
