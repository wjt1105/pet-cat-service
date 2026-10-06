package com.petsitter.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petsitter.common.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
