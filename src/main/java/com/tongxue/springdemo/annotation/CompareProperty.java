package com.tongxue.springdemo.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CompareProperty {

    /**
     * 转换名称
     * @return
     */
    String name() default "";

    /**
     * 执行类
     * @return
     */
    Class<?> executeClass() default Object.class;

    /**
     * 是否静态方法
     * @return
     */
    boolean staticMethod() default true;

    /**
     * 执行方法
     * @return
     */
    String executeMethod() default "";

    /**
     * 执行方法类型（默认以属性类型为参数）
     * @return
     */
    Class<?>[] methodParamType() default {};

    /**
     * 执行方法参数属性（默认以当前属性值为参数）
     * @return
     */
    String[] methodParamField() default {};

    /**
     * 忽略值为null
     * @return
     */
    boolean ignoreNull() default false;

    /**
     * 忽略比较，直接定义有差异
     * @return
     */
    boolean ignoreCompare() default false;

    /**
     * 隐藏（目前未使用）
     * @return
     */
    boolean hidden() default false;

}
