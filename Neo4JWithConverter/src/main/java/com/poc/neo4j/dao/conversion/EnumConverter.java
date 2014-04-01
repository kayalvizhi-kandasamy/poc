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
public class EnumConverter implements CustomPropertyConverter {

	
	@Override
	public <T> void marshall(T source, Node destination, String fieldName, Object sourceValue)
			throws ConverterException {
		
		if (sourceValue.getClass().isArray()) {
			destination.setProperty(fieldName,sourceValue);
		} else {
			destination.setProperty(fieldName, getMarshalledVlue(sourceValue));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Object unmarshall(Node source, T destination, String propertyName, T child)
			throws ConverterException {
		Enum<?> value = null;
		if (child != null) {//TODO to be checked 
		} else {
			value = (Enum<?>) getUnMarshalledVlue(
						(Class<Enum>) ReflectionUtil.getType(destination.getClass(), propertyName),  
						(String) source.getProperty(propertyName));

		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getUnMarshalledVlue(Class<?> type, Object stringValue)
			throws ConverterException {
		
		return Enum.valueOf((Class<Enum>)type, (String) stringValue);
	}

	@Override
	public Object getMarshalledVlue(Object sourceValue)
			throws ConverterException {
		
		return ((Enum<?>)sourceValue).name();
	}
}
