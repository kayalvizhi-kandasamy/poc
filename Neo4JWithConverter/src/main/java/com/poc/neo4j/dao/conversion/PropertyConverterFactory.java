package com.poc.neo4j.dao.conversion;

import static com.poc.neo4j.dao.Constants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.poc.neo4j.dao.util.ReflectionUtil;


public class PropertyConverterFactory {

	private static Map<String, PropertyConverter> converters = new HashMap<String, PropertyConverter>();
	
	static{
		converters.put(SimpleTypeConverter.class.getName(), new SimpleTypeConverter());
		converters.put(ListConverter.class.getName(), new ListConverter());
		converters.put(MapConverter.class.getName(), new MapConverter());
		converters.put(ChildNodeConverter.class.getName(), new ChildNodeConverter());
		converters.put(ArrayConverter.class.getName(), new ArrayConverter());
		converters.put(SetConverter.class.getName(), new SetConverter());
	}
	
	public static PropertyConverter getMarshallingConverter(Object sourceValue) {
		
		PropertyConverter propertyConverter = null;
		if (ReflectionUtil.isSimpleType(sourceValue)){
			propertyConverter = converters.get(SimpleTypeConverter.class.getName());
    	} else if (sourceValue instanceof List) {
    		propertyConverter = converters.get(ListConverter.class.getName());
    	} else if (sourceValue instanceof Set) {
    		propertyConverter = converters.get(SetConverter.class.getName());
    	} else if (sourceValue instanceof Map<?,?> ) {
    		propertyConverter = converters.get(MapConverter.class.getName());
    	} else if (sourceValue instanceof Object[]) {
    		propertyConverter = converters.get(ArrayConverter.class.getName());
    	} else {
    		propertyConverter = converters.get(ChildNodeConverter.class.getName());
    	}
		return propertyConverter;
	}
	
	public static PropertyConverter getUnMarshallingConverter(String nodeKey) {
		
		PropertyConverter propertyConverter = null;
		if (nodeKey.startsWith(MAP_KEY)){
			propertyConverter = converters.get(MapConverter.class.getName());
		} else if (nodeKey.startsWith(LIST_KEY)){
			propertyConverter = converters.get(ListConverter.class.getName());
		} else if (nodeKey.startsWith(SET_KEY)){
			propertyConverter = converters.get(SetConverter.class.getName());
		} else if (nodeKey.startsWith(ARRAY_KEY)){
			propertyConverter = converters.get(ArrayConverter.class.getName());
		} else {
			propertyConverter =  converters.get(SimpleTypeConverter.class.getName());
		}
		return propertyConverter;
	}
}
