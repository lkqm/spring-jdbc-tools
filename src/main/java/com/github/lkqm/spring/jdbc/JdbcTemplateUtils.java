package com.github.lkqm.spring.jdbc;

import java.util.List;
import org.springframework.jdbc.core.RowMapper;

/**
 * JdbcTemplate工具类，解析实体类信息为JdbcTemplate相关数据。
 */
public class JdbcTemplateUtils {

    private static final boolean SNAKE = true;

    /**
     * 解析插入语句
     */
    public static PreparedSql parseInsert(Object data) {
        EntityInfo<Object> entityInfo = getEntityInfo(data.getClass());
        String table = entityInfo.getTableName(SNAKE);
        String columns = entityInfo.getInsertColumnsSqlSnippetSelective(data, SNAKE);
        List<Object> values = entityInfo.getColumnValuesSelective(data);
        String valuesReplacer = InnerUtils.join(", ", InnerUtils.fillList("?", values.size()));

        String sql = String.format("insert into %s(%s) values(%s)", table, columns, valuesReplacer);
        Object[] args = values.toArray();
        return new PreparedSql(sql, args);
    }

    /**
     * 解析删除语句
     */
    public static PreparedSql parseDelete(Object id, Class<?> entityClass) {
        EntityInfo<Object> entityInfo = getEntityInfo(entityClass);
        String table = entityInfo.getTableName(SNAKE);
        String idColumn = entityInfo.getIdColumnName(SNAKE);
        String sql = String.format("delete from %s where %s = ?", table, idColumn);
        Object[] args = {id};
        return new PreparedSql(sql, args);
    }

    /**
     * 解析更新语句, 实体null参数不参与更新.
     */
    public static PreparedSql parseUpdate(Object data) {
        EntityInfo<Object> entityInfo = getEntityInfo(data.getClass());
        String table = entityInfo.getTableName(SNAKE);
        String columns = entityInfo.getUpdateSetSqlSnippetSelective(data, SNAKE);
        String idColumn = entityInfo.getIdColumnName(SNAKE);

        Object idValue = entityInfo.getIdValue(data);
        List<Object> values = entityInfo.getColumnValuesExcludeIdSelective(data);
        values.add(idValue);

        String sql = String.format("update %s set %s where %s = ?", table, columns, idColumn);
        return new PreparedSql(sql, values.toArray());
    }

    /**
     * 解析查询语句
     */
    public static PreparedSql parseQuery(Object id, Class<?> entityClass) {
        EntityInfo<Object> entityInfo = getEntityInfo(entityClass);
        String table = entityInfo.getTableName(SNAKE);
        String idColumn = entityInfo.getIdColumnName(SNAKE);
        String columns = entityInfo.getInsertColumnsSqlSnippet(SNAKE);

        String sql = String.format("select %s from %s where %s = ?", columns, table, idColumn);
        Object[] args = {id};
        return new PreparedSql(sql, args);
    }

    /**
     * 解析查询映射类
     */
    public static <T> RowMapper<T> parseQueryRowMapper(Class<T> entityClass) {
        EntityInfo<T> entityInfo = getEntityInfo(entityClass);
        return new EntityRowMapper<>(entityInfo, SNAKE);
    }

    private static <T> EntityInfo<T> getEntityInfo(Class entityClass) {
        return EntityInfo.newInstance(entityClass);
    }
}
