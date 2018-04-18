package com.bxcloud.controller;

import com.bxcloud.base.ResponseBase;
import com.bxcloud.constants.Constants;
import com.bxcloud.entity.User;
import com.bxcloud.feign.MemberServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class RegisterController {

    @Autowired
    private MemberServiceFeign memberServiceFeign;

    private static final String REGISTER = "register";
    private static final String LOGIN = "login";

    // 跳转注册页面
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String regiserGet() {
        return REGISTER;
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String registerPost(User user, HttpServletRequest request) {
        // 1. 验证参数
        // 2. 调用会员注册接口
        ResponseBase regUser = memberServiceFeign.regUser(user);
        // 3. 如果失败，跳转到失败页面
        if(!regUser.getCode().equals(Constants.HTTP_RES_CODE_200)){
            request.setAttribute("error","注册失败");
            return REGISTER;
        }
        // 4. 如果成功,跳转到成功页面
        return LOGIN;
    }


}
