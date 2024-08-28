package com.geeke.databus.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.User;
import com.geeke.ten.dao.TenantAdminDao;
import com.geeke.ten.entity.Tenant;
import com.geeke.ten.entity.TenantAdmin;
import com.geeke.ten.service.TenantAdminService;
import com.geeke.ten.service.TenantService;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author TBH
 */
@RestController
@RequestMapping("/tenant")
public class TenantGrantController {

    @Autowired
    private TenantService tenantService;

    @GetMapping("/admin/find/{id}")
    public JSONObject tenantFindAdmin(@PathVariable("id") String tenantId) {
        Tenant tenant = tenantService.get(tenantId);
        Objects.requireNonNull(tenant, "查询不到租户管理员");
        List<User> users = tenant.getTenantAdminList().stream().map(TenantAdmin::getUser).collect(Collectors.toList());
        Objects.requireNonNull(users, "查询不到租户管理员");
        List<String> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        return ResultUtil.successJson(userIds);
    }
}