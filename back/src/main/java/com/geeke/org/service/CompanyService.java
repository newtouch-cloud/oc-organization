package com.geeke.org.service;

import com.geeke.admin.dao.UserDao;
import com.geeke.admin.entity.Role;
import com.geeke.admin.entity.User;
import com.geeke.admin.entity.UserRole;
import com.geeke.admin.service.UserRoleService;
import com.geeke.admin.service.UserService;
import com.geeke.common.constants.ActionConstants;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.TreeService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.org.dao.CompanyAdminDao;
import com.geeke.org.dao.CompanyDao;
import com.geeke.org.entity.Company;
import com.geeke.org.entity.CompanyAdmin;
import com.geeke.org.entity.Department;
import com.geeke.org.service.CompanyAdminService;
import com.geeke.sys.entity.Action;
import com.geeke.sys.entity.ActionRecycle;
import com.geeke.ten.entity.Tenant;
import com.geeke.ten.entity.TenantAdmin;
import com.geeke.utils.Reflections;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公司管理Service
 * @author
 * @version
 */

@Service("companyService")
@Transactional(readOnly = true)
public class CompanyService extends TreeService<CompanyDao, Company> {

    private static final String COMPANY_ADMIN_ID = "2031";

    @Autowired
    private CompanyAdminDao companyAdminDao;

    @Autowired
    private CompanyAdminService companyAdminService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Override
    public Page<Company> listPage(List<Parameter> parameters, int offset, int limit, String orderby) {
        String filter =
            "{'columnName':'tenant_id', 'queryType': '=', 'value': currentTenant.id},{'columnName':'id', 'queryType': '=', 'value': currentUser.company.id=='0' ? '': currentUser.company.id}";
        return super.listPage(super.addFilter(parameters, filter), offset, limit, orderby);
    }

    @Override
    public List<Company> listAll(List<Parameter> parameters, String orderby) {
        String filter =
            "{'columnName':'tenant_id', 'queryType': '=', 'value': currentTenant.id},{'columnName':'id', 'queryType': '=', 'value': currentUser.company.id=='0' ? '': currentUser.company.id}";
        PageRequest pageRequest = new PageRequest(super.addFilter(parameters, filter), orderby);
        return dao.listAll(pageRequest);
    }

    @Override
    public Company get(String id) {
        Company company = super.get(id);

        List<Parameter> params = null;
        PageRequest pageRequest;

        /*获取子表列表   公司管理员*/
        params = Lists.newArrayList();

        params.add(new Parameter("company_id", "=", company.getId()));
        pageRequest = new PageRequest(params);
        company.setCompanyAdminList(companyAdminDao.listAll(pageRequest));

        return company;
    }

