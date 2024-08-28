package com.geeke.common.service;

import com.alibaba.fastjson.JSONArray;
import com.geeke.common.data.Parameter;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.sys.utils.SessionUtils;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Service基类
 * @author lys
 * @version 2014-05-16
 */
@Transactional(readOnly = true)
public abstract class BaseService {
	
	// 当前用户和当前公司的key值
	private static String CURRENT_USER_KEY = "currentUser";
	private static String CURRENT_COMPANY_KEY = "currentCompany";
	private static String CURRENT_TENANT_KEY = "currentTenant";


	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());


	//创建锁对象
	Lock lock = new ReentrantLock(); //不指定布尔类型，默认非公平锁

	private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");

	/**
	 * 筛选条件过滤
	 * @param parameters
	 * @return
	 * @throws ScriptException
	 */
	protected List<Parameter> addFilter(List<Parameter> parameters, String filter) {
		List<Parameter> newList = Lists.newArrayList();
		String myFilter = evalFilter(filter);
		List<Parameter> filters = JSONArray.parseArray(myFilter, Parameter.class);

		if(null != filters && filters.size() > 0) {
			// 刷选条件使用（）包含，避免引发逻辑错误
			newList.add(new Parameter("", "(", "" ));
			newList.addAll(filters);
			newList.add(new Parameter("", ")", "" ));
		}

		if(null != parameters && parameters.size() > 0) {
			// 客户端条件使用（）包含，避免引发逻辑错误
			newList.add(new Parameter("", "(", "" ));
			newList.addAll(parameters);
			newList.add(new Parameter("", ")", "" ));
		}
		return newList;
	}

	private String evalFilter(String filter) {
		StringBuffer buffer =  new StringBuffer();
		buffer.append("[");
		buffer.append(filter);
		buffer.append("]");
		
		if(!filter.contains(CURRENT_USER_KEY) && !filter.contains(CURRENT_COMPANY_KEY)) {
			return buffer.toString();
		}

        // 获取锁  
        lock.lock();  
        try {  
			engine.put(CURRENT_USER_KEY, SessionUtils.getUserJson());
			engine.put(CURRENT_COMPANY_KEY,SessionUtils.getUserJson().getJSONObject("company"));
			engine.put(CURRENT_TENANT_KEY,SessionUtils.getUserJson().getJSONObject("tenant"));
			StringBuffer bufferEval =  new StringBuffer();
			bufferEval.append("var obj = JSON.stringify(");
			bufferEval.append(buffer);
			bufferEval.append(");");

			engine.eval(bufferEval.toString());
			return engine.get("obj").toString();
        } catch(Exception e) {
			throw new CommonJsonException(ResultUtil.errorJson(ErrorEnum.E_50001, "业务表筛选条件配置格式不正确"));
		}finally {  
            // 释放锁  
            lock.unlock();  
        }  
	}

}
