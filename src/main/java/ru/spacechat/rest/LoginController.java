package ru.spacechat.rest;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String init() {
        return "index";
    }


    @RequestMapping(value = "/2", method = RequestMethod.GET)
    public String init2() {
        return "index2";
    }


}
