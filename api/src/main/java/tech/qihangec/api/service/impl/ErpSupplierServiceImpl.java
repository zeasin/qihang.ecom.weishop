package tech.qihangec.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import tech.qihangec.api.common.PageQuery;
import tech.qihangec.api.common.PageResult;
import tech.qihangec.api.domain.ErpGoods;
import tech.qihangec.api.domain.ErpSupplier;
import tech.qihangec.api.service.ErpSupplierService;
import tech.qihangec.api.mapper.ErpSupplierMapper;
import org.springframework.stereotype.Service;

/**
* @author TW
* @description 针对表【erp_supplier】的数据库操作Service实现
* @createDate 2024-04-12 15:07:31
*/
@AllArgsConstructor
@Service
public class ErpSupplierServiceImpl extends ServiceImpl<ErpSupplierMapper, ErpSupplier>
    implements ErpSupplierService{
    private final ErpSupplierMapper mapper;

    @Override
    public PageResult<ErpSupplier> queryPageList(ErpSupplier bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpSupplier> queryWrapper = new LambdaQueryWrapper<>();

        Page<ErpSupplier> pages = mapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
}




