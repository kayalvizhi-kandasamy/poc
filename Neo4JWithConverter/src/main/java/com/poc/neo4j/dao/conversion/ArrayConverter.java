package com.poc.neo4j.dao.conversion;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.Constants;
import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.ReflectionUtil;
import com.poc.neo4j.model.BaseEntity;

/**
 * Conversion of a {@link Node} property value to any entity field value
 * if the entity's field is of array type
 *  
 * @author kayalv
 *
 */
public class ArrayConverter implements PropertyConverter {

	private static final Logger LOGGER = Logger.getLogger(ArrayConverter.class);
	
	@Override
	public <T> void marshall(T source, Node destination, String fieldName, Object sourceValue)
			throws ConverterException {
		
		Object[] objects = (Object[]) sourceValue;
		if (Object.class.getName().equals(sourceValue.getClass().getComponentType().getName())){
			LOGGER.error(Constants.ERROR_TYPE);
			System.err.println(Constants.ERROR_TYPE);
			return;
		}
		Object[] complexTypeArray = null;
		boolean isSimpleType = false;
		boolean isDateType = false;
		boolean isEnumType = false;
		boolean isBaseEntityType = false;
		boolean isErrorType = false;
		Class<?> expectedType = null;
		boolean nullValuePresentInArray = false;
		CustomPropertyConverter simplePropertyConverter = null;
		for (int i = 0; i < objects.length; i++) {
			Object value =  objects[i];
			if (value == null) {
				continue;
			}
			Object marshalledValue = null;
			Class<?> valueClass = value.getClass();
			
			if (ReflectionUtil.isSimpleType(value)) {
				if (complexTypeArray == null) {
					isSimpleType = true;
					expectedType = valueClass;
					complexTypeArray = (Object[]) Array.newInstance(expectedType, objects.length);
				}
				marshalledValue = value;
			} else {
				if (valueClass.isEnum()){
					if (complexTypeArray == null) {
						isEnumType = true;
						expectedType = valueClass;
						simplePropertyConverter =
								PropertyConverterFactory.getSimplePropertyConverter(EnumConverter.class.getName());
						complexTypeArray = (String[]) Array.newInstance(String.class, objects.length);
					}
					marshalledValue = (String) simplePropertyConverter.getMarshalledVlue(value);
				} else if (ReflectionUtil.isDateType(value)){
					if (complexTypeArray == null) {
						isDateType = true;
						expectedType = value.getClass();
						simplePropertyConverter =
								PropertyConverterFactory.getSimplePropertyConverter(DateConverter.class.getName());
						complexTypeArray = (String[]) Array.newInstance(String.class, objects.length);
					}
					marshalledValue = simplePropertyConverter.getMarshalledVlue(value);
				} else if (value instanceof BaseEntity){
					if (complexTypeArray == null) {
						isBaseEntityType = true;
						expectedType = value.getClass();
						complexTypeArray = (Object[]) Array.newInstance(valueClass, objects.length);
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
					complexTypeArray[i] = marshalledValue;
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
				for (int i = 0; i < complexTypeArray.length; i++) {
					converter.marshall(source, destination, fieldName, complexTypeArray[i]);
				}
			} else {
				LOGGER.error(Constants.ERROR_TYPE);
				System.err.println(Constants.ERROR_TYPE);
			}
		}
	}

	@Override
	public <T> Object unmarshall(Node source, T destination, String propertyName, T unmarshalledChild)
			throws ConverterException {
		
		Class<?> componentType = ReflectionUtil.getType(destination.getClass(), propertyName).getComponentType();
		Object objects = null;
		if (ReflectionUtil.isSimpleType(componentType)) {
			objects =  source.getProperty(propertyName);
		} else if (componentType.isEnum() || ReflectionUtil.isDateType(componentType)) {
			String[] stringObjects = (String[]) source.getProperty(propertyName);
			objects = (Object[]) Array.newInstance(componentType, stringObjects.length);
			CustomPropertyConverter simplePropertyConverter = componentType.isEnum() ? 
					PropertyConverterFactory.getSimplePropertyConverter(EnumConverter.class.getName()) :
						PropertyConverterFactory.getSimplePropertyConverter(DateConverter.class.getName()); 
			for (int i = 0; i < stringObjects.length; i++) {
				((Object[])objects)[i] = simplePropertyConverter.getUnMarshalledVlue(componentType, stringObjects[i]);
			}
		} else if (unmarshalledChild != null) {
			int index = -1;
			objects =  ReflectionUtil.getProperty(destination, propertyName);
			if (objects == null) {
				objects = Array.newInstance(componentType, 0);
			}
			index = ((Object[])objects).length;
			if (((Object[])objects).length < index  + 1) {
				objects = Arrays.copyOf((Object[]) objects, index + 1);
			}
			((Object[])objects)[index] = unmarshalledChild;
		}
		return objects;
	}

}
