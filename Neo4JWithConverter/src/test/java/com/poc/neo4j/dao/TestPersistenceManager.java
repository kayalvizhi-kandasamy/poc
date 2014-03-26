package com.poc.neo4j.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import com.poc.neo4j.model.AccountInfo;
import com.poc.neo4j.model.AnsibleModuleDefinition;
import com.poc.neo4j.model.Filter;
import com.poc.neo4j.model.InstanceProvider;
import com.poc.neo4j.model.IpPermissionInfo;
import com.poc.neo4j.model.Module;
import com.poc.neo4j.model.ScriptFile;
import com.poc.neo4j.model.Task;
import com.poc.neo4j.model.Tuple;

public class TestPersistenceManager {

	static PersistenceManager persistenceManager = PersistenceManager.get();
	
	@Test
	public void testPersistEntity_simpleObject(){
		
		System.out.println("\nTestPersistenceManager.testPersistEntity_simpleObject()");
		System.out.println("---------------------------------------------------------");
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
		System.out.println("----------------------------------------------------------");
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
		Set<ScriptFile> roleSFList = new HashSet<ScriptFile>();
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
	public void testPersistEntity_EntityWithMap(){
		
		System.out.println("\nTestPersistenceManager.testPersistEntity_EntityWithMap()");
		System.out.println("----------------------------------------------------------");
		Task task = new Task();
		task.setName("NewTask");
		Map<String, String> taskEntriesMap = new HashMap<String, String>();
		taskEntriesMap.put("Key1", "Value1");
		taskEntriesMap.put("Key1", "Value1");
		task.setTaskEntries(taskEntriesMap);
		persistenceManager.persistEntity(task);
		
		List<Task> tasks = persistenceManager.getEntities(Task.class);
		assertNotNull(tasks);
		assertEquals(1, tasks.size());
		Task retrievedTask = tasks.get(0);
		assertNotNull(retrievedTask.getId());
		assertEquals(task.getName(), retrievedTask.getName());
		assertEquals(task.getTaskEntries(), retrievedTask.getTaskEntries());
	}
	
	@Test
	public void testPersistEntity_EntityWithListOfSimpleType(){
		
		System.out.println("\nTestPersistenceManager.testPersistEntity_EntityWithListOfSimpleType()");
		System.out.println("------------------------------------------------------------------------");
		Filter filter = new Filter();
		List<String> values = new ArrayList<String>();
		values.add("Value1");
		values.add("Value2");
		filter.setValues(values);
		
		List<Object> mixedValues = new ArrayList<Object>();
		mixedValues.add("Hi");
		mixedValues.add(0);
		filter.setMixedValues(mixedValues);
		persistenceManager.persistEntity(filter);
		
		List<Filter> filters = persistenceManager.getEntities(Filter.class);
		assertNotNull(filters);
		assertEquals(1, filters.size());
		Filter retrievedFilter = filters.get(0);
		assertNotNull(retrievedFilter.getId());
		assertEquals(filter.getValues(), retrievedFilter.getValues());
	}
	
	@Test
	public void testPersistEntity_EntityWithSetOfSimpleType(){
		
		System.out.println("\nTestPersistenceManager.testPersistEntity_EntityWithSetOfSimpleType()");
		System.out.println("-----------------------------------------------------------------------");
		IpPermissionInfo ipPermInfo = new IpPermissionInfo();
		
		List<String> ipRanges = new ArrayList<String>();
		ipRanges.add("ipRange1");
		ipRanges.add("ipRange2");
		ipPermInfo.setIpRanges(ipRanges);
		
		Set<String> groups = new HashSet<String>();
		groups.add("groupId1");
		groups.add("groupId2");
		ipPermInfo.setGroupIds(groups);
		
		persistenceManager.persistEntity(ipPermInfo);
		
		List<IpPermissionInfo> ipPermissionInfos = persistenceManager.getEntities(IpPermissionInfo.class);
		assertNotNull(ipPermissionInfos);
		assertEquals(1, ipPermissionInfos.size());
		IpPermissionInfo retrievedIpPermissionInfo = ipPermissionInfos.get(0);
		assertNotNull(retrievedIpPermissionInfo.getId());
		assertEquals(ipPermInfo.getIpRanges().size(), 
				retrievedIpPermissionInfo.getIpRanges().size());
		assertEquals(ipPermInfo.getGroupIds().size(), 
				retrievedIpPermissionInfo.getGroupIds().size());
		
		for (String ipRange : ipRanges) {
			assertTrue(retrievedIpPermissionInfo.getIpRanges().contains(ipRange));
		}
		
		for (String group : groups) {
			assertTrue(retrievedIpPermissionInfo.getGroupIds().contains(group));
		}
	}
	
	@Test
	public void testPersistEntity_EntityWithArray(){
		
		System.out.println("\nTestPersistenceManager.testPersistEntity_EntityWithArray()");
		System.out.println("------------------------------------------------------------");
		Filter filter = new Filter();
		List<String> values = new ArrayList<String>();
		values.add("Value1");
		values.add("Value2");
		filter.setValues(values);
		
		String[] simpleArray = new String[] {"Kayal", "Vizhi"};
		filter.setSimpleArray(simpleArray);
		Tuple[] complexArray = new Tuple[] {new Tuple("a", "A"), new Tuple("b", "B")};
		filter.setComplexArray(complexArray);
		
		persistenceManager.persistEntity(filter);
		
		List<Filter> filters = persistenceManager.getEntities(Filter.class);
		assertNotNull(filters);
		assertEquals(1, filters.size());
		Filter retrievedFilter = filters.get(0);
		assertNotNull(retrievedFilter.getId());
		assertEquals(filter.getValues(), retrievedFilter.getValues());
		assertEquals(complexArray.length, retrievedFilter.getComplexArray().length);
		for (int i = 0; i < complexArray.length; i++) {
			assertEquals(complexArray[i], retrievedFilter.getComplexArray()[i]);
		}
	}
	@Test
	public void testPersistEntity_EntityWithCollectionOfMixedType(){
		
		System.out.println("\nTestPersistenceManager.testPersistEntity_EntityWithCollectionOfMixedType()");
		System.out.println("----------------------------------------------------------------------------");
		Filter filter = new Filter();
		
		List<Object> mixedValues = new ArrayList<Object>();
		mixedValues.add("Hi");
		mixedValues.add(0);
		filter.setMixedValues(mixedValues);
		persistenceManager.persistEntity(filter);
		
		List<Filter> filters = persistenceManager.getEntities(Filter.class);
		assertNotNull(filters);
		assertEquals(1, filters.size());
		Filter retrievedFilter = filters.get(0);
		assertNotNull(retrievedFilter.getId());
		assertNull(retrievedFilter.getMixedValues());
		assertNotSame(filter.getMixedValues(), retrievedFilter.getMixedValues());
	}
	
	@Test
	public void testPersistEntity_EntityWithArrayOfMixedType(){
		
		System.out.println("\nTestPersistenceManager.testPersistEntity_EntityWithArrayOfMixedType()");
		System.out.println("----------------------------------------------------------------------------");
		Filter filter = new Filter();
		
		Object[] objects = new Object[] {"Hi", new Integer(1)};
		filter.setMixedArray(objects);
		persistenceManager.persistEntity(filter);
		
		List<Filter> filters = persistenceManager.getEntities(Filter.class);
		assertNotNull(filters);
		assertEquals(1, filters.size());
		Filter retrievedFilter = filters.get(0);
		assertNotNull(retrievedFilter.getId());
		assertNull(retrievedFilter.getMixedArray());
		assertNotSame(filter.getMixedArray(), retrievedFilter.getMixedArray());
	}
	
	@Test
	public void testPersistEntity_EntityWithEnum(){
		
		System.out.println("\nTestPersistenceManager.testPersistEntity_EntityWithEnum()");
		System.out.println("------------------------------------------------------------");
		
		AccountInfo acctInfo = new AccountInfo(InstanceProvider.AWS, "accesskey", "secretKey");
		persistenceManager.persistEntity(acctInfo);
		
		List<AccountInfo> accounts = persistenceManager.getEntities(AccountInfo.class);
		assertNotNull(accounts);
		assertEquals(1, accounts.size());
		assertSame(acctInfo.getType(), accounts.get(0).getType());
		
//		TODO
//		AccountInfo acctInfo1 = new AccountInfo(InstanceProvider.AWS, "accesskey", "secretKey");
//		List<InstanceProvider> types =  new ArrayList<InstanceProvider>(0);
//		types.add(InstanceProvider.AWS);
//		types.add(InstanceProvider.OPENSTACK);
//		acctInfo1.setTypes(types);
//		persistenceManager.persistEntity(acctInfo1);
//		
//		accounts = persistenceManager.getEntities(AccountInfo.class);
//		assertNotNull(accounts);
//		assertEquals(1, accounts.size());
//		assertSame(acctInfo.getType(), accounts.get(0).getType());
//		assertSame(acctInfo.getTypes(), accounts.get(0).getTypes());
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
		Set<ScriptFile> roleSFList = new HashSet<ScriptFile>();
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
		persistenceManager.clearDB();
	}
	
	@AfterClass
	public static void shutDown() {
		persistenceManager.shutDownDB();
	}
}
