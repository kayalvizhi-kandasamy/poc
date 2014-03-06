package com.poc.neo4j.dao;

import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;


public class GraphDB {

	private static final String DB_PATH = "target/neo4j-dozer-db";
	private static GraphDatabaseService graphDb = null;
	
	private GraphDB(){
		
	}
	
	public static GraphDatabaseService getDatabaseService()
    {
		if (graphDb == null) {
			createDb();
		}
        return graphDb;
    }
	
	public static void shutDown()
    {
		if (graphDb != null) {
	        graphDb.shutdown();
        }
    }
	
	private static void createDb()
    {
		clearDb();
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        registerShutdownHook( graphDb );
    }

	private static void clearDb()
    {
        try
        {
        	shutDown();
        	FileUtils.deleteRecursively( new File( DB_PATH ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
    
    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
}
