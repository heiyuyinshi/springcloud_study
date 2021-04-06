package weichai.fuzheng.springcloud.service.impl;

import org.springframework.stereotype.Service;
import weichai.fuzheng.springcloud.dao.PaymentDao;
import weichai.fuzheng.springcloud.entities.Payment;
import weichai.fuzheng.springcloud.service.PaymentService;

import javax.annotation.Resource;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Resource
    private PaymentDao paymentDao;

    @Override
    public Integer create(Payment payment) {
        return paymentDao.create(payment);
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentDao.getPaymentById(id);
    }
}
