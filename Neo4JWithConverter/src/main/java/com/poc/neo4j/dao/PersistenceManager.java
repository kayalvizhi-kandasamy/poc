package com.poc.neo4j.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import com.poc.neo4j.dao.conversion.Converter;
import com.poc.neo4j.dao.util.GraphDbUtil;
import com.poc.neo4j.model.BaseEntity;


public class PersistenceManager {
	
	private static PersistenceManager manager = null;
	
	private PersistenceManager(){
		
	}
		
	public static PersistenceManager get()
    {
		if (manager == null) {
			manager = new PersistenceManager();
		}
        return manager;
    }
		
	public long persistEntity(BaseEntity entity) {
		
		return GraphDbUtil.convertAndPersistNode(entity, null, null);
	}

	
	public <T extends BaseEntity> List<T> getEntities(Class<T> requiredType) {
		
		String cypher = "MATCH (entity:" + requiredType.getSimpleName() + ") RETURN entity";
		return getEntities(cypher, "entity", requiredType);
	}
	
	public <T extends BaseEntity> List<T> getEntities(String cypher, String returnData, Class<T> requiredType) {
		
		Transaction tx = null;
		List<T> entityList = new ArrayList<T>();
		try {
			tx = GraphDB.getDatabaseService().beginTx();
			Iterator<Node> nodes = CypherQueryExecuter.getExecutor(GraphDB.getDatabaseService()).
					getQueryResult(cypher, returnData);

			while (nodes.hasNext()) {
				Node node = nodes.next();
				T entity = Converter.getConverter().unmarshall(node, requiredType);
				System.out.println("Retrieved:" + entity);
				entityList.add(entity);
			}
			tx.success();
		} catch (Exception e) {
			System.err.println(e);
		}
		return entityList;
	}

	public void clearDB() {
		
		System.out.println("Deleting all Entities and relationships");
		Transaction tx = null;
		try {
			tx = GraphDB.getDatabaseService().beginTx();
			String cypher = "MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n, r";
			CypherQueryExecuter.getExecutor(GraphDB.getDatabaseService()).execute(cypher);
			tx.success();			
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public void shutDownDB() {
		GraphDB.getDatabaseService().shutdown();
	}
	
}