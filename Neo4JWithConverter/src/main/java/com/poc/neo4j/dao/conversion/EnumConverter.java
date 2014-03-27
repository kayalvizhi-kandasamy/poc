package com.poc.neo4j.dao.conversion;

import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.ReflectionUtil;

/**
 * Conversion of a {@link Node} property value to {@link Enum}
 * and vice versa
 * 
 * @author kayalv
 *
 */
public class EnumConverter implements PropertyConverter{

	
	@Override
	public <T> void marshall(T source, Node destination, String fieldName, Object sourceValue)
			throws ConverterException {
		
		if (sourceValue.getClass().isArray()) {
			destination.setProperty(fieldName,sourceValue);
		} else {
			destination.setProperty(fieldName, ReflectionUtil.getEnumKey((Enum<?>)sourceValue));
		}
	}

	@Override
	public <T> void unmarshall(Node source, T destination, String propertyName, T child)
			throws ConverterException {
		if (child != null) {//TODO to check
			ReflectionUtil.setProperty(destination, propertyName, child);
		} else {
			Enum<?> value = ReflectionUtil.getEnumValue((String) source.getProperty(propertyName));
			if (value != null) {
				ReflectionUtil.setProperty(destination, propertyName, value);
			}
		}
	}
}
