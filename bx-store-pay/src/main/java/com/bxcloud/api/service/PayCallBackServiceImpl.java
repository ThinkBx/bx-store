package com.bxcloud.api.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.config.AlipayConfig;
import com.bxcloud.base.BaseApiService;
import com.bxcloud.base.ResponseBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class PayCallBackServiceImpl extends BaseApiService implements PayCallBackService {

    @Override
    public ResponseBase synCallBack(Map<String, String> params) {
        //获取支付宝GET过来反馈信息
        boolean signVerified = false; //调用SDK验证签名
        try {
            log.info("####同步回调开始####params:", params);
            // 调用SDK验证签名
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
            //——请在这里编写您的程序（以下代码仅作参考）——
            if (!signVerified) {
                return setResultError("验签失败！");
            }
            //商户订单号
            String out_trade_no = params.get("out_trade_no");
            //支付宝交易号
            String trade_no = params.get("trade_no");
            //付款金额
            String total_amount = params.get("total_amount");
            JSONObject data = new JSONObject();
            data.put("out_trade_no", out_trade_no);
            data.put("trade_no", trade_no);
            data.put("total_amount", total_amount);
            return setResultSuccess(data);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            log.info("######PayCallBackServiceImpl##ERROR:#####", e);
            return setResultError("系统错误!");
        } finally {
            log.info("####同步回调结束####params:", params);
        }
    }

    @Override
    public String asynCallBack(Map<String, String> params) {
        return null;
    }
}
