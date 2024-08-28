package com.geeke.common.constants;

/**
 * @author FallenRunning(TBH)
 * 数据库类型
 */
public enum DbType {
    MYSQL("mysql"),
    ORACLE("oracle"),
    SQLSERVER("sqlserver");

    private String type;

    DbType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
