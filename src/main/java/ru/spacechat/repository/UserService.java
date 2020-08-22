package ru.spacechat.repository;

import org.springframework.stereotype.Service;
import ru.spacechat.model.User;





@Service
public class UserService {

    private static final ThreadLocal<User> currentUser = ThreadLocal.withInitial(() -> null);


    public void setCurrentUser(User user) {
        currentUser.set(user);
    }


    public User getCurrentUser() {
        return currentUser.get();
    }

}
