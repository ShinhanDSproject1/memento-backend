package com.shinhanDS5gi.memento.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* 이 어노테이션이 붙은 컨트롤러는 WebConfig에 설정된  전역 API 경로('/api')가 적용되지 않음. - API가 아닌 컨트롤러에 사용 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoApiPrefix {
}
