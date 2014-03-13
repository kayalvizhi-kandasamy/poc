package com.poc.neo4j.model;

import java.util.List;
import java.util.Set;

import com.poc.neo4j.dao.annotation.IgnoreField;


public class IpPermissionInfo extends BaseEntity{

	@IgnoreField
	private static final long serialVersionUID = 1L;
	
	private int fromPort;
	private int toPort;
	private Set<String> groupIds;
	private List<String> ipRanges;
	
	public IpPermissionInfo() {
	}

	public int getFromPort() {
		return fromPort;
	}

	public int getToPort() {
		return toPort;
	}


	public Set<String> getGroupIds() {
		return groupIds;
	}

	public List<String> getIpRanges() {
		return ipRanges;
	}

	public void setFromPort(int fromPort) {
		this.fromPort = fromPort;
	}

	public void setToPort(int toPort) {
		this.toPort = toPort;
	}

	public void addGroupId(String group) {
		this.groupIds.add(group);
	}
	
	public void setGroupIds(Set<String> groups) {
		this.groupIds = groups;
	}

	public void setIpRanges(List<String> ipRanges) {
		this.ipRanges = ipRanges;
	}

	@Override
	public String toString() {
		return "IpPermissionInfo [fromPort=" + fromPort + ", toPort=" + toPort
				+ ", groupIds=" + groupIds + ", ipRanges=" + ipRanges + "]";
	}
	
	
}
