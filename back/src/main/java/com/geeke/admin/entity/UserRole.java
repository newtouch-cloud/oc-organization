package com.geeke.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.admin.entity.Role;
import com.geeke.admin.entity.User;
import com.geeke.common.persistence.DataEntity;
import javax.validation.constraints.NotNull;

/**
 * 用户管理Entity
 * @author
 * @version
 */

public class UserRole extends DataEntity<UserRole> {

    private static final long serialVersionUID = 1295352172150431774L;

    private User user; // 标识

    private Role role; // 角色

    // 构造方法
    public UserRole() {
        super();
    }

    public UserRole(String id) {
        super(id);
    }

    // 生成get和set方法

    @NotNull(message = "标识不能为空")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @NotNull(message = "角色不能为空")
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * 获取实体对应表名
     */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_user_role";
    }

    /**
     * 获取实体对应Id
     */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1295352172150431774";
    }

    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
        return true;
    }
}
