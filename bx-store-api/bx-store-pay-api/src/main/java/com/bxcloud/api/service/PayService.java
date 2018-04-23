package com.bxcloud.api.service;

import com.bxcloud.base.ResponseBase;
import com.bxcloud.entity.PaymentInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/pay")
public interface PayService {

    //创建支付令牌
    @RequestMapping(value = "/createPayToken")
    ResponseBase createPayToken(@RequestBody PaymentInfo paymentInfo);

    //使用支付令牌查找支付信息
    @RequestMapping(value = "/findPayToken")
    ResponseBase findPayToken(@RequestParam("payToken") String payToken);


}
