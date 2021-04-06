package weichai.fuzheng.springcloud.service;

import cn.hutool.core.util.IdUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {
    //正常访问OK的方法
    public String paymentInfo_OK(Integer id){
        return "线程池：" + Thread.currentThread().getName() + "payment_OK, id = " + id;
    }

    //模拟出错的方法
    @HystrixCommand(fallbackMethod = "errHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3050")
    })//设置自身超时时间、兜底的方法
    public String paymentInfo_Err(Integer id){
        int timeunit = 2;
        try{
            TimeUnit.SECONDS.sleep(timeunit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池：" + Thread.currentThread().getName() + "payment_Timeout, id = " + id;
    }

    public String errHandler(Integer id){
        return "线程池：" + Thread.currentThread().getName() + " 8001服务超时了，不好意思";
    }

    //----------服务熔断
    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),//开启熔断
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),//请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),//时间窗口
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60")//失败率
    })
    public String paymentCircuitBreaker(@PathVariable("id") Integer id){
        if (id < 0){
            throw new RuntimeException("id不能为负");
        }
        String serialNumber = IdUtil.simpleUUID();
        return Thread.currentThread().getName() + " 调用成功，流水号:"+ serialNumber;
    }
    public String paymentCircuitBreaker_fallback(@PathVariable("id") Integer id){
        return "id 不能为负数，请重试";
    }
}
