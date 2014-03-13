package com.poc.neo4j.dao.util;

import static com.poc.neo4j.dao.Constants.AT_CLASS;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import com.poc.neo4j.dao.GraphDB;
import com.poc.neo4j.model.BaseEntity;

public class GraphDbUtil {

	
	public static long convertAndPersistNode(BaseEntity entity, Node parentNode, 
			String lblRelationToParent) {
		
		long createdId = -1;
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
			entity.setId(createdId);
			System.out.println("Persisted:" + entity);
		} catch (Exception e) {
			System.err.println(e);
		}
		return createdId;
	}
	
}
