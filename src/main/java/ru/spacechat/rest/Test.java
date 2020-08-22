package ru.spacechat.rest;


import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.spacechat.repository.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;





@RestController
public class Test {


    @Autowired
    private UserService userService;


    @GetMapping("/test")
    @ResponseBody
    public TestClass test(HttpServletRequest reqt) {
        HttpSession session = reqt.getSession(true);
        TestClass result = new TestClass();
        result.setValue(session.getId());
        return result;
    }


    @Data
    protected static class TestClass {
        private int number = 0;
        private String value = "121212121212";
    }

}
