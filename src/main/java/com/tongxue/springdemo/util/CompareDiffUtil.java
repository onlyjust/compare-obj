package com.tongxue.springdemo.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tongxue.springdemo.annotation.CompareProperty;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author zhangtong
 * @Description:
 * @Date 2022/7/5 19:30
 */
@Slf4j
public class CompareDiffUtil {

    /**
     * 比较两个对象属性值是否相同,返回差异属性值
     *
     * @param newObj
     * @param oldObj
     * @param ignorePropertyNames
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> getDifferentProperty(@NonNull T newObj, T oldObj, String... ignorePropertyNames) {
        return getDifferentProperty(newObj, oldObj, false, ignorePropertyNames);
    }

    /**
     * 比较两个对象属性值是否相同,返回差异属性值
     *
     * @param newObj
     * @param oldObj
     * @param ignoreNullValue
     * @param ignorePropertyNames
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> getDifferentProperty(@NonNull T newObj, T oldObj, boolean ignoreNullValue, String... ignorePropertyNames) {
        Class<?> newObjClass = newObj.getClass();
        Field[] newOjbFields = newObjClass.getDeclaredFields();
        Map<String, Object> diffPropertyMap = Maps.newHashMap();
        Map<String, PropertyDescriptor> oldPdMap = getPropertyDescriptorMap(oldObj);
        for (Field newField : newOjbFields) {
            try {
                if (hasIgnoreProperty(newField.getName(), ignorePropertyNames)) {
                    continue;
                }
                CompareProperty annotation = newField.getAnnotation(CompareProperty.class);
                if (annotation == null) {
                    continue;
                }
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(newField.getName(), newObjClass);
                Object newObjPropertyValue = propertyDescriptor.getReadMethod().invoke(newObj);
                if (newObjPropertyValue == null && ignoreNullValue) {
                    continue;
                }
                if (newObjPropertyValue == null && annotation.ignoreNull()) {
                    continue;
                }
                if (!annotation.ignoreCompare()) {
                    PropertyDescriptor oldPd = oldPdMap.get(newField.getName());
                    if (Objects.nonNull(oldPd)) {
                        Object oldObjPropertyValue = oldPd.getReadMethod().invoke(oldObj);
                        if (Objects.equals(newObjPropertyValue, oldObjPropertyValue)) {
                            continue;
                        }
                    }
                }
                if (annotation.executeClass() != Object.class) {
                    newObjPropertyValue = convertPropertyValue(newObj, newField, newObjPropertyValue);
                }
                String name = annotation.name();
                if (StrUtil.isBlank(name)){
                    name = newField.getName();
                }
                diffPropertyMap.put(name, newObjPropertyValue);
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return diffPropertyMap;
    }


    public static <T> Map<String, PropertyDescriptor> getPropertyDescriptorMap(T obj) {
        if (obj == null){
            return Collections.emptyMap();
        }
        Class<?> objClass = obj.getClass();
        Field[] ojbFields = objClass.getDeclaredFields();
        Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<>();
        for (Field objField : ojbFields) {
            try {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(objField.getName(), objClass);
                propertyDescriptorMap.put(objField.getName(), propertyDescriptor);
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
        }
        return propertyDescriptorMap;
    }


    /**
     * 获取属性对应的值
     *
     * @param obj
     * @param propertyNames
     * @param <T>
     * @return
     */
    public static <T> Object[] getPropertyNameValue(T obj, String... propertyNames) {
        if (ObjectUtil.isEmpty(propertyNames)) {
            return null;
        }
        List<Object> paramValueList = Lists.newArrayList();
        for (String propertyName : propertyNames) {
            try {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, obj.getClass());
                paramValueList.add(propertyDescriptor.getReadMethod().invoke(obj));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return paramValueList.toArray();
    }

    /**
     * 转换属性值
     *
     * @param newObj
     * @param field
     * @param newObjPropertyValue
     * @param <T>
     * @return
     */
    private static <T> Object convertPropertyValue(T newObj, Field field, Object newObjPropertyValue) {
        CompareProperty annotation = field.getAnnotation(CompareProperty.class);
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
                if (ObjectUtil.isNotEmpty(methodParamType)) {
                    Object[] methodParamValue = getPropertyNameValue(newObj, annotation.methodParamField());
                    Method method = aClass.getMethod(methodName, methodParamType);
                    newObjPropertyValue = method.invoke(obj, methodParamValue);
                } else {
                    Method method = aClass.getMethod(methodName, field.getType());
                    newObjPropertyValue = method.invoke(obj, newObjPropertyValue);
                }
                log.debug("属性:{} 转换后=>{}:{}", field.getName(), annotation.name(), newObjPropertyValue);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return newObjPropertyValue;
    }

    /**
     * 是否忽略属性
     *
     * @param propertyName
     * @param ignoreProperties
     * @return
     */
    private static boolean hasIgnoreProperty(String propertyName, String... ignoreProperties) {
        if (ObjectUtil.isNotEmpty(ignoreProperties)) {
            for (String ignoreProperty : ignoreProperties) {
                if (Objects.equals(ignoreProperty, propertyName)) {
                    return true;
                }
            }
        }
        return false;
    }

}
