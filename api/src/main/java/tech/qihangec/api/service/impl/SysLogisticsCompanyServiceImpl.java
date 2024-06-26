package tech.qihangec.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tech.qihangec.api.common.PageQuery;
import tech.qihangec.api.common.PageResult;
import tech.qihangec.api.domain.SysLogisticsCompany;
import tech.qihangec.api.mapper.SysLogisticsCompanyMapper;
import tech.qihangec.api.service.SysLogisticsCompanyService;

import java.util.List;

/**
* @author TW
* @description 针对表【sys_logistics_company(平台快递公司表)】的数据库操作Service实现
* @createDate 2024-03-22 11:03:11
*/
@AllArgsConstructor
@Service
public class SysLogisticsCompanyServiceImpl extends ServiceImpl<SysLogisticsCompanyMapper, SysLogisticsCompany>
    implements SysLogisticsCompanyService {
    private final SysLogisticsCompanyMapper mapper;

    @Override
    public PageResult<SysLogisticsCompany> queryPageList(Integer platform, Integer shopId, PageQuery pageQuery) {
        LambdaQueryWrapper<SysLogisticsCompany> queryWrapper = new LambdaQueryWrapper<SysLogisticsCompany>().
                eq(platform != null, SysLogisticsCompany::getPlatformId, platform)
                .eq(shopId != null, SysLogisticsCompany::getShopId, shopId);

        Page<SysLogisticsCompany> pages = mapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }

    @Override
    public List<SysLogisticsCompany> queryListByStatus(Integer status, Integer shopId) {
        LambdaQueryWrapper<SysLogisticsCompany> queryWrapper = new LambdaQueryWrapper<SysLogisticsCompany>().
                eq(SysLogisticsCompany::getStatus, status)
                .eq(shopId != null, SysLogisticsCompany::getShopId, shopId);
        return mapper.selectList(queryWrapper);
    }

    @Override
    public int updateStatus(String id,Integer status) {
        SysLogisticsCompany update = new SysLogisticsCompany();
        update.setId(id);
        update.setStatus(status);
        return mapper.updateById(update);
    }
}




