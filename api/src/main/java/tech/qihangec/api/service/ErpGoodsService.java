package tech.qihangec.api.service;

import tech.qihangec.api.common.PageQuery;
import tech.qihangec.api.common.PageResult;
import tech.qihangec.api.domain.ErpGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import tech.qihangec.api.domain.ErpGoodsSku;

/**
* @author TW
* @description 针对表【erp_goods(商品库存管理)】的数据库操作Service
* @createDate 2024-04-12 14:52:05
*/
public interface ErpGoodsService extends IService<ErpGoods> {
    PageResult<ErpGoods> queryPageList(ErpGoods bo, PageQuery pageQuery);
    PageResult<ErpGoodsSku> querySkuPageList(ErpGoodsSku bo, PageQuery pageQuery);
    int insertGoods(ErpGoods goods);
}
