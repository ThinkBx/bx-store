package com.bxcloud.controller;

import com.bxcloud.feign.MemberServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RegisterController {

    @Autowired
    private MemberServiceFeign memberServiceFeign;



}
