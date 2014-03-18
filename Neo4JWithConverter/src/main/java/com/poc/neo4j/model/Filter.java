package com.poc.neo4j.model;

import java.util.Arrays;
import java.util.List;

import com.poc.neo4j.dao.annotation.IgnoreField;


public class Filter extends BaseEntity {

	@IgnoreField
	private static final long serialVersionUID = -6484619006896331528L;

	private List<String> values;
	
	private String[] simpleArray;
	
	private Tuple[] complexArray;
	
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

	public String[] getSimpleArray() {
		return simpleArray;
	}

	public void setSimpleArray(String[] simpleArray) {
		this.simpleArray = simpleArray;
	}

	public Tuple[] getComplexArray() {
		return complexArray;
	}

	public void setComplexArray(Tuple[] complexArray) {
		this.complexArray = complexArray;
	}

	@Override
	public String toString() {
		return "Filter [values=" + values + ", simpleArray="
				+ Arrays.toString(simpleArray) + ", complexArray="
				+ Arrays.toString(complexArray) + "]";
	}
	
}
