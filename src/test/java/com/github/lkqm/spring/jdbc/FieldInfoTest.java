package com.github.lkqm.spring.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import com.github.lkqm.spring.jdbc.EntityInfo.FieldInfo;
import java.lang.reflect.Field;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

class FieldInfoTest {

    @AllArgsConstructor
    public class User1 {
        @Transient
        private Integer id;

        @Column(name = "last_name")
        private String name;

        private Date createTime;
    }

    @Test
    public void test() throws NoSuchFieldException {
        User1 user1 = new User1(1, "罗", null);
        Field idField = user1.getClass().getDeclaredField("id");
        FieldInfo idFieldInfo = new FieldInfo(idField);
        assertEquals("id", idFieldInfo.getColumnName(true));
        assertEquals("id", idFieldInfo.getFieldName());
        assertNull(idFieldInfo.getAnnotationColumnName());
        assertEquals(1, idFieldInfo.get(user1));
        assertTrue(idFieldInfo.isTransients());
        idFieldInfo.set(user1, 2);
        assertEquals(2, idFieldInfo.get(user1));

        // 注解字段名
        Field nameField = user1.getClass().getDeclaredField("name");
        FieldInfo nameFieldInfo = new FieldInfo(nameField);
        assertEquals("last_name", nameFieldInfo.getColumnName(true));
        assertEquals("last_name", nameFieldInfo.getColumnName(false));

        // 名称风格
        Field timeField = user1.getClass().getDeclaredField("createTime");
        FieldInfo timeFieldInfo = new FieldInfo(timeField);
        assertEquals("create_time", timeFieldInfo.getColumnName(true));
        assertEquals("createTime", timeFieldInfo.getColumnName(false));
    }

}