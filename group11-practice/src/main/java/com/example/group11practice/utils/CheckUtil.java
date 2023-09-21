package com.example.group11practice.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class CheckUtil {

    /**
     * 判断对象是否是逻辑意义上的空值
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return "".equals(obj);
        }
        if (obj instanceof Object[]) {
            return ((Object[]) obj).length == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).size() == 0;
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).size() == 0;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }

        return false;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

}