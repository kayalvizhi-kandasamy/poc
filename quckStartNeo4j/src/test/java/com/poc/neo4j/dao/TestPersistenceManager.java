package com.poc.neo4j.dao;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

import com.poc.neo4j.model.AnsibleModuleDefinition;
import com.poc.neo4j.model.Module;
import com.poc.neo4j.model.ScriptFile;

public class TestPersistenceManager {

	static PersistenceManager persistenceManager = PersistenceManager.get();
	
	@Test
	public void testPersistEntity_simpleObject(){
		
		System.out.println("\nTestPersistenceManager.testPersistEntity_simpleObject()");
		System.out.println("--------------------------------------------------------");
		ScriptFile roleSF = new ScriptFile();
		roleSF.setName("roleSFName1");
		roleSF.setPath("roleSFPath1");
		assertNull(roleSF.getId());
		
		persistenceManager.persistEntity(roleSF);
		assertNotNull(roleSF.getId());
		
		List<ScriptFile> entities = persistenceManager.getEntities(ScriptFile.class);
		assertNotNull(entities);
		assertEquals(1, entities.size());
		assertEquals(roleSF.getName(), entities.get(0).getName());
		assertEquals(roleSF.getPath(), entities.get(0).getPath());
	}
	
	@Test
	public void testPersistEntity_complexObject(){
		
		System.out.println("\nTestPersistenceManager.testPersistEntity_complexObject()");
		System.out.println("--------------------------------------------------------");
		ScriptFile roleSF = new ScriptFile();
		roleSF.setName("roleSFName1");
		roleSF.setPath("roleSFPath1");
		assertNull(roleSF.getId());
		
		ScriptFile playBookSF1 = new ScriptFile();
		playBookSF1.setName("playBookSFName1");
		playBookSF1.setPath("playBookSFPath1");
		assertNull(playBookSF1.getId());
		
		ScriptFile playBookSF2 = new ScriptFile();
		playBookSF2.setName("playBookSFName2");
		playBookSF2.setPath("playBookSFPath2");
		assertNull(playBookSF2.getId());
		
		AnsibleModuleDefinition anModDefinition = new AnsibleModuleDefinition();
		List<ScriptFile> playBookSFList = new ArrayList<ScriptFile>();
		playBookSFList.add(playBookSF1);
		playBookSFList.add(playBookSF2);
		List<ScriptFile> roleSFList = new ArrayList<ScriptFile>();
		roleSFList.add(roleSF);
		anModDefinition.setPlayBooks(playBookSFList);
		anModDefinition.setRoles(roleSFList);
		assertNull(anModDefinition.getId());
		
		persistenceManager.persistEntity(anModDefinition);
		
		List<AnsibleModuleDefinition> entities = persistenceManager.getEntities(AnsibleModuleDefinition.class);
		assertNotNull(entities);
		assertEquals(1, entities.size());
		AnsibleModuleDefinition retrievedDefinition = entities.get(0);
		assertNotNull(retrievedDefinition);
		assertNotNull(retrievedDefinition.getId());
		
		assertEquals(2, retrievedDefinition.getPlayBooks().size());
		assertEquals(1, retrievedDefinition.getRoles().size());
		
	}
	
	@Test
	public void testGetEntities(){
		
		System.out.println("\nTestPersistenceManager.testGetEntities()");
		System.out.println("------------------------------------------");
		ScriptFile roleSF = new ScriptFile();
		roleSF.setName("roleSFName1");
		roleSF.setPath("roleSFPath1");
		assertNull(roleSF.getId());
		
		ScriptFile playBookSF1 = new ScriptFile();
		playBookSF1.setName("playBookSFName1");
		playBookSF1.setPath("playBookSFPath1");
		assertNull(playBookSF1.getId());
		
		ScriptFile playBookSF2 = new ScriptFile();
		playBookSF2.setName("playBookSFName2");
		playBookSF2.setPath("playBookSFPath2");
		assertNull(playBookSF2.getId());
		
		AnsibleModuleDefinition anModDefinition = new AnsibleModuleDefinition();
		List<ScriptFile> playBookSFList = new ArrayList<ScriptFile>();
		playBookSFList.add(playBookSF1);
		playBookSFList.add(playBookSF2);
		List<ScriptFile> roleSFList = new ArrayList<ScriptFile>();
		roleSFList.add(roleSF);
		anModDefinition.setPlayBooks(playBookSFList);
		anModDefinition.setRoles(roleSFList);
		
		
		Module module = new Module();
		module.setName("ModelueName");
		module.setPath("ModeluePath");
		module.setVersion("ModelueVersion");
		module.setDefinition(anModDefinition);
		assertNull(module.getId());
		
		persistenceManager.persistEntity(module);
		
		String playBooksQuery = "MATCH (definition:AnsibleModuleDefinition)-[:playBooks]-"
				+ "(file:ScriptFile{name:\"playBookSFName2\"}) RETURN file";
		List<ScriptFile> scriptFiles = persistenceManager.getEntities(playBooksQuery, "file", ScriptFile.class);
		assertNotNull(scriptFiles);
		assertEquals(1, scriptFiles.size());
		assertEquals(playBookSF2.getName(), scriptFiles.get(0).getName());
		assertEquals(playBookSF2.getPath(), scriptFiles.get(0).getPath());
		
		playBooksQuery = "MATCH (definition:AnsibleModuleDefinition)-[:roles]-"
				+ "(file:ScriptFile) RETURN file";
		scriptFiles = persistenceManager.getEntities(playBooksQuery, "file", ScriptFile.class);
		assertNotNull(scriptFiles);
		assertEquals(1, scriptFiles.size());
		assertEquals(roleSF.getName(), scriptFiles.get(0).getName());
		assertEquals(roleSF.getPath(), scriptFiles.get(0).getPath());
		
		String moduleQuery = "MATCH (module:Module)-[:definition]-(definition:PuppetModuleDefinition) RETURN module";
		List<Module> modules = persistenceManager.getEntities(moduleQuery, "module", Module.class);
		assertNotNull(modules);
		assertEquals(0, modules.size());
		
		moduleQuery = "MATCH (module:Module)-[:definition]-(AnsibleModuleDefinition) RETURN module";
		modules = persistenceManager.getEntities(moduleQuery, "module", Module.class);
		assertNotNull(modules);
		assertEquals(1, modules.size());
		assertEquals(module.getName(), modules.get(0).getName());
		assertEquals(module.getPath(), modules.get(0).getPath());
		assertEquals(module.getVersion(), modules.get(0).getVersion());
	}
	
	@After
	public void clearDb() {
		persistenceManager.clearDb();
	}
	
}
