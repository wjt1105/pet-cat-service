package com.petsitter.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 上门喂猫服务
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pet_service")
public class PetService extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 铲屎官用户 ID */
    private Long sitterId;

    /** 服务标题 */
    private String title;

    /** 服务描述 */
    private String description;

    /** 服务项目（逗号分隔，如：添粮换水,清理猫砂,陪玩） */
    private String serviceItems;

    /** 服务价格（元） */
    private BigDecimal price;

    /** 服务时长（分钟） */
    private Integer duration;

    /** 所在城市 */
    private String city;

    /** 所在区县 */
    private String district;

    /** 封面图地址 */
    private String coverUrl;

    /** 库存（可预约次数） */
    private Integer stock;

    /** 状态：0-下架 1-上架 */
    private Integer status;
}
