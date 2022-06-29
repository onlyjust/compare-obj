package com.tongxue.springdemo.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tongxue.springdemo.annotation.CompareProperty;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class CompareObjUtil {

    /**
     * 比较两个对象属性值是否相同,返回差异属性值
     *
     * @param newObj
     * @param oldObj
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> getDifferentProperty(@NonNull T newObj, T oldObj) {
        return getDifferentProperty(newObj, oldObj, false);
    }

    /**
     * 比较两个对象属性值是否相同,返回差异属性值
     *
     * @param newObj 新对象
     * @param oldObj 旧对象
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> getDifferentProperty(@NonNull T newObj, T oldObj, boolean ignoreNullValue, String... ignorePropertyNames) {
        Class<?> newObjClass = newObj.getClass();
        Field[] newOjbFields = newObjClass.getDeclaredFields();
        Map<String, Object> diffPropertyMap = Maps.newHashMap();
        for (Field newField : newOjbFields) {
            if (hasIgnoreProperty(newField.getName(), ignorePropertyNames)) {
                continue;
            }
            CompareProperty annotation = newField.getAnnotation(CompareProperty.class);
            if (annotation == null) {
                continue;
            }
            String name = annotation.name();
            if (StrUtil.isBlank(name)) {
                continue;
            }
            try {
                Object newObjPropertyValue = getPropertyValue(newObj, newField.getName());
                if (oldObj != null && !annotation.ignoreCompare()) {
                    Object oldObjPropertyValue = getPropertyValue(oldObj, newField.getName());
                    if (Objects.equals(newObjPropertyValue, oldObjPropertyValue)) {
                        continue;
                    }
                }
                if (newObjPropertyValue == null && ignoreNullValue) {
                    continue;
                }
                if (newObjPropertyValue == null && annotation.ignoreNull()) {
                    continue;
                }
                Class<?> aClass = annotation.executeClass();
                if (aClass != Object.class) {
                    try {
                        Object obj = null;
                        boolean staticMethod = annotation.staticMethod();
                        if (!staticMethod) {
                            // spring容器中获取beam对象
                            obj = SpringUtil.getBean(aClass);
                        }
                        String methodName = annotation.executeMethod();
                        Class<?>[] methodParamType = annotation.methodParamType();
                        if (ObjectUtil.isNotEmpty(methodParamType)){
                            Object[] methodParamValue = getMethodParamValue(newObj, annotation.methodParamField());
                            Method method = aClass.getMethod(methodName, methodParamType);
                            newObjPropertyValue = method.invoke(obj, methodParamValue);
                        } else {
                            Method method = aClass.getMethod(methodName, newField.getType());
                            newObjPropertyValue = method.invoke(obj, newObjPropertyValue);
                        }
                        log.debug("属性:{} 转换后=>{}:{}", newField.getName(), name, newObjPropertyValue);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
                diffPropertyMap.put(name, newObjPropertyValue);
            } catch (NoSuchFieldException e) {
                log.warn("对象没有对应的属性：", e);
            }
        }
        return diffPropertyMap;
    }

    public static <T> Object getPropertyValue(T obj, String propertyName) throws NoSuchFieldException {
        Class<?> objClass = obj.getClass();
        Field declaredField = null;
        try {
            declaredField = objClass.getDeclaredField(propertyName);
        } catch (NoSuchFieldException e) {
            log.warn("java.lang.NoSuchFieldException: {} objClass:{}", propertyName, objClass);
            throw new NoSuchFieldException("获取对象属性失败");
        }
        if (declaredField != null) {
            declaredField.setAccessible(true);
            Object val = null;
            try {
                val = declaredField.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return val;
        }
        return null;
    }

    /**
     * 是否忽略属性
     *
     * @param propertyName
     * @param ignorePropertys
     * @return
     */
    private static boolean hasIgnoreProperty(String propertyName, String... ignorePropertys) {
        if (ObjectUtil.isNotEmpty(ignorePropertys)) {
            for (String ignoreProperty : ignorePropertys) {
                if (Objects.equals(ignoreProperty, propertyName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static <T> Object[] getMethodParamValue(T obj,String[] methodParamField){
        if (ObjectUtil.isEmpty(methodParamField)){
            return null;
        }
        List<Object> paramValueList = Lists.newArrayList();
        for (String field:methodParamField){
            try {
                paramValueList.add(getPropertyValue(obj, field));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return paramValueList.toArray();
    }

}
