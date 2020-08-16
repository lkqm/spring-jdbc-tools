package com.github.lkqm.spring.jdbc;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 实体类信息
 *
 * @thread 线程安全的.
 */
public class EntityInfo<T> implements Serializable {

    /**
     * 类信息
     */
    private final ClassInfo<T> classInfo;
    /**
     * 主键字段信息
     */
    private FieldInfo idFieldInfo;
    /**
     * 所有字段信息
     */
    @Getter
    private final List<FieldInfo> fieldsInfo;

    /**
     * 默认主键字段名
     */
    private static final String ID_FIELD_NAME = "id";

    /**
     * 实例对象缓存
     */
    private static final ConcurrentHashMap<Class<?>, EntityInfo<?>> INSTANCE_CACHE = new ConcurrentHashMap<>();

    private EntityInfo(Class<T> clazz, boolean fastFailed) {
        this.classInfo = new ClassInfo(clazz);
        Field[] fields = clazz.getDeclaredFields();
        this.fieldsInfo = new ArrayList<>(fields.length);

        int idCount = 0;
        Field namedIdField = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (!InnerUtils.isEntityGenericField(field)) {
                continue;
            }
            Id annotation = field.getAnnotation(Id.class);
            if (annotation != null) {
                this.setIdField(field);
                idCount++;
            }
            if (ID_FIELD_NAME.equals(field.getName())) {
                namedIdField = field;
            }
            this.addField(field);
        }
        InnerUtils.assertArgument(idCount <= 1, "Found multiple @Id in class %s", clazz.getName());
        // 默认id字段
        if (this.idFieldInfo == null && namedIdField != null) {
            this.setIdField(namedIdField);
        }
        InnerUtils.assertArgument(!fastFailed || this.idFieldInfo != null,
                "Unable found id filed with annotation @Id or named 'id' in class %s", clazz.getName());
    }

    public static <T> EntityInfo<T> newInstance(Class<T> clazz) {
        return newInstance(clazz, true);
    }

    public static <T> EntityInfo<T> newInstance(Class<T> clazz, boolean fastFailed) {
        EntityInfo entityInfo = INSTANCE_CACHE.get(clazz);
        if (entityInfo == null) {
            synchronized (EntityInfo.class) {
                entityInfo = INSTANCE_CACHE.get(clazz);
                if (entityInfo == null) {
                    entityInfo = new EntityInfo(clazz, fastFailed);
                    INSTANCE_CACHE.put(clazz, entityInfo);
                }
            }
        }
        InnerUtils.assertArgument(!fastFailed || entityInfo.idFieldInfo != null,
                "Unable found id filed with annotation @Id or named 'id' in class %s", clazz.getName());
        return entityInfo;
    }

    //--------------------------------------------------------------------------
    // 表和主键
    //--------------------------------------------------------------------------

    /**
     * 获得表名
     */
    public String getTableName(boolean snake) {
        return classInfo.getTableName(snake);
    }

    /**
     * 获得主键名称
     */
    public String getIdColumnName(boolean snake) {
        checkIdInfo();
        return idFieldInfo.getColumnName(snake);
    }

    /**
     * 获得主键值
     */
    public Object getIdValue(T obj) {
        checkIdInfo();
        return idFieldInfo.get(obj);
    }

    /**
     * 设置主键值
     */
    public void setIdValue(T obj, Object value) {
        checkIdInfo();
        idFieldInfo.set(obj, value);
    }

    public void checkIdInfo() {
        InnerUtils.assertState(idFieldInfo != null, "Unable found id filed in class %s",
                classInfo.getClazz().getName());
    }

    //--------------------------------------------------------------------------
    // 列和值
    //--------------------------------------------------------------------------

    /**
     * 获取所有的列
     */
    public List<String> getColumnNames(boolean snake) {
        return doGetColumnNames(null, false, snake, true);
    }

    /**
     * 获取所有的列, 排除null列
     */
    public List<String> getColumnNamesSelective(T obj, boolean snake) {
        return doGetColumnNames(obj, true, snake, true);
    }

    /**
     * 获取列，不包括id
     */
    public List<String> getColumnNamesExcludeId(boolean snake) {
        return doGetColumnNames(null, false, snake, false);
    }

    /**
     * 获取列, 不包括id和null列
     */
    public List<String> getColumnNamesExcludeIdSelective(T obj, boolean snake) {
        return doGetColumnNames(obj, true, snake, false);
    }

    /**
     * 获得列列表
     *
     * @param obj       该类型对象,当切仅当selective==false时可以为null
     * @param selective 空值不插入
     * @param snake     字段蛇形命名
     * @return
     */
    private List<String> doGetColumnNames(T obj, boolean selective, boolean snake, boolean includeId) {
        List<FieldInfo> fields = this.fieldsInfo;
        List<String> results = new ArrayList<>(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            FieldInfo fieldInfo = fields.get(i);
            if (fieldInfo.isTransients() || (selective && fieldInfo.get(obj) == null)) {
                continue;
            }
            if (!includeId && idFieldInfo != null && idFieldInfo.getField() == fieldInfo.getField()) {
                continue;
            }
            results.add(fieldInfo.getColumnName(snake));
        }
        return results;
    }

    /**
     * 获取列值
     */
    public List<Object> getColumnValues(T obj) {
        return doGetColumnValues(obj, false, true);
    }

    /**
     * 获取列值，对null敏感
     */
    public List<Object> getColumnValuesSelective(T obj) {
        return doGetColumnValues(obj, true, true);
    }

    /**
     * 获取列值, 排除id字段
     */
    public List<Object> getColumnValuesExcludeId(T obj) {
        return doGetColumnValues(obj, false, false);
    }

    /**
     * 获取列值, 排除id字段，对null敏感
     */
    public List<Object> getColumnValuesExcludeIdSelective(T obj) {
        return doGetColumnValues(obj, true, false);
    }

    /**
     * 获得列的值
     *
     * @param obj       对象
     * @param selective null值敏感
     * @param includeId 是否包含id列
     * @return
     */
    private List<Object> doGetColumnValues(T obj, boolean selective, boolean includeId) {
        List<FieldInfo> fields = this.fieldsInfo;
        List<Object> results = new ArrayList<>(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            FieldInfo fieldInfo = fields.get(i);
            if (fieldInfo.isTransients() || (selective && fieldInfo.get(obj) == null)) {
                continue;
            }
            if (!includeId && idFieldInfo != null && idFieldInfo.getField() == fieldInfo.getField()) {
                continue;
            }
            results.add(fieldInfo.get(obj));
        }
        return results;
    }

    //--------------------------------------------------------------------------
    // SQL Snippet
    //--------------------------------------------------------------------------

    /**
     * 获得插入语句中列代码段， 例如: id, name
     */
    public String getInsertColumnsSqlSnippet(boolean snake) {
        return doGetInsertColumnsSqlSnippet(null, false, snake);
    }

    /**
     * 获得插入语句中列代码段，不包括null值字段 例如: id, name
     */
    public String getInsertColumnsSqlSnippetSelective(T obj, boolean snake) {
        return doGetInsertColumnsSqlSnippet(obj, true, snake);
    }

    /**
     * 获得插入语句中列代码段，例如: id, name
     *
     * @param obj       等待修改的对象
     * @param selective 对null敏感
     * @param snake     蛇形命名法
     * @return
     */
    public String doGetInsertColumnsSqlSnippet(T obj, boolean selective, boolean snake) {
        List<String> columns = selective ? getColumnNamesSelective(obj, snake) : getColumnNames(snake);
        return InnerUtils.join(", ", columns);
    }

    /**
     * 获得update语句set中的代码段
     *
     * @param snake 字段使用蛇形命名
     * @return
     */
    public String getUpdateSetSqlSnippet(boolean snake) {
        return doGetUpdateSetSqlSnippet(null, false, snake);
    }

    /**
     * 获得更新语句set中的代码段，对插入对象null敏感
     *
     * @param obj   等待更新的对象
     * @param snake 字段使用蛇形命名
     * @return
     */
    public String getUpdateSetSqlSnippetSelective(T obj, boolean snake) {
        return doGetUpdateSetSqlSnippet(obj, true, snake);
    }

    /**
     * 获得更新sql的set片段
     *
     * @param obj       等待修改的对象
     * @param selective 对null敏感
     * @param snake     蛇形命名法
     * @return
     */
    public String doGetUpdateSetSqlSnippet(T obj, boolean selective, boolean snake) {
        List<String> columns = selective ? getColumnNamesExcludeIdSelective(obj, snake) : getColumnNamesExcludeId(snake);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            sb.append(columns.get(i)).append("=?");
            if (i != columns.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * 创建该类型的一个实例，通过无参构造函数
     */
    public T createEntityObject() {
        Class<T> clazz = classInfo.getClazz();
        return InnerUtils.createObject(clazz);
    }

    //--------------------------------------------------------------------------
    // 辅助函数
    //--------------------------------------------------------------------------

    /**
     * 设置主键信息
     */
    private void setIdField(Field id) {
        this.idFieldInfo = new FieldInfo(id);
    }

    /**
     * 添加其他字段信息
     *
     * @param other
     */
    private void addField(Field other) {
        FieldInfo fieldInfo = new FieldInfo(other);
        this.fieldsInfo.add(fieldInfo);
    }

    //--------------------------------------------------------------------------
    // 数据结构
    //--------------------------------------------------------------------------

    /**
     * 通过反射获取类相关信息，内部不会缓存.
     *
     * @see #getTableName 获取表名
     */
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class ClassInfo<T> implements Serializable {

        private final Class<T> clazz;
        private final String className;
        private final String snakeClassName;
        private final String annotationTableName;

        public ClassInfo(Class<T> clazz) {
            this.clazz = clazz;
            this.className = clazz.getSimpleName();
            this.snakeClassName = InnerUtils.camelToSnake(this.className);
            Table tableAnnotation = clazz.getAnnotation(Table.class);
            this.annotationTableName = (tableAnnotation != null) ? tableAnnotation.name() : null;
        }

        /**
         * 获取表名称
         *
         * @param snake 是否使用蛇型命名的类名, 当指定@Table(name=)忽略该参数
         * @return
         */
        public String getTableName(boolean snake) {
            if (annotationTableName != null && annotationTableName.length() > 0) {
                return annotationTableName;
            }
            return snake ? snakeClassName : className;
        }

    }

    /**
     * 反射获取字段信息，内部不会缓存
     *
     * @see #getColumnName(boolean) 获取数据库列名
     */
    @Getter
    public static class FieldInfo implements Serializable {

        private final Field field;
        private final Class<?> type;
        private final String fieldName;
        private final String snakeFieldName;
        private final String annotationColumnName;
        private final boolean transients;

        public FieldInfo(Field field) {
            this.field = field;
            field.setAccessible(true);
            this.type = field.getType();
            this.fieldName = field.getName();
            this.snakeFieldName = InnerUtils.camelToSnake(this.fieldName);
            Column annotation = field.getAnnotation(Column.class);
            this.annotationColumnName = (annotation != null) ? annotation.name() : null;
            Transient transientAno = field.getAnnotation(Transient.class);
            this.transients = (transientAno != null);
        }

        /**
         * 获得对应数据库字段名称
         */
        public String getColumnName(boolean snake) {
            if (annotationColumnName != null) {
                return annotationColumnName;
            }
            return snake ? snakeFieldName : fieldName;
        }

        /**
         * 获取对象中特定字段值
         */
        public Object get(Object obj) {
            try {
                return this.field.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Never happen!", e);
            }
        }

        /**
         * 设置对象中特定字段值
         */
        public void set(Object obj, Object value) {
            try {
                field.set(obj, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Never happen!", e);
            }
        }
    }
}
