package com.geeke.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.geeke.admin.entity.UserRole;
import com.geeke.common.IUser;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.org.entity.Department;
import com.geeke.ten.entity.Tenant;
import com.google.common.collect.Lists;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * 用户管理Entity
 * @author
 * @version
 */

public class User extends DataEntity<User> implements IUser {

    private static final long serialVersionUID = 1289059804542828547L;

    private Company company; // 公司

    private Department department; // 部门

    private String name; // 名称

    private String loginName; // 登录账号

    // 密码字段
    private boolean loginPasswordUpdate; // 是否修改 密码
    private String loginPassword; // 密码
    // 其它字段

    private String isLocked; // 禁用

    private String phone; // 手机号

    private String email; // 邮箱地址

    private String qyOpenId; // 企业微信标识

    private String wechat; // 企业微信账号

    private Tenant tenant; // 租户

    private List<UserRole> userRoleList = Lists.newArrayList(); // 子表列表

    // 构造方法
    public User() {
        super();
    }

    public User(String id) {
        super(id);
    }

    // 生成get和set方法

    @NotNull(message = "公司不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @NotNull(message = "部门不能为空")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Length(min = 1, max = 128, message = "名称长度必须介于 1 和 128 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(min = 1, max = 10, message = "登录账号长度必须介于 1 和 10 之间")
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @JsonIgnore
    public String getLoginPassword() {
        return loginPassword;
    }

    @JsonDeserialize
    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public boolean getLoginPasswordUpdate() {
        return loginPasswordUpdate;
    }

    public void setLoginPasswordUpdate(boolean loginPasswordUpdate) {
        this.loginPasswordUpdate = loginPasswordUpdate;
    }

    @NotNull(message = "禁用不能为空")
    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    @Length(min = 1, max = 20, message = "手机号长度必须介于 1 和 20 之间")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Length(min = 1, max = 36, message = "邮箱地址长度必须介于 1 和 36 之间")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Length(min = 0, max = 100, message = "企业微信标识长度必须介于 0 和 100 之间")
    public String getQyOpenId() {
        return qyOpenId;
    }

    public void setQyOpenId(String qyOpenId) {
        this.qyOpenId = qyOpenId;
    }

    @Length(min = 1, max = 100, message = "企业微信账号长度必须介于 1 和 100 之间")
    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    @NotNull(message = "租户不能为空")
    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public List<UserRole> getUserRoleList() {
        return userRoleList;
    }

    public void setUserRoleList(List<UserRole> userRoleList) {
        this.userRoleList = userRoleList;
    }

    /**
     * 获取实体对应表名
     */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_user";
    }

    /**
     * 获取实体对应Id
     */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1289059804542828547";
    }

    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
        return true;
    }

    @Override
    public String getCompanyId() {
        if (company != null) {
            return company.getId();
        }

        return null;
    }
}
