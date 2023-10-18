package com.hcs.android.annotation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface CommandBody {

    /**
     * Whether body content is required.
     *
     * <p>Default is {@code true}, leading to an exception thrown in case there is no body content.
     */
    boolean required() default true;
}