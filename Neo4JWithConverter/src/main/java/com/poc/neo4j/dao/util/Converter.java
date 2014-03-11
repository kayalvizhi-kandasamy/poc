package com.poc.neo4j.dao.util;

import static com.poc.neo4j.dao.Constants.AT_CLASS;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.poc.neo4j.dao.annotation.IgnoreField;
import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.model.BaseEntity;
public class Converter {

	
	@SuppressWarnings("rawtypes")
	public Object marshall(BaseEntity source, Node destination, Node parentNode) 
	throws ConverterException {
		
		if (destination == null || source == null) {
  	      return null;
  	    }
    	
    	Method setPropertyMethod = ReflectionUtil.getMethod(destination.getClass(), 
    			"setProperty", String.class, Object.class);
    	Field[] sourceFields = (Field[]) ArrayUtils.addAll(source.getClass().getSuperclass().getDeclaredFields(), 
    										source.getClass().getDeclaredFields());
    	for (int i = 0; i < sourceFields.length; i++) {
    		String fieldName = sourceFields[i].getName();
    		IgnoreField ignoreField =  sourceFields[i].getAnnotation(IgnoreField.class);
    		if (ignoreField != null){
    			System.out.println("Ignored field: " + fieldName);
    			continue;
    		}
    		Object sourceValue = ReflectionUtil.getProperty(source, fieldName);
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
			ReflectionUtil.setProperty(destinationObject, key, sourceNode.getProperty(key));
        }
		ReflectionUtil.setProperty(destinationObject, "id", sourceNode.getId());
        copyProperties(sourceNode, destinationObject);
        return destinationObject;
	}
	

	@SuppressWarnings("unchecked")
	public <T> void copyProperties(Node node, T entity) 
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
				List<T> list = (List<T>) ReflectionUtil.getProperty(entity, relationType);
				if (list == null) {
					list = new ArrayList<T>();
				}
				list.add(child);
				ReflectionUtil.setProperty(entity, relationType, list);
			} else {
				ReflectionUtil.setProperty(entity, relationType, child);
			}
			if (endNode.hasRelationship(Direction.OUTGOING)) {
				copyProperties(endNode, child);
			}
		}
	}
	
}


