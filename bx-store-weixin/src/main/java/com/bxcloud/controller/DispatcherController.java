package com.bxcloud.controller;

import com.bxcloud.base.TextMessage;
import com.bxcloud.utils.CheckUtil;
import com.bxcloud.utils.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

@Slf4j
@RestController
public class DispatcherController {

    // 服务器验证接口地址
    @RequestMapping(value = "/dispatcher", method = RequestMethod.GET)
    public String dispatcherGet(String signature, String timestamp, String nonce, String echostr) {
        // 1.验证参数
        boolean checkSignature = CheckUtil.checkSignature(signature, timestamp, nonce);
        // 2.参数验证成功之后，返回随机数
        if (!checkSignature) {
            return null;
        }
        return echostr;
    }


    //微信动作推送
    @RequestMapping(value = "/dispatcher", method = RequestMethod.POST)
    public void dispatcherPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        //1.将XML转换为Map格式
        Map<String, String> resultMap = XmlUtils.parseXml(request);
        log.info("###收到微信消息####resultMap:" + resultMap.toString());
        //2.判断消息类型
        String msgType = resultMap.get("MsgType");
        //3.如果是文本类型，返回结果给微信服务端
        PrintWriter writer = response.getWriter();
        switch (msgType) {
            case "text":
                //开发者微信公众号
                String toUserName = resultMap.get("ToUserName");
                //消息来自公众号
                String fromUserName = resultMap.get("FromUserName");
                //消息内容
                String content = resultMap.get("Content");
                String msg = null;
                log.info("###给微信发送消息###msg:" + msg);
                if ("蚂蚁课堂".contains(content)) {
                    msg = setText("", toUserName, fromUserName);
                }
                writer.println(msg);
                break;
            default:
                break;
        }
    }


    public String setText(String content, String fromUserName, String toUserName) {
        TextMessage textMessage = new TextMessage();
        textMessage.setContent(content);
        textMessage.setMsgType("text");
        textMessage.setCreateTime(new Date().getTime());
        textMessage.setFromUserName(fromUserName);
        textMessage.setToUserName(toUserName);
        // 将实体类转换成xml格式
        String msg = XmlUtils.messageToXml(textMessage);
        return msg;

    }


}
