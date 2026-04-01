package com.zyd.springbootserviceseedproject.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zyd.springbootserviceseedproject.common.config.serializer.SensitiveJsonSerializer;
import com.zyd.springbootserviceseedproject.common.enums.DesensitizedType;

/**
 * 数据脱敏注解
 *
 * @author zyd
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveJsonSerializer.class)
public @interface Sensitive
{
    DesensitizedType desensitizedType();
}
