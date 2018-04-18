package com.bxcloud.controller;

import com.bxcloud.base.ResponseBase;
import com.bxcloud.constants.Constants;
import com.bxcloud.entity.User;
import com.bxcloud.feign.MemberServiceFeign;
import com.bxcloud.utils.CookieUtil;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.oauth.Oauth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;

@Controller
public class LoginController {

    @Autowired
    private MemberServiceFeign memberServiceFeign;

    private static final String LOGIN = "login";
    private static final String INDEX = "redirect:/";
    private static final String QQRELATION = "qqrelation";

    //跳转登录页面
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginGet() {
        return LOGIN;
    }

    //登录
    public String loginPost(User user, HttpServletRequest request, HttpServletResponse response) {
        // 1.验证参数
        // 2.调用登录接口，获取token信息
        ResponseBase loginBase = memberServiceFeign.login(user);
        if (!loginBase.getCode().equals(Constants.HTTP_RES_CODE_200)) {
            request.setAttribute("error", "账号或密码错误");
            return LOGIN;
        }

        //通过feign接口调用的 用LinkedHashMap 接收
        LinkedHashMap loginData = (LinkedHashMap) loginBase.getData();
        String memberToken = (String) loginData.get("memberToken");
        if (StringUtils.isEmpty(memberToken)) {
            request.setAttribute("error", "会话已失效！");
            return LOGIN;
        }
        // 3.将token信息存放在cookie里面
        setCookie(memberToken, response);
        return INDEX;
    }

    public void setCookie(String memberToken, HttpServletResponse response) {
        CookieUtil.addCookie(response, Constants.COOKIE_MEMBER_TOKEN, memberToken, Constants.COOKIE_TOKEN_MEMBER_TIME);
    }

    //生成qq授权登录链接
    public String localQQLogin(HttpServletRequest request) throws QQConnectException {
        String authorizeURL = new Oauth().getAuthorizeURL(request);
        return "redirect:" + authorizeURL;
    }

    //回调
    @RequestMapping(value = "/qqLoginCallback")
    public String qqLoginCallback(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws QQConnectException {
        // 1.获取授权码COde
        // 2.使用授权码Code获取accessToken
        AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
        if (accessTokenObj == null) {
            request.setAttribute("error", "QQ授权失败");
            return "error";
        }
        String accessToken = accessTokenObj.getAccessToken();
        if (accessToken == null) {
            request.setAttribute("error", "accessToken为null");
            return "error";
        }
        // 3.使用accessToken获取openid
        OpenID openidOj = new OpenID(accessToken);
        String userOpenId = openidOj.getUserOpenID();
        // 4.调用会员服务接口 使用userOpenId 查找是否已经关联过账号
        ResponseBase openUserBase = memberServiceFeign.findUserByOpenId(userOpenId);
        if (openUserBase.getCode().equals(Constants.HTTP_RES_CODE_201)) {
            // 5.如果没有关联账号，跳转到关联账号页面
            session.setAttribute("qqOpenid", userOpenId);
            return QQRELATION;
        }
        //6.已经绑定账号  自动登录 将用户token信息存放在cookie中
        LinkedHashMap dataTokenMap = (LinkedHashMap) openUserBase.getData();
        String memberToken = (String) dataTokenMap.get("memberToken");
        setCookie(memberToken, response);
        return INDEX;
    }

    //qq授权关联页面 已有账号
    public String qqRelation(User user, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        // 1.获取openid
        String qqOpenid = (String) session.getAttribute("qqOpenid");
        if (StringUtils.isEmpty(qqOpenid)) {
            request.setAttribute("error", "没有获取到openid");
            return "error";
        }
        // 2.调用登录接口，获取token信息
        user.setOpenid(qqOpenid);
        ResponseBase loginBase = memberServiceFeign.qqLogin(user);
        if (!loginBase.getCode().equals(Constants.HTTP_RES_CODE_200)) {
            request.setAttribute("error", "账号或者密码错误!");
            return LOGIN;
        }

        LinkedHashMap loginData = (LinkedHashMap) loginBase.getData();
        String memberToken = (String) loginData.get("memberToken");
        if (StringUtils.isEmpty(memberToken)) {
            request.setAttribute("error", "会话已经失效!");
            return LOGIN;
        }
        // 3.将token信息存放在cookie里面
        setCookie(memberToken, response);
        return INDEX;
    }


}
