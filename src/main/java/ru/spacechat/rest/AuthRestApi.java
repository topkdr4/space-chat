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

    private static final String AUDIO = "data:audio/ogg; codecs=opus;base64,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";

    @GetMapping("/audio")
    private SimpleResp<String> getAudio() {
        return new SimpleResp<>(AUDIO);
    }



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
        profile.setName(reqt.getName());

        userRepository.saveUser(result);

        profileRepository.saveUserProfile(result.getLogin(), profile);
        profileRepository.saveUserAvatar(result.getLogin(), personIcon);

        HttpSession session = request.getSession(true);
        session.setAttribute(attrName, result.getLogin());

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
