package com.github.lkqm.spring.jdbc;

import com.github.lkqm.spring.jdbc.EntityInfo.FieldInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

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
        DefaultPreparedStatementCreator psc = new DefaultPreparedStatementCreator(preparedSql.sql, preparedSql.args);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = this.update(psc, keyHolder);
        if (rows > 0) {
            setGenerateKey(data, keyHolder);
        }
        return rows;
    }

    private void setGenerateKey(Object data, KeyHolder keyHolder) {
        EntityInfo<?> entityInfo = JdbcTemplateUtils.parseEntityClass(data.getClass());
        FieldInfo idFieldInfo = entityInfo.getIdFieldInfo();
        if (idFieldInfo == null || idFieldInfo.get(data) != null) {
            return;
        }

        Number key = keyHolder.getKey();
        if (key == null) {
            return;
        }
        Object idValue = InnerUtils.convertNumberType(key, idFieldInfo.getType());
        if (idValue == null) {
            throw new TypeMismatchDataAccessException(
                    "Auto generate key can't assigned to id type: " + idFieldInfo.getType().getName());
        }
        idFieldInfo.set(data, idValue);
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
        PreparedSql preparedSql = JdbcTemplateUtils.parseFind(id, entityClass);
        RowMapper<T> rowMapper = JdbcTemplateUtils.parseRowMapper(entityClass);
        return this.queryForObject(preparedSql.sql, rowMapper, preparedSql.args);
    }

    public <T> List<T> findByIds(Collection<?> ids, Class<T> entityClass) {
        PreparedSql preparedSql = JdbcTemplateUtils.parseFind(ids, entityClass);
        RowMapper<T> rowMapper = JdbcTemplateUtils.parseRowMapper(entityClass);
        return this.query(preparedSql.sql, rowMapper, preparedSql.args);
    }


    @AllArgsConstructor
    private static class DefaultPreparedStatementCreator implements PreparedStatementCreator {

        private final String sql;
        private final Object[] args;

        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement ps = con.prepareStatement(sql);
            PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(args);
            pss.setValues(ps);
            return ps;
        }
    }


}
