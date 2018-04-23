package com.bxcloud.controller;

import com.bxcloud.base.ResponseBase;
import com.bxcloud.constants.Constants;
import com.bxcloud.feign.PayServiceFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

@Slf4j
@Controller
public class PayController {

    @Autowired
    private PayServiceFeign payServiceFeign;

    //使用payToken 进行支付
    @RequestMapping(value = "/aliPay")
    public void aliPay(HttpServletResponse response, String payToken) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = response.getWriter();
        // 1.参数验证
        if (StringUtils.isEmpty(payToken)) {
            return;
        }
        //2.调用支付服务接口，获取支付html元素
        ResponseBase payTokenResult = payServiceFeign.findPayToken(payToken);
        if (!payTokenResult.getCode().equals(Constants.HTTP_RES_CODE_200)) {
            String msg = payTokenResult.getMsg();
            writer.println(msg);
            return;
        }
        //3.返回可执行的html元素给客户端
        LinkedHashMap data = (LinkedHashMap) payTokenResult.getData();
        String payHtml = (String) data.get("payHtml");
        log.info("###PayController###payHtml:{}" + payHtml);
        //4.页面上进行渲染
        writer.println(payHtml);
        writer.close();

    }

}
