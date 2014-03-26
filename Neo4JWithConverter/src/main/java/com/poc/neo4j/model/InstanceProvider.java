package com.poc.neo4j.model;

public enum InstanceProvider {
	AWS(1, 0.0), OPENSTACK(2, 1.0);
	
	private int index;
	private double value;
	
	InstanceProvider(int index, double value) {
		this.index = index;
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
