package ru.spacechat.rest;


import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.spacechat.commons.OperationException;
import ru.spacechat.commons.SimpleResp;
import ru.spacechat.commons.Util;
import ru.spacechat.model.AvailableFriend;
import ru.spacechat.model.Friend;
import ru.spacechat.model.User;
import ru.spacechat.repository.FriendRepository;
import ru.spacechat.repository.SearchReqt;
import ru.spacechat.repository.SearchResp;
import ru.spacechat.repository.UserService;





@RestController
@RequestMapping("api/friends")
public class FriendsRestApi {


    @Autowired
    private UserService userService;


    @Autowired
    private FriendRepository repository;


    @ResponseBody
    @PostMapping("/list")
    public SimpleResp<SearchResp<Friend>> getFriends(@RequestBody SearchReqt reqt) {
        User user = userService.getCurrentUser();

        return new SimpleResp<>(repository.getFriendList(user.getLogin(), reqt));
    }


    @ResponseBody
    @PostMapping("/search")
    public SimpleResp<SearchResp<AvailableFriend>> search(@RequestBody SearchReqt reqt) {
        User user = userService.getCurrentUser();

        return new SimpleResp<>(repository.searchFriend(user.getLogin(), reqt));
    }


    @ResponseBody
    @PostMapping("/add")
    public SimpleResp<Boolean> add(@RequestBody FriendReqt reqt) {
        User user = userService.getCurrentUser();

        if (Util.isEmpty(reqt.getLogin()))
            throw new OperationException("Пользователь не определен");

        if (user.getLogin().equals(reqt.getLogin()))
            throw new OperationException("Вы не можете добавть себя в друзья");

        repository.addFriend(user.getLogin(), reqt.getLogin());

        return new SimpleResp<>(true);
    }


    @ResponseBody
    @PostMapping("/remove")
    public SimpleResp<Boolean> remove(@RequestBody FriendReqt reqt) {
        User user = userService.getCurrentUser();

        if (Util.isEmpty(reqt.getLogin()))
            throw new OperationException("Пользователь не определен");

        if (user.getLogin().equals(reqt.getLogin()))
            throw new OperationException("Вы не может удалить себя из друзей");

        repository.removeFriend(user.getLogin(), reqt.getLogin());

        return new SimpleResp<>(true);
    }


    /**
     * Отменить исходящую заявку
     */
    @ResponseBody
    @PostMapping("/request/cancel")
    public SimpleResp<Boolean> cancel(@RequestBody FriendReqt reqt) {
        User user = userService.getCurrentUser();

        repository.cancelRequest(user.getLogin(), reqt.getLogin());

        return new SimpleResp<>(true);
    }


    /**
     * Принять входящую заявку
     */
    @ResponseBody
    @PostMapping("/request/accept")
    public SimpleResp<Boolean> accept(@RequestBody FriendReqt reqt) {
        User user = userService.getCurrentUser();

        repository.acceptRequest(user.getLogin(), reqt.getLogin());

        return new SimpleResp<>(true);
    }


    /**
     * Отклонить входящую заявку
     */
    @ResponseBody
    @PostMapping("/request/abort")
    public SimpleResp<Boolean> abort(@RequestBody FriendReqt reqt) {
        User user = userService.getCurrentUser();

        repository.abortRequest(user.getLogin(), reqt.getLogin());

        return new SimpleResp<>(true);
    }


    /**
     * Список входящих заявок
     */
    @ResponseBody
    @PostMapping("/request/list/in")
    public SimpleResp<SearchResp<Friend>> getRequestListIncoming(@RequestBody SearchReqt reqt) {
        User user = userService.getCurrentUser();

        return new SimpleResp<>(repository.getIncomingFriendList(user.getLogin(), reqt));
    }


    /**
     * Список исходящих заявок
     */
    @ResponseBody
    @PostMapping("/request/list/out")
    public SimpleResp<SearchResp<Friend>> getRequestListOutcoming(@RequestBody SearchReqt reqt) {
        User user = userService.getCurrentUser();

        return new SimpleResp<>(repository.getRequestedFriendList(user.getLogin(), reqt));
    }




    @Data
    protected static class FriendReqt {
        protected String login;
    }


}
