package com.geeke.databus.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.User;
import com.geeke.common.controller.SearchParams;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.databus.service.SystemDataService;
import com.geeke.org.entity.Company;
import com.geeke.org.entity.Department;
import com.geeke.ten.entity.Tenant;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.newtouch.cloud.common.dependency.remote.rest.DynamicRestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Console;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author TBH
 * 系统主数据同步(用户/公司/部门)
 */
@RestController
@RequestMapping("/synchronization")
public class SystemDataController {

    @Value("${spring.application.name}")
    private String appName;

    private final Logger companyLogger = LoggerFactory.getLogger("companyLogger");
    private final Logger departmentLogger = LoggerFactory.getLogger("departmentLogger");
    private final Logger userLogger = LoggerFactory.getLogger("userLogger");
    private static final String TENANT_ID = "tenant_id";
    private static final String APP_ID = "app_id";
    private static final String DATA_TYPE_CODE = "data_type_code";
    private static final String DATA_TYPE_NAME = "data_type_name";
    private static final String DATA_MODEL = "data_model";

    @Autowired
    private SystemDataService systemDataService;

    @Autowired
    private DynamicRestTemplate restTemplate;

    @PostMapping("/company")
    public ResponseEntity<JSONObject> synchronizationCompany() {
        User user = SessionUtils.getUser();
        Tenant tenant = user.getTenant();
        List<Company> companies = systemDataService.receivedCompany(tenant.getId());
        //准备参数
        JSONObject param = new JSONObject();
        param.put(TENANT_ID, tenant.getId());
        param.put(APP_ID, getAppId(companyLogger));
        param.put(DATA_TYPE_CODE, "company");
        param.put(DATA_TYPE_NAME, "公司");
        param.put("data", companies);
        companyLogger.info("开始同步公司数据,同步开始时间:{}", DateUtil.now());
        companyLogger.info("当前租户Id：{},租户名称：{}", tenant.getId(),tenant.getName());
        companyLogger.info("预下发的数据为:{}", JSONUtil.toJsonStr(companies));
        companyLogger.info("调用API下发的参数为:{}", JSONUtil.toJsonStr(param));
        JSONObject jsonObject = restTemplate.postJSON("cloud-geeke-devtool", "/databus/dataBusBaseInterface/receive", JSONObject.class, param);
        companyLogger.info("调用API的结果为：{}", jsonObject);
        companyLogger.info("结束同步公司数据,同步结束时间:{}", DateUtil.now());
        return ResponseEntity.ok(jsonObject);
    }

    @PostMapping("/department")
    public ResponseEntity<JSONObject> synchronizationDepartment() {
        User user = SessionUtils.getUser();
        Tenant tenant = user.getTenant();
        List<Department> departments = systemDataService.receivedDepartment(tenant.getId());
        JSONObject param = new JSONObject();
        param.put(TENANT_ID, tenant.getId());
        param.put(APP_ID, getAppId(departmentLogger));
        param.put(DATA_TYPE_CODE, "department");
        param.put(DATA_TYPE_NAME, "部门");
        param.put("data", departments);
        departmentLogger.info("开始同步部门数据,同步开始时间:{}", DateUtil.now());
        departmentLogger.info("当前租户Id：{},租户名称：{}", tenant.getId(),tenant.getName());
        departmentLogger.info("预下发的数据为:{}", JSONUtil.toJsonStr(departments));
        departmentLogger.info("调用API下发的参数为:{}", JSONUtil.toJsonStr(param));
        JSONObject jsonObject = restTemplate.postJSON("cloud-geeke-devtool", "/databus/dataBusBaseInterface/receive", JSONObject.class, param);
        departmentLogger.info("调用API的结果为：{}", jsonObject);
        departmentLogger.info("结束同步部门数据,同步结束时间:{}", DateUtil.now());
        return ResponseEntity.ok(jsonObject);
    }

    @PostMapping("/user")
    public ResponseEntity<JSONObject> synchronizationUser() {
        User user = SessionUtils.getUser();
        Tenant tenant = user.getTenant();
        List<User> users = systemDataService.receivedUser(tenant.getId());
        JSONObject param = new JSONObject();
        param.put(TENANT_ID, tenant.getId());
        param.put(APP_ID, getAppId(userLogger));
        param.put(DATA_TYPE_CODE, "user");
        param.put(DATA_TYPE_NAME, "用户账号");
        param.put("data", users);
        userLogger.info("开始同步用户数据,同步开始时间:{}", DateUtil.now());
        userLogger.info("当前租户Id：{},租户名称：{}", tenant.getId(),tenant.getName());
        userLogger.info("预下发的数据为:{}", JSONUtil.toJsonStr(users));
        userLogger.info("调用API下发的参数为:{}", JSONUtil.toJsonStr(param));
        JSONObject jsonObject = restTemplate.postJSON("cloud-geeke-devtool", "/databus/dataBusBaseInterface/receive", JSONObject.class, param);
        userLogger.info("调用API的结果为：{}", jsonObject);
        userLogger.info("结束同步用户数据,同步结束时间:{}", DateUtil.now());
        return ResponseEntity.ok(jsonObject);
    }

    public String getAppId(Logger logger) {
        Map<String, String> param = new HashMap<>();
        param.put("appName",appName);
        JSONObject jsonObject = restTemplate.getJSON("cloud-geeke-devtool", "/micro/microVersion/key/{appName}", JSONObject.class, param);
        logger.info("查询组织架构appId的结果:{}",jsonObject);
        if(Objects.equals(jsonObject.getString("code"),"100")){
            return jsonObject.getJSONObject("data").getString("id");
        }else{
            logger.info("查询组织架构appId的结果出错！");
            throw new RuntimeException("调用组织架构查询appId失败!");
        }
    }
}
