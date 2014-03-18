package com.poc.neo4j.dao.util;

import static com.poc.neo4j.dao.Constants.AT_CLASS;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import com.poc.neo4j.dao.GraphDB;
import com.poc.neo4j.dao.conversion.Converter;
import com.poc.neo4j.model.BaseEntity;

public class GraphDbUtil {

	private static final Logger LOGGER = Logger.getLogger(GraphDbUtil.class);
	
	public static <T> long convertAndPersistNode(T entity, Node parentNode, 
			String lblRelationToParent) {
		
		long createdId = -1;
		if (!(entity instanceof BaseEntity)) {
			LOGGER.error("Instance[" + entity + "] is not of " + BaseEntity.class.getName() + " type");
			return createdId;
		}
		Transaction tx = null;
		try {
			tx = GraphDB.getDatabaseService().beginTx();
			Node node = GraphDB.getDatabaseService().createNode(DynamicLabel.label(entity.getClass().getSimpleName()));
			Converter.getConverter().marshall(entity, node);
			if (parentNode != null && lblRelationToParent!= null && lblRelationToParent.length() > 0) {
				Relationship relation = parentNode.createRelationshipTo(node, 
						DynamicRelationshipType.withName(lblRelationToParent));
				relation.setProperty(AT_CLASS, entity.getClass().getName());
			}
			tx.success();
			createdId = node.getId();
			((BaseEntity)entity).setId(createdId);
			System.out.println("Persisted:" + entity);
		} catch (Exception e) {
			LOGGER.error(e);
			System.err.println(e);
		}
		return createdId;
	}
	
}
