package com.tianji.promotion.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuyp
 * @since 2023/07/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ExchangeCodeVO {
    /**兑换码的id*/
    private Integer id;
    /**兑换码code*/
    private String code;
}