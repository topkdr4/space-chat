package ru.spacechat.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.spacechat.commons.OperationException;
import ru.spacechat.commons.SimpleResp;
import ru.spacechat.commons.Util;
import ru.spacechat.model.User;
import ru.spacechat.model.UserProfile;
import ru.spacechat.repository.UserProfileRepository;
import ru.spacechat.repository.UserService;





@RestController
@RequestMapping("api/profile")
public class ProfileRestApi {

    @Autowired
    private UserService userService;


    @Autowired
    private UserProfileRepository profileRepository;


    @ResponseBody
    @PostMapping("/get")
    public SimpleResp<UserProfile> getProfile() {
        User user = userService.getCurrentUser();

        UserProfile result = profileRepository.getUserProfile(user.getLogin());

        return new SimpleResp<>(result);
    }


    @ResponseBody
    @PostMapping("/save")
    public SimpleResp saveProfile(@RequestBody UserProfile reqt) {
        if (Util.isEmpty(reqt.getName())) {
            throw new OperationException("Имя не определено");
        }

        if (!Util.isEmpty(reqt.getPhone())) {
            if (reqt.getPhone().length() != 10) {
                throw new OperationException("Неверный форман Номера телефона");
            }

            if (!reqt.getPhone().matches("^\\d+$")) {
                throw new OperationException("Неверный формат номера телефона");
            }

            if (reqt.getPhone().charAt(0) != '9') {
                throw new OperationException("Номер телефона должен начинаться с `9`");
            }
        }


        User user = userService.getCurrentUser();

        profileRepository.saveUserProfile(user.getLogin(), reqt);

        return SimpleResp.EMPTY;
    }


    @ResponseBody
    @PostMapping("/avatar/upload")
    public SimpleResp<Boolean> uploadAvatart(@RequestParam("file") MultipartFile file) {
        try {
            if (file.getBytes().length > 2097152) {
                throw new OperationException("Размер файла не должен превышать 2мб");
            }

            User user = userService.getCurrentUser();
            profileRepository.saveUserAvatar(user.getLogin(), file.getBytes());
            return new SimpleResp<>(true);
        } catch (Exception e) {
            throw new OperationException(e);
        }
    }


    @ResponseBody
    @GetMapping("/{login}/avatar")
    public byte[] getUserAvatar(@PathVariable("login") String login) {
        return profileRepository.getAvatar(login);
    }

}
