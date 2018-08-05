package com.github.blog.annotation;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER,ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SessionAttr {
    String value();
}
