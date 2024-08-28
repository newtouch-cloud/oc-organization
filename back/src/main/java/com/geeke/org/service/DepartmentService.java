package com.geeke.org.service;

import com.geeke.common.data.Page;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.TreeService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.org.dao.DepartmentDao;
import com.geeke.org.entity.Department;
import com.geeke.sys.utils.SessionUtils;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 部门管理Service
 * @author
 * @version
 */

@Service("departmentService")
@Transactional(readOnly = true)
public class DepartmentService extends TreeService<DepartmentDao, Department> {

    @Transactional(readOnly = false)
    @Override
    public Department save(Department department) {
        // 约束条件处理
        Map<String, String> colMaps = Maps.newHashMap();

        // code

        colMaps.clear();

        colMaps.put("company_id", "company.id");

        colMaps.put("code", "code");

        if (exists(dao, department, colMaps)) {
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "同一个公司下已存在相同的部门编码"));
        }

        Department departmentTemp = super.save(department);

        return departmentTemp;
    }

    @Override
    public Page<Department> listPage(List<Parameter> parameters, int offset, int limit, String orderby) {
        parameters.add(new Parameter("company.tenant_id","=", SessionUtils.getUserJson().getJSONObject("tenant").get("id")));
        return super.listPage(parameters, offset, limit, orderby);
    }

    @Override
    public List<Department> listAll(List<Parameter> parameters, String orderby) {
        parameters.add(new Parameter("company.tenant_id","=", SessionUtils.getUserJson().getJSONObject("tenant").get("id")));
        return super.listAll(parameters, orderby);
    }

    @Override
    public List<Department> tree(List<Parameter> parameters, String orderby) {
        parameters.add(new Parameter("company.tenant_id","=", SessionUtils.getUserJson().getJSONObject("tenant").get("id")));
        return super.tree(parameters, orderby);
    }
}
