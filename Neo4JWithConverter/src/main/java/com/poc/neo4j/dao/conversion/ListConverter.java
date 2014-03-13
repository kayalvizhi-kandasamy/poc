package com.poc.neo4j.dao.conversion;

import static com.poc.neo4j.dao.Constants.LIST_KEY;
import static com.poc.neo4j.dao.Constants.SEPARATOR_DOT;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.GraphDbUtil;
import com.poc.neo4j.dao.util.ReflectionUtil;
import com.poc.neo4j.model.BaseEntity;

public class ListConverter implements PropertyConverter {

	@Override
	public <T> void marshall(T source, Node destination, String fieldName, Object sourceValue)
			throws ConverterException {
		
		Method setPropertyMethod = null;
		List<?> collection = (List<?>) sourceValue;
		for (int i = 0; i < collection.size(); i++) {
			Object value =  collection.get(i);
			if (ReflectionUtil.isSimpleType(value)) {
				if (setPropertyMethod == null) {
					setPropertyMethod = ReflectionUtil.getMethod(destination.getClass(), 
		    			"setProperty", String.class, Object.class);
				}
				String listIndexedFieldName = LIST_KEY + SEPARATOR_DOT + i + SEPARATOR_DOT + fieldName;
				ReflectionUtil.invoke(setPropertyMethod, destination, listIndexedFieldName, value);
			} else {
				GraphDbUtil.convertAndPersistNode((BaseEntity) value, destination, fieldName);
			}
		}
	}

	@Override
	public <T> void unmarshall(Node source, T destination, String propertyName)
			throws ConverterException {
		int listIndex = Integer.parseInt(propertyName.substring(LIST_KEY.length() + 1, 
				propertyName.indexOf(SEPARATOR_DOT, LIST_KEY.length() +1)));
		
		String fieldName = propertyName.substring(propertyName.lastIndexOf(SEPARATOR_DOT) + 1);
		
		List<Object> list = (List<Object>) ReflectionUtil.getProperty(destination, fieldName);
		if (list == null) {
			list = new ArrayList<Object>();
			ReflectionUtil.setProperty(destination, fieldName, list);
		} 
		if (list.size() < listIndex + 1) {
			Object[] objects = list.toArray();
			objects = Arrays.copyOf(objects, listIndex + 1);
			list = Arrays.asList(objects);
			ReflectionUtil.setProperty(destination, fieldName, list);
		}
		list.set(listIndex, source.getProperty(propertyName));
	}

}