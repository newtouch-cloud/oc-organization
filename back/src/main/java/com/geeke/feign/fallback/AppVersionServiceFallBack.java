package com.geeke.feign.fallback;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.feign.AppVersionService;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author FallenRunning(TBH)
 * 服务降级
 */
@Component
public class AppVersionServiceFallBack implements AppVersionService {

    @Override
    public ResponseEntity<JSONObject> listAll(SearchParams searchParams) {
        return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_400));
    }

    @Override
    public JSONObject getById(String id) {
        return ResultUtil.errorJson(ErrorEnum.E_400);
    }
}
