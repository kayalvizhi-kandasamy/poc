package com.poc.neo4j.dao.conversion;

import java.sql.Date;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.Constants;
import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.ReflectionUtil;

/**
 * Conversion of a {@link Node} property value to
 * {@link Date},  {@link java.util.Date} and vice versa.
 *  
 * @author kayalv
 *
 */
public class DateConverter implements CustomPropertyConverter {

	private static final Logger LOGGER = Logger.getLogger(DateConverter.class);
	
	@Override
	public <T> void marshall(T source, Node destination, String fieldName, Object sourceValue)
	throws ConverterException {
		
		if (sourceValue.getClass().isArray()) {
			destination.setProperty(fieldName,sourceValue);
		} else {
			String longValue = (String) getMarshalledVlue(sourceValue);
			if (longValue != null) {
				destination.setProperty(fieldName, longValue);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Object unmarshall(Node source, T destination, String propertyName, T child)
	throws ConverterException {
		
		Object dateObject = null;
		if (child != null) {//TODO to be checked
			dateObject = child;
		} else {
			dateObject = getUnMarshalledVlue(
					(Class<java.util.Date>) ReflectionUtil.getType(destination.getClass(), propertyName), 
					(String) source.getProperty(propertyName));
			
		}
		return dateObject;
	}
	
	public Object getMarshalledVlue(Object sourceValue)
			throws ConverterException {
		long longValue = -1;

		if (sourceValue instanceof Date) {
			longValue = ((Date)sourceValue).getTime();
		} else if (sourceValue instanceof java.util.Date) {
			longValue = ((java.util.Date)sourceValue).getTime();
		}
		return String.valueOf(longValue);
	}
	

	@Override
	public Object getUnMarshalledVlue(Class<?> type, Object stringValue)
			throws ConverterException {
		
		java.util.Date dateObject = null;
		long longValue = Long.parseLong((String)stringValue);
		if (type.isAssignableFrom(java.util.Date.class)){
			dateObject = new java.util.Date(longValue);
		} else if (type.isAssignableFrom(Date.class)) {
			dateObject = new Date(longValue);
		} else {
			LOGGER.error(Constants.ERROR_DATE_TYPE);
			System.err.println(Constants.ERROR_DATE_TYPE);
		}
		return dateObject;
	}
}
