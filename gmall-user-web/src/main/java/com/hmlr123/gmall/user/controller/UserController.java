package com.hmlr123.gmall.user.controller;

import com.hmlr123.gmall.bean.UmsMember;
import com.hmlr123.gmall.bean.UmsMemberReceiveAddress;
import com.hmlr123.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @ClassName: UserController
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/7/23 22:59
 * @Version: 1.0
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("index")
    @ResponseBody
    public String index() {
        return "Hello World!";
    }

    @GetMapping("/list")
    @ResponseBody
    public List<UmsMember> getAllUser() {
        return userService.selectUsers();
    }

    @GetMapping("/receiveAddress/{memberId}")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemeberId(@PathVariable("memberId") String memberId) {
        return userService.getReceiveAddressByMemeberId(memberId);
    }
}
