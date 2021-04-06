package weichai.fuzheng.springcloud.service;

import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import weichai.fuzheng.springcloud.entities.CommonResult;
import weichai.fuzheng.springcloud.entities.Payment;

@Component
@FeignClient(value = "CLOUD-PAYMENT-SERVICE")//选择调用的微服务
public interface PaymentFeignService {
    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id);
}
