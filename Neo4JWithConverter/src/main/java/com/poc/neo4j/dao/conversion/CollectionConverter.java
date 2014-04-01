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
		Object[] complexTypeArray = null;
		boolean isSimpleType = false;
		boolean isDateType = false;
		boolean isEnumType = false;
		boolean isBaseEntityType = false;
		boolean isErrorType = false;
		Class<?> expectedType = null;
		int i = 0;
		boolean nullValuePresentInArray = false;
		CustomPropertyConverter simplePropertyConverter = null;
		
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();) {
			Object value = (Object) iterator.next();
			Object marshalledValue = null;
			if (ReflectionUtil.isSimpleType(value)) {
				if (complexTypeArray == null) {
					isSimpleType = true;
					expectedType = value.getClass();
					complexTypeArray = (Object[]) Array.newInstance(expectedType, collection.size());
				}
				marshalledValue = value;
			} else {
				Class<?> valueClass = value.getClass();
				if (valueClass.isEnum()){
					if (complexTypeArray == null) {
						isEnumType = true;
						expectedType = valueClass;
						complexTypeArray = (String[]) Array.newInstance(String.class, collection.size());
						simplePropertyConverter =
								PropertyConverterFactory.getSimplePropertyConverter(EnumConverter.class.getName());
					}
					marshalledValue = simplePropertyConverter.getMarshalledVlue(value);
				} else if (ReflectionUtil.isDateType(value)){
					if (complexTypeArray == null) {
						isDateType = true;
						expectedType = value.getClass();
						complexTypeArray = (String[]) Array.newInstance(String.class, collection.size());
						simplePropertyConverter =
								PropertyConverterFactory.getSimplePropertyConverter(DateConverter.class.getName());
					}
					marshalledValue = simplePropertyConverter.getMarshalledVlue(value);
				} else if (value instanceof BaseEntity){
					if (complexTypeArray == null) {
						isBaseEntityType = true;
						expectedType = value.getClass();
						complexTypeArray = (Object[]) Array.newInstance(valueClass, collection.size());
					}
					marshalledValue = value;
				} else {
					LOGGER.error(Constants.ERROR_OBJECT_TYPE);
					System.err.println(Constants.ERROR_OBJECT_TYPE);
					isErrorType = true;
					nullValuePresentInArray = true;
				}
			}
			if (!isErrorType) {
				try{
					complexTypeArray[i++] = marshalledValue;
				} catch(ArrayStoreException e) {
					String error = Constants.ERROR_TYPE + ".\nExpected type is " +  
							expectedType
							+ " But the object " + value + "[" + i + "] is of different type ";
					LOGGER.error(error, e);
					System.err.println(error);
					nullValuePresentInArray = true;
				}
			}
		}
		
		if (complexTypeArray != null && !nullValuePresentInArray) {
			if (isSimpleType) {
				PropertyConverterFactory.getConverter(SimpleTypeConverter.class.getName()).
					marshall(source, destination, fieldName, complexTypeArray);
			} else if (isDateType) {
				PropertyConverterFactory.getConverter(DateConverter.class.getName()).
				marshall(source, destination, fieldName, complexTypeArray);
			} else if (isEnumType){
				PropertyConverterFactory.getConverter(EnumConverter.class.getName()).
					marshall(source, destination, fieldName, complexTypeArray);
			} else if (isBaseEntityType){
				PropertyConverter converter = PropertyConverterFactory.getConverter(ChildNodeConverter.class.getName());
				for ( i = 0; i < complexTypeArray.length; i++) {
					converter.marshall(source, destination, fieldName, complexTypeArray[i]);
				}
			} else {
				LOGGER.error(Constants.ERROR_TYPE);
				System.err.println(Constants.ERROR_TYPE);
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Object unmarshall(Node source, T destination, String propertyName,
			T unmarshalledChild) throws ConverterException {
		
		Class<?> collectionType = ReflectionUtil.getType(destination.getClass(), propertyName);
		Class<?> actualObjectType = ReflectionUtil.getGenericType(destination.getClass(), propertyName);
		Collection<Object> collection = (Collection<Object>) ReflectionUtil.getProperty(destination, propertyName);
		if (collection == null && unmarshalledChild != null){
			if (collectionType.isAssignableFrom(List.class)){
				collection = new ArrayList<Object>();
			} else if (collectionType.isAssignableFrom(Set.class)) {
				collection = new HashSet<Object>();
			}
			collection.add(unmarshalledChild);
		} else if (unmarshalledChild != null){
			collection.add(unmarshalledChild);
		} else {
			Object propertyValue = source.getProperty(propertyName);
			collection = Arrays.asList((Object[])propertyValue);
			if (actualObjectType.isEnum() || ReflectionUtil.isDateType(actualObjectType)){
				Collection<Object> tempCollection = new ArrayList<>(collection.size());
				CustomPropertyConverter simplePropertyConverter = actualObjectType.isEnum() ? 
						PropertyConverterFactory.getSimplePropertyConverter(EnumConverter.class.getName()) :
							PropertyConverterFactory.getSimplePropertyConverter(DateConverter.class.getName()); 
				for (Iterator<Object> iterator = collection.iterator(); iterator.hasNext();) {
					tempCollection.add(simplePropertyConverter.getUnMarshalledVlue(actualObjectType,iterator.next()));
				}
				if (!tempCollection.isEmpty()) {
					collection = tempCollection;
				}
			}
			if (collectionType.isAssignableFrom(Set.class)) {
				collection = new HashSet<Object>(collection);
			}
		}
		return collection;
	}

}
