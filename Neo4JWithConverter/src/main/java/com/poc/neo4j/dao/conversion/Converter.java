package com.poc.neo4j.dao.conversion;

import static com.poc.neo4j.dao.Constants.MAP_KEY;
import static com.poc.neo4j.dao.Constants.SEPARATOR_DOT;

import java.lang.reflect.Field;

import org.apache.commons.lang.ArrayUtils;
import org.neo4j.graphdb.Node;

import com.poc.neo4j.dao.Constants;
import com.poc.neo4j.dao.annotation.IgnoreField;
import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.ReflectionUtil;
public class Converter {

	private static Converter converter = null;
	private Converter(){
		
	}
	
	public static Converter getConverter() {
		if (converter == null) {
			converter = new Converter();
		}
		return converter;
	}
	
	public <T> void marshall(T source, Node destination) 
	throws ConverterException {
		
		if (destination == null || source == null) {
  	      return;
  	    }
    	Field[] sourceFields = (Field[]) ArrayUtils.addAll(source.getClass().getSuperclass().getDeclaredFields(), 
    										source.getClass().getDeclaredFields());
    	for (int i = 0; i < sourceFields.length; i++) {
    		String fieldName = sourceFields[i].getName();
    		if (sourceFields[i].getAnnotation(IgnoreField.class) != null){
    			continue;
    		}
    		Object sourceValue = ReflectionUtil.getProperty(source, fieldName);
			if (sourceValue == null) {
				continue;
			}
			PropertyConverter propertyConverter = PropertyConverterFactory.getMarshallingConverter(sourceValue);
			propertyConverter.marshall(source, destination, fieldName, sourceValue);
		}
	}
	
	public <T> T unmarshall(Node sourceNode, Class<T> destinationClass) 
	throws ConverterException {
    	
		if (sourceNode == null || destinationClass == null) {
	  	    return null;
	  	}
		T destinationObject = ReflectionUtil.newInstance(destinationClass);
		Iterable<String> keys = sourceNode.getPropertyKeys();
		for (String key : keys) {
			PropertyConverter propertyConverter = PropertyConverterFactory.getUnMarshallingConverter(destinationClass, key);
			Object object = propertyConverter.unmarshall(sourceNode, destinationObject, key, null);
			String fieldName = key;
			if (key.startsWith(Constants.MAP_KEY)){
				fieldName = key.substring(MAP_KEY.length() + 1, 
						key.indexOf(SEPARATOR_DOT, MAP_KEY.length() +1));
			}
			ReflectionUtil.setProperty(destinationObject, fieldName, object);
        }
		ReflectionUtil.setProperty(destinationObject, "id", sourceNode.getId());
		ChildNodeConverter converter = new ChildNodeConverter();
		converter.unmarshall(sourceNode, destinationObject, null, null);
        return destinationObject;
	}
	
}


