package com.petsitter.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petsitter.common.entity.PetService;
import com.petsitter.service.dto.ServicePageQuery;

/**
 * 喂猫服务业务
 */
public interface PetServiceService {

    /**
     * 分页查询上架的喂猫服务
     */
    IPage<PetService> pageList(ServicePageQuery query);

    /**
     * 根据 ID 查询服务详情
     */
    PetService getDetail(Long id);

    /**
     * 扣减库存（供订单服务远程调用）
     */
    void deductStock(Long serviceId, Integer count);
}
