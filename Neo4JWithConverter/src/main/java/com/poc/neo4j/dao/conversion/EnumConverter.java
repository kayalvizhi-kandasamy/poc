package com.poc.neo4j.dao.conversion;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.Constants;
import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.ReflectionUtil;

/**
 * Conversion of a {@link Node} property value to {@link Enum}
 * and vice versa
 * 
 * @author kayalv
 *
 */
public class EnumConverter implements PropertyConverter{

	private static final Logger LOGGER = Logger.getLogger(EnumConverter.class);
	
	@Override
	public <T> void marshall(T source, Node destination, String fieldName, Object sourceValue)
			throws ConverterException {
		destination.setProperty(fieldName, ((Enum<?>)sourceValue).name());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void unmarshall(Node source, T destination, String propertyName, T child)
			throws ConverterException {
		if (child != null) {//TODO to check
			ReflectionUtil.setProperty(destination, propertyName, child);
		} else {
			Field field;
			try {
				field = destination.getClass().getDeclaredField(propertyName);
				assert field != null;
				Enum<?> value = Enum.valueOf((Class<Enum>) field.getType(), (String)source.getProperty(propertyName));
				ReflectionUtil.setProperty(destination, propertyName, value);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException e) {
				String error = Constants.ERROR_IN_INSTANTIATION + ": Error while setting the enum[" + 
						source.getProperty(propertyName) + "] for " + 
						destination.getClass().getName() + "[" + propertyName + "]";
				LOGGER.error(error);
				System.err.println(error);
			}
			
			
		}
	}
}
