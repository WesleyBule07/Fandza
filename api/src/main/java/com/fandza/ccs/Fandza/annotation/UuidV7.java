package com.fandza.ccs.Fandza.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.IdGeneratorType;

/**
 * Generates identifier values as UUID v7 (time-ordered) on insert.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IdGeneratorType(UuidV7Generator.class)
public @interface UuidV7 {

    Class<? extends UuidV7Generator> algorithm() default UuidV7Generator.class;
}
