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
import tech.qihangec.api.domain.ErpSupplier;
import tech.qihangec.api.service.ErpSupplierService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/supplier")
public class SupplierController extends BaseController{
    private final ErpSupplierService supplierService;

    @GetMapping("/list")
    public TableDataInfo list(ErpSupplier bo, PageQuery pageQuery)
    {
        PageResult<ErpSupplier> pageResult = supplierService.queryPageList(bo, pageQuery);
        return getDataTable(pageResult);
    }

    @GetMapping("/list_all")
    public TableDataInfo listAll()
    {
        List<ErpSupplier> list = supplierService.list();
        return getDataTable(list);
    }
}
