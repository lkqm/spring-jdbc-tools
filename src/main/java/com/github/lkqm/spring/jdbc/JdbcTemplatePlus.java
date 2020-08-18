package com.github.lkqm.spring.jdbc;

import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * 基于JdbcTemplate工具增加CRUD操作.
 *
 * @see #insert(Object)
 * @see #deleteById(Object, Class)
 * @see #deleteByIds(Collection, Class)
 * @see #findById(Object, Class)
 * @see #findByIds(Collection, Class)
 * @see #updateById(Object)
 */
public class JdbcTemplatePlus extends JdbcTemplate {

    public JdbcTemplatePlus() {
    }

    public JdbcTemplatePlus(DataSource dataSource) {
        super(dataSource);
    }

    public JdbcTemplatePlus(DataSource dataSource, boolean lazyInit) {
        super(dataSource, lazyInit);
    }

    public int insert(Object data) {
        PreparedSql preparedSql = JdbcTemplateUtils.parseInsert(data);
        return this.update(preparedSql.sql, preparedSql.args);
    }

    public int deleteById(Object id, Class<?> entityClass) {
        PreparedSql preparedSql = JdbcTemplateUtils.parseDelete(id, entityClass);
        return this.update(preparedSql.sql, preparedSql.args);
    }

    public int deleteByIds(Collection<?> ids, Class<?> entityClass) {
        PreparedSql preparedSql = JdbcTemplateUtils.parseDelete(ids, entityClass);
        return this.update(preparedSql.sql, preparedSql.args);
    }

    public int updateById(Object data) {
        PreparedSql preparedSql = JdbcTemplateUtils.parseUpdate(data);
        return this.update(preparedSql.sql, preparedSql.args);
    }

    public <T> T findById(Object id, Class<T> entityClass) {
        PreparedSql preparedSql = JdbcTemplateUtils.parseQuery(id, entityClass);
        RowMapper<T> rowMapper = JdbcTemplateUtils.parseQueryRowMapper(entityClass);
        return this.queryForObject(preparedSql.sql, rowMapper, preparedSql.args);
    }

    public <T> List<T> findByIds(Collection<?> ids, Class<T> entityClass) {
        PreparedSql preparedSql = JdbcTemplateUtils.parseQuery(ids, entityClass);
        RowMapper<T> rowMapper = JdbcTemplateUtils.parseQueryRowMapper(entityClass);
        return this.query(preparedSql.sql, rowMapper, preparedSql.args);
    }

}
