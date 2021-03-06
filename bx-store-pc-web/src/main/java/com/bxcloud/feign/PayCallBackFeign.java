package com.bxcloud.feign;

import com.bxcloud.api.service.PayCallBackService;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;

@Component
@FeignClient("/pay")
public interface PayCallBackFeign extends PayCallBackService {
}
