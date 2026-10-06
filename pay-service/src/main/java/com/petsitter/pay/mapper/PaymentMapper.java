package com.petsitter.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petsitter.common.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付单 Mapper
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
