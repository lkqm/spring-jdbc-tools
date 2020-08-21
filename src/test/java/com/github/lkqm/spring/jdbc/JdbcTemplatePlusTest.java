package com.github.lkqm.spring.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JdbcTemplatePlusTest {

    private JdbcTemplatePlus jdbcTemplate;

    @BeforeEach
    public void before() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL");
        jdbcTemplate = new JdbcTemplatePlus(dataSource);
        jdbcTemplate.execute("create table user( id int auto_increment primary key, name varchar(255) )");
    }

    @AfterEach
    public void after() {
        jdbcTemplate.execute("drop table user");
    }

    @Test
    void insert() {
        User user = new User(1, "LW");
        int row = jdbcTemplate.insert(user);
        assertTrue(row > 0);

        User noneIdUser = new User(null, "LW");
        int noneIdRow = jdbcTemplate.insert(noneIdUser);
        assertTrue(noneIdRow > 0);
        assertNotNull(noneIdUser.id);
    }

    @Test
    void deleteById() {
        jdbcTemplate.insert(new User(1, "LW"));
        int rows = jdbcTemplate.deleteById(1, User.class);
        assertTrue(rows > 0);
    }

    @Test
    void deleteByIds() {
        jdbcTemplate.insert(new User(1, "LW"));
        jdbcTemplate.insert(new User(2, "LW"));
        int rows = jdbcTemplate.deleteByIds(Arrays.asList(1, 2), User.class);
        assertTrue(rows == 2);
    }

    @Test
    void updateById() {
        jdbcTemplate.insert(new User(1, "LW"));
        int rows = jdbcTemplate.updateById(new User(1, "EGM"));
        assertTrue(rows > 0);
        User user = jdbcTemplate.findById(1, User.class);
        assertEquals("EGM", user.name);
    }

    @Test
    void findById() {
        jdbcTemplate.insert(new User(1, "LW"));

        User user = jdbcTemplate.findById(1, User.class);
        assertNotNull(user);
        assertEquals(1, (int) user.id);
        assertEquals("LW", user.name);
    }

    @Test
    void findByIds() {
        jdbcTemplate.insert(new User(1, "LW"));
        jdbcTemplate.insert(new User(2, "LW"));

        List<User> users = jdbcTemplate.findByIds(Arrays.asList(1, 2), User.class);
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @AllArgsConstructor
    public static class User {
        private Integer id;
        private String name;
    }
}
