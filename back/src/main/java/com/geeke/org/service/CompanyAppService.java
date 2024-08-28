package com.geeke.org.service;

import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.org.dao.CompanyAppDao;
import com.geeke.org.entity.CompanyApp;
import com.geeke.org.entity.Department;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公司应用管理Service
 * @author
 * @version
 */

@Service("companyAppService")
@Transactional(readOnly = true)
public class CompanyAppService extends CrudService<CompanyAppDao, CompanyApp> {

    @Override
    public CompanyApp save(CompanyApp companyApp) {
        // 约束条件处理
        Map<String, String> colMaps = Maps.newHashMap();

        // code
        colMaps.clear();
        colMaps.put("company_id", "company.id");
        colMaps.put("tenant_app_version_id", "tenantAppVersion.id");
        if (exists(dao, companyApp, colMaps)) {
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "该公司已经启用该应用"));
        }
        CompanyApp companyAppTemp = super.save(companyApp);

        return companyAppTemp;
    }

    @Override
    public Page<CompanyApp> listPage(List<Parameter> parameters, int offset, int limit, String orderby) {
        String filter =
            "{'columnName':'company.tenant_id', 'queryType': '=', 'value': currentUser.tenant.id},{'columnName':'company.id', 'queryType': '=', 'value': currentUser.company.id=='0' ? '': currentUser.company.id}";
        return super.listPage(super.addFilter(parameters, filter), offset, limit, orderby);
    }

    @Override
    public List<CompanyApp> listAll(List<Parameter> parameters, String orderby) {
        String filter =
            "{'columnName':'company.tenant_id', 'queryType': '=', 'value': currentUser.tenant.id},{'columnName':'company.id', 'queryType': '=', 'value': currentUser.company.id=='0' ? '': currentUser.company.id}";
        PageRequest pageRequest = new PageRequest(super.addFilter(parameters, filter), orderby);
        return dao.listAll(pageRequest);
    }
}
