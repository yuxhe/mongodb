package com.luyouchina.comm.annotaion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReqHandleMethod {
	String value() default "";

	String sessionKey() default "";

	/**
	 * 账户类型 1:企业 ; 2:教师 ; 3:组织机构用户 ; 4:特殊用户 ; 5:普通用户
	 * 
	 * @return
	 */
	String[] role() default {};
}