    @Transactional(readOnly = false)
    @Override
    public Company save(Company company) {
        Map<String, String> colMaps = Maps.newHashMap();

        // code

        colMaps.clear();

        colMaps.put("code", "code");

        if (exists(dao, company, colMaps)) {
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "已存在相同的公司编码"));
        }

        Company companyTemp = super.save(company);

        /* 保存子表数据     公司管理员 */
        saveCompanyAdminList(companyTemp);

        return companyTemp;
    }

    /**
     * 删除
     * @param company
     */
    @Override
    @Transactional(readOnly = false)
    public int delete(Company company) {
        List<Parameter> params = null;
        PageRequest pageRequest;

        /* 处理子表     公司管理员 */
        params = Lists.newArrayList();

        params.add(new Parameter("company_id", "=", company.getId()));
        pageRequest = new PageRequest(params);
        company.setCompanyAdminList(companyAdminDao.listAll(pageRequest));

        if (company.getCompanyAdminList() != null && company.getCompanyAdminList().size() > 0) {
            companyAdminService.bulkDelete(company.getCompanyAdminList());
        }

        /* 处理子表     用户 */
        params = Lists.newArrayList();

        params.add(new Parameter("company_id", "=", company.getId()));
        pageRequest = new PageRequest(params);
        List<User> users = userDao.listAll(pageRequest);

        if (users != null && users.size() > 0) {
            userService.bulkDelete(users);
        }

        int rows = super.delete(company);
        return rows;
    }

    /**
     * 批量删除
     * @param companyList
     */
    @Override
    @Transactional(readOnly = false)
    public int bulkDelete(List<Company> companyList) {
        for (Company company : companyList) {
            delete(company);
        }

        return companyList.size();
    }

    /**
     * 生成操作日志
     * @param actionTypeId  操作类型Id
     * @param entity        操作的实体对象
     * @return
     */
    @Override
    protected Action createAction(String actionTypeId, Company entity) {
        Action action = super.createAction(actionTypeId, entity);
        if (action == null) {
            return null;
        }
        // 删除时记录把保存的数据保存到回收站
        if (ActionConstants.ACTION_DELETED.equals(actionTypeId)) {
            for (CompanyAdmin child : entity.getCompanyAdminList()) {
                ActionRecycle recycle = new ActionRecycle();
                recycle.setTableName(child.getBusTableName());
                recycle.setObjectId(child.getId());
                recycle.setObjectName((String) Reflections.invokeGetter(child, "name"));
                action.getActionRecycleList().add(recycle);
            }
        }
        return action;
    }

    /* 保存子表数据     公司管理员 */
    private void saveCompanyAdminList(Company company) {
        //先将新增的用户获取到id 回写回来
        addUserToTenantAdmin(company);
        List<Parameter> params = Lists.newArrayList();

        params.add(new Parameter("company_id", "=", company.getId()));
        PageRequest pageRequest = new PageRequest(params);
        List<CompanyAdmin> list_CompanyAdmin = companyAdminDao.listAll(pageRequest);

        List<CompanyAdmin> deletes = Lists.newArrayList(); // 删除列表
        List<CompanyAdmin> inserts = Lists.newArrayList(); // 添加列表
        List<CompanyAdmin> updates = Lists.newArrayList(); // 更新列表
        for (CompanyAdmin companyAdminSaved : list_CompanyAdmin) {
            boolean found = false;
            for (CompanyAdmin companyAdmin : company.getCompanyAdminList()) {
                if (companyAdminSaved.getId().equals(companyAdmin.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                deletes.add(companyAdminSaved);
            }
        }
        if (deletes.size() > 0) {
            companyAdminService.bulkDelete(deletes);
        }
        for (CompanyAdmin companyAdmin : company.getCompanyAdminList()) {
            if (StringUtils.isBlank(companyAdmin.getId())) {
                companyAdmin.setCompany(company);

                inserts.add(companyAdmin);
            } else {
                updates.add(companyAdmin);
            }
            // 设置加密字段  备注信息
            if (companyAdmin.getUser() != null && companyAdmin.getUser().getLoginPassword() != null) {
                Md5Hash md5 = new Md5Hash(companyAdmin.getUser().getLoginPassword(), companyAdmin.getUser().getId(), 6);
                String md5Password = md5.toHex();
                companyAdminDao.updateLoginPassword(companyAdmin.getUser().getId(), md5Password);
            }
        }
        if (updates.size() > 0) {
            companyAdminService.bulkUpdate(updates);
        }
        if (inserts.size() > 0) {
            companyAdminService.bulkInsert(inserts);
        }
        authorizedTenantAdmin(list_CompanyAdmin,inserts,updates,deletes);
    }


    @Override
    public List<Company> tree(List<Parameter> parameters, String orderby) {
        String filter =
                "{'columnName':'tenant_id', 'queryType': '=', 'value': currentTenant.id},{'columnName':'id', 'queryType': '=', 'value': currentUser.company.id=='0' ? '': currentUser.company.id}";
        PageRequest pageRequest = new PageRequest(super.addFilter(parameters, filter), orderby);
        List<Company> companies = dao.listAll(pageRequest);
        return bulid(companies);
    }

    private List<Company> bulid(List<Company> list) {
        List<Integer> childIndex = Lists.newArrayList();
        for (Company entity : list) {
            for(int i = 0; i <= list.size() - 1; i++) {
                Company it = list.get(i);
                if (it.getParent() != null && entity.getId().equals(it.getParent().getId())) {
                    if (entity.getChildren() == null) {
                        entity.setChildren(Lists.newArrayList());
                    }
                    entity.getChildren().add(it);
                    childIndex.add(i);
                }
            }
        }
        Collections.sort(childIndex);
        for(int i = childIndex.size() - 1; i >= 0; i--) {
            int idx = (int)childIndex.get(i);
            list.remove(idx);
        }
        return list;
    }

    private void addUserToTenantAdmin(Company company) {
        //获取到没有id的User
        List<CompanyAdmin> companyAdmins = company.getCompanyAdminList();
        if(CollectionUtils.isNotEmpty(companyAdmins)){
            for (CompanyAdmin admin : companyAdmins) {
                User user = admin.getUser();
                if (Objects.nonNull(user) && StringUtils.isEmpty(user.getId())) {
                    //设置初始化值
                    setInitValue(user);
                    //设置租户
                    user.setTenant(company.getTenant());
                    //设置公司
                    user.setCompany(company);
                    User resultUser = userService.save(user);
                    //保存返回有id的User
                    admin.setUser(resultUser);
                }
            }
        }
    }

    /**
     * 这个地方的逻辑 因为更新的时候 不知道更新了哪些数据 无法通过id去定位准确的用户和角色中间表
     * 然后 不管修改还是新增 都是修改后的数据 在授权的时候先到sys_user_role 删除所有数据 然后重新授权
     * @param oldCompanyAdmins 原本数据中的管理员
     * @param inserts 新插入的管理员
     * @param updates 被更改的管理员
     * @param deletes 被删除的管理员
     */
    private void authorizedTenantAdmin(List<CompanyAdmin> oldCompanyAdmins,List<CompanyAdmin> inserts,List<CompanyAdmin> updates,List<CompanyAdmin> deletes){
        //如果只有deletes不为空那就先删除一波 防止只有deletes被删数据的时候造成数据的不准确
        if(deletes.size() > 0){
            deleteUserRole(deletes);
        }
        //然后通过原本的数据 先删除掉所有的 然后再添加新增和修改的
        if(oldCompanyAdmins.size() > 0){
            deleteUserRole(oldCompanyAdmins);
        }
        //然后将修改的和新增的授权
        List<CompanyAdmin> authorized = Lists.newArrayList();
        authorized.addAll(inserts);
        authorized.addAll(updates);
        //如果没有新增和修改的 那就是单删除了 这时候判断是否为空
        if(CollectionUtils.isNotEmpty(authorized)){
            insertUserRole(authorized);
        }
    }

    private void insertUserRole(List<CompanyAdmin> companyAdmins){
        //准备数据
        List<UserRole> userRoles = Lists.newArrayList();
        Role role = new Role();
        //公司管理员
        role.setId(COMPANY_ADMIN_ID);
        //插入成功以后 给用户分配公司管理员角色
        List<User> users = companyAdmins.stream().map(CompanyAdmin::getUser).collect(Collectors.toList());
        for (User user : users) {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRoles.add(userRole);
        }
        userRoleService.bulkInsert(userRoles);
    }

    private void deleteUserRole(List<CompanyAdmin> companyAdmins){
        List<String> userIds = companyAdmins.stream().map(CompanyAdmin::getUser).map(User::getId).collect(Collectors.toList());
        userRoleService.deleteTenantAdmin(userIds);
    }

    private void setInitValue(User user){
        if(Objects.nonNull(user)){
            Department department = new Department();
            department.setId("0");
            user.setDepartment(department);
            user.setQyOpenId(" ");
            user.setWechat(" ");
            user.setIsLocked("0");
        }
    }
}
