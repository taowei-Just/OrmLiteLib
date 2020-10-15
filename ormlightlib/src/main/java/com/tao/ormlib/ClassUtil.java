package com.tao.ormlib;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassUtil {

    public static Field[] getDeclaredFieldsJustReflect(Class<? extends Object> clazz) {
        List list = new ArrayList();
        Class tClazz = clazz;
        while (!tClazz.equals(Object.class)) {
            Field[] declaredFields = tClazz.getDeclaredFields();
            list.addAll(Arrays.asList(declaredFields));
            for (int j = 0; j < list.size(); j++) {
                Field field = (Field) list.get(j);
                field.setAccessible(true);
            }
            tClazz = tClazz.getSuperclass();
        }
        return (Field[]) list.toArray(new Field[0]);
    }

    private static Map<String, Object> getFieldValueMap(Object obj, Class<?> clazz) {
        Map<String, Object> updatePerms = new HashMap<>();
        Field[] fields = getDeclaredFieldsJustReflect(clazz);
        for (Field f : fields) {
            if (!f.isAccessible())
                f.setAccessible(true);
            try {
                Object value = f.get(obj);
                updatePerms.put(f.getName(), value);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return updatePerms;
    }

    public static Object getValue(Object data, String fieldName) {
        Field[] justReflect = getDeclaredFieldsJustReflect(data.getClass());
        for (Field field : justReflect) {
            String name = field.getName();
            if (name.equals(fieldName)) {
                if (!field.isAccessible())
                    field.setAccessible(true);
                try {
                    return field.get(data);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }

        return null;
    }
}
 