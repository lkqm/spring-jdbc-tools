package com.github.lkqm.spring.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * 基于JdbcTemplate工具增加CRUD操作.
 */
public class JdbcTemplatePlus extends JdbcTemplate {

    public int insert(Object data) {
        PreparedSql preparedSql = JdbcTemplateUtils.parseInsert(data);
        return this.update(preparedSql.sql, preparedSql.args);
    }

    public int deleteById(Object id, Class<?> entityClass) {
        PreparedSql preparedSql = JdbcTemplateUtils.parseDelete(id, entityClass);
        return this.update(preparedSql.sql, preparedSql.args);
    }

    public int updateById(Object data) {
        PreparedSql preparedSql = JdbcTemplateUtils.parseUpdate(data);
        return this.update(preparedSql.sql, preparedSql.args);
    }

    public <T> T selectById(Object id, Class<T> entityClass) {
        PreparedSql preparedSql = JdbcTemplateUtils.parseQuery(id, entityClass);
        RowMapper<T> rowMapper = JdbcTemplateUtils.parseQueryRowMapper(entityClass);
        return this.queryForObject(preparedSql.sql, rowMapper, preparedSql.args);
    }

}
