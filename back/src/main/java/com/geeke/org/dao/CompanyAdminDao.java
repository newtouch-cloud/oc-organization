package com.geeke.org.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.org.entity.CompanyAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 公司管理DAO接口
 * @author
 * @version
 */
@Mapper
public interface CompanyAdminDao extends CrudDao<CompanyAdmin> {
    /**
     * 修改 密码
     */
    public int updateLoginPassword(@Param("id") String id, @Param("pass") String pass);
}
