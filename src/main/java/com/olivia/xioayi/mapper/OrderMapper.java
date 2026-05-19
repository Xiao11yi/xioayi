package com.olivia.xioayi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.olivia.xioayi.dao.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Update("UPDATE orders SET status = 1, alipay_trade_no = #{tradeNo}, pay_time = NOW() WHERE order_no = #{orderNo} AND status = 0")
    int markPaid(String orderNo, String tradeNo);

    @Update("UPDATE orders SET status = 2 WHERE status = 0 AND expire_time < NOW()")
    int expireOrders();
}
