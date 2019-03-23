package org.ajoberstar.grgit.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.Callable;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Repeatable(WithOperations.class)
@GroovyASTTransformationClass("org.ajoberstar.grgit.internal.WithOperationASTTransformation")
public @interface WithOperation {
  String name();

  Class<? extends Callable> implementation();

  boolean isStatic() default false;
}
