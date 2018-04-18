package com.bxcloud.base;

import com.bxcloud.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseApiService {

    @Autowired
    protected BaseRedisService baseRedisService;

    public ResponseBase setResultError(Integer code,String msg) {
        return setResult(code, msg, null);
    }

    // 返回成功 ,data值为null
    public ResponseBase setResultSuccess() {
        return setResult(Constants.HTTP_RES_CODE_200, Constants.HTTP_RES_CODE_200_VALUE, null);
    }
    // 返回成功 ,data可传
    public ResponseBase setResultSuccess(Object data) {
        return setResult(Constants.HTTP_RES_CODE_200, Constants.HTTP_RES_CODE_200_VALUE, data);
    }

    // 返回成功，沒有data值
    public ResponseBase setResultSuccess(String msg) {
        return setResult(Constants.HTTP_RES_CODE_200, msg, null);
    }

    // 返回失败
    public ResponseBase setResultError(String msg){
        return setResult(Constants.HTTP_RES_CODE_500,msg, null);
    }
    // 自定义返回结果
    public ResponseBase setResult(Integer code, String msg, Object data) {
        ResponseBase responseBase = new ResponseBase();
        responseBase.setCode(code);
        responseBase.setMsg(msg);
        if (data != null)
            responseBase.setData(data);
        return responseBase;
    }
}
