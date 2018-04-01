package by.ititon.orm.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Cascade {

    /**
     * The cascade value.
     */
    CascadeType[] value();

    /**
     * Use it when CascadeType.REMOVE
     *
     * Remove column means that if
     * CascadeType.REMOVE sets
     * value in database to this column 0
     */
    String removeColumn() default "";
}
