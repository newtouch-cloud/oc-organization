package com.geeke.feign.fallback;

import com.alibaba.fastjson.JSONObject;
import com.geeke.feign.DataBusService;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataBusServiceFallBack implements DataBusService {
    @Override
    public ResponseEntity<JSONObject> receive(String tenantId, String sourceAppId, JSONObject jsonObject) {
        log.info("数据总线接收数据失败请检查!");
        JSONObject obj = new JSONObject();
        obj.put("code",500);
        obj.put("msg","数据总线服务下线,请检查相关服务");
        return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_500));
    }

}
