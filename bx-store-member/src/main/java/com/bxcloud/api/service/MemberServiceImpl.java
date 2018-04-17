package com.bxcloud.api.service;

import com.alibaba.fastjson.JSONObject;
import com.bxcloud.base.BaseApiService;
import com.bxcloud.base.ResponseBase;
import com.bxcloud.com.bxcloud.constants.Constants;
import com.bxcloud.com.bxcloud.utils.MD5Util;
import com.bxcloud.com.bxcloud.utils.TokenUtils;
import com.bxcloud.dao.UserDao;
import com.bxcloud.entity.User;
import com.bxcloud.mq.RegisterMailboxProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
public class MemberServiceImpl extends BaseApiService implements MemberService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RegisterMailboxProducer registerMailboxProducer;

    @Value("${messages.queue}")
    private String MESSAGESQUEUE;

    @Override
    public ResponseBase testResponse() {
        return setResultSuccess();
    }

    @Override
    public ResponseBase findUserById(Long userId) {
        User user = userDao.findUserById(userId);
        if (user == null) {
            return setResultError("未查找到用户信息.");
        }
        return setResultSuccess(user);
    }

    @Override
    public ResponseBase regUser(@RequestBody User user) {
        // 参数验证
        String password = user.getPassword();
        if (StringUtils.isEmpty(password)) {
            return setResultError("密码不能为空.");
        }
        String newPassword = MD5Util.MD5(password);
        user.setPassword(newPassword);
        user.setCreated(new Date());
        user.setUpdated(new Date());
        Integer result = userDao.insertUser(user);
        if (result < 0) {
            return setResultError("注册用户信息失败.");
        }
        // 采用异步方式发送消息
        String email = user.getEmail();
        String json = emailJson(email);
        log.info("####会员服务推送消息到消息服务平台####json:{}", json);
        sendMsg(json);
        return setResultSuccess("用户注册成功.");
    }

    private String emailJson(String email) {
        JSONObject rootJson = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", Constants.MSG_EMAIL);
        JSONObject content = new JSONObject();
        content.put("email", email);
        rootJson.put("header", header);
        rootJson.put("content", content);
        return rootJson.toJSONString();
    }

    private void sendMsg(String json) {
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(MESSAGESQUEUE);
        registerMailboxProducer.sendMsg(activeMQQueue, json);
    }

    @Override
    public ResponseBase login(@RequestBody User user) {
        // 1.验证参数
        String username = user.getUsername();
        if (StringUtils.isEmpty(username)) {
            return setResultError("用戶名称不能为空!");
        }
        String password = user.getPassword();
        if (StringUtils.isEmpty(password)) {
            return setResultError("密码不能为空!");
        }

        // 2.数据库查找账号密码是否正确
        String newPassWrod = MD5Util.MD5(password);
        User userEntity = userDao.login(username, newPassWrod);
        if (userEntity == null) {
            return setResultError("账号或者密码不能正确");
        }
        // 3.如果账号密码正确，对应生成token
        String memberToken = TokenUtils.getMemberToken();
        // 4.存放在redis中，key为token value 为 userid
        Integer userId = userEntity.getId();
        log.info("####用户信息token存放在redis中... key为:{},value", memberToken, userId);
        baseRedisService.setString(memberToken, userId + "", Constants.TOKEN_MEMBER_TIME);

        // 5.直接返回token
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memberToken", memberToken);
        return setResultSuccess(jsonObject);

    }

    @Override
    public ResponseBase findUserByToken(String token) {
        // 1.验证参数
        if (StringUtils.isEmpty(token)) {
            return setResultError("token不能为空!");
        }
        // 2.从redis中 使用token 查找对应 userid
        String strUserId = (String) baseRedisService.getString(token);
        if (StringUtils.isEmpty(strUserId)) {
            return setResultError("token无效或者已经过期!");
        }
        // 3.使用userid数据库查询用户信息返回给客户端
        Long userId = Long.parseLong(strUserId);
        User userEntity = userDao.findUserById(userId);
        if (userEntity == null) {
            return setResultError("为查找到该用户信息");
        }
        userEntity.setPassword(null);
        return setResultSuccess(userEntity);
    }
}
