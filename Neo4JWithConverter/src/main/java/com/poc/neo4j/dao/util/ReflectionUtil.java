package com.poc.neo4j.dao.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;

import static com.poc.neo4j.dao.Constants.*;

import com.poc.neo4j.dao.exception.ConverterException;

public class ReflectionUtil {

	private static final Logger LOGGER = Logger.getLogger(ReflectionUtil.class);
	
	public static boolean isSimpleType(Object object) {
		
		if (object instanceof String || object instanceof Long  ||
			object instanceof Double || object instanceof Integer ||
			object instanceof Float || object instanceof Character ||
			object instanceof Byte || object instanceof Short ||
			object instanceof Boolean) {
			return true;
		}
		return false;	
	}
	
	public static boolean isSimpleType(Class<?> type) {
		
		if (type.isPrimitive() ||  type.isAssignableFrom(String.class) || 
			type.isAssignableFrom(Long.class)  || type.isAssignableFrom(Double.class) ||
			type.isAssignableFrom(Integer.class)  || type.isAssignableFrom(Float.class) ||
			type.isAssignableFrom(Character.class) || type.isAssignableFrom(Byte.class) ||
			type.isAssignableFrom(Short.class) || type.isAssignableFrom(Boolean.class)) {
			return true;
		}
		return false;	
	}

	public static Class<?> getType(Class<?> classObject, String fieldName) {
		
		Class<?> type = null;
		try {
			Field field = classObject.getDeclaredField(fieldName);
			if (field != null) {
				type = field.getType();
			}
		} catch (NoSuchFieldException | SecurityException e) {
			ConverterException ce = new ConverterException(FIELD_NOT_FOUND,"Field:[" + fieldName + 
					"] not found in Class[" + classObject.getName() + "]", e);
			LOGGER.error(ce);
		}
		return type;
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
	
	public static Object getProperty(Object entity, String fieldName)
	{
		Object sourceValue = null;
		try {
			sourceValue = PropertyUtils.getProperty(entity, fieldName);
		} catch (IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			ConverterException ce = new ConverterException(ERROR_ACCESSING_METHOD,
					"Get Method for property:[" + fieldName + 
					"] is not found/accessible in Class[" + entity.getClass().getName() + "]", e);
			LOGGER.error(ce);
			System.err.println(ce);
		}
		return sourceValue;
	}
	
	public static void setProperty(Object entity, String fieldName, Object value)
	{
		try {
			PropertyUtils.setProperty(entity, fieldName, value);
		} catch (IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			ConverterException ce = new ConverterException(ERROR_ACCESSING_METHOD,
					"Get Method for property:[" + fieldName + 
					"] is not found/accessible in Class[" + entity.getClass().getName() + "]", e);
			LOGGER.error(ce);
			System.err.println(ce);
		}
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
			 associatedClass = ClassUtils.getClass(className);
		} catch (ClassNotFoundException e) {
			ConverterException ce =  new  ConverterException(CLASS_NOT_FOUND,
					"Error while registering the class" + className, e);
			LOGGER.error(ce);
			throw ce;
		}
		return associatedClass;
	 }
	 
	 public static Class<?> getSetterMethodType(Class<?> classObject, String fieldName) {
		 
	    String setterMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		Method setterMethod;
		try {
			setterMethod = ReflectionUtil.findMethod(classObject, setterMethodName, 1);
		} catch (ConverterException e) {
			System.err.println(e);//TODO Logging
			return null;
		}
		Class<?>[] setterParamType = setterMethod.getParameterTypes();
		assert (setterParamType != null && setterParamType.length == 1);
		return setterParamType[0];
	 }
}
