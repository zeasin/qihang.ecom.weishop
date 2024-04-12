package tech.qihangec.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import tech.qihangec.api.common.PageQuery;
import tech.qihangec.api.common.PageResult;
import tech.qihangec.api.domain.ErpGoods;
import tech.qihangec.api.domain.ErpGoodsSku;
import tech.qihangec.api.mapper.ErpGoodsSkuMapper;
import tech.qihangec.api.service.ErpGoodsService;
import tech.qihangec.api.mapper.ErpGoodsMapper;
import org.springframework.stereotype.Service;

/**
* @author TW
* @description 针对表【erp_goods(商品库存管理)】的数据库操作Service实现
* @createDate 2024-04-12 14:52:05
*/
@AllArgsConstructor
@Service
public class ErpGoodsServiceImpl extends ServiceImpl<ErpGoodsMapper, ErpGoods>
    implements ErpGoodsService{
    private final ErpGoodsMapper mapper;
    private final ErpGoodsSkuMapper skuMapper;
    @Override
    public PageResult<ErpGoods> queryPageList(ErpGoods bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpGoods> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(bo.getErpSkuId()!=null,OGoodsSku::getErpSkuId,bo.getErpSkuId());
//        queryWrapper.eq(bo.getErpGoodsId()!=null,OGoodsSku::getErpGoodsId,bo.getErpGoodsId());
//        queryWrapper.eq(StringUtils.hasText(bo.getSkuNum()),OGoodsSku::getSkuNum,bo.getSkuNum());
        Page<ErpGoods> pages = mapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }

    @Override
    public PageResult<ErpGoodsSku> querySkuPageList(ErpGoodsSku bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpGoodsSku> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(bo.getErpSkuId()!=null,OGoodsSku::getErpSkuId,bo.getErpSkuId());
//        queryWrapper.eq(bo.getErpGoodsId()!=null,OGoodsSku::getErpGoodsId,bo.getErpGoodsId());
//        queryWrapper.eq(StringUtils.hasText(bo.getSkuNum()),OGoodsSku::getSkuNum,bo.getSkuNum());
        Page<ErpGoodsSku> pages = skuMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
}




