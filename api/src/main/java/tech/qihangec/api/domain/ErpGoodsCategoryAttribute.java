package tech.qihangec.api.domain;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName erp_goods_category_attribute
 */
@Data
public class ErpGoodsCategoryAttribute implements Serializable {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 
     */
    private Integer categoryId;

    /**
     * 类型：0属性1规格
     */
    private Integer type;

    /**
     * '属性名'
     */
    private String title;

    /**
     * 固定值color颜色size尺码style款式
     */
    private String code;

    private static final long serialVersionUID = 1L;
}