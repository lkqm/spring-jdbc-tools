package com.github.lkqm.spring.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import lombok.Getter;
import org.springframework.jdbc.core.RowMapper;

/**
 * 通过反射获得对应POJO对象
 *
 * 注：反射对象类型必须拥有一个默认构造函数
 * @param <T>
 */
@Getter
public class EntityRowMapper<T> implements RowMapper<T> {

    private final EntityInfo<T> entityInfo;
    private final boolean snake;

    public EntityRowMapper(EntityInfo<T> entityInfo, boolean snake) {
        this.entityInfo = entityInfo;
        this.snake = snake;
    }

    public EntityRowMapper(Class<T> entityClass, boolean snake) {
        this.entityInfo = EntityInfo.newInstance(entityClass);
        this.snake = snake;
    }


    @Override
    public T mapRow(ResultSet rs, int i) throws SQLException {
        List<EntityInfo.FieldInfo> fields = entityInfo.getFieldsInfo();
        T entity = entityInfo.createEntityObject();

        for (EntityInfo.FieldInfo field : fields) {
            String column = field.getColumnName(snake);
            Class<?> type = convertType(field.getType());
            Object value = rs.getObject(column, type);
            field.set(entity, value);
        }
        return entity;
    }

    private Class<?> convertType(Class<?> originType) {
        Class<?> type = originType;
        if(type == java.util.Date.class) {
            type = Timestamp.class;
        }
        return type;
    }
};