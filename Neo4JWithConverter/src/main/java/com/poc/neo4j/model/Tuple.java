package com.poc.neo4j.model;


import com.poc.neo4j.dao.annotation.IgnoreField;

public class Tuple extends BaseEntity{
	
	@IgnoreField
    private static final long serialVersionUID = 8826992823975181356L;

    private String key;
    private String value;

    public Tuple() {
	}

	public Tuple(String type, String value) {
        this.key = type;
        this.value = value;
    }
	
    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

    @Override
	public String toString() {
		return "Tuple [key=" + key + ", value=" + value + "]";
	}

	public int hashCode() {
        return value.hashCode();
    }

    public boolean equals(Object otherObject) {
        if (otherObject instanceof Tuple) {
            Tuple other = (Tuple) otherObject;
            return (other.key.equals(key) && other.value.equals(value));
        }
        return false;
    }
}
