package tech.qihangec.api.service;

import tech.qihangec.api.common.PageQuery;
import tech.qihangec.api.common.PageResult;
import tech.qihangec.api.domain.ErpGoods;
import tech.qihangec.api.domain.ErpGoodsCategory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author TW
* @description 针对表【erp_goods_category】的数据库操作Service
* @createDate 2024-04-12 15:01:52
*/

public interface ErpGoodsCategoryService extends IService<ErpGoodsCategory> {
    PageResult<ErpGoodsCategory> queryPageList(ErpGoodsCategory bo, PageQuery pageQuery);
}
