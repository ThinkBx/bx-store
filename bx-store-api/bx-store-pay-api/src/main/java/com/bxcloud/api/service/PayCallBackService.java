package com.bxcloud.api.service;

import com.bxcloud.base.ResponseBase;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RequestMapping(value = "/callBackService")
public interface PayCallBackService {

    //同步通知
    @RequestMapping(value = "/synCallBack")
    ResponseBase synCallBack(@RequestParam Map<String,String> params);

    //异步通知
    @RequestMapping(value = "/asynCallBack")
    String asynCallBack(@RequestParam Map<String,String> params);


}
