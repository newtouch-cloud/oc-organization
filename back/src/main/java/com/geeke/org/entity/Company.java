package com.geeke.org.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.admin.entity.User;
import com.geeke.common.persistence.TreeEntity;
import com.geeke.org.entity.CompanyAdmin;
import com.geeke.ten.entity.Tenant;
import com.google.common.collect.Lists;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * 公司管理Entity
 * @author
 * @version
 */

public class Company extends TreeEntity<Company> {

    private static final long serialVersionUID = 1378153592526331937L;

    private String code; // 编号

    private String fullName; // 全称

    private String name; // 名称

    private User chairman; // 董事长

    private User manager; // 总经理

    private Tenant tenant; // 租户

    private List<CompanyAdmin> companyAdminList = Lists.newArrayList(); // 子表列表

    // 构造方法
    public Company() {
        super();
    }

    public Company(String id) {
        super(id);
    }

    // 生成get和set方法

    @Length(min = 1, max = 64, message = "编号长度必须介于 1 和 64 之间")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Length(min = 1, max = 512, message = "全称长度必须介于 1 和 512 之间")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Length(min = 1, max = 128, message = "名称长度必须介于 1 和 128 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getChairman() {
        return chairman;
    }

    public void setChairman(User chairman) {
        this.chairman = chairman;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    @NotNull(message = "租户不能为空")
    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public List<CompanyAdmin> getCompanyAdminList() {
        return companyAdminList;
    }

    public void setCompanyAdminList(List<CompanyAdmin> companyAdminList) {
        this.companyAdminList = companyAdminList;
    }

    /**
     * 获取实体对应表名
     */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "org_company";
    }

    /**
     * 获取实体对应Id
     */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1378153592526331937";
    }

    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
        return true;
    }
}
