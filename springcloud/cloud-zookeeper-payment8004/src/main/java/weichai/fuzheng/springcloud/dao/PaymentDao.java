package weichai.fuzheng.springcloud.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import weichai.fuzheng.springcloud.entities.Payment;

@Mapper
public interface PaymentDao {
    public Integer create(Payment payment);

    public Payment getPaymentById(@Param("id") Long id);
}
