package com.poc.neo4j.dao.conversion;

import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.exception.ConverterException;

/**
 * This interface declares methods 
 * for converting {@link Node} property values to any object field values and vice versa
 *  
 * @author kayalv
 *
 */
public interface PropertyConverter {

	/**
	 * Sets value of any field of an object 'source' to the 'destination' node object
	 * 
	 * @param source - object to be converted
	 * @param destination - Node object to which the source object field value to be set
	 * @param fieldName - Source Field name which must be set in the node 'destination' object
	 * @param sourceValue - The value of the field, 'fieldName' 
	 * @throws ConverterException
	 */
	
	<T> void marshall(T source, Node destination, String fieldName, Object sourceValue) throws ConverterException;
	
	/**
	 * Retrieves the value of a property specified by 'propertyName' of the node 
	 * and sets in the destination object. 
	 * 
	 * @param source - The node object from which the property must be fetched
	 * @param destination - The object to which the value must be set
	 * @param propertyName - 
	 * @param unmarshalledChild - child object which must be set in the destination object
	 * @throws ConverterException
	 */
	<T> void unmarshall(Node source, T destination, String propertyName, T unmarshalledChild) throws ConverterException;
}
