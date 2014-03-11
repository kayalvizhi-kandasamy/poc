package com.poc.neo4j.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to ignore a field while marshaling
 * 
 * @author kayalv
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface IgnoreField {

}
