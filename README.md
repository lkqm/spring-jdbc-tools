# spring-jdbc-tools ![Maven Central](https://img.shields.io/maven-central/v/com.github.lkqm/spring-jdbc-tools)
Spring jdbc tools for curd operations.

# Features
- Easy CRUD operations.
- Supports Java persistent api annotation.

# Quick
> JdbcTemplatePlus
```
    int insert(Object data); 
    int deleteById(Object id, Class<?> entityClass);    // deleteByIds
    int updateById(Object data);
    T findById(Object id, Class<T> entityClass);        // findByIds
```

> OR JdbcTemplateUtils
```
    PreparedSql parseInsert(Object data);
    PreparedSql parseDelete(Object id, Class<?> entityClass);
    PreparedSql parseUpdate(Object data);
    PreparedSql parseQuery(Object id, Class<?> entityClass);
    RowMapper<T> parseQueryRowMapper(Class<T> entityClass);
```

# Java Persistent API
- @Table: custom table name.
- @Column: custom column name.
- @Id: identify primary key, default field named 'id'.
