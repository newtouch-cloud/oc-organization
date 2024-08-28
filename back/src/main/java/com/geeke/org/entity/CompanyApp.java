package com.geeke.org.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.ten.entity.TenantApp;
import com.google.common.collect.Lists;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * 公司应用管理Entity
 * @author
 * @version
 */

public class CompanyApp extends DataEntity<CompanyApp> {

    private static final long serialVersionUID = 1392930032371744773L;

    private Company company; // 公司

    private TenantApp tenantAppVersion; // 租户应用版本

    private String isLocked = "0"; // 禁用

    // 构造方法
    public CompanyApp() {
        super();
    }

    public CompanyApp(String id) {
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

    @NotNull(message = "租户应用版本不能为空")
    public TenantApp getTenantAppVersion() {
        return tenantAppVersion;
    }

    public void setTenantAppVersion(TenantApp tenantAppVersion) {
        this.tenantAppVersion = tenantAppVersion;
    }

    @Length(min = 1, max = 1, message = "禁用长度必须介于 1 和 1 之间")
    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    /**
     * 获取实体对应表名
     */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "gen_company_app";
    }

    /**
     * 获取实体对应Id
     */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1392930032371744773";
    }

    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
        return true;
    }
}
