package tech.qihangec.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import tech.qihangec.api.common.PageQuery;
import tech.qihangec.api.common.PageResult;
import tech.qihangec.api.domain.ErpGoods;
import tech.qihangec.api.domain.ErpGoodsCategory;
import tech.qihangec.api.service.ErpGoodsCategoryService;
import tech.qihangec.api.mapper.ErpGoodsCategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author TW
* @description 针对表【erp_goods_category】的数据库操作Service实现
* @createDate 2024-04-12 15:01:52
*/
@AllArgsConstructor
@Service
public class ErpGoodsCategoryServiceImpl extends ServiceImpl<ErpGoodsCategoryMapper, ErpGoodsCategory>
    implements ErpGoodsCategoryService{
    private final ErpGoodsCategoryMapper mapper;

    @Override
    public PageResult<ErpGoodsCategory> queryPageList(ErpGoodsCategory bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpGoodsCategory> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(bo.getErpSkuId()!=null,OGoodsSku::getErpSkuId,bo.getErpSkuId());
//        queryWrapper.eq(bo.getErpGoodsId()!=null,OGoodsSku::getErpGoodsId,bo.getErpGoodsId());
//        queryWrapper.eq(StringUtils.hasText(bo.getSkuNum()),OGoodsSku::getSkuNum,bo.getSkuNum());
        Page<ErpGoodsCategory> pages = mapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
}




