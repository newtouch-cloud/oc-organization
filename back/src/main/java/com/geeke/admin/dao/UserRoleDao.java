package com.geeke.admin.dao;

import com.geeke.admin.entity.UserRole;
import com.geeke.common.persistence.CrudDao;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户管理DAO接口
 * @author
 * @version
 */
@Mapper
public interface UserRoleDao extends CrudDao<UserRole> {
    //根据用户id删除租户管理员
    void deleteTenantAdmin(@Param("userIds") List<String> userIds);
}
