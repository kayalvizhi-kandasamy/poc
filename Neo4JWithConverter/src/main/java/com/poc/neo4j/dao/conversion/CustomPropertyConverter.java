package com.poc.neo4j.dao.conversion;

import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.exception.ConverterException;

/**
 * 
 * Special type of {@link PropertyConverter} where any object's field value is 
 * processed during marshalling.
 * And the same logic is applied on {@link Node} property value to get the field value unmarshalled.
 *  
 * @author kayalv
 * @see {@link PropertyConverter}
 *
 */
public interface CustomPropertyConverter extends PropertyConverter {

	
	/**
	 * Gets the marshalled value which is to be stored as node property value
	 * 
	 * @param sourceValue
	 * @return
	 * @throws ConverterException
	 */
	Object getMarshalledVlue(Object sourceValue) throws ConverterException;
	
	/**
	 * Gets the unmarshalled value from node property value
	 * 
	 * @param type
	 * @param value
	 * @return
	 * @throws ConverterException
	 */
	Object getUnMarshalledVlue(Class<?> type, Object value) throws ConverterException;
	
}
