package ru.spacechat.rest;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.spacechat.commons.SimpleResp;





@RestController
@RequestMapping("api/session")
public class SessionRestApi {

    @ResponseBody
    @PostMapping("/check")
    public SimpleResp<Boolean> check() {
        return new SimpleResp<>(true);
    }

}
