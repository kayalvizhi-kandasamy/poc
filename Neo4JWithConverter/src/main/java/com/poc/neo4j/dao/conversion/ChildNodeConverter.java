package com.poc.neo4j.dao.conversion;

import static com.poc.neo4j.dao.Constants.AT_CLASS;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.poc.neo4j.dao.exception.ConverterException;
import com.poc.neo4j.dao.util.GraphDbUtil;
import com.poc.neo4j.dao.util.ReflectionUtil;

/**
 * Conversion of a {@link Node} property value to any entity field value
 * if the entity's field is of a complex type
 * 
 * @author kayalv 
 *
 */
public class ChildNodeConverter implements PropertyConverter {

	PropertyConverter propertyConverter = null;
	
	public <T> void marshall(T source, Node destination, String fieldName, Object sourceValue)
			throws ConverterException {
		GraphDbUtil.convertAndPersistNode(sourceValue, destination, fieldName);
	}

	@SuppressWarnings("unchecked")
	public <T> Object unmarshall(Node source, T destination, String propertyName, T child)
			throws ConverterException {
		
		Iterable<Relationship> relations = source.getRelationships(Direction.OUTGOING);
		if (relations == null) {
			return null;
		}
		for (Relationship relationship : relations) {
			assert relationship != null;
			Node childNode = relationship.getEndNode();
			String relationType = relationship.getType().name();
			Class<?> associatedClass = null;
			try {
				associatedClass = ReflectionUtil.registerClass((String) relationship.getProperty(AT_CLASS));
			} catch (ConverterException ce) {
				continue;
			}

			child = (T) unmarshallChild(childNode, associatedClass);
			PropertyConverter propertyConverter = 
					PropertyConverterFactory.getUnMarshallingConverter(destination.getClass(), relationType);
			Object object = propertyConverter.unmarshall(childNode, destination, relationType, child);
			ReflectionUtil.setProperty(destination, relationType, object);
//			if (endNode.hasRelationship(Direction.OUTGOING)) {
//				unmarshall(endNode, child, null, null);
//			}
		}
		return null;
	}
	
	public <T> T unmarshallChild(Node childNode, Class<T> childClass) 
		throws ConverterException {
	    	
		if (childNode == null || childClass == null) {
	  	    return null;
	  	}
		T child = ReflectionUtil.newInstance(childClass);
		Iterable<String> keys = childNode.getPropertyKeys();
		for (String key : keys) {
			PropertyConverter propertyConverter = PropertyConverterFactory.getUnMarshallingConverter(childClass, key);
			Object object = propertyConverter.unmarshall(childNode, child, key, null);
			ReflectionUtil.setProperty(child, key, object);
        }
		ReflectionUtil.setProperty(child, "id", childNode.getId());
		unmarshall(childNode, child, null, null);
        return child;
	}

}
