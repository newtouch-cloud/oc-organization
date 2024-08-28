package com.geeke.ten.service;

import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.org.entity.CompanyApp;
import com.geeke.ten.dao.TenantAppDao;
import com.geeke.ten.entity.TenantApp;
import java.util.List;
import java.util.Map;

import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租户应用管理Service
 * @author
 * @version
 */

@Service("tenantAppService")
@Transactional(readOnly = true)
public class TenantAppService extends CrudService<TenantAppDao, TenantApp> {

    @Override
    public TenantApp save(TenantApp tenantApp) {
        // 约束条件处理
        Map<String, String> colMaps = Maps.newHashMap();

        // code
        colMaps.clear();
        colMaps.put("tenant_id", "tenant.id");
        colMaps.put("app_version_id", "appVersion");
        if (exists(dao, tenantApp, colMaps)) {
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "该租户已经启用该应用"));
        }
        TenantApp tenantAppTemp = super.save(tenantApp);

        return tenantAppTemp;
    }
}
