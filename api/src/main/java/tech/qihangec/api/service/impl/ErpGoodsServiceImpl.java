package tech.qihangec.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tech.qihangec.api.common.PageQuery;
import tech.qihangec.api.common.PageResult;
import tech.qihangec.api.domain.ErpGoods;
import tech.qihangec.api.domain.ErpGoodsSku;
import tech.qihangec.api.domain.ErpGoodsSkuAttr;
import tech.qihangec.api.domain.bo.GoodsSpecAddBo;
import tech.qihangec.api.mapper.ErpGoodsSkuAttrMapper;
import tech.qihangec.api.mapper.ErpGoodsSkuMapper;
import tech.qihangec.api.service.ErpGoodsService;
import tech.qihangec.api.mapper.ErpGoodsMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    private final ErpGoodsSkuAttrMapper skuAttrMapper;
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

    @Transactional
    @Override
    public int insertGoods(ErpGoods goods)
    {
        // 查询编码是否存在

//        ErpGoods goods1 = mapper.selectGoodsByNumber(goods.getNumber());
        ErpGoods goods1 = mapper.selectOne(new LambdaQueryWrapper<ErpGoods>().eq(ErpGoods::getNumber,goods.getNumber()));
        if(goods1!=null) return -1;

        // 1、添加主表erp_goods
        goods.setCreateTime(new Date());
        mapper.insert(goods);

        // 2、添加规格表erp_goods_spec
        for (GoodsSpecAddBo bo:goods.getSpecList()) {
            ErpGoodsSku spec = new ErpGoodsSku();
            spec.setGoodsId(goods.getId());
            spec.setSpecNum(bo.getSpecNum());
            spec.setColorId(bo.getColorId());
            spec.setColorValue(bo.getColorValue());
            if(goods.getColorImages()!=null && StringUtils.hasText(goods.getColorImages().get(bo.getColorId()))){
                spec.setColorImage(goods.getColorImages().get(bo.getColorId()));
            }else {
                spec.setColorImage(goods.getImage());
            }
            spec.setSizeId(bo.getSizeId());
            spec.setSizeValue(bo.getSizeValue());
            spec.setStyleId(bo.getStyleId());
            spec.setStyleValue(bo.getStyleValue());
            if(bo.getPurPrice() == null){
                spec.setPurPrice(goods.getPurPrice());
            }else spec.setPurPrice(bo.getPurPrice());
            spec.setStatus(1);
            spec.setDisable(0);
            skuMapper.insert(spec);
        }

        // 3、添加规格属性表erp_goods_spec_attr
        if(goods.getColorValues()!=null) {
            for (Integer val:goods.getColorValues()) {
                ErpGoodsSkuAttr specAttr = new ErpGoodsSkuAttr();
                specAttr.setGoodsId(goods.getId());
                specAttr.setType("color");
                specAttr.setK("颜色");
                specAttr.setKid(114);
                specAttr.setVid(val);
                skuAttrMapper.insert(specAttr);
            }

        }
        if(goods.getSizeValues()!=null) {
            for (Integer val:goods.getSizeValues()) {
                ErpGoodsSkuAttr specAttr = new ErpGoodsSkuAttr();
                specAttr.setGoodsId(goods.getId());
                specAttr.setType("size");
                specAttr.setK("尺码");
                specAttr.setKid(115);
                specAttr.setVid(val);
                skuAttrMapper.insert(specAttr);
            }

        }
        if(goods.getColorValues()!=null) {
            for (Integer val:goods.getColorValues()) {
                ErpGoodsSkuAttr specAttr = new ErpGoodsSkuAttr();
                specAttr.setGoodsId(goods.getId());
                specAttr.setType("style");
                specAttr.setK("款式");
                specAttr.setKid(116);
                specAttr.setVid(val);
                skuAttrMapper.insert(specAttr);
            }

        }
//        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return 1;
    }
}




