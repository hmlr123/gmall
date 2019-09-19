package com.hmlr123.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
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
@CrossOrigin(origins = "*", maxAge = 3600)//解决跨域问题 跨域访问注解
public class UserController {

    //远程协助代理
    @Reference
    private UserService userService;

    @GetMapping("index")
    @ResponseBody
    public String index() {
        return "Hello World!";
    }

    @GetMapping("list")
    @ResponseBody
    public List<UmsMember> getAllUser() {
        return userService.selectUsers();
    }

    @GetMapping("receiveAddress/{memberId}")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemeberId(@PathVariable("memberId") String memberId) {
        return userService.getReceiveAddressByMemeberId(memberId);
    }
}
