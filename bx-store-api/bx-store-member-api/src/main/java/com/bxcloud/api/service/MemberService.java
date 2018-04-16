package com.bxcloud.api.service;

import com.bxcloud.ResponseBase;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/member")
public interface MemberService {

    @RequestMapping(value = "/testResponse")
    ResponseBase testResponse();

}
