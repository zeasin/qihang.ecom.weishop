package tech.qihangec.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.qihangec.api.common.BaseController;
import tech.qihangec.api.common.PageQuery;
import tech.qihangec.api.common.PageResult;
import tech.qihangec.api.common.TableDataInfo;
import tech.qihangec.api.domain.ErpGoods;
import tech.qihangec.api.domain.ErpGoodsCategory;
import tech.qihangec.api.domain.ErpGoodsCategoryAttributeValue;
import tech.qihangec.api.service.ErpGoodsCategoryAttributeValueService;
import tech.qihangec.api.service.ErpGoodsCategoryService;
import tech.qihangec.api.service.ErpGoodsService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/goods/category")
public class GoodsCategoryController extends BaseController {
    private final ErpGoodsCategoryService categoryService;
    private final ErpGoodsCategoryAttributeValueService attributeValueService;


    @GetMapping("/list")
    public TableDataInfo list()
    {
        List<ErpGoodsCategory> list = categoryService.list();
        return getDataTable(list);
    }

    @GetMapping("/attr_value/{id}")
    public TableDataInfo attrValueList(@PathVariable("id") Integer id)
    {
        var list = attributeValueService.list(
                new LambdaQueryWrapper<ErpGoodsCategoryAttributeValue>()
                        .eq(ErpGoodsCategoryAttributeValue::getCategoryAttributeId,id)
        );

        return getDataTable(list);
    }
}
