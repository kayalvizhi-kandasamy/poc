package com.poc.neo4j.dao.conversion;

import java.lang.reflect.Method;

import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.ReflectionUtil;

/**
 * Conversion of a {@link Node} property value to any entity field value
 * if the entity's field is of simple type like
 * {@link Integer},  {@link Long},  {@link String} etc...
 * 
 * @author kayalv
 *
 */
public class SimpleTypeConverter implements PropertyConverter{

	@Override
	public <T> void marshall(T source, Node destination, String fieldName, Object sourceValue)
			throws ConverterException {
		
		Method setPropertyMethod = ReflectionUtil.getMethod(destination.getClass(), 
    			"setProperty", String.class, Object.class);
		ReflectionUtil.invoke(setPropertyMethod, destination, fieldName, sourceValue);
	}

	@Override
	public <T> void unmarshall(Node source, T destination, String propertyName, T child)
			throws ConverterException {
		if (child != null) {
			ReflectionUtil.setProperty(destination, propertyName, child);
		} else {
			ReflectionUtil.setProperty(destination, propertyName, source.getProperty(propertyName));
		}
	}
}
