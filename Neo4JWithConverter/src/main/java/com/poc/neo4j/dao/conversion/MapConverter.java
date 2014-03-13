package com.poc.neo4j.dao.conversion;

import static com.poc.neo4j.dao.Constants.MAP_KEY;
import static com.poc.neo4j.dao.Constants.SEPARATOR_DOT;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.Constants;
import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.ReflectionUtil;

public class MapConverter implements PropertyConverter{

	private static final Logger LOGGER = Logger.getLogger(MapConverter.class);
	@Override
	public <T> void marshall(T source, Node destination, String fieldName, Object sourceValue)
			throws ConverterException {
		for (Entry<?, ?> entry : (( Map<?,?>) sourceValue).entrySet()) {
			if (ReflectionUtil.isSimpleType(entry.getKey()) && ReflectionUtil.isSimpleType(entry.getValue())){
				destination.setProperty(Constants.MAP_KEY + SEPARATOR_DOT + fieldName + SEPARATOR_DOT + entry.getKey(), entry.getValue());
			} else {
				String error = Constants.ERROR_MAP_KEY_VALUE + ": " + 
						entry.getKey() + " or " + entry.getValue() + " is not of simple type";
				LOGGER.error(error);
				System.err.println(error);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void unmarshall(Node source, T destination, String propertyName)
			throws ConverterException {
		
		String fieldName = propertyName.substring(MAP_KEY.length() + 1, 
				propertyName.indexOf(SEPARATOR_DOT, MAP_KEY.length() +1));
		String mapKey = propertyName.substring(MAP_KEY.length() + 1 + fieldName.length() + 1);
		Map<Object, Object> map = (Map<Object, Object>) ReflectionUtil.getProperty(destination, fieldName);
		if (map == null) {
			map = new HashMap<Object, Object>();
			ReflectionUtil.setProperty(destination, fieldName, map);
		} 
		map.put(mapKey, source.getProperty(propertyName));
	}

}
