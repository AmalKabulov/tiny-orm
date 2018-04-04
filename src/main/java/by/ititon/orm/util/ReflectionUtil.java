package by.ititon.orm.util;

import by.ititon.orm.exception.ReflectionException;

import java.lang.reflect.*;

public class ReflectionUtil {

    public static Object invokeMethod(Object object, Method method) {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException("COULD NOT INVOKE METHOD " + method.getName() + " CAUSE: ", e);
        }

    }

    public static Method getMethod(Class objClass, String methodName) {
        try {
            return objClass.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new ReflectionException("METHOD: " + methodName + " NOT FOUND ", e);
        }
    }


    public static Class<?> newClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ReflectionException("CLASS: " + className + " NOT FOUND ", e);
        }
    }


    public static Type[] getFieldGenericType(Field field) {
        ParameterizedType genericReturnType = (ParameterizedType) field.getGenericType();
        return genericReturnType.getActualTypeArguments();
    }


    public static Object newInstance(String className) {

        try {
            return newClass(className).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ReflectionException("COULD NOT CREATE INSTANCE OF: " + className, e);
        }
    }


    public static Object invokeGetter(Object object, String fieldName) {
        Method getter = getMethod(object.getClass(), "get" + capitalizeFirstLetter(fieldName));
        return ReflectionUtil.invokeMethod(object, getter);
    }


    public static Class<?> classFromFieldGenericType(Field field) {
        String fieldGenericType = ReflectionUtil.getFieldGenericType(field)[0].getTypeName();
        return ReflectionUtil.newClass(fieldGenericType);
    }


    private static String capitalizeFirstLetter(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
