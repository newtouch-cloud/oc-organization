package com.geeke.databus.service;

import com.geeke.admin.dao.UserDao;
import com.geeke.admin.entity.User;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.org.dao.CompanyDao;
import com.geeke.org.dao.DepartmentDao;
import com.geeke.org.entity.Company;
import com.geeke.org.entity.Department;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author TBH
 */
@Service
public class SystemDataService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private DepartmentDao departmentDao;

    public List<Company> receivedCompany(String tenantId){
        List<Parameter> parameters = Lists.newArrayList();
        parameters.add(new Parameter("tenant_id","=",tenantId));
        PageRequest request = new PageRequest(parameters);
        return companyDao.listAll(request);
    }

    public List<Department> receivedDepartment(String tenantId){
        List<Parameter> parameters = Lists.newArrayList();
        parameters.add(new Parameter("company.tenant_id","=",tenantId));
        PageRequest request = new PageRequest(parameters);
        return departmentDao.listAll(request);
    }

    public List<User> receivedUser(String tenantId){
        List<Parameter> parameters = Lists.newArrayList();
        parameters.add(new Parameter("tenant_id","=",tenantId));
        PageRequest request = new PageRequest(parameters);
        return userDao.listAll(request);
    }
}
