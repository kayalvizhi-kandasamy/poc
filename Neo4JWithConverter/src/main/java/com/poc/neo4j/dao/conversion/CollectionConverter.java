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
		String[] enumTypeArray = null;
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
					LOGGER.error(error, e);
					System.err.println(error);
					nullValuePresentInArray = true;
				}
			} else {
				Class<?> valueClass = value.getClass();
				
				if (valueClass.isEnum()){
					if (enumTypeArray == null) {
						enumTypeArray = (String[]) Array.newInstance(String.class, collection.size());
					}
					try{
						enumTypeArray[i++] = ReflectionUtil.getEnumKey((Enum<?>)value);
					} catch(ArrayStoreException e) {
						String error = Constants.ERROR_TYPE + ".\nExpected type is Enum"
								+ " But the object " + value + "[" + i + "] is of different type ";
						LOGGER.error(error, e);
						System.err.println(error);
						nullValuePresentInArray = true;
					}
				} else if (value instanceof BaseEntity){
					if (complexTypeArray == null) {
						complexTypeArray = (Object[]) Array.newInstance(valueClass, collection.size());
					}
					try{
						complexTypeArray[i++] = value;
					} catch(ArrayStoreException e) {
						String error = Constants.ERROR_TYPE + ".\nExpected type is " +  
								complexTypeArray.getClass().getComponentType()
								+ " But the object " + value + "[" + i + "] is of different type ";
						LOGGER.error(error, e);
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
			PropertyConverterFactory.getConverter(SimpleTypeConverter.class.getName()).
				marshall(source, destination, fieldName, simpleTypeArray);
		} else if (complexTypeArray != null && !nullValuePresentInArray) {
			PropertyConverter converter = PropertyConverterFactory.getConverter(ChildNodeConverter.class.getName());
			for ( i = 0; i < complexTypeArray.length; i++) {
				converter.marshall(source, destination, fieldName, complexTypeArray[i]);
			}
		} else if (enumTypeArray != null && !nullValuePresentInArray) {
			PropertyConverterFactory.getConverter(EnumConverter.class.getName()).
					marshall(source, destination, fieldName, enumTypeArray);
		} else {
			LOGGER.error(Constants.ERROR_TYPE);
			System.err.println(Constants.ERROR_TYPE);
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void unmarshall(Node source, T destination, String propertyName,
			T unmarshalledChild) throws ConverterException {
		
		Class<?> collectionType = ReflectionUtil.getType(destination.getClass(), propertyName);
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
			Object propertyValue = source.getProperty(propertyName);
			collection = Arrays.asList((Object[]) propertyValue);
			Collection<Object> tempCollection = null;
			if( collection != null && collection.size() > 0) {
				for (Iterator<Object> iterator = collection.iterator(); iterator
						.hasNext();) {
					Object object = (Object) iterator.next();
					if (object != null && object instanceof String && ((String)object).startsWith(Constants.ENUM_KEY)) {
						Enum<?> value = ReflectionUtil.getEnumValue((String) object);
						if (tempCollection == null){
							tempCollection = new ArrayList<>(collection.size());
						}
						tempCollection.add(value);
					} else {
						break;
					}
				}
				if (tempCollection != null) {
					collection = tempCollection;
				}
			}
			
			if (collectionType.isAssignableFrom(Set.class)) {
				collection = new HashSet<Object>(collection);
			}
			ReflectionUtil.setProperty(destination, propertyName, collection);
		} 
	}

}
