package com.example.group11practice.utils;


import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.util.CollectionUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class OrikaUtil {

    private static final MapperFactory MAPPER_FACTORY = new DefaultMapperFactory
            .Builder()
            .useAutoMapping(true)
            .mapNulls(true)
            .build();

    /**
     * 缓存 mapperFacade 实例
     */
    private static final Map<String, MapperFacade> CACHE_MAPPER = new ConcurrentHashMap<>();

    public static Map<String, MapperFacade> getCacheMapper() {
        return CACHE_MAPPER;
    }

    /**
     * 工具类私有方法，结合字段映射关系的缓存表，获取指定映射实体的 mapperFacade 实例，
     *
     * @param src      源实体
     * @param dest     目标实体
     * @param fieldMap 字段映射表
     * @param <S>      源类
     * @param <D>      目标类
     * @return mapperFacade 实例
     */
    private static synchronized <S, D> MapperFacade classMap(Class<S> src, Class<D> dest, Map<String, String> fieldMap) {
        String key = src.getCanonicalName() + ":" + dest.getCanonicalName();
        if (CACHE_MAPPER.containsKey(key)) {
            return CACHE_MAPPER.get(key);
        }

        // 缓存字段映射表
        return register(src, dest, fieldMap);
    }

    /**
     * 注册实体映射，字段映射表可选
     *
     * @param src      源实体
     * @param dest     目标实体
     * @param fieldMap 字段映射表
     * @param <S>      源类
     * @param <D>      目标类
     */
    public static synchronized <S, D> MapperFacade register(Class<S> src, Class<D> dest, Map<String, String> fieldMap) {
        if (CollectionUtils.isEmpty(fieldMap)) {
            MAPPER_FACTORY.classMap(src, dest).byDefault().register();
        } else {
            ClassMapBuilder<S, D> classMapBuilder = MAPPER_FACTORY.classMap(src, dest);
            fieldMap.forEach(classMapBuilder::field);
            classMapBuilder.byDefault().register();
        }
        String key = src.getCanonicalName() + ":" + dest.getCanonicalName();
        MapperFacade mapperFacade = MAPPER_FACTORY.getMapperFacade();
        CACHE_MAPPER.put(key, mapperFacade);
        return mapperFacade;
    }

    /*************************** 工具方法部分 ***************************/

    /**
     * 字段名相同的实体映射
     *
     * @param src      源实体
     * @param dest     目标实体
     * @param <S>      源类
     * @param <D>      目标类
     */
    public static <S, D> void map(S src, D dest) {
        if (src == null) {
            return;
        }
        map(src, dest, null);
    }

    public static <S, D> D map(S src, Class<D> destClazz) {
        if (src == null) {
            return null;
        }
        return map(src, destClazz, null);
    }

    public static <S, D> List<D> mapAsList(List<S> src, Class<D> destClazz) {
        if (src == null) {
            return null;
        }
        return mapAsList(src, destClazz, null);
    }

    /**
     * 携带字段映射关系的实体映射
     *
     * @param src      源实体
     * @param dest     目标实体
     * @param fieldMap 字段映射表
     * @param <S>      源类
     * @param <D>      目标类
     */
    public static <S, D> void map(S src, D dest, Map<String, String> fieldMap) {
        if (src == null) {
            return;
        }
        classMap(src.getClass(), dest.getClass(), fieldMap).map(src, dest);
    }

    public static <S, D> D map(S src, Class<D> destClazz, Map<String, String> fieldMap) {
        if (src == null) {
            return null;
        }
        return classMap(src.getClass(), destClazz, fieldMap).map(src, destClazz);
    }

    public static <S, D> List<D> mapAsList(List<S> src, Class<D> destClazz, Map<String, String> fieldMap) {
        if (src == null) {
            return null;
        }
        return classMap(src.get(0).getClass(), destClazz, fieldMap).mapAsList(src, destClazz);
    }

    public static <M, F extends M> M mergeNotNullProperties(M target, F from) throws Exception {
        if(from == null) {
            return target;
        }
        BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());

        for (PropertyDescriptor desc : beanInfo.getPropertyDescriptors()) {
            if (desc.getWriteMethod() == null) {
                continue;
            }

            /*
             * 读取from的非null字段，复制到target
             */
            Object fromVal = desc.getReadMethod().invoke(from);
            if (fromVal != null) {
                desc.getWriteMethod().invoke(target, fromVal);
            }

        }

        return target;
    }
}

