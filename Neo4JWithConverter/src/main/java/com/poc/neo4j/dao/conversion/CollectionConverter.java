package com.poc.neo4j.dao.conversion;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.Constants;
import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.GraphDbUtil;
import com.poc.neo4j.dao.util.ReflectionUtil;
import com.poc.neo4j.model.BaseEntity;

public class CollectionConverter implements PropertyConverter {

	private static final Logger LOGGER = Logger.getLogger(CollectionConverter.class);
	
	@Override
	public <T> void marshall(T source, Node destination, String fieldName,
			Object sourceValue) throws ConverterException {
		
		Collection<?> collection = (Collection<?>) sourceValue;
		Object[] simpleTypeArray = null;
		Object[] complexTypeArray = null;
		int i = 0;
		boolean nullValuePresentInArray = false;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();) {
			Object value = (Object) iterator.next();
			if (ReflectionUtil.isSimpleType(value)) {
				if (simpleTypeArray == null) {
					simpleTypeArray = (Object[]) Array.newInstance(value.getClass(), collection.size());
				}
				try{
					simpleTypeArray[i++] = value;
				} catch(ArrayStoreException e) {
					String error = Constants.ERROR_TYPE + ".\nExpected type is " +  
							simpleTypeArray.getClass().getComponentType()
							+ " But the object " + value + "[" + i + "] is of different type ";
					LOGGER.error(error);
					System.err.println(error);
					nullValuePresentInArray = true;
				}
			} else {
				if (complexTypeArray == null) {
					complexTypeArray = (Object[]) Array.newInstance(value.getClass(), collection.size());
				}
				if (value instanceof BaseEntity){
					try{
						complexTypeArray[i++] = value;
					} catch(ArrayStoreException e) {
						String error = Constants.ERROR_TYPE + ".\nExpected type is " +  
								complexTypeArray.getClass().getComponentType()
								+ " But the object " + value + "[" + i + "] is of different type ";
						LOGGER.error(error);
						System.err.println(error);
						nullValuePresentInArray = true;
					}
				} else {
					LOGGER.error(Constants.ERROR_OBJECT_TYPE);
					System.err.println(Constants.ERROR_OBJECT_TYPE);
				}
			}
		}
		
		if (simpleTypeArray != null && !nullValuePresentInArray) {
			destination.setProperty(fieldName, simpleTypeArray);
		} else if (complexTypeArray != null && !nullValuePresentInArray) {
			for ( i = 0; i < complexTypeArray.length; i++) {
				GraphDbUtil.convertAndPersistNode((BaseEntity) complexTypeArray[i], destination, fieldName);
			}
		} else {
			LOGGER.error(Constants.ERROR_TYPE);
			System.err.println(Constants.ERROR_TYPE);
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void unmarshall(Node source, T destination, String propertyName,
			T unmarshalledChild) throws ConverterException {
		
		Class<?> collectionType = ReflectionUtil.getSetterMethodType(destination.getClass(), propertyName);
		Collection<Object> collection = (Collection<Object>) ReflectionUtil.getProperty(destination, propertyName);
		if (collection == null && unmarshalledChild != null){
			if (collectionType.isAssignableFrom(List.class)){
				collection = new ArrayList<Object>();
			} else if (collectionType.isAssignableFrom(Set.class)) {
				collection = new HashSet<Object>();
			}
			collection.add(unmarshalledChild);
			ReflectionUtil.setProperty(destination, propertyName, collection);
		} else if (unmarshalledChild != null){
			collection.add(unmarshalledChild);
		} else {
			collection = Arrays.asList((Object[]) source.getProperty(propertyName));
			if (collectionType.isAssignableFrom(Set.class)) {
				collection = new HashSet<Object>(collection);
			}
			ReflectionUtil.setProperty(destination, propertyName, collection);
		} 
	}

}
