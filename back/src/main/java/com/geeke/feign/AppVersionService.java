package com.geeke.feign;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.feign.fallback.AppVersionServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author FallenRunning(TBH)
 * 应用版本(尝试使用真实地址)
 */
@FeignClient(name = "cloud-geeke-devtool", path = "/micro/microVersion", fallback = AppVersionServiceFallBack.class)
public interface AppVersionService {
    /**
     * @param searchParams 查询参数
     * @return 查询结果
     */
    @PostMapping(value = "listAll")
    ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams);

    @GetMapping("/{id}")
    JSONObject getById(@PathVariable("id") String id);
}
