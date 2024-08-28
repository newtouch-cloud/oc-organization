package com.geeke.org.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.feign.AppVersionService;
import com.geeke.feign.LogService;
import com.geeke.org.entity.CompanyApp;
import com.geeke.org.service.CompanyAppService;
import com.geeke.sys.controller.BaseController;
import com.geeke.sys.service.LoginService;
import com.geeke.sys.utils.SessionUtils;
import com.geeke.ten.entity.TenantApp;
import com.geeke.utils.RemoteResultResolver;
import com.geeke.utils.ResultUtil;

import java.util.*;
import java.util.stream.Collectors;

import com.newtouch.cloud.common.dependency.remote.feign.DynamicFeignClient;
import com.newtouch.cloud.common.dependency.remote.rest.DynamicRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公司应用管理Controller
 * @author
 * @version
 */
@RestController
@RequestMapping(value = "/org/companyApp")
public class CompanyAppController extends BaseController {

    @Autowired
    private CompanyAppService companyAppService;

    @Autowired
    private AppVersionService appVersionService;

    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        CompanyApp entity = companyAppService.get(id);

        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @PostMapping(value = { "list", "" })
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<CompanyApp> result = companyAppService.listPage(
            searchParams.getParams(),
            searchParams.getOffset(),
            searchParams.getLimit(),
            searchParams.getOrderby()
        );

        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<CompanyApp> result = companyAppService.listAll(searchParams.getParams(), searchParams.getOrderby());

        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody CompanyApp entity) {
        String id = companyAppService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody CompanyApp entity) {
        int rows = companyAppService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<CompanyApp> entitys) {
        List<String> ids = companyAppService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<CompanyApp> entitys) {
        List<String> ids = companyAppService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<CompanyApp> entitys) {
        int rows = companyAppService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @SuppressWarnings({"unchecked"})
    @PostMapping(value = { "remoteList", "" })
    public ResponseEntity<JSONObject> pageList(@RequestBody SearchParams searchParams) {
        Page<CompanyApp> result = companyAppService.listPage(
                searchParams.getParams(),
                searchParams.getOffset(),
                searchParams.getLimit(),
                searchParams.getOrderby()
        );
        //组装数据
        if(Objects.nonNull(result.getRows())){
            List<TenantApp> tenantApps = result.getRows().stream().map(CompanyApp::getTenantAppVersion).collect(Collectors.toList());
            SearchParams appSearch = RemoteResultResolver.param(tenantApps, "appVersion", "id");
            ResponseEntity<JSONObject> listAll = appVersionService.listAll(appSearch);
            Integer code = Objects.requireNonNull(listAll.getBody()).getInteger("code");
            Map<String, Map<String,Object>> resultMap = new HashMap<>();
            if(code == 100){
                List<Map<String,Object>> applicationVersions = (ArrayList<Map<String,Object>>) listAll.getBody().get("data");
                for (Map<String, Object> applicationVersion : applicationVersions) {
                    resultMap.put((String) applicationVersion.get("id"),applicationVersion);
                }
                List<CompanyApp> companyApps = result.getRows();
                for (CompanyApp companyApp : companyApps){
                    TenantApp tenantAppVersion = companyApp.getTenantAppVersion();
                    tenantAppVersion.setApplicationVersion(resultMap.get(tenantAppVersion.getAppVersion()));
                    companyApp.setTenantAppVersion(tenantAppVersion);
                }
                result = new Page<>(result.getTotal(),companyApps);
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @SuppressWarnings({"unchecked"})
    @PostMapping(value = { "remoteAll", "" })
    public ResponseEntity<JSONObject> allList(@RequestBody SearchParams searchParams) {
        List<CompanyApp> result = companyAppService.listAll(searchParams.getParams(), searchParams.getOrderby());
        //组装数据
        if(Objects.nonNull(result)){
            List<TenantApp> tenantApps = result.stream().map(CompanyApp::getTenantAppVersion).collect(Collectors.toList());
            SearchParams appSearch = RemoteResultResolver.param(tenantApps, "appVersion", "id");
            ResponseEntity<JSONObject> listAll = appVersionService.listAll(appSearch);
            Integer code = Objects.requireNonNull(listAll.getBody()).getInteger("code");
            Map<String, Map<String,Object>> resultMap = new HashMap<>();
            if(code == 100){
                List<Map<String,Object>> applicationVersions = (ArrayList<Map<String,Object>>) listAll.getBody().get("data");
                for (Map<String, Object> applicationVersion : applicationVersions) {
                    resultMap.put((String) applicationVersion.get("id"),applicationVersion);
                }
                for (CompanyApp companyApp : result){
                    TenantApp tenantAppVersion = companyApp.getTenantAppVersion();
                    tenantAppVersion.setApplicationVersion(resultMap.get(tenantAppVersion.getAppVersion()));
                    companyApp.setTenantAppVersion(tenantAppVersion);
                }
            }
        }
        List<TenantApp> tenantApps = result.stream().map(CompanyApp::getTenantAppVersion).collect(Collectors.toList());
        return ResponseEntity.ok(ResultUtil.successJson(tenantApps));
    }
}
