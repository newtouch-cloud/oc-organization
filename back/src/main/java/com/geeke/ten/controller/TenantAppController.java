package com.geeke.ten.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.common.data.Parameter;
import com.geeke.feign.AppVersionService;
import com.geeke.sys.controller.BaseController;
import com.geeke.ten.annotation.CreateAppDatabase;
import com.geeke.ten.entity.TenantApp;
import com.geeke.ten.service.TenantAppService;
import com.geeke.utils.RemoteResultResolver;
import com.geeke.utils.ResultUtil;

import java.util.*;
import java.util.stream.Collectors;

import com.newtouch.cloud.common.dependency.remote.rest.DynamicRestTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 租户应用管理Controller
 *
 * @author
 */
@RestController
@RequestMapping(value = "/ten/tenantApp")
public class TenantAppController extends BaseController {

    @Autowired
    private TenantAppService tenantAppService;

    @Autowired
    private AppVersionService appVersionService;

    @Autowired
    private DynamicRestTemplate restTemplate;

//    @GetMapping("/{id}")
//    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
//        TenantApp entity = tenantAppService.get(id);
//
//        return ResponseEntity.ok(ResultUtil.successJson(entity));
//    }
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        TenantApp entity = tenantAppService.get(id);
        //准备参数
        Map<String, Object> params = new HashMap<>();
        params.put("id", entity.getAppVersion());
        JSONObject jsonObject = restTemplate.get("cloud-geeke-devtool", "/micro/microVersion/{id}" ,JSONObject.class, params, null);
        entity.setApplicationVersion(jsonObject);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @PostMapping(value = { "list", "" })
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<TenantApp> result = tenantAppService.listPage(
            searchParams.getParams(),
            searchParams.getOffset(),
            searchParams.getLimit(),
            searchParams.getOrderby()
        );

        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<TenantApp> result = tenantAppService.listAll(searchParams.getParams(), searchParams.getOrderby());

        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @CreateAppDatabase
    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody TenantApp entity) {
        String id = tenantAppService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody TenantApp entity) {
        int rows = tenantAppService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<TenantApp> entitys) {
        List<String> ids = tenantAppService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<TenantApp> entitys) {
        List<String> ids = tenantAppService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<TenantApp> entitys) {
        int rows = tenantAppService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "versionListAll")
    public ResponseEntity<JSONObject> versionListAll(@RequestBody SearchParams searchParams) {
        ResponseEntity<JSONObject> responseEntity = appVersionService.listAll(searchParams);
        //todo 正确的做法应用使用feign拦截器 但是这里为了效果 先做了再说
        JSONObject body = responseEntity.getBody();
        return ResponseEntity.ok(ResultUtil.successJson(body));
    }

//    @SuppressWarnings({"unchecked"})
//    @PostMapping(value = {"remoteList", ""})
//    public ResponseEntity<JSONObject> pageList(@RequestBody SearchParams searchParams) {
//        Page<TenantApp> result = tenantAppService.listPage(
//                searchParams.getParams(),
//                searchParams.getOffset(),
//                searchParams.getLimit(),
//                searchParams.getOrderby()
//        );
//        //组装数据
//        if (Objects.nonNull(result.getRows())) {
//            SearchParams appSearch = RemoteResultResolver.param(result.getRows(), "appVersion", "id");
//            ResponseEntity<JSONObject> listAll = appVersionService.listAll(appSearch);
//            System.out.println(listAll);
//            Integer code = Objects.requireNonNull(listAll.getBody()).getInteger("code");
//            if (code == 100) {
//                List<Map<String, Object>> applicationVersions = (ArrayList<Map<String, Object>>) listAll.getBody().get("data");
//                RemoteResultResolver.resultResolver(result.getRows(), applicationVersions, "applicationVersion", "appVersion");
//            }
//        }
//        return ResponseEntity.ok(ResultUtil.successJson(result));
//    }
    @SuppressWarnings("unchecked")
    @PostMapping(value = {"remoteList", ""})
    public ResponseEntity<JSONObject> pageList(@RequestBody SearchParams searchParams) {
        Page<TenantApp> result = tenantAppService.listPage(
                searchParams.getParams(),
                searchParams.getOffset(),
                searchParams.getLimit(),
                searchParams.getOrderby()
        );
        //查看是否多次调用
        int flag = RemoteResultResolver.getRemoteFlag();
        if(flag <= 1){
            //组装数据
            if (Objects.nonNull(result.getRows())) {
                flag ++;
                SearchParams appSearch = RemoteResultResolver.param(result.getRows(), "appVersion", "id");
                //设置Header
                HttpHeaders headers = new HttpHeaders();
                headers.add(RemoteResultResolver.REMOTE_FLAG, String.valueOf(flag));
                JSONObject listAll = restTemplate.post("cloud-geeke-devtool", "/micro/microVersion/listAll", JSONObject.class, appSearch,headers);
                Integer code = Objects.requireNonNull(listAll.getInteger("code"));
                if (code == 100) {
                    List<Map<String, Object>> applicationVersions = listAll.getObject("data", ArrayList.class);
                    RemoteResultResolver.resultResolver(result.getRows(), applicationVersions, "applicationVersion", "appVersion");
                }
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }


    @SuppressWarnings({"unchecked"})
    @PostMapping(value = "remoteListAll")
    public ResponseEntity<JSONObject> remoteListAll(@RequestBody SearchParams searchParams) {
        List<TenantApp> result = tenantAppService.listAll(searchParams.getParams(), searchParams.getOrderby());
        //组装数据
        if (CollectionUtils.isNotEmpty(result)) {
            SearchParams appSearch = RemoteResultResolver.param(result, "appVersion", "id");
            ResponseEntity<JSONObject> listAll = appVersionService.listAll(appSearch);
            System.out.println(listAll);
            Integer code = Objects.requireNonNull(listAll.getBody()).getInteger("code");
            if (code == 100) {
                List<Map<String, Object>> applicationVersions = (ArrayList<Map<String, Object>>) listAll.getBody().get("data");
                RemoteResultResolver.resultResolver(result, applicationVersions, "applicationVersion", "appVersion");
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
}
