package com.olivia.xioayi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.olivia.xioayi.dao.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    @Update("UPDATE coupon SET used_count = used_count + 1 WHERE id = #{id} AND (usage_limit = 0 OR used_count < usage_limit)")
    int incrementUsedCount(Long id);

    @Update("UPDATE coupon SET used_count = used_count - 1 WHERE id = #{id} AND used_count > 0")
    int decrementUsedCount(Long id);
}
