package com.bxcloud.api.service;

import com.bxcloud.BaseController;
import com.bxcloud.ResponseBase;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberServiceImpl  extends BaseController implements MemberService {

    @Override
    public ResponseBase testResponse() {
        return setResultSuccess();
    }
}
