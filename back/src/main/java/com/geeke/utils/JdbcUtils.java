package com.geeke.utils;

import com.geeke.config.exception.CommonJsonException;
import com.geeke.utils.constants.ErrorEnum;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

/**
 * @author FallenRunning(TBH)
 */
public class JdbcUtils {

    private final String username; //用户名
    private final String password; //密码
    private final String url; //拼接到连接地址
    private final String driver;//数据库驱动

    public JdbcUtils(String username, String password,String url, String driver) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.driver = driver;
    }

    @SneakyThrows
    public Connection getConnection() {
        Connection connection = null;
        Class.forName(driver);
        try{
            //设置连接超时时间 http超时时间15 若是10秒没连接到数据库 则视为超时 否则会被http超时影响而混淆错误信息
            DriverManager.setLoginTimeout(10);
            connection = DriverManager.getConnection(url, username, password);
        }catch (Exception e){
            throw new CommonJsonException(ErrorEnum.E_500);
        }
        return connection;
    }

    @SneakyThrows
    public void close(AutoCloseable... closeables) {
        if (Objects.nonNull(closeables)) {
            for (AutoCloseable closeable : closeables) {
                if (Objects.nonNull(closeable)) {
                    closeable.close();
                }
            }
        }
    }
}