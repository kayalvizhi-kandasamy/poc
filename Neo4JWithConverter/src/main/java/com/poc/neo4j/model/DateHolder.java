package com.poc.neo4j.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.poc.neo4j.dao.annotation.IgnoreField;

public class DateHolder extends BaseEntity {

	@IgnoreField
	private static final long serialVersionUID = 5307187833045123250L;
	
	private Date utilDate;
	private java.sql.Date sqlDate;
	private List<Date> utilDatesList;
	private List<java.sql.Date> sqlDatesList;
	private Date[] utilDatesArray;
	private java.sql.Date[] sqlDatesArray;
	
	public Date getUtilDate() {
		return utilDate;
	}
	
	public void setUtilDate(Date utilDate) {
		this.utilDate = utilDate;
	}
	
	public java.sql.Date getSqlDate() {
		return sqlDate;
	}
	
	public void setSqlDate(java.sql.Date sqlDate) {
		this.sqlDate = sqlDate;
	}

	public List<Date> getUtilDatesList() {
		return utilDatesList;
	}

	public void setUtilDatesList(List<Date> utilDates) {
		this.utilDatesList = utilDates;
	}

	public List<java.sql.Date> getSqlDatesList() {
		return sqlDatesList;
	}

	public void setSqlDatesList(List<java.sql.Date> sqlDates) {
		this.sqlDatesList = sqlDates;
	}

	public Date[] getUtilDatesArray() {
		return utilDatesArray;
	}

	public void setUtilDatesArray(Date[] utilDatesArray) {
		this.utilDatesArray = utilDatesArray;
	}

	public java.sql.Date[] getSqlDatesArray() {
		return sqlDatesArray;
	}

	public void setSqlDatesArray(java.sql.Date[] sqlDatesArray) {
		this.sqlDatesArray = sqlDatesArray;
	}

	@Override
	public String toString() {
		return "DateHolder [utilDate=" + utilDate + ", sqlDate=" + sqlDate
				+ ", utilDatesList=" + utilDatesList + ", sqlDatesList="
				+ sqlDatesList + ", utilDatesArray="
				+ Arrays.toString(utilDatesArray) + ", sqlDatesArray="
				+ Arrays.toString(sqlDatesArray) + "]";
	}

	
}
