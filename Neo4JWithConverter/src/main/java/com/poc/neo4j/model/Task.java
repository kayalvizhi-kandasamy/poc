/**
 * Copyright (c) 2013 - 2014 CloudJee Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of CloudJee Inc.
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the source code license agreement you entered into with CloudJee Inc.
 */
package com.poc.neo4j.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.poc.neo4j.dao.annotation.IgnoreField;



/**
 * @author <a href="mailto:chaitanya.y@imaginea.com">Chaitanya.Yenugu<a>
 * 
 * @version Date: 03-Jan-2014
 */
public class Task extends BaseEntity{
	
	@IgnoreField
	private static final long serialVersionUID = 4839140274243905157L;

	private String name;

	private Map<String, String> taskEntries = new LinkedHashMap<String, String>();

	public Map<String, String> getTaskEntries() {
		return taskEntries;
	}

	public void setTaskEntries(Map<String, String> taskentry) {
		this.taskEntries = taskentry;
	}

	public void addTaskEntry(String key, String value) {
		this.taskEntries.put(key, value);
	}

	public void addTaskEntries(Map<String, String> taskentry) {
		this.taskEntries.putAll(taskentry);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Task [name=" + name + ", taskEntries=" + taskEntries + "]";
	}
	
}
