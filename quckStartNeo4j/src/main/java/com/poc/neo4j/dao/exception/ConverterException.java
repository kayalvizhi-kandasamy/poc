package com.poc.neo4j.dao.exception;


public class ConverterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3702935292490609973L;
	
	public ConverterException(String errorCode, String errorMsg, Throwable e) {
		super("ErrorCode: " + errorCode + "\nMessage: " + errorMsg, e);
	}

	
}