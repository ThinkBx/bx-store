package com.bxcloud.controller;

import com.bxcloud.base.ResponseBase;
import com.bxcloud.constants.Constants;
import com.bxcloud.feign.MemberServiceFeign;
import com.bxcloud.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;

@Controller
public class IndexController {

    @Autowired
    private MemberServiceFeign memberServiceFeign;

    private static final String INDEX = "index";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest request) {
        // 1.从cookie中获取 token信息
        String token = CookieUtil.getUid(request, Constants.COOKIE_MEMBER_TOKEN);
        // 2.如果cookie 存在 token，调用会员服务接口，使用token查询用户信息
        if (!StringUtils.isEmpty(token)) {
            ResponseBase responseBase = memberServiceFeign.findUserByToken(token);
            if (responseBase.getCode().equals(Constants.HTTP_RES_CODE_200)) {
                LinkedHashMap userData = (LinkedHashMap) responseBase.getData();
                String username = (String) userData.get("username");
                request.setAttribute("username", username);
            }
        }


        return INDEX;
    }
}
