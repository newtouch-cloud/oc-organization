package com.geeke.org.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.org.entity.CompanyApp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 公司应用管理DAO接口
 * @author
 * @version
 */
@Mapper
public interface CompanyAppDao extends CrudDao<CompanyApp> {}
