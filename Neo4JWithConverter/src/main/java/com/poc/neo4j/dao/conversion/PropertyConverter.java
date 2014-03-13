package com.poc.neo4j.dao.conversion;

import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.exception.ConverterException;

public interface PropertyConverter {

	/**
	 * 
	 * @param source
	 * @param destination
	 * @param fieldName
	 * @param sourceValue
	 * @throws ConverterException
	 */
	
	<T> void marshall(T source, Node destination, String fieldName, Object sourceValue) throws ConverterException;
	
	/**
	 * 
	 * @param source
	 * @param destination
	 * @param propertyName
	 * @throws ConverterException
	 */
	<T> void unmarshall(Node source, T destination, String propertyName) throws ConverterException;
}
