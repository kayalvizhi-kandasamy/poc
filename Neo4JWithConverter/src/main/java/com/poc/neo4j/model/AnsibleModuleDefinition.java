/**
 * Copyright (c) 2013 - 2014 CloudJee Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of CloudJee Inc.
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the source code license agreement you entered into with CloudJee Inc.
 */
package com.poc.neo4j.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.poc.neo4j.dao.annotation.IgnoreField;


public class AnsibleModuleDefinition extends ModuleDefinition{

	@IgnoreField
	private static final long serialVersionUID = 3819692126574348045L;

	private List<ScriptFile> playBooks = new ArrayList<ScriptFile>();
	
	private Set<ScriptFile> roles = new HashSet<ScriptFile>();

	public List<ScriptFile> getPlayBooks() {
		return playBooks;
	}

	public void setPlayBooks(List<ScriptFile> playBooks) {
		this.playBooks = playBooks;
	}
	
	public Set<ScriptFile> getRoles() {
		return roles;
	}

	public void setRoles(Set<ScriptFile> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "AnsibleModuleDefinition [id=" + getId() + ", playBooks=" + playBooks + ", roles="
				+ roles + "]";
	}
	
	
}
