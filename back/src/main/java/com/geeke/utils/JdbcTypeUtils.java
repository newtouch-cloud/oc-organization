package com.geeke.utils;

import com.geeke.common.constants.DbType;
import com.geeke.common.data.Parameter;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

/**
 * @author FallenRunning(TBH)
 * 通过数据源获取当前数据库连接是哪个数据库(动态数据源无法通过databaseId切换)
 */
public class JdbcTypeUtils {

    public final static String MYSQL = "1";
    public final static String ORACLE = "2";
    public final static String SQLSERVER = "3";

    public final static String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    public final static String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    public final static String MSSQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    @SneakyThrows
    public static DbType getType(DataSource dataSource) {
        if (Objects.isNull(dataSource)) {
            return DbType.MYSQL;
        }
        //获取元数据的连接
        String driverName = dataSource.getConnection().getMetaData().getDriverName();
        if (driverName.contains("SQL Server")) {
            return DbType.SQLSERVER;
        }
        if (driverName.contains("MySQL")) {
            return DbType.MYSQL;
        }
        if (driverName.contains("Oracle")) {
            return DbType.ORACLE;
        }
        return DbType.MYSQL;
    }

    public static String spliceUrl(String type, String ip, String port, String database) {
        StringBuffer sb = new StringBuffer();
        if (Objects.equals(type, MYSQL)) {
            sb.append("jdbc:mysql://");
            sb.append(ip);
            sb.append(":");
            sb.append(port);
            sb.append("/");
            sb.append(database);
            sb.append("?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8");
        }
        if (Objects.equals(type, ORACLE)) {
            sb.append("jdbc:oracle:thin:@");
            sb.append(ip);
            sb.append(":");
            sb.append(port);
            sb.append(":");
            sb.append(database);
        }
        if (Objects.equals(type, SQLSERVER)) {
            sb.append("jdbc:sqlserver://");
            sb.append(ip);
            sb.append(":");
            sb.append(port);
            sb.append(";");
            sb.append("DatabaseName=");
            sb.append(database);
        }
        return sb.toString();
    }

    public static String getDriver(String type) {
        if (Objects.equals(type, MYSQL)) {
            return MYSQL_DRIVER;
        }
        if (Objects.equals(type, ORACLE)) {
            return ORACLE_DRIVER;
        }
        if (Objects.equals(type, SQLSERVER)) {
            return MSSQL_DRIVER;
        }
        return null;
    }

    public static String paramsExtract(String paramName, List<Parameter> parameters){
       if(CollectionUtils.isNotEmpty(parameters)){
           for (Parameter parameter : parameters) {
               if(Objects.equals(parameter.getColumnName(),paramName) || Objects.equals(parameter.getColumnName(),'`' + paramName + '`') ){
                   return (String) parameter.getValue();
               }
           }
       }
       return null;
    }
}