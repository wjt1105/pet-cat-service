package com.petsitter.service.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 服务列表分页查询条件
 */
@Data
public class ServicePageQuery {

    @Min(value = 1, message = "页码最小为 1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数最小为 1")
    @Max(value = 100, message = "每页条数最大为 100")
    private Integer pageSize = 10;

    /** 城市（精确匹配，可选） */
    private String city;

    /** 标题关键字（模糊匹配，可选） */
    private String keyword;
}
