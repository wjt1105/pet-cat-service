package com.petsitter.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petsitter.common.entity.PetService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 喂猫服务 Mapper
 */
@Mapper
public interface PetServiceMapper extends BaseMapper<PetService> {

    /**
     * 原子扣减库存：库存充足才扣减，影响行数为 0 即库存不足
     */
    @Update("UPDATE pet_service SET stock = stock - #{count} " +
            "WHERE id = #{serviceId} AND stock >= #{count} AND is_deleted = 0")
    int deductStock(@Param("serviceId") Long serviceId, @Param("count") Integer count);
}
