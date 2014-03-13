package com.poc.neo4j.dao.conversion;

import static com.poc.neo4j.dao.Constants.AT_CLASS;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.Converter;
import com.poc.neo4j.dao.util.GraphDbUtil;
import com.poc.neo4j.dao.util.ReflectionUtil;
import com.poc.neo4j.model.BaseEntity;

public class ChildNodeConverter implements PropertyConverter{

	public <T> void marshall(T source, Node destination, String fieldName, Object sourceValue)
			throws ConverterException {
		GraphDbUtil.convertAndPersistNode((BaseEntity) sourceValue, destination, fieldName);
	}

	public <T> void unmarshall(Node source, T destination, String propertyName)
			throws ConverterException {
		
		Iterable<Relationship> relations = source.getRelationships(Direction.OUTGOING);
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
				setterMethod = ReflectionUtil.findMethod(destination.getClass(), setterMethodName, 1);
			} catch (ConverterException e) {
				continue;
			}
			Class[] setterParamType = setterMethod.getParameterTypes();
			assert (setterParamType != null && setterParamType.length == 1);
			T child = (T) Converter.getConverter().unmarshall(endNode, associatedClass);
			if (setterParamType[0].isAssignableFrom(List.class))
			{//TODO for all collection types
				List<T> list = (List<T>) ReflectionUtil.getProperty(destination, relationType);
				if (list == null) {
					list = new ArrayList<T>();
				}
				list.add(child);
				ReflectionUtil.setProperty(destination, relationType, list);
			} else {
				ReflectionUtil.setProperty(destination, relationType, child);
			}
			if (endNode.hasRelationship(Direction.OUTGOING)) {
				unmarshall(endNode, child, null);
			}
		}
	}

}
