package com.github.lkqm.spring.jdbc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class InnerUtils {

    /**
     * 驼峰命名转蛇形
     */
    public static String camelToSnake(String camel) {
        char[] camelChars = camel.toCharArray();
        StringBuilder snakeSb = new StringBuilder();
        int distance = 'A' - 'a';
        for (int i = 0; i < camelChars.length; i++) {
            char cchar = camelChars[i];
            if (i < camelChars.length - 1) {
                // 非最后一个字符 && 当前字符'_' && 下一个字符为大写
                char nextChar = camelChars[i + 1];
                if (cchar == '_' && nextChar >= 'A' && nextChar <= 'Z') {
                    continue;
                }
            }

            char schar;
            if (cchar >= 'A' && cchar <= 'Z') {
                if (i != 0) {
                    // 非首字符插入_
                    snakeSb.append("_");
                }
                schar = (char) (cchar - distance);
            } else {
                schar = cchar;
            }
            snakeSb.append(schar);
        }
        return snakeSb.toString();
    }

    /**
     * 连接成字符串
     */
    public static String join(String separator, List<?> elements) {
        if(elements == null || elements.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.size() - 1; i++) {
            sb.append(elements.get(i)).append(separator);
        }
        sb.append(elements.get(elements.size() - 1));
        return sb.toString();
    }

    /**
     * 填充集合并返回
     */
    public static <T> List<T> fillList(T member, int size) {
        List<T> results = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            results.add(member);
        }
        return results;
    }

    /**
     * 是否是常规的字段
     */
    public static boolean isEntityGenericField(Field field) {
        int modifiers = field.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            return false;
        }
        if(field.getName().startsWith("this$")) {
            return false;
        }
        return true;
    }

    /**
     * 创建实例
     */
    public static <T> T createObject(Class<T> clazz) {
        T obj = null;
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor constructor : constructors) {
            constructor.setAccessible(true);
            int count = constructor.getParameterTypes().length;
            if (count == 0) {
                try {
                    obj = (T) constructor.newInstance();
                    break;
                } catch (InstantiationException e) {
                    throw new IllegalStateException("对应实体类型没有无参构造函数" + clazz.getName(), e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("对应实体类型没有无参构造函数" + clazz.getName(), e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException("对应实体类型没有无参构造函数" + clazz.getName(), e);
                }
            }
        }
        if (obj == null) {
            throw new IllegalStateException("没有无参构造方法" + clazz.getName());
        }
        return obj;
    }

    public static void assertArgument(boolean except, String message, Object...args) {
        if(!except) {
            String msg = String.format(message, args);
            throw new IllegalArgumentException(msg);
        }
    }

    public static void assertState(boolean except, String message, Object...args) {
        if(!except) {
            String msg = String.format(message, args);
            throw new IllegalStateException(msg);
        }
    }

}