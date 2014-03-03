package com.poc.neo4j.dao.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.model.BaseEntity;
import static com.poc.neo4j.dao.Constants.*;
public class Converter {

	
	@SuppressWarnings("rawtypes")
	public Object marshall(BaseEntity source, Node destination, Node parentNode) 
	throws ConverterException {
		
		if (destination == null || source == null) {
  	      return null;
  	    }
    	Field[] sourceFields = source.getClass().getDeclaredFields();
    	Method setPropertyMethod = ReflectionUtil.getMethod(destination.getClass(), "setProperty", String.class, Object.class);
    	for (int i = 0; i < sourceFields.length; i++) {
    		
    		String fieldName = sourceFields[i].getName();
    		String sourceGetterMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    		Method sourceGetterMethod = ReflectionUtil.getMethod(source.getClass(), sourceGetterMethodName, null);
    		if (sourceGetterMethod == null) {
				continue;
			}
    		Object sourceValue = ReflectionUtil.invoke(sourceGetterMethod,source, null);
			if (sourceValue == null) {
				continue;
			}
	    	if (ReflectionUtil.isSimpleType(sourceValue)){
	    		ReflectionUtil.invoke(setPropertyMethod,destination, fieldName, sourceValue);
	    	} else if (sourceValue instanceof List || sourceValue instanceof Set ) {
	    		Collection collection = (Collection) sourceValue;
	    		for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
		    		GraphDbUtil.convertAndPersistNode((BaseEntity) iterator.next(), destination, fieldName);
				}
	    	} else {
	    		GraphDbUtil.convertAndPersistNode((BaseEntity) sourceValue, destination, fieldName);
	    	}
		}
    	return destination;
	}
	
//	convert Node To BaseEntity
	public <T> T unmarshall(Node sourceNode, Class<T> destinationClass) 
	throws ConverterException {
    	
		if (sourceNode == null || destinationClass == null) {
	  	    return null;
	  	}
		T destinationObject = ReflectionUtil.newInstance(destinationClass);
		Iterable<String> keys = sourceNode.getPropertyKeys();
		for (String key : keys) {
	    	String setterMethodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
	    	Object value = sourceNode.getProperty(key);
	    	Method setterMethod;
			try 
			{
				setterMethod = ReflectionUtil.getMethod(destinationObject.getClass(), setterMethodName, value.getClass());
				ReflectionUtil.invoke(setterMethod, destinationObject, value);
			} catch (ConverterException e) {
				continue;
			}
        }
		ReflectionUtil.invoke( ReflectionUtil.getMethod(destinationObject.getClass(), "setId", Long.class), 
				destinationObject, sourceNode.getId());
        return destinationObject;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends BaseEntity> void copyProperties(Node node, T entity) 
	throws ConverterException {
		
		Iterable<Relationship> relations = node.getRelationships(Direction.OUTGOING);
		if (relations == null) {
			return;
		}
		for (Relationship relationship : relations) {
			assert relationship != null;
			Node endNode = relationship.getEndNode();
			String relationType = relationship.getType().name();
			Class<?> associatedClass = null;
			try {
				associatedClass = ReflectionUtil.registerClass((String) relationship.getProperty(AT_CLASS));
			} catch (ConverterException ce) {
				continue;
			}
			String setterMethodName = "set" + relationType.substring(0, 1).toUpperCase() + relationType.substring(1);
			Method setterMethod;
			try {
				setterMethod = ReflectionUtil.findMethod(entity.getClass(), setterMethodName, 1);
			} catch (ConverterException e) {
				continue;
			}
			Class[] setterParamType = setterMethod.getParameterTypes();
			assert (setterParamType != null && setterParamType.length == 1);
			T child = (T) unmarshall(endNode, associatedClass);
			if (setterParamType[0].isAssignableFrom(List.class))
			{//TODO for all collection types
				String getterMethodName = "get" + relationType.substring(0, 1).toUpperCase() + relationType.substring(1);
				Method getterMethod = ReflectionUtil.getMethod(entity.getClass(), getterMethodName, null);
				if (getterMethod != null) {
					List<T> list = (List<T>) ReflectionUtil.invoke(getterMethod, entity, null);
					if (list == null) {
						list = new ArrayList<T>();
					}
					list.add(child);
					ReflectionUtil.invoke(setterMethod, entity, list);
				}
			} else {
				ReflectionUtil.invoke(setterMethod, entity, child);
			}
			if (endNode.hasRelationship(Direction.OUTGOING)) {
				copyProperties(endNode, child);
			}
		}
	}
	
}


