package com.geeke.ten.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.ten.entity.TenantApp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 租户应用管理DAO接口
 * @author
 * @version
 */
@Mapper
public interface TenantAppDao extends CrudDao<TenantApp> {}
