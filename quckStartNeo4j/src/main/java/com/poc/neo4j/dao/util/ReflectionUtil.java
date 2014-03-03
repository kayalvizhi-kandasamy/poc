package com.poc.neo4j.dao.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import static com.poc.neo4j.dao.Constants.*;
import com.poc.neo4j.dao.exception.ConverterException;

public class ReflectionUtil {

	private static final Logger LOGGER = Logger.getLogger(ReflectionUtil.class);
	
	public static boolean isSimpleType(Object object) {
		
		if (object instanceof String || object instanceof Long  ||
			object instanceof Double || object instanceof Integer ||
			object instanceof Float || object instanceof Character ||
			object instanceof Byte) {
			return true;
		}
		return false;	
	}
	
	public static  Method getMethod(Class<?> destinationClass, String name, Class<?>... parameterTypes)
	        throws ConverterException {
		Method method = null;
    	try {
    		method = destinationClass.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			ConverterException ce = new ConverterException(METHOD_NOT_FOUND,"Method:[" + name + 
					"] not found in Class[" + destinationClass.getName() + "]", e);
			LOGGER.error(ce);
			throw ce;
		}
    	return method;
	}
	
	public static  Method findMethod(Class<?> destinationClass, String name, int argLengh)
	throws ConverterException {
		
		Method[] methods = destinationClass.getDeclaredMethods();
		Method method = null;
		
		for (int i = 0; i < methods.length; i++) {
			if (name.equals(methods[i].getName()) && methods[i].getParameterTypes().length == argLengh ){
				method = methods[i];
				break;
			}
		}
		if (method == null) {
			ConverterException ce = new ConverterException(METHOD_NOT_FOUND,"Method:[" + name + 
					"] not found in Class[" + destinationClass.getName() + "]", new Exception("Method Not found"));
			LOGGER.error(ce);
			throw ce;
		}
		return method;
	}
	
	public static Object invoke(Method method, Object destinationObject, Object... args)
	throws ConverterException {
		
		Object value = null;
		try {
			value = method.invoke(destinationObject, args);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e1) {
			ConverterException ce =  new ConverterException(ERROR_ACCESSING_METHOD, 
					"Error while accessing: " + method.getName(), e1);
			LOGGER.error(ce);
			throw ce;
		}
		return value;
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> destinationClass) throws ConverterException {
		T newIns = null;
		try {
			newIns = (T) destinationClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			ConverterException ce =  new  ConverterException(ERROR_IN_INSTANTIATION,
					"Error while instantiating: " + destinationClass.getName() , e);
			LOGGER.error(ce);
			throw ce;
		}
		return newIns;
	 }
	
	
	 public static Class<?> registerClass(String className)
	 throws ConverterException {
		 
		Class<?> associatedClass = null;
		try {
			if (className != null){
				associatedClass = Class.forName(className);
			}
		} catch (ClassNotFoundException e1) {
			ConverterException ce =  new  ConverterException(CLASS_NOT_FOUND,
					"Error while registering the class" + className, e1);
			LOGGER.error(ce);
			throw ce;
		}
		return associatedClass;
	 }
}
