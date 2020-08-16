package com.github.lkqm.spring.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class InnerUtilsTest {

    @Test
    void camelToSnake() {
        assertEquals("user_id", InnerUtils.camelToSnake("userId"));
        assertEquals("user_id", InnerUtils.camelToSnake("UserId"));
        assertEquals("user_id", InnerUtils.camelToSnake("user_Id"));
        assertEquals("$_user_id_", InnerUtils.camelToSnake("$UserId_"));
    }

    @Test
    void join() {
        List<Integer> elements = new ArrayList<Integer>() {{
            add(1);
            add(2);
            add(3);
        }};
        assertEquals("1, 2, 3", InnerUtils.join(", ", elements));

        List<Integer> elements2 = new ArrayList<>();
        assertEquals("", InnerUtils.join(", ", elements2));
        List<Integer> elements3 = new ArrayList<>();
        elements3.add(1);
        assertEquals("1", InnerUtils.join(", ", elements3));
    }

    @Test
    void fillList() {
        List<String> values = InnerUtils.fillList("1", 4);
        assertEquals("1,1,1,1", InnerUtils.join(",", values));
    }

}