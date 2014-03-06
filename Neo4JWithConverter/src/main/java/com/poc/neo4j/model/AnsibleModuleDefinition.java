/**
 * Copyright (c) 2013 - 2014 CloudJee Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of CloudJee Inc.
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the source code license agreement you entered into with CloudJee Inc.
 */
package com.poc.neo4j.model;

import java.util.ArrayList;
import java.util.List;


public class AnsibleModuleDefinition extends ModuleDefinition{

	private List<ScriptFile> playBooks = new ArrayList<ScriptFile>();
	
	private List<ScriptFile> roles = new ArrayList<ScriptFile>();

	public List<ScriptFile> getPlayBooks() {
		return playBooks;
	}

	public void setPlayBooks(List<ScriptFile> playBooks) {
		this.playBooks = playBooks;
	}
	
//	public void addPlayBook(File file) {
//		playBooks.add(new ScriptFile(file));
//	}

	public List<ScriptFile> getRoles() {
		return roles;
	}

	public void setRoles(List<ScriptFile> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "AnsibleModuleDefinition [id=" + getId() + ", playBooks=" + playBooks + ", roles="
				+ roles + "]";
	}
	
	
}
