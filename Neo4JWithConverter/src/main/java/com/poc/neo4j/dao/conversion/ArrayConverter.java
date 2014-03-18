package com.poc.neo4j.dao.conversion;

import static com.poc.neo4j.dao.Constants.ARRAY_KEY;
import static com.poc.neo4j.dao.Constants.SEPARATOR_DOT;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.GraphDbUtil;
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

	@Override
	public <T> void marshall(T source, Node destination, String fieldName, Object sourceValue)
			throws ConverterException {
		
		Method setPropertyMethod = null;
		Object[] objects = (Object[]) sourceValue;
		for (int i = 0; i < objects.length; i++) {
			Object value =  objects[i];
			if (ReflectionUtil.isSimpleType(value)) {
				if (setPropertyMethod == null) {
					setPropertyMethod = ReflectionUtil.getMethod(destination.getClass(), 
		    			"setProperty", String.class, Object.class);
				}
				String arrayIndexedPropertyName = ARRAY_KEY + SEPARATOR_DOT + i + SEPARATOR_DOT + fieldName;
				ReflectionUtil.invoke(setPropertyMethod, destination, arrayIndexedPropertyName, value);
			} else {
				GraphDbUtil.convertAndPersistNode((BaseEntity) value, destination, fieldName);
			}
		}
	}

	@Override
	public <T> void unmarshall(Node source, T destination, String propertyName, T child)
			throws ConverterException {
		
		int index = -1;
		String fieldName = null;
		Object object = null;
		if (child == null) {
			index = Integer.parseInt(propertyName.substring(ARRAY_KEY.length() + 1, 
					propertyName.indexOf(SEPARATOR_DOT, ARRAY_KEY.length() +1)));
			fieldName = propertyName.substring(propertyName.lastIndexOf(SEPARATOR_DOT) + 1);
			object =  source.getProperty(propertyName);
		} else {
			fieldName = propertyName;
			object = child;
		}
		Object objects =  ReflectionUtil.getProperty(destination, fieldName);
		if (objects == null) {
			objects = Array.newInstance(ReflectionUtil.getType(destination.getClass(), fieldName).getComponentType(), 
					index +1);
			ReflectionUtil.setProperty(destination, fieldName, objects);
		} 
		if (child != null) {
			index = ((Object[])objects).length;
		}
		if (((Object[])objects).length < index  + 1) {
			objects = Arrays.copyOf((Object[]) objects, index + 1);
			ReflectionUtil.setProperty(destination, fieldName, objects);
		}
		((Object[])objects)[index] = object;
	}

}
