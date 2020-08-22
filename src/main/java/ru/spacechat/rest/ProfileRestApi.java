package ru.spacechat.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.spacechat.commons.OperationException;
import ru.spacechat.commons.SimpleResp;
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
    private UserProfileRepository repository;


    @ResponseBody
    @PostMapping("/get")
    public SimpleResp<UserProfile> getProfile() {
        User user = userService.getCurrentUser();

        UserProfile result = repository.getUserProfile(user.getLogin());

        return new SimpleResp<>(result);
    }


    @ResponseBody
    @PostMapping("/avatar/upload")
    public SimpleResp<Boolean> uploadAvatart(@RequestParam("file") MultipartFile file) {
        try {
            User user = userService.getCurrentUser();
            repository.saveUserAvatar(user.getLogin(), file.getBytes());
            return new SimpleResp<>(true);
        } catch (Exception e) {
            throw new OperationException(e);
        }
    }


    @ResponseBody
    @GetMapping("/{login}/avatar")
    public byte[] getUserAvatar(@PathVariable("login") String login) {
        return repository.getUserAvatar(login);
    }

}
