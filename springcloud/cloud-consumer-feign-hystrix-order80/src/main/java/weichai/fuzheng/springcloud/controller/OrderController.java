package weichai.fuzheng.springcloud.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import weichai.fuzheng.springcloud.service.PaymentService;

import javax.annotation.Resource;

@RestController
@Slf4j
@DefaultProperties(defaultFallback = "global_fallback")
public class OrderController {
    @Resource
    private PaymentService paymentService;

    @GetMapping("/order/ok/{id}")
    public String Payment_OK(@PathVariable("id") Integer id){
        return paymentService.paymentInfo_OK(id);
    }

    @GetMapping("/order/err/{id}")
//    @HystrixCommand(fallbackMethod = "errHandler", commandProperties = {
//            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
//    })//设置自身超时时间、兜底的方法
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    public String Payment_Err(@PathVariable("id") Integer id){
        return paymentService.paymentInfo_Err(id);
    }

    public String errHandler(@PathVariable("id") Integer id){
        return "线程池：" + Thread.currentThread().getName() + " 80服务超时了，不好意思";
    }
    //全局fallback
    public String global_fallback(){
        return "全局错误处理";
    }
}
