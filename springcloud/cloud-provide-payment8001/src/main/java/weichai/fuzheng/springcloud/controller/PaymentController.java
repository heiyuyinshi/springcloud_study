package weichai.fuzheng.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import weichai.fuzheng.springcloud.entities.CommonResult;
import weichai.fuzheng.springcloud.entities.Payment;
import weichai.fuzheng.springcloud.service.PaymentService;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Resource
    private DiscoveryClient discoveryClient;

    @Value("${server.port}")
    private String serverPort;

    @PostMapping(value = "/payment/create")
    public CommonResult creat(@RequestBody Payment payment){
        int result = paymentService.create(payment);
        log.info("**************插入结果："+ result);
        if (result > 0){
            return new CommonResult(200,"插入数据成功, server" + serverPort,result);
        }else {
            return new CommonResult(444,"插入数据失败",null);
        }
    }

    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id){
        Payment result = paymentService.getPaymentById(id);
        log.info("**************查询结果："+ result);
        if (result != null){
            return new CommonResult(200,"查询数据成功, server" + serverPort,result);
        }else {
            return new CommonResult(444,"查询数据失败",null);
        }
    }

    @GetMapping("/payment/discovery")
    public Object discovery(){
        //服务清单列表
        List<String> services = discoveryClient.getServices();
        for (String service: services
             ) {
            log.info(service);
        }

        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance : instances) {
            log.info(instance.getServiceId() + "\t" + instance.getHost() + "\t" + instance.getPort() + "\t" + instance.getUri());
        }
        return this.discoveryClient;
    }
}
