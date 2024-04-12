package tech.qihangec.api.domain;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName erp_goods_sku_attr
 */
@Data
public class ErpGoodsSkuAttr implements Serializable {
    /**
     * 
     */
    private Long id;

    /**
     * 
     */
    private Long goodsId;

    /**
     * 
     */
    private String type;

    /**
     * 
     */
    private String k;

    /**
     * 
     */
    private Integer kid;

    /**
     * 
     */
    private Integer vid;

    /**
     * 
     */
    private String v;

    /**
     * 
     */
    private String img;

    private static final long serialVersionUID = 1L;
}