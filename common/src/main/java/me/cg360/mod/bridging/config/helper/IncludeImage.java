package me.cg360.mod.bridging.config.helper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IncludeImage {

    String value();

    int width() default 854;
    int height() default 480;

}
