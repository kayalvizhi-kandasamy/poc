package com.poc.neo4j.dao.conversion;

import static com.poc.neo4j.dao.Constants.ARRAY_KEY;
import static com.poc.neo4j.dao.Constants.SEPARATOR_DOT;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.GraphDbUtil;
import com.poc.neo4j.dao.util.ReflectionUtil;
import com.poc.neo4j.model.BaseEntity;

public class ArrayConverter implements PropertyConverter{

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
	public <T> void unmarshall(Node source, T destination, String propertyName)
			throws ConverterException {
		
//		TODO
	}

}
