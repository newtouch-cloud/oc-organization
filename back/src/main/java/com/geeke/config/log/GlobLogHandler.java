package com.geeke.config.log;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.geeke.common.controller.SearchParams;
import com.geeke.feign.LogService;
import com.geeke.sys.utils.SessionUtils;
import com.google.common.collect.Lists;
import com.newtouch.cloud.common.dependency.logstash.config.LogIpConfig;
import com.newtouch.cloud.common.dependency.logstash.interceptor.LogHandler;
import com.newtouch.cloud.common.dependency.logstash.utils.InvocationUtils;
import io.swagger.annotations.ApiOperation;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author FallenRunning(TBH)
 */
@Component
public class GlobLogHandler implements LogHandler {

    @Autowired
    private LogService logService;

    @Autowired
    private LogIpConfig logIpConfig;

    @Value("${elasticsearch.index}")
    private String es_index;

    @Override
    public void beforeHandler(MethodInvocation invocation) {
        JSONObject obj = getDefaultValue(invocation);
        obj.set("action", "ENTER");
        logService.before(obj);
    }

    @Override
    public void successHandler(MethodInvocation invocation, Object result) {
        JSONObject obj = getDefaultValue(invocation);
        obj.set("action", "SUCCESS");
        obj.set("result_value", result);
        logService.success(obj);
    }

    @Override
    public void exceptionHandler(MethodInvocation invocation, Throwable th) {
        JSONObject obj = getDefaultValue(invocation);
        obj.set("action", "EXCEPTION");
        JSONObject jsonExp = new JSONObject();
        jsonExp.set("stack_trace", th.getStackTrace());
        jsonExp.set("message", th.getLocalizedMessage());
        obj.set("exception", jsonExp);
        logService.exception(obj);
    }

    private JSONObject getDefaultValue(MethodInvocation invocation) {
        JSONObject obj = new JSONObject();
        obj.set("es_index", es_index);
        obj.set("state_time", DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_FORMATTER));
        obj.set("operator", SessionUtils.getUserJson());
        obj.set("tenant_id", "default");
        obj.set("ipAddress", logIpConfig.getPropertyValue());
        obj.set("javaController", InvocationUtils.getClassNameAndMethodName(invocation));
        obj.set("methodDesc", InvocationUtils.getMethodName(invocation));
        obj.set("argsVal", argsToJsonArray(InvocationUtils.getArgs(invocation)));
        ApiOperation apiOperation = InvocationUtils.getMethodAnnotation(invocation, ApiOperation.class);
        if (Objects.nonNull(apiOperation)) {
            obj.set("methodDesc", apiOperation.value());
        }
        return obj;
    }

    private JSONArray argsToJsonArray(Object[] args) {
        JSONArray array = new JSONArray();
        if (Objects.isNull(args)) {
            return array;
        }
        List<Object> params = Lists.newArrayListWithCapacity(args.length);
        for (Object param : args) {
            if (!(param instanceof ServletRequest)
                    && !(param instanceof ServletResponse)
                    && !(param instanceof HttpSession)
                    && !(param instanceof MultipartFile)
                    && !(param instanceof SearchParams)
            ) {
                params.add(param);
            }
        }
        array = JSONUtil.parseArray(params);
        return array;
    }
}
