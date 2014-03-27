package com.poc.neo4j.model;

import java.util.Arrays;
import java.util.List;

import com.poc.neo4j.dao.annotation.IgnoreField;



public class AccountInfo extends BaseEntity{

	@IgnoreField
	private static final long serialVersionUID = -3435427248275777079L;
	
	private String accountId;
	private InstanceProvider type; // AWS or OpenStack or Physical etc..
	private List<InstanceProvider> types; 
	private InstanceProvider[] arrayTypes;
	private String ownerAlias; //optional
	private String accessKey;
	private String secretKey;
	private String regionName; // region or end-point 

	public AccountInfo() {
	}
	
	public AccountInfo(InstanceProvider type, String accessKey, String secretKey) {
		this(null, type, null, accessKey, secretKey, "us-east-1");
	}

	public AccountInfo(String accountId, InstanceProvider type, String ownerAlias,
			String accessKey, String secretKey, String region) {
		this.accountId = accountId;
		this.type = type;
		this.ownerAlias = ownerAlias;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.regionName = region;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public InstanceProvider getType() {
		return type;
	}

	public void setType(InstanceProvider type) {
		this.type = type;
	}

	public String getOwnerAlias() {
		return ownerAlias;
	}

	public void setOwnerAlias(String ownerAlias) {
		this.ownerAlias = ownerAlias;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public List<InstanceProvider> getTypes() {
		return types;
	}

	public void setTypes(List<InstanceProvider> types) {
		this.types = types;
	}

	public InstanceProvider[] getArrayTypes() {
		return arrayTypes;
	}

	public void setArrayTypes(InstanceProvider[] arrayTypes) {
		this.arrayTypes = arrayTypes;
	}

	@Override
	public String toString() {
		return "AccountInfo [accountId=" + accountId + ", type=" + type
				+ ", types=" + types + ", arrayTypes="
				+  Arrays.toString(arrayTypes) + ", ownerAlias=" + ownerAlias
				+ ", accessKey=" + accessKey + ", secretKey=" + secretKey
				+ ", regionName=" + regionName + "]";
	}
	
}
