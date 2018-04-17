package com.bxcloud.api.service;

import com.bxcloud.base.ResponseBase;
import com.bxcloud.entity.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/member")
public interface MemberService {

    @RequestMapping(value = "/testResponse")
    ResponseBase testResponse();

    //主键查询
    @RequestMapping(value = "/findUserById")
    ResponseBase findUserById(Long userId);

    //用户注册
    @RequestMapping(value = "/regUser")
    ResponseBase regUser(@RequestBody User user);

    //用户登录
    @RequestMapping(value = "/login")
    ResponseBase login(@RequestBody User user);

    //使用token进行登录
    @RequestMapping(value = "/findUserByToken")
    ResponseBase findUserByToken(String token);


}
