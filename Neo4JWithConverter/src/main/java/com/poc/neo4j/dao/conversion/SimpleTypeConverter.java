package com.poc.neo4j.dao.conversion;

import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.exception.ConverterException;

/**
 * Conversion of a {@link Node} property value to any entity field value
 * if the entity's field is of simple type like
 * {@link Integer},  {@link Long},  {@link String} etc...
 * 
 * @author kayalv
 *
 */
public class SimpleTypeConverter implements PropertyConverter {

	@Override
	public <T> void marshall(T source, Node destination, String fieldName, Object sourceValue)
			throws ConverterException {
		destination.setProperty(fieldName, sourceValue);
	}

	@Override
	public <T> Object unmarshall(Node source, T destination, String propertyName, T child)
			throws ConverterException {
		Object object = null;
		if (child != null) {
			object = child;
		} else {
			object = source.getProperty(propertyName);
		}
		return object;
	}

}
