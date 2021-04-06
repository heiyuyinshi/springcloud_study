package weichai.fuzheng.springcloud.service;

import org.apache.ibatis.annotations.Param;
import weichai.fuzheng.springcloud.entities.Payment;

public interface PaymentService {
    public Integer create(Payment payment);

    public Payment getPaymentById(Long id);
}
