package com.poc.neo4j.dao.conversion;

import static com.poc.neo4j.dao.Constants.SEPARATOR_DOT;
import static com.poc.neo4j.dao.Constants.SET_KEY;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.GraphDbUtil;
import com.poc.neo4j.dao.util.ReflectionUtil;
import com.poc.neo4j.model.BaseEntity;

public class SetConverter implements PropertyConverter {

	@Override
	public <T> void marshall(T source, Node destination, String fieldName, Object sourceValue)
			throws ConverterException {
		
		Method setPropertyMethod = null;
		Set<?> collection = (Set<?>) sourceValue;
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();) {
			Object value = (Object) iterator.next();
			if (ReflectionUtil.isSimpleType(value)) {
				if (setPropertyMethod == null) {
					setPropertyMethod = ReflectionUtil.getMethod(destination.getClass(), 
		    			"setProperty", String.class, Object.class);
				}
				String setFieldName = SET_KEY + SEPARATOR_DOT + i++ + SEPARATOR_DOT + fieldName;
				ReflectionUtil.invoke(setPropertyMethod, destination, setFieldName, value);
			} else {
				GraphDbUtil.convertAndPersistNode((BaseEntity) value, destination, fieldName);
			}
		}
	}

	@Override
	public <T> void unmarshall(Node source, T destination, String propertyName)
			throws ConverterException {
		
		String fieldName = propertyName.substring(propertyName.lastIndexOf(SEPARATOR_DOT) + 1);
		Set<Object> set = (Set<Object>) ReflectionUtil.getProperty(destination, fieldName);
		if (set == null) {
			set = new HashSet<Object>();
			ReflectionUtil.setProperty(destination, fieldName, set);
		} 
		set.add(source.getProperty(propertyName));
	}

}