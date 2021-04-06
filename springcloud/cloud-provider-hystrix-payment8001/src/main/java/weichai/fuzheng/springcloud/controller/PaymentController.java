package weichai.fuzheng.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import weichai.fuzheng.springcloud.service.PaymentService;

import javax.annotation.Resource;

@RestController
@Slf4j
public class PaymentController {
    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/payment/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id){
        String result = paymentService.paymentInfo_OK(id);
        log.info("*****result: "+ result);
        return result;
    }

    @GetMapping("/payment/err/{id}")
    public String paymentInfo_Err(@PathVariable("id") Integer id){
        String result = paymentService.paymentInfo_Err(id);
        log.info("*****result: "+ result);
        return result;
    }
    //熔断服务
    @GetMapping("/payment/breaker/{id}")
    public String paymentCircuitBreaker(@PathVariable("id") Integer id){
        String result = paymentService.paymentCircuitBreaker(id);
        log.info("****result:" + result);
        return result;
    }
}
