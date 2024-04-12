package tech.qihangec.api.service;

import tech.qihangec.api.common.PageQuery;
import tech.qihangec.api.common.PageResult;
import tech.qihangec.api.domain.ErpGoods;
import tech.qihangec.api.domain.ErpSupplier;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author TW
* @description 针对表【erp_supplier】的数据库操作Service
* @createDate 2024-04-12 15:07:31
*/
public interface ErpSupplierService extends IService<ErpSupplier> {
    PageResult<ErpSupplier> queryPageList(ErpSupplier bo, PageQuery pageQuery);
}
