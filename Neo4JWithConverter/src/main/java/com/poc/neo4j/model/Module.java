/**
 * Copyright (c) 2013 - 2014 CloudJee Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of CloudJee Inc.
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the source code license agreement you entered into with CloudJee Inc.
 */
package com.poc.neo4j.model;


public class Module extends BaseEntity{
	
	/*
	 * Name of the module must match the name of the
	 * puppet module if the script type is puppet DSL
	 */
	
	private String name;

	private String path;
	
	private String version;
	
	private ModuleDefinition definition;
	
	/**
	 * 
	 */
	public Module() {
		super();
	}

	/**
	 * @param name
	 */
	public Module(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ModuleDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(ModuleDefinition definition) {
		this.definition = definition;
	}

	@Override
	public String toString() {
		return "Module [id=" + getId() + ", name=" + name + ", path=" + path + ", version="
				+ version + ", definition=" + definition + "]";
	}
	
	
}
