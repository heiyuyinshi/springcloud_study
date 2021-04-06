package weichai.fuzheng.springcloud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import weichai.fuzheng.springcloud.entities.CommonResult;
import weichai.fuzheng.springcloud.entities.Payment;

import javax.annotation.Resource;

@RestController
public class TestController {

    public static final String INVOKE_URL = "http://cloud-payment-service";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id") Long id){
        return restTemplate.getForObject(INVOKE_URL + "/payment/get/" + id, CommonResult.class);
    }
}
