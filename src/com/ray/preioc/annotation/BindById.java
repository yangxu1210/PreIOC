package com.ray.preioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 根据id绑定控件的注解
 * @author Ray
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface BindById {

	int value();
}
