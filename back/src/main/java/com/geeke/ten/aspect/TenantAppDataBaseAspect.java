package com.geeke.ten.aspect;

import com.alibaba.fastjson.JSONObject;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.feign.AppVersionService;
import com.geeke.ten.annotation.CreateAppDatabase;
import com.geeke.ten.entity.Tenant;
import com.geeke.ten.entity.TenantApp;
import com.geeke.ten.service.TenantAppService;
import com.geeke.utils.JdbcUtils;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.newtouch.cloud.common.dependency.remote.rest.DynamicRestTemplate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

/**
 * @author TBH
 * 租户添加应用后,创建相关数据库和App下添加相关数据源
 */
@Slf4j
@Aspect
@Component
public class TenantAppDataBaseAspect {

    private static final String REFRESH_DATASOURCE = "/refresh/dataSource";

    @Autowired
    private AppVersionService appVersionService;

    @Autowired
    private TenantAppService tenantAppService;

    @Autowired
    private DynamicRestTemplate restTemplate;

    @Pointcut("@annotation(com.geeke.ten.annotation.CreateAppDatabase)")
    public void point() {
    }

    @Transactional(readOnly = false)
    @Around("point() && @annotation(createAppDatabase)")
    public Object execute(ProceedingJoinPoint pjp, CreateAppDatabase createAppDatabase) throws Throwable {
        Object proceed = pjp.proceed();
        TenantApp tenantApp = null;
        //准备参数 TenantApp
        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            if (arg instanceof TenantApp) {
                tenantApp = (TenantApp) arg;
            }
        }
        if (Objects.isNull(tenantApp)) {
            log.info("不是分配App,业务逻辑结束");
        }
        //执行目标方法,这里就是保存了租户App的数据
        //处理逻辑
        createDatabase(tenantApp);
        return proceed;
    }

    //创建数据库
    private void createDatabase(TenantApp tenantApp) {
        Tenant tenant = tenantApp.getTenant();
        String dbIp = tenantApp.getDbIp();
        Integer dbPort = tenantApp.getDbPort();
        String dbUsername = tenantApp.getDbUsername();
        String dbPassword = tenantApp.getDbPassword();
        String dbUrl = tenantApp.getDbUrl();
        String dbName = tenantApp.getDbName();
        JSONObject appVersionResult = appVersionService.getById(tenantApp.getAppVersion());
        if (Objects.equals(appVersionResult.getString("code"),"100")) {
            JSONObject appVersion = appVersionResult.getJSONObject("data");
            String name = appVersion.getString("name");
            String appKey = appVersion.getJSONObject("app").getString("appKey");
            String driver = appVersion.getJSONObject("dbType").getString("driver");
            String profile = appVersion.getJSONObject("type").getString("value");
            String url = "jdbc:mysql://" + dbIp + ":" + dbPort + "/?allowPublicKeyRetrieval=true&useSSL=false";
            //创建数据库规则 就以AppKey + 版本号 + 环境 + $$ + 租户ID
            String genDatabase = appKey + "$" + profile  + "$" + tenant.getId();
            JdbcUtils jdbcUtils = new JdbcUtils(dbUsername, dbPassword, url, driver);
            Connection connection = jdbcUtils.getConnection();
            if(Objects.isNull(connection)){
                throw new CommonJsonException(ErrorEnum.E_90004);
            }
            //第一步 先检测这个数据库有没有创建 没有创建才创建
            boolean databaseExist = checkDatabase(jdbcUtils, dbName, genDatabase);
            //如果存在了，那么就不管,jdbc连接也还是照常
            if(!databaseExist || (Objects.equals(dbName,genDatabase))){
                //如果不存在 执行到了这里 那就是创建了数据库 不仅要更新数据库名称 还需要更新jdbc连接
                tenantApp.setDbName(genDatabase);
                String jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPort + "/" + genDatabase + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8";
                tenantApp.setDbUrl(jdbcUrl);
                tenantAppService.save(tenantApp);
                //refreshDataSource(appKey,name);
            }
        }
    }

    //添加数据源
    private void refreshDataSource(String appKey,String appName) {
        if(StringUtils.isBlank(appName) || StringUtils.isBlank(appKey)){
            log.info("刷新数据源出错,appKey:{},appName:{}",appKey,appName);
        }
        String serviceId = appKey + "-" +  appName.replace(".","-");
        JSONObject jsonObject = restTemplate.post(serviceId, REFRESH_DATASOURCE, JSONObject.class, null,null);
        if(Objects.equals(jsonObject.getString("code"),"100")){
            log.info("刷新数据源成功,请求结果:{}",jsonObject);
        }else{
            log.info("刷新数据源失败,请求结果:{}",jsonObject);
        }
    }

    private boolean checkDatabase(JdbcUtils jdbcUtils, String database, String genDatabase) {
        boolean exist = false;
        Connection connection = null;
        PreparedStatement ps = null;
        try{
            connection = jdbcUtils.getConnection();
            String sql = "SHOW DATABASES LIKE \"" + database + "\"";
            log.info("执行sql语句:{}" , sql);
            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                log.info("数据库存在");
                exist = true;
            }else {
                log.info("数据库不存在,将会创建数据库:{}",genDatabase);
                String createSql = "CREATE DATABASE `" + genDatabase + "`";
                log.info("执行sql语句:{}" , createSql);
                ps = connection.prepareStatement(createSql);
                ps.executeUpdate();
                log.info("数据库创建成功");
            }
        }catch (Exception e){
            log.error("创建数据库出现错误:{}",e.getMessage());
        }finally {
            if(ps != null){
                jdbcUtils.close(ps);
            }
            if(connection != null){
                jdbcUtils.close(connection);
            }
        }
        return exist;
    }
}
