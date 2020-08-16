package com.github.lkqm.spring.jdbc;

import java.io.Serializable;
import java.sql.PreparedStatement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 封装PreparedStatement执行参数.
 */
@Getter
@EqualsAndHashCode
@ToString
public class PreparedSql implements Serializable {

    /**
     * sql of jdbc prepared statement.
     */
    protected final String sql;

    /**
     * args of jdbc prepared statement.
     */
    protected final Object[] args;

    public PreparedSql(String sql, Object[] args) {
        this.sql = sql;
        this.args = args;
    }
}
