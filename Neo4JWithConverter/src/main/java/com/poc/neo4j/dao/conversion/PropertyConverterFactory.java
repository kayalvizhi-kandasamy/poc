package com.poc.neo4j.dao.conversion;

import static com.poc.neo4j.dao.Constants.MAP_KEY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.poc.neo4j.dao.util.ReflectionUtil;

/**
 * Defines factory methods to get property converters
 * 
 * @author kayalv
 *
 */
public class PropertyConverterFactory {

	private static Map<String, PropertyConverter> converters = new HashMap<String, PropertyConverter>();
	
	static{
		converters.put(SimpleTypeConverter.class.getName(), new SimpleTypeConverter());
		converters.put(CollectionConverter.class.getName(), new CollectionConverter());
		converters.put(MapConverter.class.getName(), new MapConverter());
		converters.put(ChildNodeConverter.class.getName(), new ChildNodeConverter());
		converters.put(ArrayConverter.class.getName(), new ArrayConverter());
		converters.put(EnumConverter.class.getName(), new EnumConverter());
	}
	
	public static PropertyConverter getMarshallingConverter(Object sourceValue) {
		
		PropertyConverter propertyConverter = null;
		if (ReflectionUtil.isSimpleType(sourceValue)){
			propertyConverter = converters.get(SimpleTypeConverter.class.getName());
    	} else if (sourceValue instanceof List || sourceValue instanceof Set) {
    		propertyConverter = converters.get(CollectionConverter.class.getName());
    	} else if (sourceValue instanceof Map<?,?> ) {
    		propertyConverter = converters.get(MapConverter.class.getName());
    	} else if (sourceValue instanceof Object[]) {
    		propertyConverter = converters.get(ArrayConverter.class.getName());
    	} else if (sourceValue instanceof Enum) {
    		propertyConverter = converters.get(EnumConverter.class.getName());
    	} else {
    		propertyConverter = converters.get(ChildNodeConverter.class.getName());
    	}
		return propertyConverter;
	}
	
	public static PropertyConverter getUnMarshallingConverter(Class<?> classObject, String key) {
		
		PropertyConverter propertyConverter = null;
		if (key.startsWith(MAP_KEY)){
			propertyConverter = converters.get(MapConverter.class.getName());
		} else {
			Class<?> setterParamType = ReflectionUtil.getSetterMethodType(classObject, key);
			if (setterParamType.isArray()){
				propertyConverter = converters.get(ArrayConverter.class.getName());
			} else if (setterParamType.isEnum()){
				propertyConverter = converters.get(EnumConverter.class.getName());
			} else if (setterParamType.isAssignableFrom(List.class) || 
					setterParamType.isAssignableFrom(Set.class)){
				propertyConverter = converters.get(CollectionConverter.class.getName());
			} else {
				propertyConverter =  converters.get(SimpleTypeConverter.class.getName());
			}
		}
		return propertyConverter;
	}
	
	public static PropertyConverter getConverter(String name) {
		return converters.get(name);
	}

}
