package com.geeke.ten.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;

import java.util.Map;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * 租户应用管理Entity
 * @author
 * @version
 */

public class TenantApp extends DataEntity<TenantApp> {

    private static final long serialVersionUID = 1391465689041903620L;

    private Tenant tenant; // 租户

    private String appVersion; // 应用版本id

    private String isLocked = "0"; // 禁用

    private String dbIp; // 数据库IP

    private Integer dbPort; // 数据库端口

    private String dbName; // 数据库名

    private String dbUsername; // 数据库用户名

    private String dbPassword; // 数据库密码

    private String dbUrl; // 数据库URL

    private Map<String,Object> applicationVersion; //当前

    // 构造方法
    public TenantApp() {
        super();
    }

    public TenantApp(String id) {
        super(id);
    }

    // 生成get和set方法

    @NotNull(message = "租户不能为空")
    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @Length(min = 1, max = 20, message = "应用版本长度必须介于 1 和 20 之间")
    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Length(min = 1, max = 1, message = "禁用长度必须介于 1 和 1 之间")
    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    @Length(min = 1, max = 128, message = "数据库IP长度必须介于 1 和 128 之间")
    public String getDbIp() {
        return dbIp;
    }

    public void setDbIp(String dbIp) {
        this.dbIp = dbIp;
    }

    @NotNull(message = "数据库端口不能为空")
    public Integer getDbPort() {
        return dbPort;
    }

    public void setDbPort(Integer dbPort) {
        this.dbPort = dbPort;
    }

    @Length(min = 1, max = 64, message = "数据库名长度必须介于 1 和 64 之间")
    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Length(min = 1, max = 64, message = "数据库用户名长度必须介于 1 和 64 之间")
    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    @Length(min = 1, max = 64, message = "数据库密码长度必须介于 1 和 64 之间")
    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    @Length(min = 1, max = 512, message = "数据库URL长度必须介于 1 和 512 之间")
    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public Map<String, Object> getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(Map<String, Object> applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    /**
     * 获取实体对应表名
     */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "ten_tenant_app";
    }

    /**
     * 获取实体对应Id
     */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1391465689041903620";
    }

    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
        return true;
    }
}
