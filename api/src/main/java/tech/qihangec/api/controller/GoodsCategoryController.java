package tech.qihangec.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.qihangec.api.common.BaseController;
import tech.qihangec.api.common.PageQuery;
import tech.qihangec.api.common.PageResult;
import tech.qihangec.api.common.TableDataInfo;
import tech.qihangec.api.domain.ErpGoods;
import tech.qihangec.api.domain.ErpGoodsCategory;
import tech.qihangec.api.service.ErpGoodsCategoryService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/goods/category")
public class GoodsCategoryController extends BaseController {
    private final ErpGoodsCategoryService categoryService;
    @GetMapping("/list")
    public TableDataInfo list(ErpGoods goods, PageQuery pageQuery)
    {
        List<ErpGoodsCategory> list = categoryService.list();
//        PageResult<ErpGoods> pageResult = goodsService.queryPageList(goods, pageQuery);
        return getDataTable(list);
    }
}
