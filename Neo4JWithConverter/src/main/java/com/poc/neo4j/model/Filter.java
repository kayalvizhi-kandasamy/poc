package com.poc.neo4j.model;

import java.util.Arrays;
import java.util.List;

import com.poc.neo4j.dao.annotation.IgnoreField;


public class Filter extends BaseEntity {

	@IgnoreField
	private static final long serialVersionUID = -6484619006896331528L;

	private List<String> values;
	
	private String[] array;
	
	public Filter() {
	}

	public Filter(List<String> values) {
		this.values = values;
	}

	
	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public String[] getArray() {
		return array;
	}

	public void setArray(String[] array) {
		this.array = array;
	}

	@Override
	public String toString() {
		return "Filter [values=" + values + ", array=" + Arrays.toString(array)
				+ "]";
	}

	
}
