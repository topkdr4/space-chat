package ru.spacechat.rest;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.spacechat.commons.OperationException;
import ru.spacechat.commons.SimpleResp;
import ru.spacechat.commons.Util;
import ru.spacechat.model.User;
import ru.spacechat.model.UserProfile;
import ru.spacechat.repository.UserProfileRepository;
import ru.spacechat.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;





@RestController
@RequestMapping("user")
public class AuthRestApi {


    @Qualifier("attrName")
    @Autowired
    private String attrName;


    @Autowired
    private UserRepository userRepository;
    @Qualifier("personIcon")
    @Autowired
    private byte[] personIcon;
    @Autowired
    private UserProfileRepository profileRepository;


    @ResponseBody
    @PostMapping("/login")
    public SimpleResp login(@RequestBody LoginRrqt reqt, HttpServletRequest request, HttpServletResponse response) {
        if (Util.isEmpty(reqt.getLogin()))
            throw new OperationException("Не указан логин");

        if (Util.isEmpty((reqt.getPassword())))
            throw new OperationException("Не указан пароль");

        HttpSession session = request.getSession();
        session.invalidate();

        User user = userRepository.getUser(reqt.getLogin());

        if (user == null)
            throw new OperationException("Неверный логин или пароль");

        String passwordHash = Hashing.sha512()
                .hashBytes(reqt.getPassword().getBytes(StandardCharsets.UTF_8))
                .toString();

        if (!passwordHash.equals(user.getPassword()))
            throw new OperationException("Неверный логин или пароль");

        session = request.getSession(true);
        session.setAttribute(attrName, user.getLogin());

        return SimpleResp.EMPTY;
    }


    @ResponseBody
    @PostMapping("/logout")
    public SimpleResp logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return SimpleResp.EMPTY;
    }


    @ResponseBody
    @PostMapping("registration")
    public SimpleResp registration(@RequestBody RegistrationReqt reqt, HttpServletRequest request) {
        if (Util.isEmpty(reqt.getLogin()))
            throw new OperationException("Логин не указан");

        if (Util.isEmpty(reqt.getPassword()))
            throw new OperationException("Пароль не указан");

        if (Util.isEmpty(reqt.getPasswordConfirm()))
            throw new OperationException("Подтверждение пароля не указано");

        if (reqt.getLogin().length() > 16)
            throw new OperationException("Длина логина не может быть больше 16 символов");

        if (reqt.getLogin().length() < 6)
            throw new OperationException("Длина логина не может быть меньше 6 символов");

        if (reqt.getPassword().length() > 32)
            throw new OperationException("Длина пароля не может быть больше 32 символов");

        if (reqt.getPassword().length() < 6)
            throw new OperationException("Длина пароля не может быть менее 6 символов");

        if (!reqt.getPassword().equals(reqt.getPasswordConfirm()))
            throw new OperationException("Пароли не совпадают");

        User user = userRepository.getUser(reqt.getLogin());

        if (user != null)
            throw new OperationException("Такой логин уже используется");


        String passwordHash = Hashing.sha512()
                .hashBytes(reqt.getPassword().getBytes(StandardCharsets.UTF_8))
                .toString();

        User result = new User();
        result.setLogin(reqt.getLogin());
        result.setPassword(passwordHash);

        UserProfile profile = new UserProfile();
        profile.setStatus("");
        profile.setLogin(result.getLogin());
        profile.setName(reqt.getName());

        userRepository.saveUser(result);
        HttpSession session = request.getSession(true);

        session.setAttribute(attrName, result.getLogin());

        profileRepository.saveUserProfile(profile);
        profileRepository.saveUserAvatar(result.getLogin(), personIcon);

        return SimpleResp.EMPTY;
    }




    @Getter
    @Setter
    protected static class LoginRrqt {
        protected String login;
        protected String password;
    }




    @Getter
    @Setter
    protected static class RegistrationReqt {
        protected String login;
        protected String password;
        protected String passwordConfirm;
        protected String name;
    }


}
