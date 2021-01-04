package com.github.lkqm.spring.jdbc;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class BaseServiceImpl<T, ID> implements BaseService<T, ID> {

    @Autowired
    private JdbcTemplatePlus jdbcTemplate;

    private Class<T> entityClass;
    private EntityInfo<T> entityInfo;

    public BaseServiceImpl() {
        init();
    }

    public void setJdbcTemplate(JdbcTemplatePlus jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("unchecked")
    private void init() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        this.entityInfo = EntityInfo.newInstance(this.entityClass, true);
    }

    @Override
    public void insert(T entity) {
        Assert.notNull(entity, "Entity must not be null.");
        jdbcTemplate.insert(entity);
    }

    @Override
    public void update(T entity) {
        Assert.notNull(entity, "Entity must not be null.");
        jdbcTemplate.updateById(entity);
    }

    @Override
    public long deleteById(ID id) {
        Assert.notNull(id, "Id must not be null.");
        return jdbcTemplate.deleteById(id, entityClass);
    }

    @Override
    public long deleteById(Collection<ID> ids) {
        Assert.notNull(ids, "Ids must not be null.");
        return jdbcTemplate.deleteByIds(ids, entityClass);
    }

    @Override
    public T findById(ID id) {
        Assert.notNull(id, "Id must not be null.");
        return jdbcTemplate.findById(id, entityClass);
    }

    @Override
    public List<T> findById(Collection<ID> ids) {
        Assert.notEmpty(ids, "Ids must not be null or empty.");
        return jdbcTemplate.findByIds(ids, entityClass);
    }
}
