package com.poc.neo4j.dao;

import com.poc.neo4j.model.BaseEntity;
/**
 * 
 * @author kayalv
 *
 */
public interface Constants {
	
	String AT_CLASS = "@Class";
	String METHOD_NOT_FOUND = "METHOD_NOT_FOUND";
	String FIELD_NOT_FOUND = "FIELD_NOT_FOUND";
	String ERROR_ACCESSING_METHOD= "ERROR_ACCESSING_METHOD";
	String ERROR_INVOKING_SETPROPERTY_METHOD= "ERROR_INVOKING_SETPROPERTY_METHOD";
	String CLASS_NOT_FOUND = "CLASS_NOT_FOUND";
	String ERROR_IN_INSTANTIATION = "ERROR_IN_INSTANTIATION";
	String ERROR_MAP_KEY_VALUE ="The map key and value must be of simple type";
	String ERROR_OBJECT_TYPE ="The object must be of " + BaseEntity.class.getName() + " type";
	String ERROR_TYPE ="The object must be one of the following types"
			+ "1. " + BaseEntity.class.getName() + ", "  
			+ "2. Simple types like Long, String, Integer etc...,"
			+ "3. Enum type, "
			+ "4. Date type and must not be of mixed type";
	String ERROR_DATE_TYPE ="The date object must be either of java.util.Date (or) java.sql.Date";
	String MAP_KEY ="MAP.KEY";
	String LIST_KEY ="LIST.KEY";
	String SET_KEY ="SET.KEY";
	String ARRAY_KEY ="ARRAY.KEY";
	String SEPARATOR_DOT = ".";
}
