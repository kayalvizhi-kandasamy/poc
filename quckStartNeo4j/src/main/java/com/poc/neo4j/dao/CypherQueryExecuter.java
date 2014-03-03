/**
 * Copyright (c) 2013 - 2014 CloudJee Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of CloudJee Inc.
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the source code license agreement you entered into with CloudJee Inc.
 */

package com.poc.neo4j.dao;

import java.util.Iterator;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class CypherQueryExecuter {


	private static ExecutionEngine engine = null;
	private static CypherQueryExecuter queryExecutor;
	
	private CypherQueryExecuter(GraphDatabaseService graphDbService) {
		if (engine == null) {
			engine = new ExecutionEngine(graphDbService);
		}
	}
	
	public static CypherQueryExecuter getExecutor(GraphDatabaseService graphDbService){
		if(queryExecutor == null){
			queryExecutor = new CypherQueryExecuter(graphDbService);
		}
		return queryExecutor;
	}

	public Iterator<Node> getQueryResult(String queryString, String returnData) {
		
		ExecutionResult result = engine.execute(queryString);
		return result.columnAs(returnData);
	}
	
	public void execute(String queryString) {
		
		engine.execute(queryString);
	}
}
