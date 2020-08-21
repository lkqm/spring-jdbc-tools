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
        if (elements == null || elements.size() == 0) {
            return "";
        }
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
        if (field.getName().startsWith("this$")) {
            return false;
        }
        return true;
    }

    /**
     * 创建实例
     */
    public static <T> T createObject(Class<T> clazz) {
        Constructor<?> constructor = null;
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor one : constructors) {
            if (0 == one.getParameterTypes().length) {
                constructor = one;
                break;
            }
        }
        if (constructor == null) {
            constructor = constructors[0];
        }
        return createObject(constructor);
    }

    private static <T> T createObject(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] args = getTypeDefaultValues(parameterTypes);
        try {
            constructor.setAccessible(true);
            return (T) constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 获得类型的默认值
     */
    public static Object[] getTypeDefaultValues(Class<?>[] types) {
        Object[] values = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            values[i] = getTypeDefaultValue(type);
        }
        return values;
    }

    public static Object getTypeDefaultValue(Class type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (byte.class == type) {
            return (byte) 0;
        } else if (char.class == type) {
            return '\u0000';
        } else if (short.class == type) {
            return (short) 0;
        } else if (int.class == type) {
            return 0;
        } else if (long.class == type) {
            return 0L;
        } else if (double.class == type) {
            return 0D;
        } else if (float.class == type) {
            return 0F;
        } else if (boolean.class == type) {
            return false;
        } else {
            return null;
        }
    }

    public static void assertArgument(boolean except, String message, Object... args) {
        if (!except) {
            String msg = String.format(message, args);
            throw new IllegalArgumentException(msg);
        }
    }

    public static void assertState(boolean except, String message, Object... args) {
        if (!except) {
            String msg = String.format(message, args);
            throw new IllegalStateException(msg);
        }
    }

    /**
     * 转化类型, 返回null代表不能转换
     */
    public static Object convertNumberType(Number key, Class<?> type) {
        if (type.isAssignableFrom(key.getClass())) {
            return key;
        }
        if (String.class.isAssignableFrom(type)) {
            return String.valueOf(key.longValue());
        }
        if (type == byte.class || type == Byte.class) {
            return key.byteValue();
        }
        if (type == short.class || type == Short.class) {
            return key.shortValue();
        }
        if (type == int.class || type == Integer.class) {
            return key.intValue();
        }
        if (type == long.class || type == Long.class) {
            return key.longValue();
        }
        return null;
    }
}