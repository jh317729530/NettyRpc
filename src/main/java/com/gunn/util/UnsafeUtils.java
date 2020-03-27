package com.gunn.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtils {

    static {
        Field singleoneInstanceField = null;
        try {
            singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        singleoneInstanceField.setAccessible(true);

        try {
            unsafe = (Unsafe) singleoneInstanceField.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Unsafe unsafe;


    public static Unsafe getUnsafe() {
        return unsafe;
    }

    /**
     * 获取字段的偏移值
     * @param clazz
     * @param fieldName
     * @return
     */
    public static long calcFieldOffset(Class clazz, String fieldName) {
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }

        return unsafe.objectFieldOffset(field);
    }

}
