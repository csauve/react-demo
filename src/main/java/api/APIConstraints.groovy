package api

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

public interface Context {}

public interface ContextProvider {
  Map<Class<? extends Context>, Boolean> determineContexts()
}

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Applicable {
  Class[] groups()
}


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Immutable {
  Class[] groups() default []
  String message() default "immutableField"
}