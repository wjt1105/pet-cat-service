package com.petsitter.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petsitter.common.entity.PetService;
import com.petsitter.common.exception.BusinessException;
import com.petsitter.common.result.ResultCode;
import com.petsitter.service.dto.ServicePageQuery;
import com.petsitter.service.mapper.PetServiceMapper;
import com.petsitter.service.service.PetServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 喂猫服务业务实现
 */
@Service
@RequiredArgsConstructor
public class PetServiceServiceImpl implements PetServiceService {

    private final PetServiceMapper petServiceMapper;

    @Override
    public IPage<PetService> pageList(ServicePageQuery query) {
        Page<PetService> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PetService> wrapper = new LambdaQueryWrapper<PetService>()
                .eq(PetService::getStatus, 1)
                .eq(StringUtils.hasText(query.getCity()), PetService::getCity, query.getCity())
                .like(StringUtils.hasText(query.getKeyword()), PetService::getTitle, query.getKeyword())
                .orderByDesc(PetService::getCreateTime);
        return petServiceMapper.selectPage(page, wrapper);
    }

    @Override
    public PetService getDetail(Long id) {
        PetService service = petServiceMapper.selectById(id);
        if (service == null) {
            throw new BusinessException(ResultCode.SERVICE_NOT_FOUND);
        }
        return service;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long serviceId, Integer count) {
        int affectedRows = petServiceMapper.deductStock(serviceId, count);
        if (affectedRows == 0) {
            throw new BusinessException(ResultCode.SERVICE_STOCK_NOT_ENOUGH);
        }
    }
}
