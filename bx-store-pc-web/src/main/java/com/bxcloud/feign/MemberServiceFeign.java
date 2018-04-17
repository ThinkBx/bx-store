package com.bxcloud.feign;

import com.bxcloud.api.service.MemberService;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;

@Component
@FeignClient("/member")
public interface MemberServiceFeign extends MemberService {

}
