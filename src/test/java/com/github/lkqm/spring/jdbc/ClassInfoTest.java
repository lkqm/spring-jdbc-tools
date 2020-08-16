package com.github.lkqm.spring.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.lkqm.spring.jdbc.EntityInfo.ClassInfo;
import javax.persistence.Table;
import org.junit.jupiter.api.Test;

class ClassInfoTest {

    public class UserDetail {
    }

    @Table(name = "user")
    public class AnnotationUser {
    }

    @Test
    public void test() throws NoSuchFieldException {
        // 基本测试
        ClassInfo<UserDetail> userClassInfo = new ClassInfo<>(UserDetail.class);
        assertEquals("user_detail", userClassInfo.getTableName(true));
        assertEquals("UserDetail", userClassInfo.getTableName(false));
        assertEquals(UserDetail.class, userClassInfo.getClazz());
        assertEquals("UserDetail", userClassInfo.getClassName());
        assertEquals("user_detail", userClassInfo.getSnakeClassName());
        assertNull(userClassInfo.getAnnotationTableName());

        // 注解测试
        ClassInfo<AnnotationUser> annotationUserClassInfo = new ClassInfo<>(AnnotationUser.class);
        assertEquals("user", annotationUserClassInfo.getTableName(true));
    }

}