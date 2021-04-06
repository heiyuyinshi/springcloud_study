package weichai.fuzheng.springcloud.service;

import org.springframework.stereotype.Component;

@Component
public class PaymentFallbackService implements PaymentService{
    @Override
    public String paymentInfo_OK(Integer id) {
        return "fallback ok";
    }

    @Override
    public String paymentInfo_Err(Integer id) {
        return "fallback timeout";
    }
}
