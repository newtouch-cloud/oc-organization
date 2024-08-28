package com.geeke.admin.service;

import com.geeke.admin.dao.RoleDao;
import com.geeke.admin.entity.Role;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色管理Service
 * @author
 * @version
 */

@Service("roleService")
@Transactional(readOnly = true)
public class RoleService extends CrudService<RoleDao, Role> {

    @Transactional(readOnly = false)
    @Override
    public Role save(Role role) {
        // 约束条件处理
        Map<String, String> colMaps = Maps.newHashMap();

        //  角色编号唯一

        colMaps.clear();

        colMaps.put("code", "code");

        colMaps.put("del_flag", "delFlag");

        if (exists(dao, role, colMaps)) {
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "角色编号唯一"));
        }

        Role roleTemp = super.save(role);

        return roleTemp;
    }
}
