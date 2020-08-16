package com.github.lkqm.spring.jdbc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcTemplateUtilsTest {

    @AllArgsConstructor
    public class User {
        private Integer id;
        private String name;
        private Date createTime;
    }

    @Test
    void parseInsert() {
        User user = new User(1, "Mario Luo", new Date());
        Object[] args = {user.id, user.name, user.createTime};
        PreparedSql preparedSql = JdbcTemplateUtils.parseInsert(user);
        assertNotNull(preparedSql);
        assertEquals("insert into user(id, name, create_time) values(?, ?, ?)", preparedSql.sql);
        assertArrayEquals(args, preparedSql.args);
    }

    @Test
    void parseDelete() {
        Object[] args = {1};
        PreparedSql preparedSql = JdbcTemplateUtils.parseDelete(1, User.class);
        assertNotNull(preparedSql);
        assertEquals("delete from user where id = ?", preparedSql.sql);
        assertArrayEquals(args, preparedSql.args);
    }

    @Test
    void parseUpdate() {
        User user = new User(1, "Mario Luo", null);
        Object[] args = {user.name, user.id};
        PreparedSql preparedSql = JdbcTemplateUtils.parseUpdate(user);
        assertNotNull(preparedSql);
        assertEquals("update user set name=? where id = ?", preparedSql.sql);
        assertArrayEquals(args, preparedSql.args);
    }

    @Test
    void parseQuery() {
        int id = 1;
        Object[] args = {id};
        PreparedSql preparedSql = JdbcTemplateUtils.parseQuery(id, User.class);
        assertNotNull(preparedSql);
        assertEquals("select id, name, create_time from user where id = ?", preparedSql.sql);
        assertArrayEquals(args, preparedSql.args);
    }
}